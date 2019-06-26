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
package de.hybris.platform.cmsfacades.media.populator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreateMediaPopulatorTest
{
	private static final String CATALOG = "electronics";
	private static final String VERSION = "staged";
	private static final String MEDIA_CODE = "mouse123";
	private static final String MEDIA_DESC = "Wireless Mouse";
	private static final String MEDIA_ALT_TEXT = "Apple Wireless Mouse";
	private static final String INVALID = "invalid";

	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogVersionService catalogVersionService;
	@InjectMocks
	private CreateMediaPopulator populator;

	private MediaData source;
	private MediaModel target;

	@Before
	public void setUp()
	{
		target = new MediaModel();
		source = new MediaData();
		source.setAltText(MEDIA_ALT_TEXT);
		source.setCode(MEDIA_CODE);
		source.setDescription(MEDIA_DESC);
		source.setCatalogId(CATALOG);
		source.setCatalogVersion(VERSION);

		when(catalogVersionService.getCatalogVersion(CATALOG, VERSION)).thenReturn(catalogVersion);
		when(catalogVersionService.getCatalogVersion(INVALID, INVALID)).thenThrow(ConversionException.class);
	}

	@Test
	public void shouldPopulateAllFields()
	{
		populator.populate(source, target);

		assertEquals(source.getAltText(), target.getAltText());
		assertEquals(source.getCode(), target.getCode());
		assertEquals(source.getDescription(), target.getDescription());
		assertEquals(catalogVersion, target.getCatalogVersion());
	}

	@Test(expected = ConversionException.class)
	public void shouldNotPopulateCatalogVersion()
	{
		source.setCatalogId(INVALID);
		source.setCatalogVersion(INVALID);

		populator.populate(source, target);
	}

}
