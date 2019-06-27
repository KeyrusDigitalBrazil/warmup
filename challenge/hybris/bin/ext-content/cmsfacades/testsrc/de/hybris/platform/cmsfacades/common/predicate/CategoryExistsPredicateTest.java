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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.common.service.ProductCatalogItemModelFinder;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryExistsPredicateTest
{

	@InjectMocks
	private CategoryExistsPredicate predicate;

	@Mock
	private ProductCatalogItemModelFinder productCatalogItemModelFinder;


	@Test
	public void shouldFail_CategoryCodeNotFound()
	{
		final String invalidKey = "invalid";
		when(productCatalogItemModelFinder.getCategoryForCompositeKey(invalidKey))
				.thenThrow(new UnknownIdentifierException("invalid key"));

		final boolean result = predicate.test(invalidKey);

		assertFalse(result);
	}

	@Test
	public void shouldPass_CategoryCodeExists()
	{
		final String validKey = "apple-staged-phone";
		when(productCatalogItemModelFinder.getCategoryForCompositeKey(validKey)).thenReturn(new CategoryModel());

		final boolean result = predicate.test(validKey);

		assertTrue(result);
	}
}