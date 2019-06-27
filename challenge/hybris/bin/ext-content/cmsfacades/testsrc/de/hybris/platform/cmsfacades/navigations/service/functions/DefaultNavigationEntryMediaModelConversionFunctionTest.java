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
package de.hybris.platform.cmsfacades.navigations.service.functions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryMediaModelConversionFunctionTest
{

	@Mock
	private MediaService mediaService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private NavigationEntryData navigationEntry;
	@Mock
	private MediaModel media;

	@InjectMocks
	private DefaultNavigationEntryMediaModelConversionFunction conversionFunction;

	private String itemId = "itemId";

	@Before
	public void setup()
	{
		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersion);
		when(navigationEntry.getItemId()).thenReturn(itemId);

	}

	@Test
	public void testApplyNavigationEntryConversion()
	{
		when(mediaService.getMedia(catalogVersion, itemId)).thenReturn(media);

		assertThat(conversionFunction.apply(navigationEntry), is(media));
	}

	@Test(expected = ConversionException.class)
	public void testApplyNavigationEntryConversionWithInvalidMedia()
	{
		when(mediaService.getMedia(catalogVersion, itemId)).thenThrow(new UnknownIdentifierException(""));

		conversionFunction.apply(navigationEntry);
	}
}
