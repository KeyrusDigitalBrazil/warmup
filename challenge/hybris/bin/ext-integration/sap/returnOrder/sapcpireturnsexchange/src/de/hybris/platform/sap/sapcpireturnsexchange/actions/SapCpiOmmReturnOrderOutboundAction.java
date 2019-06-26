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
package de.hybris.platform.sap.sapcpireturnsexchange.actions;

import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.SapOrderExchangeActionConstants;
import de.hybris.platform.sap.sapcpireturnsexchange.constants.SapcpireturnsexchangeConstants;
import de.hybris.platform.sap.sapcpireturnsexchange.service.SapCpiOutboundReturnService;
import de.hybris.platform.sap.sapcpireturnsexchange.service.SapCpiReturnOutboundConversionService;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.returnsexchange.actions.SendReturnOrderToDataHubAction;

/**
 *
 */
public class SapCpiOmmReturnOrderOutboundAction extends SendReturnOrderToDataHubAction {

    private static final Logger LOG = Logger.getLogger(SapCpiOmmReturnOrderOutboundAction.class);

    private SapCpiOutboundReturnService sapCpiOutboundReturnService;

    private SapCpiReturnOutboundConversionService sapCpiReturnsOutboundConversionService;

    private BusinessProcessService businessProcessService;

    public static final String ERROR_END_MESSAGE = "Sending to SCPI went wrong.";

    @Override
    public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException {

        final ReturnRequestModel returnRequest = process.getReturnRequest();
        getSapCpiOutboundReturnService().sendReturnOrder(getSapCpiReturnsOutboundConversionService()
                .convertReturnOrderToSapCpiOutboundReturnOrder(returnRequest)).subscribe(

                        // onNext
                        responseEntityMap -> {

                            Registry.activateMasterTenant();

                            if (SapCpiOutboundReturnService.isSentSuccessfully(responseEntityMap)) {

                                resetEndMessage(process);
                                LOG.info(String.format(
                                        "The OMM return order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
                                        returnRequest.getCode(), SapCpiOutboundReturnService.getPropertyValue(
                                                responseEntityMap, SapcpireturnsexchangeConstants.RESPONSE_MESSAGE)));

                            } else {

                                LOG.error(String.format(
                                        "The OMM return order [%s] has not been sent to the SAP backend! %n%s",
                                        returnRequest.getCode(), SapCpiOutboundReturnService.getPropertyValue(
                                                responseEntityMap, SapcpireturnsexchangeConstants.RESPONSE_MESSAGE)));

                            }

                            final String eventName = new StringBuilder()
                                    .append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT)
                                    .append(returnRequest.getCode()).toString();
                            getBusinessProcessService().triggerEvent(eventName);
                        }, error -> {

                            Registry.activateMasterTenant();

                            LOG.error(String.format(
                                    "The OMM return order [%s] has not been sent to the SAP backend through SCPI! %n%s",
                                    returnRequest.getCode(), error.getMessage()));

                            final String eventName = new StringBuilder()
                                    .append(SapOrderExchangeActionConstants.ERP_ORDER_SEND_COMPLETION_EVENT)
                                    .append(returnRequest.getCode()).toString();
                            getBusinessProcessService().triggerEvent(eventName);

                        }

        );
        return Transition.OK;
    }

    @Override
    protected void resetEndMessage(final ReturnProcessModel process) {
        final String endMessage = process.getEndMessage();
        if (ERROR_END_MESSAGE.equals(endMessage)) {
            process.setEndMessage("");
            modelService.save(process);
        }
    }

    protected SapCpiOutboundReturnService getSapCpiOutboundReturnService() {
        return sapCpiOutboundReturnService;
    }

    @Required
    public void setSapCpiOutboundReturnService(final SapCpiOutboundReturnService sapCpiOutboundReturnService) {
        this.sapCpiOutboundReturnService = sapCpiOutboundReturnService;
    }

    protected SapCpiReturnOutboundConversionService getSapCpiReturnsOutboundConversionService() {
        return sapCpiReturnsOutboundConversionService;
    }

    @Required
    public void setSapCpiReturnsOutboundConversionService(
            final SapCpiReturnOutboundConversionService sapCpiReturnsOutboundConversionService) {
        this.sapCpiReturnsOutboundConversionService = sapCpiReturnsOutboundConversionService;
    }

    protected BusinessProcessService getBusinessProcessService() {
        return businessProcessService;
    }

    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

}
