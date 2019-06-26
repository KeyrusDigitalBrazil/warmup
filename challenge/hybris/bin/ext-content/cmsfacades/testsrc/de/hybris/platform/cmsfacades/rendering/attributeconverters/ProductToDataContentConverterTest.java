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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.rendering.attributeconverters.ProductToDataContentConverter;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductToDataContentConverterTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String PRODUCT_CODE = "some_product_code";

	@Mock
	private ProductModel productModel;

	@InjectMocks
	private ProductToDataContentConverter productToDataContentConverter;

	// --------------------------------------------------------------------------
	// Tests Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenSourceIsNull_WhenConvertIsCalled_ThenItReturnsNull()
	{
		// WHEN
		String result = productToDataContentConverter.convert(null);

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenProduct_WhenConvertIsCalled_ThenItReturnsTheProductCode()
	{
		// WHEN
		String result = productToDataContentConverter.convert(productModel);

		// THEN
		assertThat(result, is(PRODUCT_CODE));
	}
}
