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
import de.hybris.platform.sap.sapcpiorderexchange.data.SapSendToSapCpiResult;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class SapCpiSendOmmOrderCancellationToScpiAction extends SendOrderCancelRequestAsCSVTaskRunner {

    private static final Logger LOG = Logger.getLogger(SapCpiSendOmmOrderCancellationToScpiAction.class);
    private SapCpiOrderService sapCpiOrderService;
    private SapCpiOrderConversionService sapCpiOrderConversionService;

    @Override
    public void run(TaskService paramTaskService, TaskModel taskModel) throws RetryLaterException {

        final OrderCancelRecordEntryModel orderCancelRecordEntry = (OrderCancelRecordEntryModel) taskModel.getContext();

        getSapCpiOrderService().sendOrderCancellation(getSapCpiOrderConversionService().convertCancelOrderToSapCpiCancelOrder(orderCancelRecordEntry).get(0)).subscribe(
                sendToScpiResult -> handleSuccessMessage(orderCancelRecordEntry, sendToScpiResult)
                , error -> handleErrorMessage(orderCancelRecordEntry, error.getMessage())
        );

    }

    protected void handleSuccessMessage(OrderCancelRecordEntryModel orderCancelRecordEntry, SapSendToSapCpiResult sapSendToSapCpiResult) {

        if (sapSendToSapCpiResult.isSuccessful()) {

            LOG.info(String.format("Cancellation request for order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
                    orderCancelRecordEntry.getModificationRecord().getOrder().getCode(), sapSendToSapCpiResult.getMessage()));

        } else {

            handleErrorMessage(orderCancelRecordEntry, sapSendToSapCpiResult.getMessage());

        }
    }

    protected void handleErrorMessage(OrderCancelRecordEntryModel orderCancelRecordEntry, String message) {

        Registry.activateMasterTenant();

        final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
        order.setStatus(OrderStatus.CANCELLING);
        getModelService().save(order);

        LOG.error(String.format("The order [%s] cancellation has not been sent to the SAP backend through SCPI! %n%s", order.getCode(), message));

    }

    protected SapCpiOrderService getSapCpiOrderService() {
        return sapCpiOrderService;
    }

    @Required
    public void setSapCpiOrderService(SapCpiOrderService sapCpiOrderService) {
        this.sapCpiOrderService = sapCpiOrderService;
    }

    protected SapCpiOrderConversionService getSapCpiOrderConversionService() {
        return sapCpiOrderConversionService;
    }

    @Required
    public void setSapCpiOrderConversionService(SapCpiOrderConversionService sapCpiOrderConversionService) {
        this.sapCpiOrderConversionService = sapCpiOrderConversionService;
    }


}
