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
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@code java.util.Map.Entry<CatalogModel, Set<CatalogVersionModel>} object to a {@link CatalogData} dto
 */
public class EntryToCatalogDataPopulator implements Populator<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData>
{
	private Populator<CatalogModel, CatalogData> catalogModelToDataPopulator;
	private Converter<CatalogVersionModel, CatalogVersionData> catalogVersionDataConverter;
	private Comparator<CatalogVersionData> catalogVersionDataComparator; 
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public void populate(final Entry<CatalogModel, Set<CatalogVersionModel>> source, final CatalogData target)
			throws ConversionException
	{
		// populate the basic fields (id, name)
		getCatalogModelToDataPopulator().populate(source.getKey(), target);

		// populate the catalog versions
		if (!source.getValue().isEmpty())
		{
			final List<CatalogVersionData> versions = source.getValue().stream() //
					.map(this::convertCatalogVersionModelToData) //
					.sorted(getCatalogVersionDataComparator()) //
					.collect(Collectors.toList());
			target.setVersions(versions);
							}
							}

	/**
	 * Converts {@code CatalogVersionModel} into {@code CatalogVersionData} dto.
	 *
	 * @param catalogVersionModel
	 *           the catalog version to be converted
	 * @return a catalog version dto
	 */
	protected CatalogVersionData convertCatalogVersionModelToData(final CatalogVersionModel catalogVersionModel)
	{
		final CatalogVersionData catalogVersionData = getCatalogVersionDataConverter().convert(catalogVersionModel);
		catalogVersionData.setUuid(getUniqueItemIdentifierService().getItemData(catalogVersionModel)
				.orElseThrow(
						() -> new UnknownIdentifierException("Cannot generate uuid for component in EntryToCatalogDataPopulator"))
				.getItemId());
						return catalogVersionData;
	}

	protected Populator<CatalogModel, CatalogData> getCatalogModelToDataPopulator()
	{
		return catalogModelToDataPopulator;
	}

	@Required
	public void setCatalogModelToDataPopulator(final Populator<CatalogModel, CatalogData> catalogModelToDataPopulator)
	{
		this.catalogModelToDataPopulator = catalogModelToDataPopulator;
	}

	protected Converter<CatalogVersionModel, CatalogVersionData> getCatalogVersionDataConverter()
	{
		return catalogVersionDataConverter;
	}

	@Required
	public void setCatalogVersionDataConverter(
			final Converter<CatalogVersionModel, CatalogVersionData> catalogVersionDataConverter)
	{
		this.catalogVersionDataConverter = catalogVersionDataConverter;
	}

	protected Comparator<CatalogVersionData> getCatalogVersionDataComparator()
	{
		return catalogVersionDataComparator;
	}

	@Required
	public void setCatalogVersionDataComparator(final Comparator<CatalogVersionData> catalogVersionDataComparator)
	{
		this.catalogVersionDataComparator = catalogVersionDataComparator;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
}
