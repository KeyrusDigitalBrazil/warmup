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
package de.hybris.platform.acceleratorfacades.product.converts.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.product.converters.populator.ProductKeywordsPopulator;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductDescriptionPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductDescriptionPopulator}
 */
@UnitTest
public class ProductKeywordsPopulatorTest
{
	private static final List<KeywordModel> KEYWORDS = new ArrayList<>();
	private static final String KEYWORD = "erjflerjfeldfmnvfaehjndk";

	private ProductKeywordsPopulator productKeywordsPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productKeywordsPopulator = new ProductKeywordsPopulator();

		final KeywordModel k = new KeywordModel();
		k.setKeyword(KEYWORD);
		KEYWORDS.add(k);
	}


	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);

		given(source.getKeywords()).willReturn(KEYWORDS);

		final ProductData result = new ProductData();
		productKeywordsPopulator.populate(source, result);

		final Set<String> expected = Collections.singleton(KEYWORD);
		final Set<String> actual = result.getKeywords();

		Assert.assertEquals(
				String.format("Expecting [%s] but got [%s] instead", String.join(",", expected), String.join(" ", actual)), expected,
				actual);
	}
}
