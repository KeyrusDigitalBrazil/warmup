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

import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class GroupOrderEntryPopulatorTest
{
	public static final BigDecimal MIN_PRICE_RANGE_TEST = valueOf(4L);
	public static final BigDecimal MAX_PRICE_RANGE_TEST = valueOf(6L);
	public static final String BASE_PRODUCT_2_PRICE_RANGE_TEST = "baseProduct2";
	public static final BigDecimal TOTAL_PRICE_RANGE_TEST = valueOf(15L);

	@Mock
	ProductService productService;
	@Mock
	CommerceEntryGroupUtils entryGroupUtils;

	PriceDataFactory priceDataFactory = new PriceDataFactory()
	{

		@Override
		public PriceData create(final PriceDataType priceType, final BigDecimal value, final String currencyIso)
		{
			final PriceData priceData = new PriceData();

			priceData.setPriceType(priceType);
			priceData.setValue(value);
			priceData.setCurrencyIso(currencyIso);

			return priceData;
		}

		@Override
		public PriceData create(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currency)
		{
			return null;
		}
	};

	@InjectMocks
	protected GroupOrderEntryPopulator populator = new GroupOrderEntryPopulator();


	@Before
	public void setUp()
	{

		MockitoAnnotations.initMocks(this);

		when(productService.getProductForCode(anyString())).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocationOnMock) throws Throwable
			{

				final String code = (String) invocationOnMock.getArguments()[0];
				final ProductModel productModel = mock(ProductModel.class);
				when(productModel.getCode()).thenReturn(code);
				when(productModel.getName()).thenReturn("Name " + code);
				when(productModel.getDescription()).thenReturn("Description " + code);

				return productModel;
			}
		});

		populator.setPriceDataFactory(priceDataFactory);
	}

	@Test
	public void shouldGroupEntries()
	{
		final AbstractOrderData order = new AbstractOrderData();
		final ArrayList<OrderEntryData> originalEntries = new ArrayList<>();
		order.setEntries(originalEntries);

		originalEntries.add(createOrderEntry("productCode1", "baseProduct1", valueOf(1L), valueOf(1L)));
		originalEntries.add(createOrderEntry("productCode2", "baseProduct1", valueOf(2L), valueOf(5L)));
		originalEntries.add(createOrderEntry("productCode3", null, valueOf(3L), valueOf(5L)));
		originalEntries.add(createOrderEntry("productCode4", BASE_PRODUCT_2_PRICE_RANGE_TEST, MIN_PRICE_RANGE_TEST,
				TOTAL_PRICE_RANGE_TEST));
		originalEntries.add(createOrderEntry("productCode5", BASE_PRODUCT_2_PRICE_RANGE_TEST, MIN_PRICE_RANGE_TEST.add(BigDecimal.ONE),
				TOTAL_PRICE_RANGE_TEST));
		originalEntries.add(createOrderEntry("productCode6", BASE_PRODUCT_2_PRICE_RANGE_TEST, MAX_PRICE_RANGE_TEST,
				TOTAL_PRICE_RANGE_TEST));
		originalEntries.add(createOrderEntry("productCode7", "baseProduct3", valueOf(7L), valueOf(7L)));
		originalEntries.add(createOrderEntry("productCode8", null, valueOf(5L), valueOf(5L))); //Product without parent with multiple entries in the cart
		originalEntries.add(createOrderEntry("productCode8", null, valueOf(7L), valueOf(5L))); //Product without parent with multiple entries in the cart
		final OrderEntryData notGroupedEntry = createOrderEntry("productCode9", "nonMultid", MAX_PRICE_RANGE_TEST,
				TOTAL_PRICE_RANGE_TEST);
		notGroupedEntry.getProduct().getBaseOptions().get(0).setVariantType("AnotherTypeOfVariant");
		originalEntries.add(notGroupedEntry);
		final EntryGroupData group = new EntryGroupData();
		group.setChildren(Collections.emptyList());
		group.setOrderEntries(Collections.emptyList());
		when(entryGroupUtils.getGroup(any(), any(), any())).thenReturn(group);

		populator.populate(mock(AbstractOrderModel.class), order);

		assertThat(Integer.valueOf(order.getEntries().size()), is(Integer.valueOf(7)));

		for (final OrderEntryData parentEntry : order.getEntries())
		{
			validatePriceRange(parentEntry);

			if (parentEntry.getEntries() != null)
			{
				long totalQuantities = 0;
				int totalPrice = 0;
				for (final OrderEntryData childEntry : parentEntry.getEntries())
				{
					final OrderEntryData firstEntry = parentEntry.getEntries().get(0);
					assertThat(childEntry.getProduct().getBaseProduct(), is(firstEntry.getProduct().getBaseProduct()));

					totalQuantities += childEntry.getQuantity().longValue();
					totalPrice += childEntry.getTotalPrice().getValue().intValue();
				}

				assertTrue(totalQuantities > 0);
				assertTrue(totalPrice > 0);

				assertThat(parentEntry.getQuantity(), is(Long.valueOf(totalQuantities)));
				assertThat(Integer.valueOf(parentEntry.getTotalPrice().getValue().intValue()), is(Integer.valueOf(totalPrice)));

				final OrderEntryData firstEntry = parentEntry.getEntries().get(0);
				validateProductInfo(parentEntry, firstEntry);
			}
		}
	}

	/**
	 * Given:
	 * <code>
	 * entry1(multid=true,baseProduct=A,group=standalone,children=[])
	 * entry2(multid=true,baseProduct=A,group=standalone,children=[])
	 * entry3(multid=true,baseProduct=B,group=standalone,children=[])
	 * </code>
	 * Result:
	 * <code>
	 * entry4(fake=true,baseProduct=A,group=standalone,children=[entry1(group=null),entry2(group=null)])
	 * entry5(fake=true,baseProduct=B,group=standalone,children=[entry3(group=null)])
	 * </code>
	 */
	@Test
	public void shouldGroupEntriesWithEntryGroups()
	{
		final AbstractOrderData order = new AbstractOrderData();

		final OrderEntryData entryA1 = createOrderEntry("A1", "A", valueOf(1L), valueOf(1L));
		final OrderEntryData entryA2 = createOrderEntry("A2", "A", valueOf(2L), valueOf(5L));
		final OrderEntryData entryB1 = createOrderEntry("B1", "B", valueOf(7L), valueOf(5L));
		order.setEntries(Arrays.asList(entryA1, entryA2, entryB1));

		final EntryGroupData groupA1 = createEntryGroupData(entryA1, 1);
		final EntryGroupData groupA2 = createEntryGroupData(entryA2, 2);
		final EntryGroupData groupB1 = createEntryGroupData(entryB1, 3);
		order.setRootGroups(Arrays.asList(groupA1, groupA2, groupB1));

		populator.populate(mock(AbstractOrderModel.class), order);

		assertThat(order.getEntries(), containsInAnyOrder(
				allOf(
						hasProperty("entryNumber", is(Integer.valueOf(-1))),
						hasProperty("product", hasProperty("code", is("A"))),
						hasProperty("entries", containsInAnyOrder(entryA1, entryA2))),
				allOf(hasProperty("entryNumber", is(Integer.valueOf(-1))),
						hasProperty("product", hasProperty("code", is("B"))),
						hasProperty("entries", contains(entryB1)))
		));
		assertThat(order.getRootGroups(), iterableWithSize(2));
	}

	protected void validateProductInfo(final OrderEntryData parentEntry, final OrderEntryData firstEntry)
	{
		assertThat(parentEntry.getBasePrice(), is(firstEntry.getBasePrice()));
		assertThat(parentEntry.getProduct().getCode(), is(firstEntry.getProduct().getBaseProduct()));
		assertThat(parentEntry.getProduct().getImages(), is(firstEntry.getProduct().getImages()));
		assertThat(parentEntry.getProduct().getUrl(), is(firstEntry.getProduct().getUrl()));
	}

	protected void validatePriceRange(final OrderEntryData parentEntry)
	{
		if (BASE_PRODUCT_2_PRICE_RANGE_TEST.equals(parentEntry.getProduct().getBaseProduct()))
		{
			final BigDecimal actualMinValue = parentEntry.getProduct().getPriceRange().getMinPrice().getValue();
			final BigDecimal actualMaxValue = parentEntry.getProduct().getPriceRange().getMaxPrice().getValue();

			assertThat(actualMinValue, is(MIN_PRICE_RANGE_TEST));
			assertThat(actualMaxValue, is(MAX_PRICE_RANGE_TEST));
		}
	}

	public OrderEntryData createOrderEntry(final String productCode, final String baseProductCode, final BigDecimal price,
			final BigDecimal totalPrice)
	{
		final OrderEntryData entry = new OrderEntryData();

		final ProductData product = new ProductData();
		product.setCode(productCode);
		product.setBaseProduct(baseProductCode);
		product.setBaseOptions(new ArrayList<BaseOptionData>());
		if (baseProductCode != null)
		{
			final BaseOptionData baseOption = new BaseOptionData();
			baseOption.setVariantType(GroupOrderEntryPopulator.VARIANT_TYPE);
			product.getBaseOptions().add(baseOption);
		}
		product.setImages(new ArrayList<ImageData>());

		product.setMultidimensional(Boolean.valueOf(baseProductCode != null));

		entry.setProduct(product);

		entry.setBasePrice(new PriceData());
		entry.getBasePrice().setValue(price);
		entry.getBasePrice().setCurrencyIso("USD");
		entry.getBasePrice().setPriceType(PriceDataType.BUY);

		entry.setTotalPrice(new PriceData());
		entry.getTotalPrice().setCurrencyIso("USD");
		entry.getTotalPrice().setValue(totalPrice);
		entry.getTotalPrice().setPriceType(PriceDataType.BUY);

		entry.setQuantity(Long.valueOf(1L));
		entry.setEntryGroupNumbers(Collections.emptyList());

		return entry;
	}

	protected EntryGroupData createEntryGroupData(final OrderEntryData entry, final int number)
	{
		final EntryGroupData group = new EntryGroupData();
		group.setChildren(Collections.emptyList());
		group.setGroupType(GroupType.STANDALONE);
		group.setGroupNumber(Integer.valueOf(number));
		group.setOrderEntries(new ArrayList<>());
		group.setOrderEntries(Collections.singletonList(entry));
		entry.setEntryGroupNumbers(Collections.singletonList(Integer.valueOf(number)));
		when(entryGroupUtils.getGroup(any(), (Collection<Integer>) argThat(contains(Integer.valueOf(number))), any())).thenReturn(group);
		when(entryGroupUtils.getGroup(any(), eq(Integer.valueOf(number)))).thenReturn(group);
		return group;
	}

}
