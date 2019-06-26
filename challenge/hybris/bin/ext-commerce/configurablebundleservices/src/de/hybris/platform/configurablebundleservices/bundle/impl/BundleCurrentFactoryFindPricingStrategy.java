package de.hybris.platform.configurablebundleservices.bundle.impl;


import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.impl.FindPricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.DiscountValue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Override of pricing strategies to involve bundle discounts.
 */
public class BundleCurrentFactoryFindPricingStrategy extends FindPricingWithCurrentPriceFactoryStrategy
{
	private BundleRuleService bundleRuleService;
	private BundleTemplateService bundleTemplateService;

	@Override
	@Nonnull
	public List<DiscountValue> findDiscountValues(@Nonnull final AbstractOrderEntryModel entry) throws CalculationException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("entry", entry);
		final EntryGroup component = getBundleTemplateService().getBundleEntryGroup(entry);
		if (component == null)
		{
			return super.findDiscountValues(entry);
		}
		final List<DiscountValue> discountValues = new ArrayList<>();

		final CurrencyModel currency = entry.getOrder().getCurrency();

		final ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(entry);
		if (priceRule != null)
		{
			discountValues.add(createDiscountValue(priceRule.getPrice().doubleValue(), entry.getBasePrice().doubleValue(),
					priceRule.getId(), currency));
		}
		return discountValues;
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

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
