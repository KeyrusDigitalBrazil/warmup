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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.strategies.BusinessProcessStrategy;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.impl.DefaultCreateOrderFromCartStrategy;
import java.util.List;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class DefaultB2BCreateOrderFromCartStrategy extends DefaultCreateOrderFromCartStrategy implements BusinessProcessStrategy
{
	private List<BusinessProcessStrategy> businessProcessStrategies;

	@Override
	public OrderModel createOrderFromCart(final CartModel cart) throws InvalidCartException
	{
		final OrderModel orderFromCart = super.createOrderFromCart(cart);
		return orderFromCart;
	}

	public void createB2BBusinessProcess(final OrderModel order)
	{
		final OrderStatus status = order.getStatus();
		Assert.notNull(status, "Order status should have been set for order " + order.getCode());
		// retrieve an appropriate strategy for the order based on status.
		final BusinessProcessStrategy businessProcessStrategy = getBusinessProcessStrategy(status.getCode());
		Assert.notNull(businessProcessStrategy,
				String.format("The strategy for creating a business process with name %s should have been created", status.getCode()));
		businessProcessStrategy.createB2BBusinessProcess(order);
	}


	/**
	 * Looks up the correct business process creation strategy based on the order status. The strategy.code attribute
	 * should be injected with an appropriate OrderStatus enumeration value
	 * 
	 * @param code
	 * @return BusinessProcessStrategy
	 */
	public BusinessProcessStrategy getBusinessProcessStrategy(final String code)
	{
		final BeanPropertyValueEqualsPredicate predicate = new BeanPropertyValueEqualsPredicate("processName", code);
		// filter the Collection
		return (BusinessProcessStrategy) CollectionUtils.find(getBusinessProcessStrategies(), predicate);
	}

	@Required
	public void setBusinessProcessStrategies(final List<BusinessProcessStrategy> businessProcessStrategies)
	{
		this.businessProcessStrategies = businessProcessStrategies;
	}

	protected List<BusinessProcessStrategy> getBusinessProcessStrategies()
	{
		return businessProcessStrategies;
	}
}
