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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;


/**
 * The Class AbstractSimpleB2BApproveOrderDecisionAction.
 */
public abstract class AbstractSimpleB2BApproveOrderDecisionAction extends AbstractSimpleDecisionAction
{

	/*
	 * (non-Javadoc)
	 * @see
	 * de.hybris.platform.processengine.action.AbstractSimpleDecisionAction#executeAction(de.hybris.platform.processengine
	 * .model.BusinessProcessModel)
	 */
	@Override
	public final Transition executeAction(final BusinessProcessModel process) throws RetryLaterException
	{
		return executeAction((B2BApprovalProcessModel) process);
	}

	/**
	 * Execute an action of a B2B approval process
	 * 
	 * @param process
	 *           the b2b approval process
	 * @return the transition OK, NOK.
	 * @throws RetryLaterException
	 *            Triggers the action to be processes again.
	 * @throws Exception
	 *            Any error has occurred.
	 */
	public abstract Transition executeAction(B2BApprovalProcessModel process) throws RetryLaterException;

}
