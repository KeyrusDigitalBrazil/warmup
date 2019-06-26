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
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemCatalogLevelComparatorTest
{
	public static final int ROOT_LEVEL = 0;
	public static final int LEAF_LEVEL = 5;

	@InjectMocks
	private CMSItemCatalogLevelComparator<AbstractCMSComponentModel> comparator;
	@Mock
	private CatalogLevelService cmsCatalogLevelService;

	@Mock
	private CatalogVersionModel catalogVersion1;
	@Mock
	private CatalogVersionModel catalogVersion2;
	@Mock
	private ContentCatalogModel contentCatalog;
	@Mock
	private AbstractCMSComponentModel component1;
	@Mock
	private AbstractCMSComponentModel component2;

	@Before
	public void setUp()
	{
		when(catalogVersion1.getCatalog()).thenReturn(contentCatalog);
		when(catalogVersion2.getCatalog()).thenReturn(contentCatalog);
		when(component1.getCatalogVersion()).thenReturn(catalogVersion1);
		when(component2.getCatalogVersion()).thenReturn(catalogVersion2);
	}

	@Test
	public void shouldBeGreater_component1Null()
	{
		component1 = null;

		final int value = comparator.compare(component1, component2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeSmaller_component2Null()
	{
		component2 = null;

		final int value = comparator.compare(component1, component2);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeSmaller_DifferentCatalogLevels()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(ROOT_LEVEL).thenReturn(LEAF_LEVEL);

		final int value = comparator.compare(component1, component2);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeGreater_DifferentCatalogLevels()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(LEAF_LEVEL).thenReturn(ROOT_LEVEL);

		final int value = comparator.compare(component1, component2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeGreater_SameCatalogLevels_component2FromActiveCatalogVersion()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(ROOT_LEVEL);
		when(catalogVersion1.getActive()).thenReturn(Boolean.FALSE);
		when(catalogVersion2.getActive()).thenReturn(Boolean.TRUE);

		final int value = comparator.compare(component1, component2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeSmaller_SameCatalogLevels_component1FromActiveCatalogVersion()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(ROOT_LEVEL);
		when(catalogVersion1.getActive()).thenReturn(Boolean.TRUE);
		when(catalogVersion2.getActive()).thenReturn(Boolean.FALSE);

		final int value = comparator.compare(component1, component2);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeEqual()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(ROOT_LEVEL);
		when(catalogVersion1.getActive()).thenReturn(Boolean.TRUE);
		when(catalogVersion2.getActive()).thenReturn(Boolean.TRUE);

		final int value = comparator.compare(component1, component2);

		assertThat(value, equalTo(0));
	}

}
