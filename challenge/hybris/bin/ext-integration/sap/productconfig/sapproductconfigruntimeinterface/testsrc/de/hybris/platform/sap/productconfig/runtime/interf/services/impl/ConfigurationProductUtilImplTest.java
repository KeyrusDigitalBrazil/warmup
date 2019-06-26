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
package de.hybris.platform.sap.productconfig.runtime.interf.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProductUtilImplTest
{
	private static final String PRODUCT_CODE = "product code";
	private Collection<CatalogVersionModel> currentCatalogVersions;
	@Mock
	private ProductModel productModel;
	@Mock
	private CatalogModel productCatalog;
	@Mock
	private ContentCatalogModel contentCatalog;
	@Mock
	private ClassificationSystemModel classificationCatalog;
	@Mock
	private CatalogVersionModel currentCatalogVersion;
	@Mock
	private CatalogVersionModel currentContentCatalogVersion;
	@Mock
	private ProductService productService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@InjectMocks
	private ConfigurationProductUtilImpl classUnderTest;

	@Before
	public void setup()
	{
		currentCatalogVersions = new ArrayList<>();
		currentCatalogVersions.add(currentCatalogVersion);
		currentCatalogVersions.add(currentContentCatalogVersion);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(currentCatalogVersions);
		when(productService.getProductForCode(currentCatalogVersion, PRODUCT_CODE)).thenReturn(productModel);
		when(currentCatalogVersion.getActive()).thenReturn(Boolean.TRUE);
		when(currentCatalogVersion.getCatalog()).thenReturn(productCatalog);
		when(currentContentCatalogVersion.getCatalog()).thenReturn(contentCatalog);
		when(currentContentCatalogVersion.getActive()).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testGetProductForCurrentCatalog()
	{
		final ProductModel result = classUnderTest.getProductForCurrentCatalog(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(productModel, result);
		verify(productService).getProductForCode(currentCatalogVersion, PRODUCT_CODE);
	}

	@Test
	public void testGetCurrentCatalogVersion()
	{
		final CatalogVersionModel result = classUnderTest.getCurrentCatalogVersion();
		assertNotNull(result);
		assertEquals(currentCatalogVersion, result);
		verify(catalogVersionService).getSessionCatalogVersions();
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void testGetCurrentCatalogVersionAmbigous()
	{
		currentCatalogVersions.add(currentCatalogVersion);
		classUnderTest.getCurrentCatalogVersion();
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetCurrentCatalogVersionNoneFound()
	{
		currentCatalogVersions.clear();
		classUnderTest.getCurrentCatalogVersion();
	}

	@Test
	public void testIsProductCatalogProduct()
	{
		assertTrue(classUnderTest.isProductCatalogActive(currentCatalogVersion));
	}

	@Test
	public void testIsProductCatalogContent()
	{
		when(currentCatalogVersion.getCatalog()).thenReturn(contentCatalog);
		assertFalse(classUnderTest.isProductCatalogActive(currentCatalogVersion));
	}

	@Test
	public void testIsProductCatalogClassification()
	{
		when(currentCatalogVersion.getCatalog()).thenReturn(classificationCatalog);
		assertFalse(classUnderTest.isProductCatalogActive(currentCatalogVersion));
	}

	@Test
	public void testIsPrductCatalogInactive()
	{
		when(currentCatalogVersion.getActive()).thenReturn(Boolean.FALSE);
		assertFalse(classUnderTest.isProductCatalogActive(currentCatalogVersion));
	}
}
