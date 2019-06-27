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
package de.hybris.platform.cms2.multicountry.comparator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionLevelComparatorTest
{
	private static final int ROOT_LEVEL = 0;
	private static final int LEAF_LEVEL = 5;

	@InjectMocks
	private CatalogVersionLevelComparator comparator;
	@Mock
	private CatalogLevelService cmsCatalogLevelService;

	@Mock
	private CatalogVersionModel catalogVersion1;
	@Mock
	private CatalogVersionModel catalogVersion2;
	@Mock
	private ContentCatalogModel contentCatalog1;
	@Mock
	private ContentCatalogModel contentCatalog2;

	@Before
	public void setUp()
	{
		when(catalogVersion1.getCatalog()).thenReturn(contentCatalog1);
		when(catalogVersion2.getCatalog()).thenReturn(contentCatalog2);
	}

	@Test
	public void shouldBeGreater_catalogVersion1Null()
	{
		final int value = comparator.compare(null, catalogVersion2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeSmaller_catalogVersion2Null()
	{
		final int value = comparator.compare(catalogVersion1, null);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeSmaller_DifferentCatalogLevels()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(ROOT_LEVEL);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(LEAF_LEVEL);

		final int value = comparator.compare(catalogVersion1, catalogVersion2);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeGreater_DifferentCatalogLevels()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(LEAF_LEVEL);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(ROOT_LEVEL);

		final int value = comparator.compare(catalogVersion1, catalogVersion2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeGreater_SameCatalogLevels_catalogVersion1Active()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(ROOT_LEVEL);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(ROOT_LEVEL);
		when(catalogVersion1.getActive()).thenReturn(Boolean.TRUE);
		when(catalogVersion2.getActive()).thenReturn(Boolean.FALSE);

		final int value = comparator.compare(catalogVersion1, catalogVersion2);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeGreater_SameCatalogLevels_catalogVersion2Active()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(ROOT_LEVEL);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(ROOT_LEVEL);
		when(catalogVersion1.getActive()).thenReturn(Boolean.FALSE);
		when(catalogVersion2.getActive()).thenReturn(Boolean.TRUE);

		final int value = comparator.compare(catalogVersion1, catalogVersion2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeEqual()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(ROOT_LEVEL);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(ROOT_LEVEL);

		final int value = comparator.compare(catalogVersion1, catalogVersion2);

		assertThat(value, equalTo(0));
	}
}
