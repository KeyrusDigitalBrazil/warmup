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
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductPageTypeCodePredicateTest
{
	@InjectMocks
	private ProductPageTypeCodePredicate predicate;

	@Test
	public void shouldReturnTrueIfPageIsProductPage()
	{
		// WHEN
		boolean result = predicate.test(ProductPageModel._TYPECODE);

		// THEN
		Assert.assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfPageIsNotProductPage()
	{
		// WHEN
		boolean result = predicate.test("fakePageCode");

		// THEN
		Assert.assertFalse(result);
	}
}
