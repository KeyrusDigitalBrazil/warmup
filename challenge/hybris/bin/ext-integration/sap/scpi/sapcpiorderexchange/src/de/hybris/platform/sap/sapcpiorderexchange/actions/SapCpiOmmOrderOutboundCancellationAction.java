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

package de.hybris.platform.sap.sapcpiorderexchange.actions;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.orderexchange.taskrunners.SendOrderCancelRequestAsCSVTaskRunner;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderOutboundConversionService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

public class SapCpiOmmOrderOutboundCancellationAction extends SendOrderCancelRequestAsCSVTaskRunner {

  private static final Logger LOG = Logger.getLogger(SapCpiOmmOrderOutboundCancellationAction.class);

  private SapCpiOutboundService sapCpiOutboundService;
  private SapCpiOrderOutboundConversionService sapCpiOrderOutboundConversionService;


  @Override
  public void run(TaskService paramTaskService, TaskModel taskModel) throws RetryLaterException {

    final OrderCancelRecordEntryModel orderCancelRecordEntry = (OrderCancelRecordEntryModel) taskModel.getContext();

    getSapCpiOutboundService().sendOrderCancellation(getSapCpiOrderOutboundConversionService().convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry).iterator().next()).subscribe(

            // onNext
            responseEntityMap -> {

              if (isSentSuccessfully(responseEntityMap)) {

                LOG.info(String.format("The OMM order [%s] cancellation request has been successfully sent to the SAP backend through SCPI! %n%s",
                        orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

              } else {

                handleOrderCancellationFailure(orderCancelRecordEntry);
                LOG.error(String.format("The OMM order [%s] cancellation request has not been sent to the SAP backend! %n%s",
                        orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

              }

            }

            // onError
            , error -> {

              handleOrderCancellationFailure(orderCancelRecordEntry);
              LOG.error(String.format("The OMM order [%s] cancellation request has not been sent to the SAP backend through SCPI! %n%s",
                      orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), error.getMessage()));

            }
    );

  }

  protected void handleOrderCancellationFailure(OrderCancelRecordEntryModel orderCancelRecordEntry) {

    Registry.activateMasterTenant();

    final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
    order.setStatus(OrderStatus.CANCELLING);
    getModelService().save(order);

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
  public void setSapCpiOrderOutboundConversionService(SapCpiOrderOutboundConversionService
                                                              sapCpiOrderOutboundConversionService) {
    this.sapCpiOrderOutboundConversionService = sapCpiOrderOutboundConversionService;
  }

}


