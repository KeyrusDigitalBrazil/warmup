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
package de.hybris.platform.commerceservices.order.hook.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A hook that add the quote discounts back after calculating the cart and then calculating the cart totals
 */
public class DefaultCommerceQuoteCartCalculationMethodHook implements CommerceCartCalculationMethodHook
{
	private static final Logger LOG = Logger.getLogger(DefaultCommerceQuoteCartCalculationMethodHook.class);

	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;
	private CalculationService calculationService;
	private ModelService modelService;

	@Override
	public void afterCalculate(final CommerceCartParameter parameter)
	{
		validateParameterNotNullStandardMessage("CommerceCartParameter", parameter);
		final CartModel cartModel = parameter.getCart();
		if (cartModel == null)
		{
			throw new IllegalArgumentException("The cart model is null.");
		}

		final List<DiscountValue> quoteDiscounts = getOrderQuoteDiscountValuesAccessor().getQuoteDiscountValues(cartModel);
		if (CollectionUtils.isNotEmpty(quoteDiscounts))
		{
			final List<DiscountValue> globalDiscounts = new ArrayList<>(cartModel.getGlobalDiscountValues());
			globalDiscounts.addAll(quoteDiscounts);
			cartModel.setGlobalDiscountValues(globalDiscounts);
			try
			{
				getCalculationService().calculateTotals(cartModel, true);
			}
			catch (final CalculationException e)
			{
				LOG.error("Failed to calculate cart totals [" + cartModel.getCode() + "]", e);
				throw new SystemException("Could not calculate cart [" + cartModel.getCode() + "] due to : " + e.getMessage(), e);
			}
			getModelService().save(cartModel);
		}
	}

	@Override
	public void beforeCalculate(final CommerceCartParameter parameter)
	{
		// No need to do anything here
	}

	protected OrderQuoteDiscountValuesAccessor getOrderQuoteDiscountValuesAccessor()
	{
		return orderQuoteDiscountValuesAccessor;
	}

	@Required
	public void setOrderQuoteDiscountValuesAccessor(final OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor)
	{
		this.orderQuoteDiscountValuesAccessor = orderQuoteDiscountValuesAccessor;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
