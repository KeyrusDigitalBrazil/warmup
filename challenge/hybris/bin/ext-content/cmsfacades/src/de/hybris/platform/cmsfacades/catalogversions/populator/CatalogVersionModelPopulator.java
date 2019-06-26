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
package de.hybris.platform.cmsfacades.catalogversions.populator;


import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Populates a {@Link CatalogVersionData} dto from a {@Link CatalogVersionModel} item
 */
public class CatalogVersionModelPopulator implements Populator<CatalogVersionModel, CatalogVersionData>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final CatalogVersionModel source, final CatalogVersionData target) throws ConversionException
	{
		target.setVersion(source.getVersion());
		target.setActive(source.getActive());

		target.setUuid(getUniqueItemIdentifierService().getItemData(source).orElseThrow(
				() -> new UnknownIdentifierException("Cannot generate uuid for component in CatalogVersionModelPopulator")).getItemId());

		final Map<String, String> nameMap = Optional.ofNullable(target.getName()).orElseGet(HashMap::new);
		getLocalizedPopulator().populate(
				((locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value)),
				(locale) -> source.getCatalog().getName(locale) + " - " + source.getVersion()
		);

		target.setName(nameMap);
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(
			UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}
}
