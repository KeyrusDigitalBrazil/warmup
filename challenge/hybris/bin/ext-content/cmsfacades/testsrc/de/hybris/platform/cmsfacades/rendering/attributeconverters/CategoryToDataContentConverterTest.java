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
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.rendering.attributeconverters.CategoryToDataContentConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryToDataContentConverterTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String CATEGORY_CODE = "some_category_code";

	@Mock
	private CategoryModel categoryModel;

	@InjectMocks
	private CategoryToDataContentConverter categoryToDataContentConverter;

	// --------------------------------------------------------------------------
	// Tests Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(categoryModel.getCode()).thenReturn(CATEGORY_CODE);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenSourceIsNull_WhenConvertIsCalled_ThenItReturnsNull()
	{
		// WHEN
		String result = categoryToDataContentConverter.convert(null);

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenACategory_WhenConvertIsCalled_ThenItReturnsItsCode()
	{
		// WHEN
		String result = categoryToDataContentConverter.convert(categoryModel);

		// THEN
		assertThat(result, is(CATEGORY_CODE));
	}
}
