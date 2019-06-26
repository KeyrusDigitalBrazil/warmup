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
package de.hybris.platform.cmssmarteditwebservices.catalogs.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;

import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ContentCatalogLevelComparatorTest
{
	@InjectMocks
	private ContentCatalogLevelComparator comparator;
	@Mock
	private CatalogLevelService cmsCatalogLevelService;

	@Mock
	private ContentCatalogModel contentCatalog1;
	@Mock
	private ContentCatalogModel contentCatalog2;
	@Mock
	private Entry<CatalogModel, Set<CatalogVersionModel>> entry1;
	@Mock
	private Entry<CatalogModel, Set<CatalogVersionModel>> entry2;

	@Before
	public void setUp()
	{
		when(entry1.getKey()).thenReturn(contentCatalog1);
		when(entry2.getKey()).thenReturn(contentCatalog2);
	}

	@Test
	public void shouldBeGreaterFirstEntryNull()
	{
		final int value = comparator.compare(null, entry2);

		assertThat(value, greaterThan(0));
		verifyZeroInteractions(cmsCatalogLevelService);
	}

	@Test
	public void shouldBeSmallerSecondEntryNull()
	{
		final int value = comparator.compare(entry1, null);

		assertThat(value, lessThan(0));
		verifyZeroInteractions(cmsCatalogLevelService);
	}

	@Test
	public void shouldBeSmaller()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(1);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(3);

		final int value = comparator.compare(entry1, entry2);

		assertThat(value, lessThan(0));
	}

	@Test
	public void shouldBeGreater()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(4);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(2);

		final int value = comparator.compare(entry1, entry2);

		assertThat(value, greaterThan(0));
	}

	@Test
	public void shouldBeEqual()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog1)).thenReturn(1);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog2)).thenReturn(1);

		final int value = comparator.compare(entry1, entry2);

		assertThat(value, equalTo(0));
	}

}
