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

import de.hybris.platform.acceleratorfacades.ordergridform.OrderGridFormFacade;
import de.hybris.platform.acceleratorfacades.product.data.LeafDimensionData;
import de.hybris.platform.acceleratorfacades.product.data.ReadOnlyOrderGridData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;


/**
 * Default implementation of {@link OrderGridFormFacade}
 */
public class DefaultOrderGridFormFacade implements OrderGridFormFacade
{
	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Override
	public Map<String, ReadOnlyOrderGridData> getReadOnlyOrderGrid(final List<OrderEntryData> orderEntryDataList)
	{
		final Map<String, ReadOnlyOrderGridData> readOnlyMultiDMap = new LinkedHashMap<>();

		for (final OrderEntryData dimensionEntry : orderEntryDataList)
		{
			final Collection<CategoryData> categoryDataCollection = dimensionEntry.getProduct().getCategories();

			final Optional<AbstractMap.SimpleImmutableEntry<String, ReadOnlyOrderGridData>> readOnlyMultiDMapEntry = (categoryDataCollection
					.size() > 1) ? populateReadonlyGridMapFromCategories(dimensionEntry, categoryDataCollection, readOnlyMultiDMap)
							: populateDataFor1DGrid(categoryDataCollection.iterator().next(), dimensionEntry);

			if (readOnlyMultiDMapEntry.isPresent())
			{
				final AbstractMap.SimpleImmutableEntry<String, ReadOnlyOrderGridData> entry = readOnlyMultiDMapEntry.get();
				readOnlyMultiDMap.put(entry.getKey(), entry.getValue());
			}
		}

		return readOnlyMultiDMap;
	}

	@Override
	public Map<String, ReadOnlyOrderGridData> getReadOnlyOrderGridForProductInOrder(final String productCode,
			final Collection<ProductOption> productOptions, final AbstractOrderData abstractOrderData)
	{
		final ProductData productData = productFacade.getProductForCodeAndOptions(productCode, productOptions);
		final String baseProductCode = productData.getBaseProduct();

		final Optional<OrderEntryData> orderEntryDataOptional = abstractOrderData.getEntries().stream()
				.filter(orderEntryData -> orderEntryData.getProduct().getCode().equals(baseProductCode)).findFirst();

		return getReadOnlyOrderGrid(orderEntryDataOptional.map(entry -> entry.getEntries()).orElse(Collections.EMPTY_LIST));
	}

	protected Optional<AbstractMap.SimpleImmutableEntry<String, ReadOnlyOrderGridData>> populateReadonlyGridMapFromCategories(
			final OrderEntryData dimensionEntry, final Collection<CategoryData> categoryDataCollection,
			final Map<String, ReadOnlyOrderGridData> readOnlyOrderGridDataMap)
	{
		final Map<String, String> dimensionHeaderMap = new LinkedHashMap<>();
		final List<CategoryData> categoryDataList = new LinkedList<>(categoryDataCollection);
		final LeafDimensionData leafDimensionData = new LeafDimensionData();
		final StringBuilder hashKey = new StringBuilder();
		Optional<AbstractMap.SimpleImmutableEntry<String, ReadOnlyOrderGridData>> entry = Optional.empty();

		for (int i = categoryDataList.size() - 1; i >= 0; i--)
		{
			final CategoryData categoryData = categoryDataList.get(i);
			if (i > 0)
			{
				dimensionHeaderMap.put(categoryData.getParentCategoryName(), categoryData.getName());
				hashKey.append(categoryData.getCode());
			}
			else
			{
				populateLeafDimensionData(categoryData, dimensionEntry, leafDimensionData);
			}
		}

		final Optional<ReadOnlyOrderGridData> readOnlyOrderGridDataOptional = groupOrderEntriesWithCategory(hashKey.toString(),
				dimensionEntry, leafDimensionData, dimensionHeaderMap, readOnlyOrderGridDataMap);
		if (readOnlyOrderGridDataOptional.isPresent())
		{
			entry = Optional.of(new AbstractMap.SimpleImmutableEntry<>(hashKey.toString(), readOnlyOrderGridDataOptional.get()));
		}

		return entry;
	}


