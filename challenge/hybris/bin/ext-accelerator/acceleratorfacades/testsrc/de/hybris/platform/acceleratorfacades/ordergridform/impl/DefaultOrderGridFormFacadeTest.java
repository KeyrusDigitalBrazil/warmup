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
package de.hybris.platform.acceleratorfacades.ordergridform.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.product.data.LeafDimensionData;
import de.hybris.platform.acceleratorfacades.product.data.ReadOnlyOrderGridData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class DefaultOrderGridFormFacadeTest
{
	@Mock
	ProductFacade productFacade;

	@InjectMocks
	private final DefaultOrderGridFormFacade orderGridFormFacade = new DefaultOrderGridFormFacade();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetReadOnlyOrderGrid()
	{
		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGrid(create3DOrderEntries());
		Assert.assertEquals(readOnlyOrderGridDataMap.size(), 2);
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_BlackB2B_M"));
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_BrownB2B_M"));
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_BlackB2B_M").getLeafDimensionDataSet().size(), 2);
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_BrownB2B_M").getLeafDimensionDataSet().size(), 1);

		final ReadOnlyOrderGridData readOnlyOrderGridData = readOnlyOrderGridDataMap.get("B2B_BlackB2B_M");
		Assert.assertTrue(readOnlyOrderGridData.getDimensionHeaderMap().containsKey("Color"));
		Assert.assertTrue(readOnlyOrderGridData.getDimensionHeaderMap().containsKey("Fit"));
		final Set<LeafDimensionData> leafDimensionSet = readOnlyOrderGridData.getLeafDimensionDataSet();
		final LeafDimensionData[] leafDimensionDatas = leafDimensionSet.toArray(new LeafDimensionData[leafDimensionSet.size()]);
		//Check sorting
		Assert.assertEquals(leafDimensionDatas[0].getLeafDimensionValue(), "5");
		Assert.assertEquals(leafDimensionDatas[1].getLeafDimensionValue(), "6.5");
	}

	@Test
	public void testGet2DReadOnlyOrderGrid()
	{
		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGrid(create2DOrderEntries());
		Assert.assertEquals(readOnlyOrderGridDataMap.size(), 2);
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Black"));
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Brown"));
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_Black").getLeafDimensionDataSet().size(), 2);
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_Brown").getLeafDimensionDataSet().size(), 1);

		final ReadOnlyOrderGridData readOnlyOrderGridData = readOnlyOrderGridDataMap.get("B2B_Black");
		Assert.assertTrue(readOnlyOrderGridData.getDimensionHeaderMap().containsKey("Color"));
		final Set<LeafDimensionData> leafDimensionSet = readOnlyOrderGridData.getLeafDimensionDataSet();
		final LeafDimensionData[] leafDimensionDatas = leafDimensionSet.toArray(new LeafDimensionData[leafDimensionSet.size()]);
		//Check sorting
		Assert.assertEquals(leafDimensionDatas[0].getLeafDimensionValue(), "5");
		Assert.assertEquals(leafDimensionDatas[1].getLeafDimensionValue(), "6.5");
	}

	@Test
	public void testGet1DReadOnlyOrderGrid()
	{
		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGrid(create1DOrderEntries());
		Assert.assertEquals(readOnlyOrderGridDataMap.size(), 3);
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Black"));
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Brown"));
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Blue"));
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_Black").getLeafDimensionDataSet().size(), 1);
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_Brown").getLeafDimensionDataSet().size(), 1);
		Assert.assertEquals(readOnlyOrderGridDataMap.get("B2B_Blue").getLeafDimensionDataSet().size(), 1);

		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap2 = orderGridFormFacade
				.getReadOnlyOrderGrid(createSingle1DOrderEntry());
		Assert.assertTrue(readOnlyOrderGridDataMap2.size() == 1);
		Assert.assertTrue(readOnlyOrderGridDataMap2.containsKey("B2B_Brown"));
	}

	@Test
	public void testReadOnlyOrderGridTestNulls()
	{
		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGrid(new ArrayList<OrderEntryData>());
		Assert.assertTrue(readOnlyOrderGridDataMap.isEmpty());

		final List<OrderEntryData> orderEntries = new LinkedList<>();
		final ProductData productData = createProductData("product1", "baseProduct", null);
		orderEntries.add(createOrderEntryData(productData, 8L));

		final CategoryData categoryData1 = createCategoryData(null, null, null, 1);
		final ProductData productData2 = createProductData("product2", "baseProduct", createCategoryList(categoryData1));
		orderEntries.add(createOrderEntryData(productData2, 8L));
	}

	@Test
	public void testGet2DReadOnlyOrderGridForProductInOrder()
	{
		final AbstractOrderData orderData = new AbstractOrderData();
		final OrderEntryData entry = new OrderEntryData();
		final ProductData productData = create2DBaseProductData("baseProduct");
		entry.setProduct(productData);
		entry.setEntries(create2DOrderEntries());
		orderData.setEntries(Arrays.asList(entry));
		final String productCode = productData.getCode();
		final Collection<ProductOption> productOptions = Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES);

		Mockito.when(productFacade.getProductForCodeAndOptions(productCode, productOptions)).thenReturn(productData);

		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGridForProductInOrder(productCode, productOptions, orderData);

		Assert.assertEquals(readOnlyOrderGridDataMap.size(), 2);
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Black"));
		Assert.assertTrue(readOnlyOrderGridDataMap.containsKey("B2B_Brown"));
		Assert.assertEquals(2, readOnlyOrderGridDataMap.get("B2B_Black").getLeafDimensionDataSet().size());
		Assert.assertEquals(1, readOnlyOrderGridDataMap.get("B2B_Brown").getLeafDimensionDataSet().size());

		final ReadOnlyOrderGridData readOnlyOrderGridData = readOnlyOrderGridDataMap.get("B2B_Black");
		Assert.assertTrue(readOnlyOrderGridData.getDimensionHeaderMap().containsKey("Color"));
		final Set<LeafDimensionData> leafDimensionSet = readOnlyOrderGridData.getLeafDimensionDataSet();
		final LeafDimensionData[] leafDimensionDatas = leafDimensionSet.toArray(new LeafDimensionData[leafDimensionSet.size()]);
		//Check sorting
		Assert.assertEquals(leafDimensionDatas[0].getLeafDimensionValue(), "5");
		Assert.assertEquals(leafDimensionDatas[1].getLeafDimensionValue(), "6.5");
	}

	@Test
	public void testReadOnlyOrderGridForNonMultiDProductInOrder()
	{
		final AbstractOrderData orderData = new AbstractOrderData();
		final OrderEntryData entry = new OrderEntryData();
		final ProductData productData = create2DBaseProductData("baseProduct");
		entry.setProduct(productData);
		// No Multi-D Variants
		orderData.setEntries(Arrays.asList(entry));
		final String productCode = productData.getCode();

		Mockito.when(productFacade.getProductForCodeAndOptions(productCode, null)).thenReturn(productData);

		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGridForProductInOrder(productCode, null, orderData);

		Assert.assertTrue(readOnlyOrderGridDataMap.isEmpty());
	}

	@Test
	public void testReadOnlyOrderGridForProductNotInOrder()
	{
		final AbstractOrderData orderData = new AbstractOrderData();
		final OrderEntryData entry = new OrderEntryData();
		final ProductData baseProductData = create2DBaseProductData("baseProduct");
		entry.setProduct(baseProductData);
		entry.setEntries(create2DOrderEntries());
		orderData.setEntries(Arrays.asList(entry));

		final ProductData otherProductData = create2DBaseProductData("dummyProduct");
		final String otherProductCode = otherProductData.getCode();
		final Collection<ProductOption> productOptions = Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES);

		Mockito.when(productFacade.getProductForCodeAndOptions(otherProductCode, productOptions)).thenReturn(otherProductData);

		final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap = orderGridFormFacade
				.getReadOnlyOrderGridForProductInOrder(otherProductCode, productOptions, orderData);

		Assert.assertTrue(readOnlyOrderGridDataMap.isEmpty());
	}

	protected List<OrderEntryData> create2DOrderEntries()
	{
		final List<OrderEntryData> orderEntriesFor2D = new LinkedList<>();

		final CategoryData categoryData1 = createCategoryData("B2B_5", "5", "Size", 1);
		final CategoryData categoryData2 = createCategoryData("B2B_Black", "Black", "Color", 1);
		final ProductData productData1 = createProductData("product1", "baseProduct",
				createCategoryList(categoryData1, categoryData2));
		orderEntriesFor2D.add(createOrderEntryData(productData1, 7L));

		final CategoryData categoryData11 = createCategoryData("B2B_6.5", "6.5", "Size", 2);
		final CategoryData categoryData21 = createCategoryData("B2B_Black", "Black", "Color", 2);
		final ProductData productData2 = createProductData("product2", "baseProduct",
				createCategoryList(categoryData11, categoryData21));
		orderEntriesFor2D.add(createOrderEntryData(productData2, 8L));

		final CategoryData categoryData12 = createCategoryData("B2B_5", "5", "Size", 1);
		final CategoryData categoryData22 = createCategoryData("B2B_Brown", "Brown", "Color", 1);
		final ProductData productData3 = createProductData("product3", "baseProduct",
				createCategoryList(categoryData12, categoryData22));
		orderEntriesFor2D.add(createOrderEntryData(productData3, 5L));

		return orderEntriesFor2D;
	}

	protected ProductData create2DBaseProductData(final String baseProductCode)
	{
		final CategoryData categoryData1 = createCategoryData("B2B_5", "5", "Size", 1);
		final CategoryData categoryData2 = createCategoryData("B2B_Black", "Black", "Color", 1);
		final ProductData productData1 = createProductData(baseProductCode, baseProductCode,
				createCategoryList(categoryData1, categoryData2));
		return productData1;
	}

	protected List<OrderEntryData> create3DOrderEntries()
	{
		final List<OrderEntryData> orderEntriesFor3D = new LinkedList<>();

		final CategoryData categoryData1 = createCategoryData("B2B_5", "5", "Size", 1);
		final CategoryData categoryData2 = createCategoryData("B2B_M", "M", "Fit", 1);
		final CategoryData categoryData3 = createCategoryData("B2B_Black", "Black", "Color", 1);
		final ProductData productData1 = createProductData("product1", "baseProduct",
				createCategoryList(categoryData1, categoryData2, categoryData3));
		orderEntriesFor3D.add(createOrderEntryData(productData1, 7L));

		final CategoryData categoryData11 = createCategoryData("B2B_6.5", "6.5", "Size", 2);
		final CategoryData categoryData21 = createCategoryData("B2B_M", "M", "Fit", 2);
		final CategoryData categoryData31 = createCategoryData("B2B_Black", "Black", "Color", 2);
		final ProductData productData2 = createProductData("product2", "baseProduct",
				createCategoryList(categoryData11, categoryData21, categoryData31));
		orderEntriesFor3D.add(createOrderEntryData(productData2, 8L));

		final CategoryData categoryData12 = createCategoryData("B2B_5", "5", "Size", 1);
		final CategoryData categoryData22 = createCategoryData("B2B_M", "M", "Fit", 1);
		final CategoryData categoryData32 = createCategoryData("B2B_Brown", "Brown", "Color", 1);
		final ProductData productData3 = createProductData("product3", "baseProduct",
				createCategoryList(categoryData12, categoryData22, categoryData32));
		orderEntriesFor3D.add(createOrderEntryData(productData3, 5L));

		return orderEntriesFor3D;
	}

	protected List<OrderEntryData> create1DOrderEntries()
	{
		final List<OrderEntryData> orderEntriesFor1D = new LinkedList<>();

		final CategoryData categoryData1 = createCategoryData("B2B_Black", "Black", "Color", 1);
		final ProductData productData1 = createProductData("product1", "baseProduct", createCategoryList(categoryData1));
		orderEntriesFor1D.add(createOrderEntryData(productData1, 7L));

		final CategoryData categoryData2 = createCategoryData("B2B_Blue", "Black", "Color", 2);
		final ProductData productData2 = createProductData("product2", "baseProduct", createCategoryList(categoryData2));
		orderEntriesFor1D.add(createOrderEntryData(productData2, 8L));

		final CategoryData categoryData3 = createCategoryData("B2B_Brown", "Brown", "Color", 1);
		final ProductData productData3 = createProductData("product3", "baseProduct", createCategoryList(categoryData3));
		orderEntriesFor1D.add(createOrderEntryData(productData3, 5L));

		return orderEntriesFor1D;
	}

	protected List<OrderEntryData> createSingle1DOrderEntry()
	{
		final List<OrderEntryData> orderEntriesFor1D = new LinkedList<>();

		final CategoryData categoryData3 = createCategoryData("B2B_Brown", "Brown", "Color", 1);
		final ProductData productData3 = createProductData("product3", "baseProduct", createCategoryList(categoryData3));
		orderEntriesFor1D.add(createOrderEntryData(productData3, 5L));

		return orderEntriesFor1D;
	}

	protected List<CategoryData> createCategoryList(final CategoryData... values)
	{
		final List<CategoryData> categoryDataList = new LinkedList<>();
		for (final CategoryData categoryData : values)
		{
			categoryDataList.add(categoryData);
		}
		return categoryDataList;
	}

	protected OrderEntryData createOrderEntryData(final ProductData productData, final long quantity)
	{
		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setProduct(productData);
		orderEntryData.setQuantity(Long.valueOf(quantity));

		return orderEntryData;
	}


	protected ProductData createProductData(final String code, final String baseProductCode,
			final List<CategoryData> categoryDataList)
	{
		final ProductData productData = new ProductData();
		productData.setBaseProduct(baseProductCode);
		productData.setCode(code);
		productData.setCategories(categoryDataList);

		return productData;
	}

	protected CategoryData createCategoryData(final String code, final String name, final String parentCategoryName,
			final int sequence)
	{
		final CategoryData categoryData = new CategoryData();
		categoryData.setCode(code);
		categoryData.setName(name);
		categoryData.setParentCategoryName(parentCategoryName);
		categoryData.setSequence(sequence);

		return categoryData;
	}

}
