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
package de.hybris.platform.marketplaceservices.vendor.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.strategies.impl.DefaultVendorOrderExportStrategy;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;


@UnitTest
public class DefaultVendorOrderExportStrategyTest
{
	private static final String VENDOR_CODE = "code1";
	private static final String CONSIGNMENT1_CODE = "a10001000";
	private static final String CONSIGNMENT2_CODE = "a10001001";
	private static final String ORDER_CODE = "10001001";
	private static final String VENDOR_SKU = "11111";
	private static final Date date = new Date(20100101);
	private static final String UNIT_CODE_TYPE = "pieces";
			
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private DefaultVendorOrderExportStrategy exportVendorOrderStrategy;

	@Mock
	private VendorDao vendorDao;

	@Before
	public void setUp() throws IOException
	{
		MockitoAnnotations.initMocks(this);
		exportVendorOrderStrategy = new DefaultVendorOrderExportStrategy();
		exportVendorOrderStrategy.setExportDataBaseDirectory(tempFolder.getRoot().getAbsolutePath());
		
		CountryModel country = new CountryModel();
		country.setName("China", Locale.ENGLISH);
		country.setIsocode("zh");
		
		UnitModel unit = new UnitModel();
		unit.setCode(UNIT_CODE_TYPE);
		unit.setUnitType(UNIT_CODE_TYPE);
		unit.setName("Mock Unit", Locale.ENGLISH);
		
		CurrencyModel currency = new CurrencyModel();
		currency.setName("USD", new Locale("en"));
		currency.setSymbol("$");
		currency.setIsocode("en");
		
		AddressModel address = new AddressModel();
		address.setStreetname("street");
		address.setStreetnumber("number");
		address.setTown("town");
		address.setPostalcode("000000");
		address.setCountry(country);
		
		UserModel user = new UserModel();
		user.setName("user");
		
		OrderModel order = new OrderModel();
		order.setDeliveryAddress(address);
		order.setUser(user);
		order.setCode(ORDER_CODE);
		order.setDate(date);
		order.setCurrency(currency);
		
		ProductModel product = new ProductModel();
		product.setVendorSku(VENDOR_SKU);
		
		OrderEntryModel orderentry = new OrderEntryModel();
		orderentry.setUnit(unit);
		orderentry.setOrder(order);
		orderentry.setProduct(product);
		orderentry.setQuantity(1L);
		orderentry.setTotalPrice(19.99d);
		
		DeliveryModeModel deliverymode = new DeliveryModeModel();
		deliverymode.setName("Standard Delivery", Locale.ENGLISH);
		deliverymode.setCode("standard-gross");
		
		ConsignmentModel consignment1 = new ConsignmentModel();
		consignment1.setCode(CONSIGNMENT1_CODE);
		consignment1.setStatus(ConsignmentStatus.WAITING);
		consignment1.setDeliveryMode(deliverymode);
		
		ConsignmentModel consignment2 = new ConsignmentModel();
		consignment2.setCode(CONSIGNMENT2_CODE);
		consignment2.setStatus(ConsignmentStatus.WAITING);
		consignment2.setDeliveryMode(deliverymode);
		
		ConsignmentEntryModel consignmententry1 = new ConsignmentEntryModel();
		consignmententry1.setOrderEntry(orderentry);
		consignmententry1.setConsignment(consignment1);

		ConsignmentEntryModel consignmententry2 = new ConsignmentEntryModel();
		consignmententry2.setOrderEntry(orderentry);
		consignmententry2.setConsignment(consignment2);
		
		final List<ConsignmentEntryModel> consignmententries = new ArrayList<ConsignmentEntryModel>();
		consignmententries.add(consignmententry1);
		consignmententries.add(consignmententry2);

		Mockito.when(vendorDao.findPendingConsignmentEntryForVendor(VENDOR_CODE)).thenReturn(consignmententries);
		exportVendorOrderStrategy.setVendorDao(vendorDao);
	}

	@Test
	public void testReadyToExportOrdersForVendor() throws IOException
	{
		assertFalse(exportVendorOrderStrategy.readyToExportOrdersForVendor(VENDOR_CODE));
		tempFolder.newFolder(VENDOR_CODE);
		assertTrue(exportVendorOrderStrategy.readyToExportOrdersForVendor(VENDOR_CODE));
	}

	@Test
	public void testExportOrdersForVendor() throws IOException
	{
		final File folder = tempFolder.newFolder(VENDOR_CODE);
		exportVendorOrderStrategy.exportOrdersForVendor(VENDOR_CODE);
		final File[] files = folder.listFiles();
		assertEquals(files.length, 1);
		
		final String expectresult1 = "ConsignmentCode,OrderCode,Date,VendorSku,Quantity,TotalPrice,User,DeliveryMode,DeliveryAddress,PaymentMode,PaymentStatus,Status";
		final String expectresult2 = "a10001000,10001001,"+ date.toString() + ",11111,1pieces,$19.99,user,Standard Delivery,street-number-town-000000-China,,,null";
		final String expectresult3 = "a10001001,10001001,"+ date.toString() + ",11111,1pieces,$19.99,user,Standard Delivery,street-number-town-000000-China,,,null";
		final List<String> lines = FileUtils.readLines(files[0]);

		assertEquals(expectresult1, lines.get(0));
		assertEquals(expectresult2, lines.get(1));
		assertEquals(expectresult3, lines.get(2));
	}
}
