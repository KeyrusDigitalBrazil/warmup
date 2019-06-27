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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Populates the pricing item input data for querying static pricing information, such as value prices, based on the
 * configuration master data.<br>
 * <br>
 * We are caching the value prices per product. This of course assumes that a product is part of a KB only in
 * <b> one </b> unit, the base unit. This assumption is correct for IPC/SSC in general, even if it is possible to create
 * BOM's with different UOMs for the same product: You can achieve this by assigning different unit of issues on plant
 * level. Such situations however lead to errors during KB generation. *
 */
public class PricingItemInputKBProductPopulator extends AbstractPricingItemInputPopulator
		implements ContextualPopulator<CPSMasterDataProductContainer, PricingItemInput, MasterDataContext>
{
	private static final Logger LOG = Logger.getLogger(PricingItemInputKBProductPopulator.class);

	@Override
	public void populate(final CPSMasterDataProductContainer source, final PricingItemInput target,
			final MasterDataContext context)
	{
		fillCoreAttributes(source.getId(), createQty(BigDecimal.ONE, getIsoUOM(source)), target);
		fillPricingAttributes(retrievePricingProduct(source, context), target);
		fillAccessDates(target, null);
		fillVariantConditions(source, target);
	}

	protected String retrievePricingProduct(final CPSMasterDataProductContainer source, final MasterDataContext context)
	{
		// specifying a different pricing product (e.g for changeable product variants) is only supported for singlelevel / root products
		// stetting this also for non-root procuts would break multilevel pricing!
		// we rely on that the pricing product will  only be specified for singlelebel, as we can not check this constraint easily here
		String pricingProduct;
		if (context != null && StringUtils.isNotEmpty(context.getPricingProduct()))
		{
			pricingProduct = context.getPricingProduct();
		}
		else
		{
			pricingProduct = source.getId();
		}
		return pricingProduct;

	}

	protected void fillVariantConditions(final CPSMasterDataProductContainer source, final PricingItemInput target)
	{
		target.setVariantConditions(new ArrayList<>());
		handleCstics(target, source.getCstics());
	}

	protected void handleCstics(final PricingItemInput target,
			final Map<String, CPSMasterDataCharacteristicSpecificContainer> cstics)
	{
		final Iterator<Entry<String, CPSMasterDataCharacteristicSpecificContainer>> csticsIterator = cstics.entrySet().iterator();
		while (csticsIterator.hasNext())
		{
			final Entry<String, CPSMasterDataCharacteristicSpecificContainer> csticSpecificContainerEntry = csticsIterator.next();
			final CPSMasterDataCharacteristicSpecificContainer csticSpecificContainer = csticSpecificContainerEntry.getValue();
			addVariantConditionsForPossibleValueSpecifics(target, csticSpecificContainer);
		}
	}

	protected void addVariantConditionsForPossibleValueSpecifics(final PricingItemInput target,
			final CPSMasterDataCharacteristicSpecificContainer csticSpecificContainer)
	{
		final Iterator<Entry<String, CPSMasterDataPossibleValueSpecific>> valuesIterator = csticSpecificContainer
				.getPossibleValueSpecifics().entrySet().iterator();
		while (valuesIterator.hasNext())
		{
			final Entry<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecificEntry = valuesIterator.next();
			final CPSMasterDataPossibleValueSpecific possibleValueSpecific = possibleValueSpecificEntry.getValue();
			final String variantConditionKey = possibleValueSpecific.getVariantConditionKey();
			if (StringUtils.isNotEmpty(variantConditionKey))
			{
				target.getVariantConditions().add(createVariantCondition(variantConditionKey));
			}
		}
	}

	protected CPSVariantCondition createVariantCondition(final String key)
	{
		final CPSVariantCondition variantCondition = new CPSVariantCondition();
		variantCondition.setFactor(String.valueOf(1));
		variantCondition.setKey(key);
		return variantCondition;
	}

	protected String getIsoUOM(final CPSMasterDataProductContainer product)
	{
		final String productId = product.getId();
		try
		{
			final ProductModel productModel = getProductService().getProductForCode(productId);
			final UnitModel unitModel = productModel.getUnit();
			return getPricingConfigurationParameter().retrieveUnitIsoCode(unitModel);
		}
		catch (final UnknownIdentifierException ex)
		{
			//In this case we fall back to the unit of measure of the root item
			final String unitOfMeasure = product.getUnitOfMeasure();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Use root unit of measure " + unitOfMeasure + " for product " + productId);
			}
			return unitOfMeasure;
		}
	}
}
