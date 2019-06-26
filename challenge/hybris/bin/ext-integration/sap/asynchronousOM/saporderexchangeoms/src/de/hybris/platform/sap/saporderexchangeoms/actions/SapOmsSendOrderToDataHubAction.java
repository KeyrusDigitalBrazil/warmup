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
package de.hybris.platform.sap.saporderexchangeoms.actions;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.sap.saporderexchangeoms.constants.SapOmsOrderExchangeConstants;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

/**
 * To send the order to Data Hub to be processed and sent as an IDoc to the SAP back end.
 * The retry logic is applied in case the sending did not succeed.
 * The order export status is set to EXPORTED / NOT EXPORTED accordingly.
 */
public class SapOmsSendOrderToDataHubAction extends AbstractProceduralAction<OrderProcessModel> {

    private static final Logger LOG = Logger.getLogger(SapOmsSendOrderToDataHubAction.class);

    private int maxRetries = SapOmsOrderExchangeConstants.DEFAULT_MAX_RETRIES;
    private int retryDelay = SapOmsOrderExchangeConstants.DEFAULT_RETRY_DELAY;
    private SendToDataHubHelper<OrderModel> sendOrderToDataHubHelper;
    private OrderHistoryService orderHistoryService;
    private OrderService orderService;
    private SapPlantLogSysOrgService sapPlantLogSysOrgService;
    private TimeService timeService;
    private BusinessProcessService businessProcessService;

    @Override
    public void executeAction(final OrderProcessModel process) throws RetryLaterException {

        final OrderModel order = process.getOrder();

        List<SendToDataHubResult> results = new ArrayList<>();
        List<OrderHistoryEntryModel> orderHistoryList = new ArrayList<>();
        List<SAPOrderModel> sapOrders = new ArrayList<>();

        groupOrderConsignments(order)//
                .stream()//
                .forEach(consignmentSet -> sendOrder(order, results, orderHistoryList, consignmentSet, sapOrders));

        if (!results.isEmpty() && results.stream().allMatch(result -> result.isSuccess())) {

            saveOrderHistory(orderHistoryList);
            saveSapOrders(order, sapOrders);
            setOrderStatus(order, ExportStatus.EXPORTED);
            startSapConsignmentSubProcess(order.getConsignments(), process);
            resetEndMessage(process);
            LOG.info(String.format("The order [%s] has been sent successfully to the SAP backend through data hub!", order.getCode()));

        } else {

            setOrderStatus(order, ExportStatus.NOTEXPORTED);
            handleRetry(process);
            LOG.error(String.format("The order [%s] has not been sent to the SAP backend through data hub! The order resend will be retried shortly.", order.getCode()));

        }

        final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
        getBusinessProcessService().triggerEvent(eventName);

    }

    /**
     * Group the order consignments by SAP logical system and SAP sales
     * organization
     *
     * @param order
     * @return
     */
    protected Set<Set<ConsignmentModel>> groupOrderConsignments(OrderModel order) {

        Set<ConsignmentModel> orderConsignments = order.getConsignments();

		/*
         * Sample input data
		 * 
		 * { LOGSYS001={ 1000_10_00=[ConsignmentModel (8797240100819),
		 * ConsignmentModel (8797240100888)], 2000_20_00=[ConsignmentModel
		 * (8797240068051)]}
		 * 
		 * LOGSYS002={ 3000_30_00=[ConsignmentModel (8797240100777),
		 * ConsignmentModel (8797240100666)], 2000_20_00=[ConsignmentModel
		 * (8797240068555)]}
		 * 
		 * }
		 */
        Map<String, Map<String, Set<ConsignmentModel>>> mapByLogSysSalesOrg = orderConsignments.stream().collect(
                Collectors.groupingBy(consignment -> getSapLogSysName(order, consignment), Collectors.groupingBy(
                        consignment -> getSapSalesOrgName(order, consignment), Collectors.toSet())));

        Set<Set<ConsignmentModel>> consignmentSets = new HashSet<Set<ConsignmentModel>>();

        mapByLogSysSalesOrg
                .entrySet()
                .stream()
                .forEach(
                        entryKey -> entryKey.getValue().entrySet().stream()
                                .forEach(entryValue -> consignmentSets.add(entryValue.getValue())));
        /*
         * Sample out data
		 * 
		 * [ [ConsignmentModel (8797240100819), ConsignmentModel
		 * (8797240100888)], [ConsignmentModel(8797240068051)],
		 * 
		 * [ConsignmentModel (8797240100777), ConsignmentModel (8797240100666)],
		 * [ConsignmentModel(8797240068555)], ],
		 */
        return consignmentSets;
    }

