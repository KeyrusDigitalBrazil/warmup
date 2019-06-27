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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CloneContextSameAsActiveCatalogVersionPredicateTest
{
	@InjectMocks
	private CloneContextSameAsActiveCatalogVersionPredicate predicate;

	@Mock
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private CatalogVersionModel targetVersionModel;

	@Mock
	private CatalogModel targetCatalogModel;

	private final String CATALOG_ID = "testCatalog";
	private final String CATALOG_VERSION = "testCatalogVersion";
	private final Object fakeObject = new Object();

	@Before
	public void start()
	{
		Map<String, String> cloneContext = new HashMap<>();
		cloneContext.put(CURRENT_CONTEXT_CATALOG, CATALOG_ID);
		cloneContext.put(CURRENT_CONTEXT_CATALOG_VERSION, CATALOG_VERSION);
		when(cmsAdminSiteService.getCloneContext()).thenReturn(cloneContext);

		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(targetVersionModel);
		when(targetVersionModel.getCatalog()).thenReturn(targetCatalogModel);
		when(targetVersionModel.getVersion()).thenReturn(CATALOG_VERSION);
		when(targetCatalogModel.getId()).thenReturn(CATALOG_ID);

		predicate.setCmsAdminSiteService(cmsAdminSiteService);
	}

	@Test
	public void shouldReturnTrueWhenCatalogIdAndCatalogVersionEqual()
	{
		// WHEN
		boolean result = predicate.test(fakeObject);

		// THEN
		assertTrue("CMSItemCloneToSameCatalogVersionPredicateTest should return true if cloneContext is the same as active catalog version", result);
	}

	@Test
	public void shouldReturnTrueWhenCloneContextIsUndefined()
	{
		// GIVEN
		when(cmsAdminSiteService.getCloneContext()).thenReturn(null);

		// WHEN
		boolean result = predicate.test(fakeObject);

		// THEN
		assertTrue("CMSItemCloneToSameCatalogVersionPredicateTest should return true if clone context is undefined", result);
	}

	@Test
	public void shouldReturnFalseWhenVersionIsNotSameForCloneContextAndActiveVersion()
	{
		// GIVEN
		when(targetVersionModel.getVersion()).thenReturn("diffVersion");

		// WHEN
		boolean result = predicate.test(fakeObject);

		// THEN
		assertFalse("CMSItemCloneToSameCatalogVersionPredicateTest should return false if clone context version is not the same as for active catalog version", result);
	}

	@Test
	public void shouldReturnFalseWhenCatalogIdIsNotSameForCloneContextAndActiveVersion()
	{
		// GIVEN
		when(targetCatalogModel.getId()).thenReturn("diffCatalogId");

		// WHEN
		boolean result = predicate.test(fakeObject);

		// THEN
		assertFalse("CMSItemCloneToSameCatalogVersionPredicateTest should return false if clone context catalog id is not the same as for active catalog id", result);
	}
}
