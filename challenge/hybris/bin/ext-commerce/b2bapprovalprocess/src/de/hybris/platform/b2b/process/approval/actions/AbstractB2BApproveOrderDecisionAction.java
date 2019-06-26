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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractB2BApproveOrderDecisionAction extends AbstractAction
{
	public enum Transition
	{
		OK, NOK, ERROR;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();

			for (final Transition t : Transition.values())
			{
				res.add(t.toString());
			}
			return res;
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	@Override
	public final String execute(final BusinessProcessModel process) throws RetryLaterException, Exception
	{
		return executeAction(process).toString();


	}

	public Transition executeAction(final BusinessProcessModel process) throws RetryLaterException, Exception
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
	public abstract Transition executeAction(B2BApprovalProcessModel process) throws RetryLaterException, Exception;

	/**
	 * Retrieves the order object from the process
	 * 
	 * @param process
	 *           The business process
	 * @return An order model refreshed from database.
	 */
	public OrderModel getOrderForProcess(final B2BApprovalProcessModel process)
	{
		final OrderModel order = process.getOrder();
		return order;

	}
}
