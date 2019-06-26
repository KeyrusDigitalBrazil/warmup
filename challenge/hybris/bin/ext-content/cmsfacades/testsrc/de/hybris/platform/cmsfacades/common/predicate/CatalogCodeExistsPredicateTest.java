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
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogCodeExistsPredicateTest
{
	@InjectMocks
	private CatalogCodeExistsPredicate predicate;

	@Mock
	private CatalogService catalogService;

	@Mock
	private CatalogModel catalogModel;

	private String VALID_CATALOG_CODE = "validCatalogCode";
	private String INVALID_CATALOG_CODE = "invalidCatalogCode";

	@Test
	public void shouldReturnTrueIfCatalogCodeExists()
	{
		// GIVEN
		when(catalogService.getCatalogForId(VALID_CATALOG_CODE)).thenReturn(catalogModel);

		// WHEN
		boolean result = predicate.test(VALID_CATALOG_CODE);

		// THEN
		Assert.assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfCatalogCodeNotExists()
	{
		// GIVEN
		when(catalogService.getCatalogForId(INVALID_CATALOG_CODE)).thenThrow(new RuntimeException(""));

		// WHEN
		boolean result = predicate.test(INVALID_CATALOG_CODE);

		// THEN
		Assert.assertFalse(result);
	}
}
