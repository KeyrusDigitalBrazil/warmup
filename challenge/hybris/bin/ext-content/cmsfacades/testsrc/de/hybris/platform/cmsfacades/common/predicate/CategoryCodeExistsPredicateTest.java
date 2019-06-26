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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryCodeExistsPredicateTest
{
	@InjectMocks
	private CategoryCodeExistsPredicate predicate;

	@Mock
	private CategoryService categoryService;

	@Mock
	private CategoryModel categoryModel;

	private String VALID_CATEGORY_CODE = "validCategoryCode";
	private String INVALID_CATEGORY_CODE = "invalidCategoryCode";

	@Test
	public void shouldReturnTrueIfCategoryCodeExists()
	{
		// GIVEN
		when(categoryService.getCategoryForCode(VALID_CATEGORY_CODE)).thenReturn(categoryModel);

		// WHEN
		boolean result = predicate.test(VALID_CATEGORY_CODE);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfCategoryCodeNotExists()
	{
		// GIVEN
		when(categoryService.getCategoryForCode(INVALID_CATEGORY_CODE)).thenThrow(new RuntimeException(""));

		// WHEN
		boolean result = predicate.test(INVALID_CATEGORY_CODE);

		// THEN
		assertFalse(result);
	}
}
