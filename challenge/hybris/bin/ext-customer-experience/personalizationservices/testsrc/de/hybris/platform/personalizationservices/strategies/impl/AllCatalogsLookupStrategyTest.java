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
/**
 *
 */
package de.hybris.platform.personalizationservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.service.CxCatalogService;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AllCatalogsLookupStrategyTest
{
	private AllCatalogsLookupStrategy strategy;

	@Mock
	private CxCatalogService cxCatalogService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CatalogVersionModel cv1, cv2, cv3;

	@Before
	public void setupTest()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new AllCatalogsLookupStrategy();
		strategy.setCatalogVersionService(catalogVersionService);
		strategy.setCxCatalogService(cxCatalogService);
	}

	@Test
	public void shouldReturnOneCatalog()
	{
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(Arrays.asList(cv1, cv2, cv3));
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv1))).willReturn(Boolean.TRUE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv2))).willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv3))).willReturn(Boolean.FALSE);

		final List<CatalogVersionModel> results = strategy.getCatalogVersionsForCalculation();
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0), cv1);
	}

	@Test
	public void shouldReturnAllCatalogs()
	{
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(Arrays.asList(cv1, cv2, cv3));
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv1))).willReturn(Boolean.TRUE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv2))).willReturn(Boolean.TRUE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv3))).willReturn(Boolean.TRUE);

		final List<CatalogVersionModel> results = strategy.getCatalogVersionsForCalculation();
		Assert.assertEquals(results.size(), 3);
		Assert.assertEquals(results.get(0), cv1);
		Assert.assertEquals(results.get(1), cv2);
		Assert.assertEquals(results.get(2), cv3);
	}
}
