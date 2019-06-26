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
package de.hybris.platform.adaptivesearch.context.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileContextFactoryTest
{
	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "indexType";

	@Mock
	private List<CatalogVersionModel> catalogVersions;

	@Mock
	private CategoryModel category1;

	@Mock
	private CategoryModel category2;

	@Mock
	private LanguageModel language;

	@Mock
	private CurrencyModel currency;

	private DefaultAsSearchProfileContextFactory asSearchProfileContextFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		asSearchProfileContextFactory = new DefaultAsSearchProfileContextFactory();
	}

	@Test
	public void create()
	{
		// given
		final List<CategoryModel> categoryPath = Arrays.asList(category1, category2);

		// when
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				catalogVersions, categoryPath, language, currency);

		// then
		assertTrue(context instanceof DefaultAsSearchProfileContext);
		assertSame(INDEX_CONFIGURATION, context.getIndexConfiguration());
		assertSame(INDEX_TYPE, context.getIndexType());
		assertSame(catalogVersions, context.getCatalogVersions());
		assertSame(categoryPath, context.getCategoryPath());
		assertSame(context.getLanguage(), language);
		assertSame(context.getCurrency(), currency);
		assertNotNull(context.getAttributes());
	}

	@Test
	public void create2()
	{
		// given
		final List<CategoryModel> categoryPath = Arrays.asList(category1, category2);

		// when
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				catalogVersions, categoryPath);

		// then
		assertTrue(context instanceof DefaultAsSearchProfileContext);
		assertSame(INDEX_CONFIGURATION, context.getIndexConfiguration());
		assertSame(INDEX_TYPE, context.getIndexType());
		assertSame(catalogVersions, context.getCatalogVersions());
		assertSame(categoryPath, context.getCategoryPath());
		assertNotNull(context.getAttributes());
	}
}
