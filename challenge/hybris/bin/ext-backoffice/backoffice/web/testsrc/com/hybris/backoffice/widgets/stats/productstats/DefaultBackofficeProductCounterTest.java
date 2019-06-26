/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.widgets.stats.productstats;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.product.ProductService;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeProductCounterTest
{
	@Spy
	@InjectMocks
	private DefaultBackofficeProductCounter counter;

	@Mock
	private ProductService productService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private CatalogService catalogService;

	@Test
	public void shouldCountAllProducts()
	{
		// given
		when(counter.getCatalogService().getAllCatalogs()).thenReturn(catalogModelsStub());

		// when
		final long numberOfProducts = counter.countProducts();

		// then
		assertThat(numberOfProducts).isEqualTo(0);
	}

	@Test
	public void shouldCountUnapprovedProducts()
	{
		// given
		when(counter.getCatalogService().getAllCatalogs()).thenReturn(catalogModelsStub());

		// when
		final long numberOfProducts = counter.countProducts(ArticleApprovalStatus.UNAPPROVED);

		// then
		assertThat(numberOfProducts).isEqualTo(0);
	}

	@Test
	public void shouldCountCheckProducts()
	{
		// given
		when(counter.getCatalogService().getAllCatalogs()).thenReturn(catalogModelsStub());

		// when
		final long numberOfProducts = counter.countProducts(ArticleApprovalStatus.CHECK);

		// then
		assertThat(numberOfProducts).isEqualTo(0);
	}

	@Test
	public void shouldCountApprovedProducts()
	{
		// given
		when(counter.getCatalogService().getAllCatalogs()).thenReturn(catalogModelsStub());

		// when
		final long numberOfProducts = counter.countProducts(ArticleApprovalStatus.APPROVED);

		// then
		assertThat(numberOfProducts).isEqualTo(0);
	}

	private Collection<CatalogModel> catalogModelsStub()
	{
		return new ArrayList<>();
	}
}
