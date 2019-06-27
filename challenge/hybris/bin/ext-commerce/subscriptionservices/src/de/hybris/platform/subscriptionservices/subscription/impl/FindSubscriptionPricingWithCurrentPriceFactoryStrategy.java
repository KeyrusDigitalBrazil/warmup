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
package de.hybris.platform.subscriptionservices.subscription.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.FindPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This strategy should replace the FindPricingWithCurrentPriceFactoryStrategy and implements the price/discount finding
 * strategy for {@link ProductModel}s.
 */
public class FindSubscriptionPricingWithCurrentPriceFactoryStrategy extends FindPricingWithCurrentPriceFactoryStrategy
{
	private static final Logger LOG = Logger.getLogger(FindSubscriptionPricingWithCurrentPriceFactoryStrategy.class);

	private SubscriptionCommercePriceService commercePriceService;
	private SubscriptionProductService subscriptionProductService;

	/**
	 * Resolves the subscription price value for the given AbstractOrderEntryModel by searching a {@link ProductModel}
	 * that is applicable for the entry's subscription product. In case the entry's product is not a {@code ProductModel}
	 * or there is no {@code ProductModel} for it, the standard method in the super implementations is called
	 */
	@Override
	@Nonnull
	public PriceValue findBasePrice(@Nonnull final AbstractOrderEntryModel entry) throws CalculationException
	{
		validateParameterNotNullStandardMessage("entry", entry);

		final ProductModel product = entry.getProduct();
		final AbstractOrderModel order = entry.getOrder();
		if (!getSubscriptionProductService().isSubscription(product))
		{
			return super.findBasePrice(entry);
		}

		final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);
		if (pricePlan == null && order.getBillingTime() != null
				&& !order.getBillingTime().equals(product.getSubscriptionTerm().getBillingPlan().getBillingFrequency()))
		{
			return new PriceValue(order.getCurrency().getIsocode(), 0.0D, order.getNet());
		}

		if (pricePlan == null)
		{
			return super.findBasePrice(entry);
		}

		if (order.getBillingTime() instanceof BillingFrequencyModel)
		{
			return createPriceValueForLastRecurringPrice(order, pricePlan);
		}

		for (final OneTimeChargeEntryModel chargeEntry : pricePlan.getOneTimeChargeEntries())
		{
			if (order.getBillingTime().equals(chargeEntry.getBillingEvent()))
			{
				LOG.debug("Using onetime price " + order.getBillingTime().getCode() + ": " + chargeEntry.getPrice());

				return new PriceValue(order.getCurrency().getIsocode(), chargeEntry.getPrice(), order.getNet());
			}
		}

		return super.findBasePrice(entry);
	}

	protected PriceValue createPriceValueForLastRecurringPrice(final AbstractOrderModel order,
			final SubscriptionPricePlanModel pricePlan)
	{
		final RecurringChargeEntryModel lastRecurringPrice = getCommercePriceService().getLastRecurringPriceFromPlan(pricePlan);
		LOG.debug("Using recurring " + order.getBillingTime().getCode() + " price: "
				+ (lastRecurringPrice != null ? lastRecurringPrice.getPrice() : null));
		return new PriceValue(order.getCurrency().getIsocode(), lastRecurringPrice != null ? lastRecurringPrice.getPrice() : 0,
				order.getNet());
	}

	/**
	 * Find applicable DiscountValues for the target order entry.
	 */
	@Override
	@Nonnull
	public List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		validateParameterNotNullStandardMessage("entry", entry);

		final ProductModel product = entry.getProduct();
		final AbstractOrderModel order = entry.getOrder();
		if (!getSubscriptionProductService().isSubscription(product))
		{
			return super.findDiscountValues(entry);
		}

		final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);
		if (pricePlan == null)
		{
			if (order.getBillingTime() != null
					&& !order.getBillingTime().equals(product.getSubscriptionTerm().getBillingPlan().getBillingFrequency()))
			{
				return Collections.emptyList();
			}
		}
		else
		{
			final BillingTimeModel billingTime = order.getBillingTime();
			if (billingTime instanceof BillingFrequencyModel)
			{
				return getDiscountValuesWhenBillingFrequency(order, pricePlan);
			}
		}

		return super.findDiscountValues(entry);
	}

	protected List<DiscountValue> getDiscountValuesWhenBillingFrequency(final AbstractOrderModel order,
			final SubscriptionPricePlanModel pricePlan)
	{
		final RecurringChargeEntryModel firstRecurringPrice = getCommercePriceService().getFirstRecurringPriceFromPlan(pricePlan);
		final RecurringChargeEntryModel lastRecurringPrice = getCommercePriceService().getLastRecurringPriceFromPlan(pricePlan);

		final List<DiscountValue> discountValues = new ArrayList<>();
		LOG.debug("Discounting recurring price: first cycle: "
				+ (firstRecurringPrice != null ? firstRecurringPrice.getPrice() : null) + " last cycle:"
				+ (lastRecurringPrice != null ? lastRecurringPrice.getPrice() : null));
		if (firstRecurringPrice != null && lastRecurringPrice != null)
		{
			discountValues.add(new DiscountValue(firstRecurringPrice.getId(), lastRecurringPrice.getPrice()
					- firstRecurringPrice.getPrice(), true, order.getCurrency().getIsocode()));
		}

		return discountValues;
	}

	protected SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Required
	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	/**
	 * @return subscription product service
	 */
	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}
}
