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
package de.hybris.platform.marketplacefacades.cart.converters.populator;

import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.converters.populator.GroupOrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.BaseOptionData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.strategies.VendorOriginalEntryGroupDisplayStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class GroupVendorOrderEntryPopulatorTest
{
	public static final BigDecimal TEST_PRICE = valueOf(10L);
	private GroupVendorOrderEntryPopulator groupVendorOrderEntryPopulator;

	private static final String VENDOR1_CODE = "testvendor1";
	private static final String VENDOR2_CODE = "testvendor2";

	@Mock
	private VendorOriginalEntryGroupDisplayStrategy vendorOriginalEntryGroupDisplayStrategy;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		groupVendorOrderEntryPopulator = new GroupVendorOrderEntryPopulator();
		groupVendorOrderEntryPopulator.setVendorOriginalEntryGroupDisplayStrategy(vendorOriginalEntryGroupDisplayStrategy);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGroupEntriesWithEntryGroups()
	{
		given(vendorOriginalEntryGroupDisplayStrategy.shouldDisplayOriginalEntryGroup()).willReturn(false);

		final VendorData vendor1 = createVendor(VENDOR1_CODE);
		final VendorData vendor2 = createVendor(VENDOR2_CODE);

		final AbstractOrderData order = new AbstractOrderData();

		final OrderEntryData entryA = createOrderEntry("A", vendor1);
		entryA.setEntryNumber(1);
		final OrderEntryData entryB = createOrderEntry("B", vendor1);
		entryB.setEntryNumber(5);
		final OrderEntryData entryC = createOrderEntry("C", vendor2);
		entryC.setEntryNumber(7);
		order.setEntries(Arrays.asList(entryA, entryB, entryC));

		final AbstractOrderModel source = new AbstractOrderModel();
		final AbstractOrderEntryModel dummyEntry = new AbstractOrderEntryModel();
		dummyEntry.setEntryNumber(100);
		source.setEntries(Collections.singletonList(dummyEntry));

		groupVendorOrderEntryPopulator.populate(source, order);

		final List<EntryGroupData> vendorRoots = order.getRootGroups().stream()
				.filter(group -> group.getGroupType().equals(GroupType.VENDOR)).collect(Collectors.toList());
		assertNotNull(vendorRoots);
		final EntryGroupData vendorRootGroup = vendorRoots.get(0);
		assertThat(vendorRootGroup.getChildren(), iterableWithSize(2));
		assertThat(
				vendorRootGroup.getChildren(),
				containsInAnyOrder(
						allOf(hasProperty("groupType", is(GroupType.VENDOR)), hasProperty("externalReferenceId", is(VENDOR1_CODE)),
								hasProperty("orderEntries", contains(entryA, entryB))),
						allOf(hasProperty("groupType", is(GroupType.VENDOR)), hasProperty("externalReferenceId", is(VENDOR2_CODE)),
								hasProperty("orderEntries", contains(entryC)))));
	}

	private VendorData createVendor(final String vendorCode)
	{
		final VendorData vendor = new VendorData();
		vendor.setCode(vendorCode);
		vendor.setName(vendorCode);
		return vendor;
	}

	private OrderEntryData createOrderEntry(final String productCode, final VendorData vendor)
	{
		final OrderEntryData entry = new OrderEntryData();

		final ProductData product = new ProductData();
		product.setCode(productCode);
		product.setBaseOptions(new ArrayList<BaseOptionData>());
		if (productCode != null)
		{
			final BaseOptionData baseOption = new BaseOptionData();
			baseOption.setVariantType(GroupOrderEntryPopulator.VARIANT_TYPE);
			product.getBaseOptions().add(baseOption);
		}
		product.setImages(new ArrayList<ImageData>());

		product.setMultidimensional(false);
		product.setVendor(vendor);

		entry.setProduct(product);

		entry.setBasePrice(new PriceData());
		entry.getBasePrice().setValue(TEST_PRICE);
		entry.getBasePrice().setCurrencyIso("USD");
		entry.getBasePrice().setPriceType(PriceDataType.BUY);

		entry.setTotalPrice(new PriceData());
		entry.getTotalPrice().setCurrencyIso("USD");
		entry.getTotalPrice().setValue(TEST_PRICE);
		entry.getTotalPrice().setPriceType(PriceDataType.BUY);

		entry.setQuantity(Long.valueOf(1L));
		entry.setEntryGroupNumbers(Collections.emptyList());

		return entry;
	}
}
