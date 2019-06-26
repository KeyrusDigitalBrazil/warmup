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
package de.hybris.platform.sap.ysapomsfulfillment.actions.order;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import org.springframework.beans.factory.annotation.Required;

public class SapCheckCustomerReplicationAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private SAPGlobalConfigurationService sapGlobalConfigurationService;

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();


		if (sapGlobalConfigurationService.getProperty("replicateregistereduser").equals(Boolean.TRUE))
		{
			final CustomerModel customerModel = ((CustomerModel) order.getUser());
			final boolean isCustomerExported = customerModel.getSapIsReplicated().booleanValue();
			final boolean isGuestUser = isGuestUser(customerModel);
			final boolean isB2B = isB2BCase(order);

			if (isCustomerExported || isGuestUser || isB2B)
			{
				return Transition.OK;
			}
			else
			{
				return Transition.NOK;
			}
		}
		return Transition.OK;
	}

	protected boolean isB2BCase(final OrderModel orderModel)
	{
		if (orderModel.getSite() != null)
		{
			return SiteChannel.B2B.equals(orderModel.getSite().getChannel());
		}
		else
		{
			return false;
		}
	}

	protected boolean isGuestUser(final CustomerModel customerModel)
	{
		return CustomerType.GUEST.equals(customerModel.getType());
	}

	protected SAPGlobalConfigurationService getSapGlobalConfigurationService() {
		return sapGlobalConfigurationService;
	}

	@Required
	public void setSapGlobalConfigurationService(SAPGlobalConfigurationService sapGlobalConfigurationService) {
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}

}
