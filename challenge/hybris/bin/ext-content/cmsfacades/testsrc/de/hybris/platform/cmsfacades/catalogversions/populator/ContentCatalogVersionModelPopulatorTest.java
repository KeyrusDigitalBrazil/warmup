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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.catalogversions.service.PageDisplayConditionService;
import de.hybris.platform.cmsfacades.data.CatalogVersionData;
import de.hybris.platform.cmsfacades.data.DisplayConditionData;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentCatalogVersionModelPopulatorTest
{
	private static final String THUMBNAIL_URL = "/thumbnail/code.png";

	@InjectMocks
	private ContentCatalogVersionModelPopulator populator;
	@Mock
	private SiteThumbnailResolver siteThumbnailResolver;
	@Mock
	private PageDisplayConditionService pageDisplayConditionService;

	@Mock
	private DisplayConditionData contentPageDisplayCondition;
	@Mock
	private DisplayConditionData productPageDisplayCondition;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogVersionData catalogVersionData;

	private List<DisplayConditionData> pageDisplayConditions;

	@Before
	public void setUp()
	{
		pageDisplayConditions = Arrays.asList(contentPageDisplayCondition, productPageDisplayCondition);

		when(pageDisplayConditionService.getDisplayConditions(catalogVersionModel)).thenReturn(pageDisplayConditions);
	}

	@Test
	public void shouldPopulateAllFields()
	{
		when(siteThumbnailResolver.resolveHomepageThumbnailUrl(catalogVersionModel)).thenReturn(Optional.of(THUMBNAIL_URL));

		populator.populate(catalogVersionModel, catalogVersionData);

		verify(catalogVersionData).setThumbnailUrl(THUMBNAIL_URL);
		verify(catalogVersionData).setPageDisplayConditions(pageDisplayConditions);
	}

	@Test
	public void shouldNotPopulateThumbnail()
	{
		when(siteThumbnailResolver.resolveHomepageThumbnailUrl(catalogVersionModel)).thenReturn(Optional.empty());

		populator.populate(catalogVersionModel, catalogVersionData);

		verify(catalogVersionData, times(0)).setThumbnailUrl(THUMBNAIL_URL);
		verify(catalogVersionData).setPageDisplayConditions(pageDisplayConditions);
	}
}
