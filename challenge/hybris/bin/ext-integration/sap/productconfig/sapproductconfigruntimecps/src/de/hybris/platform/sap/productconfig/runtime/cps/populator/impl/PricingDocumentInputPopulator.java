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

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * Populates the pricing document input data for querying dynamic pricing information, such as total values, based on
 * the configuration runtime data.
 */
public class PricingDocumentInputPopulator extends AbstractPricingDocumentInputPopulator
		implements ContextualPopulator<CPSConfiguration, PricingDocumentInput, ConfigurationRetrievalOptions>
{


	private ContextualConverter<CPSItem, PricingItemInput, ConfigurationRetrievalOptions> pricingItemInputConverter;

	@Override
	public void populate(final CPSConfiguration source, final PricingDocumentInput target,
			final ConfigurationRetrievalOptions context)
	{
		fillCoreAttributes(target);
		fillPricingItemsInput(source, target, context);
	}

	protected void fillPricingItemsInput(final CPSConfiguration source, final PricingDocumentInput target,
			final ConfigurationRetrievalOptions context)
	{
		target.setItems(new ArrayList<PricingItemInput>());
		fillPricingItemInput(source.getRootItem(), target.getItems(), context);

	}

	protected void fillPricingItemInput(final CPSItem item, final List<PricingItemInput> target,
			final ConfigurationRetrievalOptions context)
	{
		PricingItemInput pricingItemInput = null;
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(item.getType()))
		{
			pricingItemInput = getPricingItemInputConverter().convertWithContext(item, context);
			target.add(pricingItemInput);
		}
		fillPricingSubItemInput(item, pricingItemInput, context);
	}

	protected void fillPricingSubItemInput(final CPSItem item, final PricingItemInput pricingItemInput,
			final ConfigurationRetrievalOptions context)
	{
		final List<CPSItem> subItems = item.getSubItems();
		if (subItems != null && pricingItemInput != null)
		{
			pricingItemInput.setSubItems(new ArrayList<>());
			for (final CPSItem subItem : subItems)
			{
				fillPricingItemInput(subItem, pricingItemInput.getSubItems(), context);
			}
		}
	}

	protected ContextualConverter<CPSItem, PricingItemInput, ConfigurationRetrievalOptions> getPricingItemInputConverter()
	{
		return pricingItemInputConverter;
	}

	/**
	 * @param pricingItemInputConverter
	 *           converter to create pricing item input data from CPS item data
	 */
	public void setPricingItemInputConverter(
			final ContextualConverter<CPSItem, PricingItemInput, ConfigurationRetrievalOptions> pricingItemInputConverter)
	{
		this.pricingItemInputConverter = pricingItemInputConverter;
	}


}
