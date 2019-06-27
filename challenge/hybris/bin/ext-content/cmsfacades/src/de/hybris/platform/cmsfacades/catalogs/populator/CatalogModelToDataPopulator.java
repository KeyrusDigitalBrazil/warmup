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
package de.hybris.platform.cmsfacades.catalogs.populator;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@Link CatalogData} DTO from a {@Link CatalogModel}.
 */
public class CatalogModelToDataPopulator implements Populator<CatalogModel, CatalogData>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final CatalogModel source, final CatalogData target) throws ConversionException
	{

		final Map<String, String> nameMap = Optional.ofNullable(target.getName()).orElseGet(() -> getNewNameMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getName(locale));

		target.setCatalogId(source.getId());
	}

	protected Map<String, String> getNewNameMap(final CatalogData target)
	{
		target.setName(new LinkedHashMap<>());
		return target.getName();
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}

}