    /**
     * @param order
     * @param consignment
     * @return
     */
    protected String getSapSalesOrgName(OrderModel order, ConsignmentModel consignment) {

        SAPSalesOrganizationModel salesOrganization = getSapPlantLogSysOrgService()
                .getSapSalesOrganizationForPlant(order.getStore(), consignment.getWarehouse().getCode());

        if (salesOrganization.getSalesOrganization() != null) {

            return new StringBuilder(salesOrganization.getSalesOrganization()).append("_")
                    .append(salesOrganization.getDistributionChannel()).append("_")
                    .append(salesOrganization.getDivision()).toString();
        }

        LOG.error("SAP Sales Organization is missing!");
        return SapOmsOrderExchangeConstants.MISSING_SALES_ORG;
    }

    /**
     * @param order
     * @param consignment
     * @return
     */
    protected String getSapLogSysName(OrderModel order, ConsignmentModel consignment) {
        String sapLogicalSystemName = getSapPlantLogSysOrgService()
                .getSapLogicalSystemForPlant(order.getStore(), consignment.getWarehouse().getCode()).getSapLogicalSystemName();

        if (sapLogicalSystemName != null) {
            return sapLogicalSystemName;
        }

        LOG.error("SAP logical system is missing!");
        return SapOmsOrderExchangeConstants.MISSING_LOG_SYS;
    }

    /**
     * Send SAP order to data-hub
     *
     * @param order
     * @param results
     * @param orderHistoryList
     * @param consignments
     */
    protected void sendOrder(final OrderModel order, List<SendToDataHubResult> results,
                             List<OrderHistoryEntryModel> orderHistoryList, final Set<ConsignmentModel> consignments,
                             final List<SAPOrderModel> sapOrders) {

        // Read customizing data from the base store configuration
        SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
                order.getStore(), consignments.stream().findFirst().get().getWarehouse().getCode());

        final OrderHistoryEntryModel orderHistoryEntry = createOrderHistory(order, sapPlantLogSysOrgModel.getLogSys()
                .getSapLogicalSystemName());

        createSapOrders(sapOrders, orderHistoryEntry, consignments);

        // Clone the order
        OrderModel clonedOrder = getOrderService().clone(null, null, order,
                orderHistoryEntry.getPreviousOrderVersion().getVersionID());

        List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();

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

        // Send cloned order to data-hub
        results.add(sendOrderToDataHubHelper.createAndSendRawItem(clonedOrder));

