/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Redirects to the proper wait node depending on whether a consignment is for ship or pickup.
 */
public class RedirectConsignmentByDeliveryModeAction extends AbstractAction<ConsignmentProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(RedirectConsignmentByDeliveryModeAction.class);

	@Override
	public String execute(final ConsignmentProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());
		final ConsignmentModel consignment = process.getConsignment();

		String transition = Transition.SHIP.toString();

		if (consignment.getDeliveryMode() instanceof PickUpDeliveryModeModel)
		{
			transition = Transition.PICKUP.toString();
		}

		LOG.debug("Process: {} transitions to {}", process.getCode(), transition);
		return transition;
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected enum Transition
	{
		SHIP, PICKUP;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}
}
