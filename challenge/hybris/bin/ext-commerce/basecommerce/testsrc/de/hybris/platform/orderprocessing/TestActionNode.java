/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.orderprocessing;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;


@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class TestActionNode extends AbstractSimpleDecisionAction
{

	private static final Logger LOG = Logger.getLogger(TestActionNode.class);

	@Override
	public Transition executeAction(final BusinessProcessModel process) throws RetryLaterException, Exception
	{
		LOG.info(getClass().getName() + "entered ");
		return Transition.OK;
	}



}
