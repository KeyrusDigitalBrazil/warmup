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
package com.sap.hybris.returnsexchange.actions;

import org.springframework.beans.factory.annotation.Required;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.returns.model.ReturnProcessModel;

public class SendReturnOrderToDataHubAction extends AbstractSimpleDecisionAction<ReturnProcessModel> {
    public static final String ERROR_END_MESSAGE = "Sending to Datahub went wrong.";

    private SendToDataHubHelper<ReturnRequestModel> sendOrderToDataHubHelper;

   
    public SendToDataHubHelper<ReturnRequestModel> getSendOrderToDataHubHelper() {
        return sendOrderToDataHubHelper;
    }

  
    @Required
    public void setSendOrderToDataHubHelper(final SendToDataHubHelper<ReturnRequestModel> sendOrderAsCSVHelper) {
        this.sendOrderToDataHubHelper = sendOrderAsCSVHelper;
    }


  
    @Override
    public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException {
        final ReturnRequestModel returnRequest = process.getReturnRequest();
        final SendToDataHubResult result = sendOrderToDataHubHelper.createAndSendRawItem(returnRequest);
        if (result.isSuccess()) {
            resetEndMessage(process);
            return Transition.OK;
        } else {
            return Transition.NOK;
        }

    }

    protected void resetEndMessage(final ReturnProcessModel process) {
        final String endMessage = process.getEndMessage();
        if (ERROR_END_MESSAGE.equals(endMessage)) {
            process.setEndMessage("");
            modelService.save(process);
        }
    }

  }
