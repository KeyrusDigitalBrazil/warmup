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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import de.hybris.platform.product.UnitService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link CommerceExternalConfigurationStrategy}
 */
public class CommerceExternalConfigurationStrategyImpl implements CommerceExternalConfigurationStrategy
{

	private UnitService unitService;

	/**
	 * @return the unitService
	 */
	protected UnitService getUnitService()
	{
		return unitService;
	}

	@Override
	public CPSExternalConfiguration extractCPSFormatFromCommerceRepresentation(
			final CPSCommerceExternalConfiguration commerceExternalConfiguration)
	{
		if (commerceExternalConfiguration == null)
		{
			throw new IllegalArgumentException("CommerceExternalConfiguration must be present");
		}
		return commerceExternalConfiguration.getExternalConfiguration();
	}

	@Override
	public CPSCommerceExternalConfiguration createCommerceFormatFromCPSRepresentation(
			final CPSExternalConfiguration externalConfiguration)
	{
		final CPSCommerceExternalConfiguration result = new CPSCommerceExternalConfiguration();
		result.setExternalConfiguration(externalConfiguration);
		result.setUnitCodes(compileUnitCodes(externalConfiguration.getRootItem()));
		return result;
	}

	protected Map<String, String> compileUnitCodes(final CPSExternalItem cpsExternalItem)
	{
		if (cpsExternalItem == null)
		{
			throw new IllegalArgumentException("Root item must be present");
		}
		final Map<String, String> unitCodeMap = new HashMap<>();
		collectUnitCodes(cpsExternalItem, unitCodeMap);

		return unitCodeMap;
	}

	protected void collectUnitCodes(final CPSExternalItem cpsExternalItem, final Map<String, String> unitCodeMap)
	{
		collectOnItemLevel(cpsExternalItem, unitCodeMap);
		collectOnSubItemLevel(cpsExternalItem, unitCodeMap);
	}

	protected void collectOnSubItemLevel(final CPSExternalItem cpsExternalItem, final Map<String, String> unitCodeMap)
	{
		final List<CPSExternalItem> subItems = cpsExternalItem.getSubItems();
		if (subItems != null)
		{
			subItems.stream().forEach(subitem -> collectUnitCodes(subitem, unitCodeMap));
		}
	}

	protected void collectOnItemLevel(final CPSExternalItem cpsExternalItem, final Map<String, String> unitCodeMap)
	{
		final CPSQuantity quantity = cpsExternalItem.getQuantity();
		if (quantity != null)
		{
			final String isoCode = quantity.getUnit();
			if (isoCode != null)
			{
				unitCodeMap.put(isoCode, getUnitService().getUnitForCode(isoCode).getSapCode());
			}
		}
	}

	/**
	 * @param unitService
	 */
	public void setUnitService(final UnitService unitService)
	{
		this.unitService = unitService;

	}

}
