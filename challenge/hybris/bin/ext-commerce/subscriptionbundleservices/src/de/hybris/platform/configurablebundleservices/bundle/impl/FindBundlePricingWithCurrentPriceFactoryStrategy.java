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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.impl.FindSubscriptionPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;


/**
 * This strategy should replace the FindPricingWithCurrentPriceFactoryStrategy and will change the discount calculation
 * for bundle entries based on the bundle rules. So based on the products that exist in the cart in the same bundle, the
 * price rules are evaluated and the a matching reduced price is found. Once the final price has been identified, the
 * same logic applies as for DiscountValues and they are persisted in exactly the same way in the database as well as
 * the calculation is don in exactly the same way.
 * 
 */
public class FindBundlePricingWithCurrentPriceFactoryStrategy extends FindSubscriptionPricingWithCurrentPriceFactoryStrategy
{

	private BundleRuleService bundleRuleService;

	private SubscriptionCommercePriceService subscriptionCommercePriceService;

	/**
	 * For non Bundle products (no bundleNo set or bundleNo=0) it should work as is, so the DiscountValues should result
	 * from the DiscountRows defined at the product.
	 * 
	 * For Bundle products (bundleNo>0) it should work in a different way: All other products where the
	 * AbstractOrderEntries have the same bundleNo should be added to a list for comparison. The Product of the current
	 * AbstractOrderEntry will be used to identify PriceRules from the current BundleTemplate where that product is set
	 * as a target. All conditional products of the rules have to be in the comparison list and that is how we find the
	 * matching rules. If there are still multiple matching rules, because, then the one with the lowest price in the
	 * current currency will be used as discount. That absolute discount will be converted into a relative DiscountValue
	 * for the AbstractOrderEntry only. All calculations of totals later will work as is. For subscription products the
	 * discount is applied to the 1st tier of recurring charge entries in the price plan. In case there is a price rule
	 * that can be applied to the subscription product and there is also a discount that comes from the tiered recurring
	 * charge entries, the better (= cheaper) price is used as discount.
	 * 
	 * For subscription product, if the there is price rule with the billing event, the specific one time charge price
	 * reduce logic will be applied.
	 */
	@Override
	@Nonnull
	// NO SONAR
	public List<DiscountValue> findDiscountValues(@Nonnull final AbstractOrderEntryModel entry) throws CalculationException
	{
		validateParameterNotNullStandardMessage("entry", entry);

		final AbstractOrderEntryModel masterEntry = entry.getMasterEntry() == null ? entry : entry.getMasterEntry();
		final Integer bundleNo = masterEntry.getBundleNo();

		if (bundleNo == null || bundleNo.intValue() < 1)
		{
			// if it is not a bundle entry, just use the existing DiscountRows for calculating the DiscountValues
			return super.findDiscountValues(entry);
		}

		if (entry.getBasePrice() == null || entry.getBasePrice().doubleValue() <= 0.0D)
		{
			return Collections.emptyList();
		}

		return getDiscountValues(entry, masterEntry);
	}

	@Nonnull
	protected List<DiscountValue> getDiscountValues(final @Nonnull AbstractOrderEntryModel entry,
			@Nonnull final AbstractOrderEntryModel masterEntry)
	{
		final List<DiscountValue> discountValues = new ArrayList<>();
		final ProductModel product = entry.getProduct();
		final AbstractOrderModel order = entry.getOrder();
		final CurrencyModel currency = order.getCurrency();

		final ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(
				masterEntry);

		if (getSubscriptionProductService().isSubscription(product))
		{
			final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);

			if (pricePlan == null)
			{
				return Collections.emptyList();
			}

			if (priceRule == null || priceRule.getBillingEvent() == null)
			{
				reduceRecurringPrice(product, priceRule, discountValues, entry, pricePlan);
			}
			else
			{
				reduceOneTimePrice(pricePlan, priceRule, discountValues, currency, entry);
			}
		}
		else
		{
			// standard products: As standard products are exclusively in the master cart (billing frequency = pay now),
			// the price rule is applied to the pay now price
			if (priceRule != null)
			{
				discountValues.add(createDiscountValue(priceRule.getPrice().doubleValue(), entry.getBasePrice().doubleValue(),
						priceRule.getId(), currency));
			}
		}

