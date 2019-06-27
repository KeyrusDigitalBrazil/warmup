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
package com.sap.hybris.sapomsreturnprocess.actions;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;



public class SapGoodsReceiptStatusChecker extends AbstractSimpleDecisionAction<ReturnProcessModel>
{


	@Override
	public Transition executeAction(final ReturnProcessModel returnProcess)
	{
		final ReturnRequestModel returnRequest = returnProcess.getReturnRequest();
		if (returnRequest.getSapReturnRequests().stream().allMatch(returnStatus -> returnStatus.getIsDelivered()))
		{
			return Transition.OK;
		}
		else
		{
			return Transition.NOK;

		}
	}

}
