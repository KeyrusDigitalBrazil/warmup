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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionExistsPredicateTest
{
	private final String catalog = "catalog";
	private final String catalogVersion = "catalogVersion";

	@InjectMocks
	private CatalogVersionExistsPredicate predicate;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	@Before
	public void setUp()
	{
		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(sessionSearchRestrictionsDisabler).execute(any());
	}


	@Test
	public void predicate_shouldFail_whenCatalogVersionDoesntExist()
	{
		// Arrange
		when(catalogVersionService.getCatalogVersion(catalog, catalogVersion)).thenReturn(null);

		// Act
		final boolean result = predicate.test(catalog, catalogVersion);

		// Assert
		assertFalse(result);
	}

	@Test
	public void predicate_shouldFail_whenGivenInvalidIdentifier()
	{
		// Arrange
		when(catalogVersionService.getCatalogVersion(catalog, catalogVersion))
				.thenThrow(new UnknownIdentifierException("invalid key"));

		// Act
		final boolean result = predicate.test(catalog, catalogVersion);

		// Assert
		assertFalse(result);
	}

	@Test
	public void predicate_shouldPass_whenGivenValidCatalogAndCatalogVersion()
	{
		// Arrange
		final CatalogVersionModel model = new CatalogVersionModel();
		when(catalogVersionService.getCatalogVersion(catalog, catalogVersion)).thenReturn(model);

		// Act
		final boolean result = predicate.test(catalog, catalogVersion);

		// Assert
		assertTrue(result);
	}

}
