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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.acceleratorfacades.order.data.PriceRangeData;
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Groups multiple {@link OrderEntryData} as one entry in a {@link AbstractOrderData} based on the multidimensional
 * variants that share the same base product. All non multidimensional product entries will be leaved unmodified as a
 * single entry.
 */
public class GroupOrderEntryPopulator<S extends AbstractOrderModel, T extends AbstractOrderData> implements Populator<S, T>
{
	private static final Logger LOG = Logger.getLogger(GroupOrderEntryPopulator.class);

	public static final String VARIANT_TYPE = "GenericVariantProduct";
	public static final Integer INVALID_ENTRY_NUMBER = Integer.valueOf(-1);
	public static final Long ZERO_QUANTITY = Long.valueOf(0L);

	private ProductService productService;
	private PriceDataFactory priceDataFactory;
	private CommerceEntryGroupUtils entryGroupUtils;

	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		target.setEntries(groupEntries(target.getEntries(), target));
	}

	protected List<OrderEntryData> groupEntries(final List<OrderEntryData> entries, final AbstractOrderData order)
	{
		final Map<String, OrderEntryData> group = new HashMap<>();
		final List<OrderEntryData> allEntries = new ArrayList<>();

		for (final OrderEntryData entry : entries)
		{
			final ProductData product = entry.getProduct();
			if (isGroupable(product))
			{
				final OrderEntryData rootEntry = group.computeIfAbsent(product.getBaseProduct(), k -> createGroupedOrderEntry(entry));
				rootEntry.getEntries().add(entry);
				setEntryGroups(entry, rootEntry, order);
			}
			else
			{
				allEntries.add(entry);
			}
		}

		if (!group.isEmpty())
		{
			consolidateGroupedOrderEntry(group);
			allEntries.addAll(group.values());
		}

		return allEntries;
	}

	// This should be replaced by product.mutidimentional but at this stage is not yet populated
	// Only works for multidimentional products
	protected boolean isGroupable(final ProductData product)
	{
		return product.getBaseProduct() != null && CollectionUtils.isNotEmpty(product.getBaseOptions())
				&& VARIANT_TYPE.equalsIgnoreCase(product.getBaseOptions().get(0).getVariantType());
	}

	protected void consolidateGroupedOrderEntry(final Map<String, OrderEntryData> group)
	{
		for (final String productCode : group.keySet())
		{
			final OrderEntryData parentEntry = group.get(productCode);
			if (parentEntry.getEntries() != null)
			{
				final PriceData firstEntryTotalPrice = parentEntry.getEntries().get(0).getTotalPrice();
				final PriceRangeData priceRange = parentEntry.getProduct().getPriceRange();

				if (firstEntryTotalPrice != null)
				{
					priceRange.setMaxPrice(getMaxPrice(parentEntry, firstEntryTotalPrice));
					priceRange.setMinPrice(getMinPrice(parentEntry, firstEntryTotalPrice));
					parentEntry.setTotalPrice(getTotalPrice(parentEntry, firstEntryTotalPrice));
				}
				parentEntry.setQuantity(getTotalQuantity(parentEntry));
			}
		}
	}

	protected PriceData getMaxPrice(final OrderEntryData parentEntry, final PriceData samplePrice)
	{
		BigDecimal newMaxPrice = BigDecimal.ZERO;

		for (final OrderEntryData childEntry : parentEntry.getEntries())
		{
			if (isNotEmptyPrice(childEntry.getBasePrice()))
			{
				final BigDecimal basePriceValue = childEntry.getBasePrice().getValue();
				if (basePriceValue.compareTo(newMaxPrice) > 0)
				{
					newMaxPrice = basePriceValue;
				}
			}
		}
		return buildPrice(samplePrice, newMaxPrice);
	}

	protected PriceData getMinPrice(final OrderEntryData parentEntry, final PriceData samplePrice)
	{
		BigDecimal newMinPrice = BigDecimal.valueOf(Double.MAX_VALUE);

		for (final OrderEntryData childEntry : parentEntry.getEntries())
		{
			if (isNotEmptyPrice(childEntry.getBasePrice()))
			{
				final BigDecimal basePriceValue = childEntry.getBasePrice().getValue();
				if (basePriceValue.compareTo(newMinPrice) < 0)
				{
					newMinPrice = basePriceValue;
				}
			}
		}
		return buildPrice(samplePrice, newMinPrice);
	}

	protected boolean isNotEmptyPrice(final PriceData price)
	{
		return price != null && price.getValue() != null;
	}

	protected PriceData getTotalPrice(final OrderEntryData parentEntry, final PriceData samplePrice)
	{
		BigDecimal newTotalPrice = BigDecimal.ZERO;

		for (final OrderEntryData childEntry : parentEntry.getEntries())
		{
			if (isNotEmptyPrice(childEntry.getBasePrice()))
			{
				newTotalPrice = newTotalPrice.add(childEntry.getTotalPrice().getValue());
			}
		}
		return buildPrice(samplePrice, newTotalPrice);
	}

	protected Long getTotalQuantity(final OrderEntryData parentEntry)
	{
		long totalQuantity = 0;
		for (final OrderEntryData childEntry : parentEntry.getEntries())
		{
			totalQuantity += (childEntry.getQuantity() != null ? childEntry.getQuantity().longValue() : 0);
		}

		return Long.valueOf(totalQuantity);
	}

	protected OrderEntryData createGroupedOrderEntry(final OrderEntryData firstEntry)
	{
		final OrderEntryData groupedEntry = new OrderEntryData();
		groupedEntry.setEntries(new ArrayList<OrderEntryData>());
		groupedEntry.setEntryNumber(INVALID_ENTRY_NUMBER);
		groupedEntry.setQuantity(ZERO_QUANTITY);

		final ProductData baseProduct = createBaseProduct(firstEntry.getProduct());
		groupedEntry.setProduct(baseProduct);
		groupedEntry.setUpdateable(firstEntry.isUpdateable());
		groupedEntry.setBasePrice(firstEntry.getBasePrice());
		return groupedEntry;
	}

	protected ProductData createBaseProduct(final ProductData variant)
	{
		final ProductData productData = new ProductData();

		productData.setUrl(variant.getUrl());
		productData.setPurchasable(variant.getPurchasable());
		productData.setMultidimensional(Boolean.TRUE);
		productData.setImages(variant.getImages());

		final ProductModel productModel = productService.getProductForCode(variant.getBaseProduct());
		productData.setCode(productModel.getCode());
		productData.setName(productModel.getName());
		productData.setDescription(productModel.getDescription());

		productData.setPriceRange(new PriceRangeData());

		return productData;
	}

	protected void setEntryGroups(final OrderEntryData entry, final OrderEntryData groupedOrderEntry, final AbstractOrderData order)
	{
		if (CollectionUtils.isEmpty(groupedOrderEntry.getEntryGroupNumbers()))
		{
			final EntryGroupData rootGroup = createRootGroup(groupedOrderEntry, order);
			groupedOrderEntry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(rootGroup.getGroupNumber())));
			final List<EntryGroupData> groups = new ArrayList<>();
			if (order.getRootGroups() != null)
			{
				groups.addAll(order.getRootGroups());
			}
			groups.add(rootGroup);
			order.setRootGroups(groups);
		}
		unwrapEntry(entry, order);
	}

	protected EntryGroupData createRootGroup(final OrderEntryData groupedOrderEntry, final AbstractOrderData order)
	{
		final EntryGroupData rootGroup = new EntryGroupData();
		rootGroup.setGroupNumber(Integer.valueOf(getEntryGroupUtils().findMaxGroupNumber(order.getRootGroups()) + 1));
		rootGroup.setGroupType(GroupType.STANDALONE);
		rootGroup.setChildren(new ArrayList<>());
		rootGroup.setOrderEntries(new ArrayList<>());
		rootGroup.getOrderEntries().add(groupedOrderEntry);
		return rootGroup;
	}

	protected void unwrapEntry(final OrderEntryData entry, final AbstractOrderData order)
	{
		try
		{
			final EntryGroupData childGroup = getEntryGroupUtils().getGroup(order, entry.getEntryGroupNumbers(),
					GroupType.STANDALONE);
			final Set<Integer> numbers = new HashSet<>(entry.getEntryGroupNumbers());
			numbers.remove(childGroup.getGroupNumber());
			entry.setEntryGroupNumbers(numbers);
			final List<OrderEntryData> entries = new ArrayList<>(childGroup.getOrderEntries());
			entries.remove(entry);
			childGroup.setOrderEntries(entries);
			if (entries.isEmpty())
			{
				order.getRootGroups().remove(childGroup);
				childGroup.setParent(null);
				childGroup.setRootGroup(null);
			}
		}
		catch (final IllegalArgumentException e) //NOSONAR
		{
			LOG.debug("Entry doesn't have entry group.", e);
		}
	}

	protected PriceData buildPrice(final PriceData base, final BigDecimal amount)
	{
		return getPriceDataFactory().create(base.getPriceType(), amount, base.getCurrencyIso());
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected CommerceEntryGroupUtils getEntryGroupUtils()
	{
		return entryGroupUtils;
	}

	@Required
	public void setEntryGroupUtils(final CommerceEntryGroupUtils entryGroupUtils)
	{
		this.entryGroupUtils = entryGroupUtils;
	}
}
