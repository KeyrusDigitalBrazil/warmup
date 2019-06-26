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
package de.hybris.platform.sap.orderexchange.actions;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.task.RetryLaterException;
import org.springframework.beans.factory.annotation.Required;

/**
 * To send the order to Data Hub to be processed and sent as an IDoc to the SAP back end.
 * The retry logic is applied in case the sending did not succeed.
 * The order export status is set to EXPORTED / NOT EXPORTED accordingly.
 */
public class SapSendOrderToDataHubAction extends AbstractProceduralAction<OrderProcessModel> {

    static final int DEFAULT_MAX_RETRIES = 10;
    static final int DEFAULT_RETRY_DELAY = 60 * 1000; // value in ms
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int retryDelay = DEFAULT_RETRY_DELAY;

    private SendToDataHubHelper<OrderModel> sendOrderToDataHubHelper;
    private BusinessProcessService businessProcessService;

    @Override
    public void executeAction(final OrderProcessModel process) throws RetryLaterException {

        final OrderModel order = process.getOrder();
        final SendToDataHubResult result = sendOrderToDataHubHelper.createAndSendRawItem(order);
        if (result.isSuccess()) {
            setOrderStatus(order, ExportStatus.EXPORTED);
            resetEndMessage(process);

        } else {
            setOrderStatus(order, ExportStatus.NOTEXPORTED);
            handleRetry(process);

        }

        final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
        getBusinessProcessService().triggerEvent(eventName);

    }

    protected void handleRetry(final OrderProcessModel process) throws RetryLaterException {

        if (process.getSendOrderRetryCount() < getMaxRetries()) {
            final OrderModel order = process.getOrder();
            process.setSendOrderRetryCount(process.getSendOrderRetryCount() + 1);
            modelService.save(process);

            final RetryLaterException ex = new RetryLaterException("Sending to backend failed for order " + order.getCode());
            ex.setRollBack(false);
            ex.setDelay(getRetryDelay());

            throw ex;
        }

    }

    protected void setOrderStatus(final OrderModel order, final ExportStatus exportStatus) {

        order.setExportStatus(exportStatus);
        save(order);

    }

    protected void resetEndMessage(final OrderProcessModel process) {

        final String endMessage = process.getEndMessage();
        if (SapOrderExchangeActionConstants.ERROR_END_MESSAGE.equals(endMessage)) {
            process.setEndMessage("");
            modelService.save(process);
        }

    }


    protected SendToDataHubHelper<OrderModel> getSendOrderToDataHubHelper() {
        return sendOrderToDataHubHelper;
    }

    @Required
    public void setSendOrderToDataHubHelper(final SendToDataHubHelper<OrderModel> sendOrderAsCSVHelper) {
        this.sendOrderToDataHubHelper = sendOrderAsCSVHelper;
    }

    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    @Required
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
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

}
