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
package de.hybris.platform.cissapdigitalpayment.actions;

import de.hybris.platform.cissapdigitalpayment.model.SapDigitPayPollCardProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 *
 * Action class to check the the session validity before start the poling process
 */
public class SapDigitalPayCheckSessionAction extends AbstractSimpleDecisionAction<SapDigitPayPollCardProcessModel>
{

	@Override
	public Transition executeAction(final SapDigitPayPollCardProcessModel pollCardProcess)
	{
		Transition returnValue = Transition.OK;
		if (null == pollCardProcess.getSessionId())
		{
			returnValue = Transition.NOK;
		}
		return returnValue;

	}


}
