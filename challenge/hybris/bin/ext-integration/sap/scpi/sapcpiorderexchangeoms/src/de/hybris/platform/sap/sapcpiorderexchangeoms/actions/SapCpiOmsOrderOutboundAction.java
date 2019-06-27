/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiorderexchangeoms.actions;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.saporderexchangeoms.actions.SapOmsSendOrderToDataHubAction;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import rx.Observable;
import rx.Single;
import rx.Subscriber;

import java.util.*;
import java.util.stream.Collectors;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

public class SapCpiOmsOrderOutboundAction extends SapOmsSendOrderToDataHubAction {

  private static final Logger LOG = Logger.getLogger(SapCpiOmsOrderOutboundAction.class);

  private SapCpiOutboundService sapCpiOutboundService;
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;


  @Override
  public void executeAction(OrderProcessModel process) throws RetryLaterException {

    final OrderModel order = process.getOrder();

    final List<OrderHistoryEntryModel> orderHistoryList = new ArrayList<>();
    final List<SAPOrderModel> sapOrders = new ArrayList<>();

    Set<Set<ConsignmentModel>> orderConsignmentsSets = groupOrderConsignments(order);

    Observable.merge(sendOrdersToScpi(order, orderConsignmentsSets, orderHistoryList, sapOrders))
            .subscribe(new ResponseEntitySubscriber(orderHistoryList, order, sapOrders, process));

  }

  protected List<Observable<ResponseEntity<Map>>> sendOrdersToScpi(OrderModel order,
                                                                   Set<Set<ConsignmentModel>> orderConsignmentsSets,
                                                                   List<OrderHistoryEntryModel> orderHistoryList,
                                                                   List<SAPOrderModel> sapOrders) {
    return orderConsignmentsSets.stream().map(consignmentsSet ->
            sendOrderToScpi(order, consignmentsSet, orderHistoryList, sapOrders).toObservable())
            .collect(Collectors.toList());
  }

  protected Single<ResponseEntity<Map>> sendOrderToScpi(OrderModel order, Set<ConsignmentModel> consignments,
                                                        List<OrderHistoryEntryModel> orderHistoryList,
                                                        List<SAPOrderModel> sapOrders) {

    // Read customizing data from the base store configuration
    SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
            order.getStore(), consignments.stream().findFirst().get().getWarehouse().getCode());

    final OrderHistoryEntryModel orderHistoryEntry = createOrderHistory(order, sapPlantLogSysOrgModel.getLogSys()
            .getSapLogicalSystemName());

    // Add send SAP order action to the order history
    orderHistoryList.add(orderHistoryEntry);

    createSapOrders(sapOrders, orderHistoryEntry, consignments);

    // Clone the order
    OrderModel clonedOrder = getOrderService().clone(null, null, order,
            orderHistoryEntry.getPreviousOrderVersion().getVersionID());

    List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

    // Copy order entries
    consignments.stream().forEach(
            consignment -> consignment.getConsignmentEntries().stream()
                    .forEach(entry -> orderEntries.add(entry.getOrderEntry())));

    // Set cloned order attributes
    clonedOrder.setConsignments(consignments);
    clonedOrder.setSapLogicalSystem(sapPlantLogSysOrgModel.getLogSys().getSapLogicalSystemName());
    clonedOrder.setSapSalesOrganization(sapPlantLogSysOrgModel.getSalesOrg());
    clonedOrder.setEntries(orderEntries);
    clonedOrder.setSapSystemType(sapPlantLogSysOrgModel.getLogSys().getSapSystemType());
    clonedOrder.setPaymentTransactions(order.getPaymentTransactions());

    // Send cloned order to SCPI
    return getSapCpiOutboundService().sendOrder(getSapCpiOrderOutboundConversionService().convertOrderToSapCpiOrder(clonedOrder)).toSingle();

  }

  protected SapCpiOutboundService getSapCpiOutboundService() {
    return sapCpiOutboundService;
  }

  @Required
  public void setSapCpiOutboundService(SapCpiOutboundService sapCpiOutboundService) {
    this.sapCpiOutboundService = sapCpiOutboundService;
  }

  protected SapCpiOrderOutboundConversionService getSapCpiOrderOutboundConversionService() {
    return sapCpiOrderOutboundConversionService;
  }

  @Required
  public void setSapCpiOrderOutboundConversionService(SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService) {
    this.sapCpiOrderOutboundConversionService = sapCpiOrderOutboundConversionService;
  }

  private class ResponseEntitySubscriber extends Subscriber<ResponseEntity<Map>> {

    private final List<ResponseEntity<Map>> results;
    private final List<OrderHistoryEntryModel> orderHistoryList;
    private final OrderModel order;
    private final List<SAPOrderModel> sapOrders;
    private final OrderProcessModel process;

    ResponseEntitySubscriber(List<OrderHistoryEntryModel> orderHistoryList, OrderModel order,
                             List<SAPOrderModel> sapOrders, OrderProcessModel process) {
      this.results = new ArrayList<>();
      this.orderHistoryList = orderHistoryList;
      this.order = order;
      this.sapOrders = sapOrders;
      this.process = process;
    }

    @Override
    public void onNext(ResponseEntity<Map> sapSendToSapCpiResult) {
      results.add(sapSendToSapCpiResult);
    }

    @Override
    public void onCompleted() {

      Registry.activateMasterTenant();

      if (!results.isEmpty() && results.stream().allMatch(result -> isSentSuccessfully(result))) {

        saveOrderHistory(orderHistoryList);
        saveSapOrders(order, sapOrders);
        startSapConsignmentSubProcess(order.getConsignments(), process);
        resetEndMessage(process);

        setOrderStatus(order, ExportStatus.EXPORTED);
        StringBuilder successMsg = new StringBuilder();
        results.forEach(result -> successMsg.append(getPropertyValue(result, RESPONSE_MESSAGE)).append("%n"));

        LOG.info(String.format("The OMS order [%s] has been sent successfully to the SAP backend through SCPI! The related SAP order(s): %n%s",
                order.getCode(), String.format(successMsg.toString())));

      } else {

        setOrderStatus(order, ExportStatus.NOTEXPORTED);
        StringBuilder errorMsg = new StringBuilder();
        results.stream().filter(result -> !isSentSuccessfully(result))
                .forEach(result -> errorMsg.append(getPropertyValue(result, RESPONSE_MESSAGE)).append("%n"));

        LOG.error(String.format("The OMS order [%s] has not been sent to the SAP backend! %n%s",
                order.getCode(), String.format(errorMsg.toString())));

      }

      final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
      getBusinessProcessService().triggerEvent(eventName);

    }

    @Override
    public void onError(Throwable error) {

      Registry.activateMasterTenant();

      LOG.error(String.format("The OMS order [%s] has not been sent to the SAP backend through SCPI! %n%s", order.getCode(), error.getMessage()));

      setOrderStatus(order, ExportStatus.NOTEXPORTED);

      final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
      getBusinessProcessService().triggerEvent(eventName);

    }

  }
}
