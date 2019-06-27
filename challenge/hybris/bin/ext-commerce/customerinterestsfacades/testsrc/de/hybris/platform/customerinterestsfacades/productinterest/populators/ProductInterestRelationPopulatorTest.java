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
package de.hybris.platform.customerinterestsfacades.productinterest.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestEntryData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.futurestock.ExtendedFutureStockFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductInterestRelationPopulatorTest
{
	private ProductInterestRelationPopulator productInterestRelationPopulator;
	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	@Mock
	private Converter<ProductModel, ProductData> productPriceAndStockConverter;
	@Mock
	private ExtendedFutureStockFacade futureStockFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productInterestRelationPopulator = new ProductInterestRelationPopulator();

		productInterestRelationPopulator.setFutureStockFacade(futureStockFacade);
		productInterestRelationPopulator.setProductConverter(productConverter);
		productInterestRelationPopulator.setProductPriceAndStockConverter(productPriceAndStockConverter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithSourceNull()
	{
		final Map<ProductModel, List<ProductInterestEntryData>> productProductInterestMap = new HashMap<>();
		final List<ProductInterestEntryData> productInterestEntries = new ArrayList<>();
		productProductInterestMap.put(new ProductModel(), productInterestEntries);

		final Entry<ProductModel, List<ProductInterestEntryData>> productInterestModel = productProductInterestMap.entrySet()
				.iterator().next();
		final ProductInterestRelationData productInterestRelationData = null;
		productInterestRelationPopulator.populate(productInterestModel, productInterestRelationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWithTargetNull()
	{
		final Entry<ProductModel, List<ProductInterestEntryData>> productInterestModel = null;
		final ProductInterestRelationData productInterestRelationData = new ProductInterestRelationData();
		productInterestRelationPopulator.populate(productInterestModel, productInterestRelationData);
	}

	@Test
	public void testPopulateWithExistTarget()
	{
		final Map<ProductModel, List<ProductInterestEntryData>> productProductInterestMap = new HashMap<>();
		final List<ProductInterestEntryData> productInterestEntries = new ArrayList<>();
		productProductInterestMap.put(new ProductModel(), productInterestEntries);

		final Entry<ProductModel, List<ProductInterestEntryData>> productInterestModel = productProductInterestMap.entrySet()
				.iterator().next();
		final ProductInterestRelationData productInterestRelationData = new ProductInterestRelationData();
		final ProductData productData = new ProductData();
		productData.setName("apple");
		Mockito.when(productConverter.convert(Mockito.any())).thenReturn(productData);

		productInterestRelationPopulator.populate(productInterestModel, productInterestRelationData);

		Assert.assertEquals(productInterestRelationData.getProduct().getName(), "apple");

	}

	@Test
	public void testPopulateWithFutureStork()
	{
		final ProductModel model = new ProductModel();
		final ProductInterestEntryData productInterestEntryData = new ProductInterestEntryData();
		final ProductData productData = new ProductData();
		productData.setName("apple");
		productInterestEntryData.setInterestType("BACK_IN_STOCK");

		final Map<ProductModel, List<ProductInterestEntryData>> productProductInterestMap = new HashMap<>();
		final List<ProductInterestEntryData> productInterestEntries = new ArrayList<>();
		productInterestEntries.add(productInterestEntryData);
		productProductInterestMap.put(model, productInterestEntries);

		final Entry<ProductModel, List<ProductInterestEntryData>> productInterestModel = productProductInterestMap.entrySet()
				.iterator().next();
		final ProductInterestRelationData productInterestRelationData = new ProductInterestRelationData();
		Mockito.when(productConverter.convert(Mockito.any())).thenReturn(productData);
		Mockito.when(futureStockFacade.getFutureAvailability(Mockito.any())).thenReturn(Collections.emptyList());
		productInterestRelationPopulator.populate(productInterestModel, productInterestRelationData);
		Assert.assertNotNull(productData.getFutureStocks());
	}
}
