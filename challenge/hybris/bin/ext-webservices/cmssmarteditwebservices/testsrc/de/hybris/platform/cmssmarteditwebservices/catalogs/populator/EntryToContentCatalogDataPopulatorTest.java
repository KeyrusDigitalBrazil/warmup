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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.data.HomePageData;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EntryToContentCatalogDataPopulatorTest
{
	private static final String UUID = "uuid";

	@InjectMocks
	private EntryToContentCatalogDataPopulator populator;
	@Mock
	private Populator<CatalogModel, CatalogData> catalogModelToDataPopulator;
	@Mock
	private Converter<CatalogVersionModel, CatalogVersionData> catalogVersionDataConverter;
	@Mock
	private Comparator<CatalogVersionData> catalogVersionDataComparator;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private Converter<CatalogVersionModel, HomePageData> homePageDataConverter;

	@Mock
	private CatalogModel catalogModel;
	@Mock
	private CatalogVersionModel catalogVersionStaged;
	@Mock
	private CatalogVersionModel catalogVersionOnline;

	private CatalogData catalogData;
	private Map.Entry<CatalogModel, Set<CatalogVersionModel>> entry;

	private ItemData itemData;
	private CatalogVersionData catalogVersionData;

	protected void setUpWithCatalogVersions(final CatalogVersionModel... catalogVersionModels)
	{
		final Map<CatalogModel, Set<CatalogVersionModel>> catalogsAndVersions = new HashMap<>();
		catalogsAndVersions.put(catalogModel, Sets.newHashSet(catalogVersionModels));

		entry = catalogsAndVersions.entrySet().iterator().next();
		catalogData = new CatalogData();
		catalogVersionData = new CatalogVersionData();

		when(catalogVersionDataConverter.convert(catalogVersionStaged)).thenReturn(catalogVersionData);
		when(catalogVersionDataConverter.convert(catalogVersionOnline)).thenReturn(catalogVersionData);

		itemData = new ItemData();
		itemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(catalogVersionStaged)).thenReturn(Optional.of(itemData));
		when(uniqueItemIdentifierService.getItemData(catalogVersionOnline)).thenReturn(Optional.of(itemData));
	}

	@Test
	public void shouldPopulateContentCatalogDataWithCatalogVersionsAndHomePageData()
	{
		setUpWithCatalogVersions(catalogVersionStaged);

		populator.populate(entry, catalogData);

		verify(catalogModelToDataPopulator).populate(catalogModel, catalogData);
		verify(catalogVersionDataConverter).convert(catalogVersionStaged);
		verify(homePageDataConverter).convert(catalogVersionStaged);
	}

	@Test(expected = ConversionException.class)
	public void shouldFailWithConversionException()
	{
		setUpWithCatalogVersions(catalogVersionStaged);
		when(catalogVersionDataConverter.convert(catalogVersionStaged))
				.thenThrow(new ConversionException("Error occured during conversion"));

		populator.populate(entry, catalogData);
	}

}