		return discountValues;
	}

	/**
	 * The price logic for subscription product: discount are applied to one time charge.
	 * 
	 * @param pricePlan
	 *           the subscription price plan
	 * @param priceRule
	 *           the change product price bundle rule
	 * @param discountValues
	 *           the discount values list
	 * @param currency
	 *           the currency
	 * @param entry
	 *           the order entry
	 */
	protected void reduceOneTimePrice(@Nonnull final SubscriptionPricePlanModel pricePlan,
									  @Nonnull final ChangeProductPriceBundleRuleModel priceRule,
									  @Nonnull final List<DiscountValue> discountValues,
									  @Nonnull final CurrencyModel currency,
									  @Nonnull final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("pricePlan", pricePlan);
		validateParameterNotNullStandardMessage("priceRule", priceRule);
		validateParameterNotNullStandardMessage("entry", entry);
		validateParameterNotNullStandardMessage("currency", currency);
		validateParameterNotNullStandardMessage("discountValues", discountValues);

		final BillingTimeModel billingTimeOrder = entry.getOrder().getBillingTime();
		if (billingTimeOrder instanceof BillingEventModel
				&& priceRule.getBillingEvent().equals(entry.getOrder().getBillingTime()))
		{
			final OneTimeChargeEntryModel chargeEntry = getSubscriptionCommercePriceService().getOneTimeChargeEntryPlan(
					pricePlan, (BillingEventModel) billingTimeOrder);

			if (chargeEntry == null)
			{
				return;
			}

			final double pricePlanPrice = chargeEntry.getPrice().doubleValue();
			final double priceRulePrice = priceRule.getPrice().doubleValue();
			double discountPrice = pricePlanPrice;
			String id = chargeEntry.getId();

			if (pricePlanPrice >= priceRulePrice)
			{
				id = priceRule.getId();
				discountPrice = priceRulePrice;
			}

			discountValues.add(createDiscountValue(discountPrice, entry.getBasePrice().doubleValue(), id, currency));
		}
	}

	/**
	 * hard coded price logic for subscription products: discounts are only applied to recurring prices
	 * 
	 * @param subscriptionProduct
	 *           the subscription product
	 * @param priceRule
	 *           the change product price bundle rule
	 * @param discountValues
	 *           the discount values list
	 * @param entry
	 *           the order entry
	 * @param pricePlan
	 *           the subscription price plan
	 */
	protected void reduceRecurringPrice(final ProductModel subscriptionProduct,
			final ChangeProductPriceBundleRuleModel priceRule, final List<DiscountValue> discountValues,
			final AbstractOrderEntryModel entry, final SubscriptionPricePlanModel pricePlan)
	{
		validateParameterNotNullStandardMessage("pricePlan", pricePlan);
		validateParameterNotNullStandardMessage("subscriptionProduct", subscriptionProduct);
		validateParameterNotNullStandardMessage("discountValues", discountValues);
		validateParameterNotNullStandardMessage("entry", entry);

		final BillingTimeModel billingTimeProduct = subscriptionProduct.getSubscriptionTerm().getBillingPlan()
				.getBillingFrequency();
		final BillingTimeModel billingTimeOrder = entry.getOrder().getBillingTime();
		// hard coded price logic for subscription products: discounts are only applied to recurring prices
		if (billingTimeProduct.equals(billingTimeOrder) && (billingTimeOrder instanceof BillingFrequencyModel)
				&& CollectionUtils.isNotEmpty(pricePlan.getRecurringChargeEntries()))
		{
			final double priceRulePrice = priceRule == null ? 0.0D : priceRule.getPrice().doubleValue();
			final RecurringChargeEntryModel chargeEntry = getCommercePriceService().getFirstRecurringPriceFromPlan(pricePlan);
			final double pricePlanPrice = chargeEntry.getPrice();

			// use best price as discount
			double discountPrice = pricePlanPrice;
			String id = chargeEntry.getId();
			if (priceRule != null && pricePlanPrice >= priceRulePrice)
			{
				id = priceRule.getId();
				discountPrice = priceRulePrice;
			}

			discountValues.add(createDiscountValue(discountPrice, entry.getBasePrice().doubleValue(), id, entry.getOrder()
					.getCurrency()));
		}
	}

	/**
	 * Creates a DiscountValue with an absolute reduction based on the given <code>basePrice</code> and the given
	 * <code>discountPrice</code>
	 * 
	 * @return {@link DiscountValue}
	 */
	protected DiscountValue createDiscountValue(final double discountPrice, final double basePrice, final String id,
			final CurrencyModel currency)
	{
		return new DiscountValue(id, basePrice - discountPrice, true, currency.getIsocode());
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	@Required
	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	protected SubscriptionCommercePriceService getSubscriptionCommercePriceService()
	{
		return subscriptionCommercePriceService;
	}

	@Required
	public void setSubscriptionCommercePriceService(final SubscriptionCommercePriceService subscriptionCommercePriceService)
	{
		this.subscriptionCommercePriceService = subscriptionCommercePriceService;
	}

}
