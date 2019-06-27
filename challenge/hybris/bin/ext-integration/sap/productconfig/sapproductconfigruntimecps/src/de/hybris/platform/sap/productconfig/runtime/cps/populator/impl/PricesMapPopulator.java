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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;

import java.math.BigDecimal;
import java.util.Map;


/**
 * Responsible populating map of prices
 */
public class PricesMapPopulator implements Populator<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>>
{

	@Override
	public void populate(final PricingDocumentResult source, final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> target)
	{
		if (source.getItems() != null)
		{
			for (final PricingItemResult item : source.getItems())
			{
				processConditionResult(target, item, source.getDocumentCurrencyUnit());
			}
		}
	}

	protected void processConditionResult(final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> target,
			final PricingItemResult item, final String docCurrency)
	{
		final String itemId = item.getItemId();
		if (item.getConditions() == null)
		{
			return;
		}
		for (final ConditionResult condition : item.getConditions())
		{
			if (condition.getVarcondKey() == null)
			{
				continue;
			}

			final CPSMasterDataVariantPriceKey priceKey = new CPSMasterDataVariantPriceKey();
			priceKey.setProductId(itemId);
			priceKey.setVariantConditionKey(condition.getVarcondKey());

			final BigDecimal conditionValue = BigDecimal.valueOf(condition.getConditionValue().doubleValue());

			CPSValuePrice valuePrice = target.get(priceKey);
			if (valuePrice == null)
			{
				valuePrice = new CPSValuePrice();
				valuePrice.setValuePrice(conditionValue);
				// here the condition value uses document currency;in contrast the currency on the level of condition refers to the condition rate
				valuePrice.setCurrency(docCurrency);
				target.put(priceKey, valuePrice);
			}
			else
			{
				// if a variant condition is assigned to more than one condition types
				final BigDecimal cummulatedPrice = valuePrice.getValuePrice().add(conditionValue);
				valuePrice.setValuePrice(cummulatedPrice);
			}
		}
	}
}
