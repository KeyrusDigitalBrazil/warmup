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
package de.hybris.platform.b2b.order.hooks;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.strategies.BusinessProcessStrategy;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class B2BApprovalBusinessProcessCreationPlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private static final Logger LOG = Logger.getLogger(B2BApprovalBusinessProcessCreationPlaceOrderMethodHook.class);

	private BusinessProcessStrategy businessProcessCreationStrategy;

	@Override
	public void afterPlaceOrder(CommerceCheckoutParameter commerceCheckoutParameter, CommerceOrderResult commerceOrderResult)
	{

		if (!isB2BContext(commerceOrderResult.getOrder()))
		{
			return;
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Post processing a b2b order %s created from cart", commerceOrderResult.getOrder()));
		}

		businessProcessCreationStrategy.createB2BBusinessProcess(commerceOrderResult.getOrder());
	}

	@Override
	public void beforePlaceOrder(CommerceCheckoutParameter commerceCheckoutParameter)
	{
		// not implemented
	}

	@Override
	public void beforeSubmitOrder(CommerceCheckoutParameter commerceCheckoutParameter, CommerceOrderResult commerceOrderResult)
	{
		// not implemented
	}

	protected boolean isB2BContext(final AbstractOrderModel order)
	{
		if (order != null && order.getUser() != null)
		{
			return order.getUser() instanceof B2BCustomerModel;
		}
		else
		{
			return false;
		}
	}

	@Required
	public void setBusinessProcessCreationStrategy(final BusinessProcessStrategy businessProcessCreationStrategy)
	{
		this.businessProcessCreationStrategy = businessProcessCreationStrategy;
	}

	protected BusinessProcessStrategy getBusinessProcessCreationStrategy()
	{
		return businessProcessCreationStrategy;
	}
}
