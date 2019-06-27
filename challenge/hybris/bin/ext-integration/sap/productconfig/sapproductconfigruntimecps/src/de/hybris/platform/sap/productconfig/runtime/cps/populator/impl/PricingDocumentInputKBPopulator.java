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

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;


/**
 * Popuplates the pricing document input data for querrying static pricing information, such as value prices, based on
 * the configuration master data.
 */
public class PricingDocumentInputKBPopulator extends AbstractPricingDocumentInputPopulator
		implements ContextualPopulator<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput, MasterDataContext>
{

	private ContextualConverter<CPSMasterDataProductContainer, PricingItemInput, MasterDataContext> pricingItemInputKBProductConverter;

	@Override
	public void populate(final CPSMasterDataKnowledgeBaseContainer source, final PricingDocumentInput target,
			final MasterDataContext context)
	{
		fillCoreAttributes(target);
		fillPricingItemsInput(source, target, context);
	}

	protected void fillPricingItemsInput(final CPSMasterDataKnowledgeBaseContainer source, final PricingDocumentInput target,
			final MasterDataContext context)
	{
		target.setItems(new ArrayList<PricingItemInput>());
		fillPricingItemsInputFromProducts(source, target, context);
	}

	protected void fillPricingItemsInputFromProducts(final CPSMasterDataKnowledgeBaseContainer source,
			final PricingDocumentInput target, final MasterDataContext context)
	{
		final Map<String, CPSMasterDataProductContainer> products = source.getProducts();
		final Iterator<Entry<String, CPSMasterDataProductContainer>> productIterator = products.entrySet().iterator();
		while (productIterator.hasNext())
		{
			final Entry<String, CPSMasterDataProductContainer> productContainer = productIterator.next();
			final CPSMasterDataProductContainer product = productContainer.getValue();
			enrichProductWithUnit(product, source);
			target.getItems().add(getPricingItemInputKBProductConverter().convertWithContext(product, context));
		}
	}

	protected void enrichProductWithUnit(final CPSMasterDataProductContainer product,
			final CPSMasterDataKnowledgeBaseContainer source)
	{
		final String rootUnitOfMeasure = source.getRootUnitOfMeasure();
		Preconditions.checkNotNull(rootUnitOfMeasure, "No root unit of measure present");
		product.setUnitOfMeasure(rootUnitOfMeasure);
	}

	protected ContextualConverter<CPSMasterDataProductContainer, PricingItemInput, MasterDataContext> getPricingItemInputKBProductConverter()
	{
		return pricingItemInputKBProductConverter;
	}

	/**
	 * @param pricingItemInputKBProductConverter
	 *           converter create the pricing input data from the product master data
	 */
	public void setPricingItemInputKBProductConverter(
			final ContextualConverter<CPSMasterDataProductContainer, PricingItemInput, MasterDataContext> pricingItemInputKBProductConverter)
	{
		this.pricingItemInputKBProductConverter = pricingItemInputKBProductConverter;
	}

}
