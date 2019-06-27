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
 package de.hybris.platform.customerinterestsfacades.productinterest.strageties.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.strategies.CollectionSortStrategy;
import de.hybris.platform.customerinterestsfacades.strategies.impl.SortByProductNameStrategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

@UnitTest
public class SortByProductNameStrategyTest
{
	private SortByProductNameStrategy sortByProductNameStrategy;
	private List<ProductInterestRelationData> list;
	private ProductData productA;
	private ProductData productB;
	private ProductInterestRelationData relationA;
	private ProductInterestRelationData relationB;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sortByProductNameStrategy = new SortByProductNameStrategy();
		list = new ArrayList<ProductInterestRelationData>();
		relationA = new ProductInterestRelationData();
		relationB = new ProductInterestRelationData();
		productA = new ProductData();
		productB = new ProductData();
		relationA.setProduct(productA);
		relationB.setProduct(productB);
		list.add(relationA);
		list.add(relationB);
	}

	@Test
	public void testSort_ProductA_name_null()
	{
		productA.setName(null);
		productB.setName("productB");
		sortByProductNameStrategy.ascendingSort(list);
		Assert.assertEquals(productA, list.get(0).getProduct());
	}

	@Test
	public void testSort_productsB_name_null()
	{
		productA.setName("productA");
		productB.setName(null);
		sortByProductNameStrategy.ascendingSort(list);
		Assert.assertEquals(productB, list.get(0).getProduct());
	}

	@Test
	public void testSort_Allproducts_name_notNull()
	{
		productA.setName("product2");
		productB.setName("product1");
		sortByProductNameStrategy.ascendingSort(list);
		Assert.assertEquals(productB, list.get(0).getProduct());
		Assert.assertEquals(productA, list.get(1).getProduct());

	}


}
