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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.BusinessProcessStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.PlaceOrderStrategy;
import de.hybris.platform.order.strategies.impl.DefaultPlaceOrderStrategy;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * A place order strategy which delegates to {@link DefaultPlaceOrderStrategy} and starts a order approval workflow
 *
 * @deprecated Since 4.4. Use {@link DefaultB2BCreateOrderFromCartStrategy}
 */

@Deprecated
public class DefaultB2BPlaceOrderStrategy implements PlaceOrderStrategy
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BPlaceOrderStrategy.class);
	private List<BusinessProcessStrategy> businessProcessStrategies;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	private I18NService i18nService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.order.strategies.PlaceOrderStrategy#placeOrder(de.hybris.platform.core.model.order.CartModel,
	 * de.hybris.platform.core.model.user.AddressModel, de.hybris.platform.core.model.user.AddressModel,
	 * de.hybris.platform.core.model.order.payment.PaymentInfoModel)
	 */
	@Override
	public OrderModel placeOrder(final CartModel cart, final AddressModel deliveryAddress, final AddressModel paymentAddress,
			final PaymentInfoModel paymentInfo) throws InvalidCartException
	{

		final boolean b2BContext = isB2BContext(cart);
		if (b2BContext)
		{
			cart.setStatus(OrderStatus.CREATED);
			if (cart.getUnit() == null)
			{
				final B2BCustomerModel currentB2BCustomer = (B2BCustomerModel) cart.getUser();
				cart.setUnit(getB2bUnitService().getParent(currentB2BCustomer));
			}
			cart.setLocale(getI18nService().getCurrentLocale().toString());
		}
		final OrderModel order = this.getPlaceOrderStrategy().placeOrder(cart, deliveryAddress, paymentAddress, paymentInfo);

		// only apply b2b post order creation logic if the order is placed within b2b context
		if (b2BContext)
		{
			//This is B2B field, which is not getting copied into order
			order.setB2bcomments(cart.getB2bcomments());
			createB2BBusinessProcess(order);
		}
		return order;
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


	protected void createB2BBusinessProcess(final OrderModel order)
	{
		final OrderStatus status = order.getStatus();
		Assert.notNull(status, "Order status should have been set for order " + order.getCode());
		final BusinessProcessStrategy businessProcessStrategy = getBusinessProcessStrategy(status.getCode());
		Assert.notNull(businessProcessStrategy,
				String.format("The stragegy for creating a business process with name %s should have been created", status.getCode()));
		businessProcessStrategy.createB2BBusinessProcess(order);
	}

	/**
	 * @return the placeOrderStrategy
	 */
	protected PlaceOrderStrategy getPlaceOrderStrategy()
	{
		throw new UnsupportedOperationException();

	}

	/**
	 * @param placeOrderStrategy
	 *           the placeOrderStrategy to set
	 */

	public void setPlaceOrderStrategy(final PlaceOrderStrategy placeOrderStrategy)
	{
		throw new UnsupportedOperationException();
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
		return (BusinessProcessStrategy) CollectionUtils.find(this.businessProcessStrategies, predicate);

	}


	@Required
	public void setBusinessProcessStrategies(final List<BusinessProcessStrategy> businessProcessStrategies)
	{
		this.businessProcessStrategies = businessProcessStrategies;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Required
	public void setB2bCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}
}