        // Add send SAP order action to the order history
        orderHistoryList.add(orderHistoryEntry);
    }

    /**
     * Create an SAP order and add it to the given list of orders
     *
     * @param sapOrders
     * @param orderHistoryEntry
     * @param consignments
     */
    protected void createSapOrders(final List<SAPOrderModel> sapOrders, final OrderHistoryEntryModel orderHistoryEntry,
                                   final Set<ConsignmentModel> consignments) {

        SAPOrderModel sapOrder = getModelService().create(SAPOrderModel.class);
        sapOrder.setCode(orderHistoryEntry.getPreviousOrderVersion().getVersionID());
        sapOrder.setConsignments(consignments);
        sapOrders.add(sapOrder);
    }

    /**
     * Save SAP orders
     *
     * @param order
     * @param sapOrders
     */
    protected void saveSapOrders(final OrderModel order, final List<SAPOrderModel> sapOrders) {

        sapOrders.stream().forEach(sapOrder -> {
            sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);
            sapOrder.setOrder(order);
            getModelService().save(sapOrder);
        });

    }

    /**
     * Save order history
     *
     * @param orderHistoryList
     */
    protected void saveOrderHistory(List<OrderHistoryEntryModel> orderHistoryList) {

        orderHistoryList.stream().forEach(entry -> {
            entry.setTimestamp(getTimeService().getCurrentTime());
            getModelService().save(entry);
        });
    }

    /**
     * Create an entry to the order history for every SAP order sent to data-hub
     *
     * @param order
     * @param logicalSystem
     * @return
     */
    protected OrderHistoryEntryModel createOrderHistory(final OrderModel order, String logicalSystem) {

        final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
        getOrderHistoryService().saveHistorySnapshot(snapshot);

        final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);

        historyEntry.setOrder(order);
        historyEntry.setPreviousOrderVersion(snapshot);

        historyEntry.setDescription(String.format("SAP sales document %s has been sent to the logical system %s",
                snapshot.getVersionID(), logicalSystem));

        return historyEntry;

    }

    /**
     * * Start an SAP consignment process for every hybris consignment
     *
     * @param consignments
     * @param process
     */
    protected void startSapConsignmentSubProcess(final Collection<ConsignmentModel> consignments,
                                                 final OrderProcessModel process) {

        for (final ConsignmentModel consignment : consignments) {

            String processCode = new StringBuilder(process.getOrder().getCode())//
                    .append(SapOmsOrderExchangeConstants.SAP_CONS)//
                    .append(consignment.getCode())//
                    .toString();

            final SapConsignmentProcessModel subProcess = getBusinessProcessService()
                    .<SapConsignmentProcessModel>createProcess(processCode,
                            SapOmsOrderExchangeConstants.CONSIGNMENT_SUBPROCESS_NAME);

            subProcess.setParentProcess(process);
            subProcess.setConsignment(consignment);
            save(subProcess);

            getBusinessProcessService().startProcess(subProcess);
            LOG.info(String.format("SAP consignment sub-process %s has started!", subProcess.getCode()));
        }
    }

    /**
     * @param process
     * @throws RetryLaterException
     */
    protected void handleRetry(final OrderProcessModel process) throws RetryLaterException {
        if (process.getSendOrderRetryCount() < getMaxRetries()) {
            final OrderModel order = process.getOrder();
            process.setSendOrderRetryCount(process.getSendOrderRetryCount() + 1);
            modelService.save(process);
            final RetryLaterException ex = new RetryLaterException(String.format(
                    "Sending to backend failed for order %s!", order.getCode()));
            ex.setDelay(getRetryDelay());
            ex.setRollBack(false);
            throw ex;
        }
    }

    /**
     * @param order
     * @param exportStatus
     */
    protected void setOrderStatus(final OrderModel order, final ExportStatus exportStatus) {
        order.setExportStatus(exportStatus);
        save(order);
    }

    /**
     * @param process
     */
    protected void resetEndMessage(final OrderProcessModel process) {
        final String endMessage = process.getEndMessage();
        if (SapOmsOrderExchangeConstants.ERROR_END_MESSAGE.equals(endMessage)) {
            process.setEndMessage("");
            modelService.save(process);
        }
    }

    protected TimeService getTimeService() {
        return timeService;
    }

    @Required
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    protected SapPlantLogSysOrgService getSapPlantLogSysOrgService() {
        return sapPlantLogSysOrgService;
    }

    @Required
    public void setSapPlantLogSysOrgService(SapPlantLogSysOrgService sapPlantLogSysOrgService) {
        this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
    }

    protected OrderService getOrderService() {
        return orderService;
    }

    @Required
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    protected OrderHistoryService getOrderHistoryService() {
        return orderHistoryService;
    }

    @Required
    public void setOrderHistoryService(OrderHistoryService orderHistoryService) {
        this.orderHistoryService = orderHistoryService;
    }

    @SuppressWarnings("javadoc")
    protected SendToDataHubHelper<OrderModel> getSendOrderToDataHubHelper() {
        return sendOrderToDataHubHelper;
    }

    @SuppressWarnings("javadoc")
    @Required
    public void setSendOrderToDataHubHelper(final SendToDataHubHelper<OrderModel> sendOrderAsCSVHelper) {
        this.sendOrderToDataHubHelper = sendOrderAsCSVHelper;
    }

    @SuppressWarnings("javadoc")
    protected int getMaxRetries() {
        return maxRetries;
    }

    @SuppressWarnings("javadoc")
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @SuppressWarnings("javadoc")
    protected int getRetryDelay() {
        return retryDelay;
    }

    @SuppressWarnings("javadoc")
    public void setRetryDelay(final int retryDelay) {
        this.retryDelay = retryDelay;
    }

    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    @Required
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

}
