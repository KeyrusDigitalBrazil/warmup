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
package de.hybris.platform.sap.saprevenuecloudorder.actions;

import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;

public class SapRevenueCloudOrderConfirmationAction extends AbstractAction<OrderProcessModel> {

	protected enum Transition {

		OK, NOK;

		public static Set<String> getStringValues() {

			final Set<String> res = new HashSet();

			for (final Transition transition : Transition.values()) {
				res.add(transition.toString());
			}

			return res;
		}

	}

	@Override
	public String execute(OrderProcessModel process) throws Exception {

		final OrderModel order = process.getOrder();
		setOrderStatus(order, OrderStatus.CREATED);
		return Transition.OK.toString();
	}

	@Override
	public Set<String> getTransitions() {
		return Transition.getStringValues();
	}
}