	protected Optional<ReadOnlyOrderGridData> groupOrderEntriesWithCategory(final String hashKey,
			final OrderEntryData dimensionEntry, final LeafDimensionData leafDimensionData,
			final Map<String, String> dimensionHeaderMap, final Map<String, ReadOnlyOrderGridData> readOnlyMultiDMap)
	{
		Optional<ReadOnlyOrderGridData> readOnlyOrderGridDataOptional = Optional.empty();
		if (readOnlyMultiDMap.containsKey(hashKey))
		{
			final ReadOnlyOrderGridData readOnlyOrderGrid = readOnlyMultiDMap.get(hashKey);
			final Set<LeafDimensionData> leafDimensionDataList = readOnlyOrderGrid.getLeafDimensionDataSet();
			leafDimensionDataList.add(leafDimensionData);
		}
		else
		{
			readOnlyOrderGridDataOptional = Optional
					.of(populateAndSortReadonlyOrderGridData(dimensionEntry, leafDimensionData, dimensionHeaderMap));
		}

		return readOnlyOrderGridDataOptional;
	}


	protected ReadOnlyOrderGridData populateAndSortReadonlyOrderGridData(final OrderEntryData dimensionEntry,
			final LeafDimensionData leafDimensionData, final Map<String, String> dimensionHeaderMap)
	{
		final ReadOnlyOrderGridData readOnlyOrderGridData = new ReadOnlyOrderGridData();
		readOnlyOrderGridData.setProduct(dimensionEntry.getProduct());
		readOnlyOrderGridData.setDimensionHeaderMap(dimensionHeaderMap);
		readOnlyOrderGridData.setLeafDimensionDataSet(populateLeafDimensionData(leafDimensionData));

		return readOnlyOrderGridData;
	}


	protected Set<LeafDimensionData> populateLeafDimensionData(final LeafDimensionData leafDimensionData)
	{
		final Set<LeafDimensionData> leafDimensionDataSet = new TreeSet<>((obj1, obj2) -> obj1.getSequence() - obj2.getSequence());
		leafDimensionDataSet.add(leafDimensionData);

		return leafDimensionDataSet;
	}


	protected Optional<AbstractMap.SimpleImmutableEntry<String, ReadOnlyOrderGridData>> populateDataFor1DGrid(
			final CategoryData categoryData, final OrderEntryData dimensionEntry)
	{
		final Map<String, String> dimensionHeaderMap = new LinkedHashMap<>();
		final LeafDimensionData leafDimensionData = new LeafDimensionData();
		dimensionHeaderMap.put(categoryData.getParentCategoryName(), categoryData.getName());
		leafDimensionData.setLeafDimensionData(dimensionEntry.getQuantity().toString());
		leafDimensionData.setPrice(dimensionEntry.getTotalPrice());
		leafDimensionData.setSequence(categoryData.getSequence());

		return Optional.of(new AbstractMap.SimpleImmutableEntry<>(categoryData.getCode(),
				populateAndSortReadonlyOrderGridData(dimensionEntry, leafDimensionData, dimensionHeaderMap)));
	}


	protected void populateLeafDimensionData(final CategoryData categoryData, final OrderEntryData dimensionEntry,
			final LeafDimensionData leafDimensionData)
	{
		leafDimensionData.setLeafDimensionHeader(categoryData.getParentCategoryName());
		leafDimensionData.setLeafDimensionValue(categoryData.getName());
		leafDimensionData.setLeafDimensionData(dimensionEntry.getQuantity().toString());
		leafDimensionData.setPrice(dimensionEntry.getTotalPrice());
		leafDimensionData.setSequence(categoryData.getSequence());
	}

}
