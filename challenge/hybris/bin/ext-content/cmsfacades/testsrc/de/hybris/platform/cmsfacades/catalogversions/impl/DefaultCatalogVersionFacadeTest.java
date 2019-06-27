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
package de.hybris.platform.cmsfacades.catalogversions.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.catalogversions.service.PageDisplayConditionService;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.data.DisplayConditionData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCatalogVersionFacadeTest
{
	private static final String CATALOG_ID = "test-catalog-id";
	private static final String VERSION_ID = "test-version-id";

	@InjectMocks
	private final DefaultCatalogVersionFacade catalogVersionFacade = new DefaultCatalogVersionFacade();

	@Mock
	private Converter<CatalogVersionModel, CatalogVersionData> catalogVersionConverter;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private PageDisplayConditionService displayConditionService;
	@Mock
	private DisplayConditionData displayConditionData;
	@Mock
	private CatalogVersionModel catalogVersionModel;

	private CatalogVersionData catalogVersionData;

	@Before
	public void setUp()
	{
		catalogVersionData = new CatalogVersionData();
		when(catalogVersionConverter.convert(catalogVersionModel)).thenReturn(catalogVersionData);
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ID)).thenReturn(catalogVersionModel);
	}

	@Test
	public void shouldGetCatalogVersion() throws CMSItemNotFoundException
	{
		when(displayConditionService.getDisplayConditions()).thenReturn(Arrays.asList(displayConditionData));

		catalogVersionData = catalogVersionFacade.getCatalogVersion(CATALOG_ID, VERSION_ID);

		verify(catalogVersionService).getCatalogVersion(CATALOG_ID, VERSION_ID);
		verify(displayConditionService).getDisplayConditions();
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailGetCatalogVersion_CatalogVersionNotFound() throws CMSItemNotFoundException
	{
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ID)).thenReturn(null);
		catalogVersionFacade.getCatalogVersion(CATALOG_ID, VERSION_ID);
	}

}
