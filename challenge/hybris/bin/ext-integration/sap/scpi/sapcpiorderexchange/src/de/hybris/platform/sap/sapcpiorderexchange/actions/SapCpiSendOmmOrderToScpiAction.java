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
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.orderexchange.actions.SapSendOrderToDataHubAction;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * To send the order to SCPI to be processed and sent as an IDoc to the SAP back end.
 * The order export status is set to EXPORTED / NOT EXPORTED accordingly.
 */
public class SapCpiSendOmmOrderToScpiAction extends SapSendOrderToDataHubAction {

    private static final Logger LOG = Logger.getLogger(SapCpiSendOmmOrderToScpiAction.class);
    private SapCpiOrderService sapCpiOrderService;
    private SapCpiOrderConversionService sapCpiOrderConversionService;

    @Override
    public void executeAction(final OrderProcessModel process) throws RetryLaterException {

        final OrderModel order = process.getOrder();

        getSapCpiOrderService().sendOrder(getSapCpiOrderConversionService().convertOrderToSapCpiOrder(order)).subscribe(

                sapSendToSapCpiResult -> {

                    Registry.activateMasterTenant();

                    if (sapSendToSapCpiResult.isSuccessful()) {

                        setOrderStatus(order, ExportStatus.EXPORTED);
                        resetEndMessage(process);
                        LOG.info(sapSendToSapCpiResult.getMessage());

                    } else {

                        setOrderStatus(order, ExportStatus.NOTEXPORTED);
                        LOG.error(sapSendToSapCpiResult.getMessage());

                    }

                    final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
                    getBusinessProcessService().triggerEvent(eventName);

                }

                ,error -> {

                    Registry.activateMasterTenant();

                    setOrderStatus(order, ExportStatus.NOTEXPORTED);
                    LOG.error(String.format("The order [%s] has not been sent to the SAP backend through SCPI!%n", order.getCode()) + error.getMessage());

                    final String eventName = new StringBuilder().append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT).append(order.getCode()).toString();
                    getBusinessProcessService().triggerEvent(eventName);

                }

        );

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
