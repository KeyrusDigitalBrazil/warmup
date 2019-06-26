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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.ConfigurationModificationHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPricingQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Popuplates the pricing item input data for querrying dynamic pricing information, such as total values, based on the
 * configuration runtime data.
 */
public class PricingItemInputPopulator extends AbstractPricingItemInputPopulator
		implements ContextualPopulator<CPSItem, PricingItemInput, ConfigurationRetrievalOptions>
{
	private ConfigurationModificationHandler configurationModificationHandler;

	@Override
	public void populate(final CPSItem source, final PricingItemInput target, final ConfigurationRetrievalOptions context)
	{
		fillCoreAttributes(source.getId(), calculateQuantity(source), target);
		fillPricingAttributes(retrievePricingProduct(source, context), target);
		fillAccessDates(target, context);
		fillVariantConditions(source, target, context);
	}

	protected String retrievePricingProduct(final CPSItem source, final ConfigurationRetrievalOptions context)
	{
		String pricingProduct = source.getKey();
		// specifying a different pricing product (e.g for changeable product variants) is only supported for singlelevel / root products
		// stetting this also for non-root procuts would break multilevel pricing!
		if (source.getParentItem() == null && context != null && StringUtils.isNotEmpty(context.getPricingProduct()))
		{
			pricingProduct = context.getPricingProduct();
		}
		return pricingProduct;
	}

	protected CPSPricingQuantity calculateQuantity(final CPSItem source)
	{
		final CPSPricingQuantity quantity = new CPSPricingQuantity();
		quantity.setUnit(source.getQuantity().getUnit());

		double quantityValue = source.getQuantity().getValue().doubleValue();
		CPSItem currentItem = source;
		while (currentItem.getParentItem() != null)
		{
			source.getParentItem();
			if (currentItem.isFixedQuantity())
			{
				break;
			}
			quantityValue = quantityValue * currentItem.getParentItem().getQuantity().getValue().doubleValue();
			currentItem = currentItem.getParentItem();
		}

		final BigDecimal quantityValueRounded = BigDecimal.valueOf(quantityValue).setScale(3, RoundingMode.HALF_UP);

		quantity.setValue(quantityValueRounded);
		return quantity;
	}

	protected void fillVariantConditions(final CPSItem source, final PricingItemInput target,
			final ConfigurationRetrievalOptions context)
	{
		Map<String, BigDecimal> variantConditionDiscounts = null;
		if (context != null && context.getDiscountList() != null)
		{
			variantConditionDiscounts = getConfigurationModificationHandler()
					.retrieveVarCondDiscounts(source.getParentConfiguration().getKbId(), source.getKey(), context.getDiscountList());
		}

		target.setVariantConditions(new ArrayList<>());
		for (final CPSVariantCondition condition : source.getVariantConditions())
		{
			if (isNotZero(condition.getFactor()))
			{
				if (" ".equals(condition.getKey()))
				{
					throw new IllegalStateException("Variant condition does not carry a key");
				}

				final CPSVariantCondition targetVariantCondition = new CPSVariantCondition();
				targetVariantCondition.setFactor(condition.getFactor());
				targetVariantCondition.setKey(condition.getKey());
				if (variantConditionDiscounts != null)
				{
					getConfigurationModificationHandler().applyConditionDiscount(targetVariantCondition, variantConditionDiscounts);
				}
				target.getVariantConditions().add(targetVariantCondition);
			}
		}
	}

	protected boolean isNotZero(final String factor)
	{
		boolean result = false;

		if (factor != null)
		{
			try
			{
				final BigDecimal factorValue = new BigDecimal(factor);
				if (BigDecimal.ZERO.compareTo(factorValue) != 0)
				{
					result = true;
				}
			}
			catch (final NumberFormatException ex)
			{

				result = false;
			}
		}

		return result;
	}

	protected ConfigurationModificationHandler getConfigurationModificationHandler()
	{
		return configurationModificationHandler;
	}

	@Required
	public void setConfigurationModificationHandler(final ConfigurationModificationHandler configurationModificationHandler)
	{
		this.configurationModificationHandler = configurationModificationHandler;
	}


}
