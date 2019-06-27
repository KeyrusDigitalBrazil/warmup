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
package com.sap.hybris.sapomsreturnprocess.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.sap.hybris.sapomsreturnprocess.outbound.impl.OmsDefaultReturnOrderContributor;


/**
 *
 */
public class OmsDefaultReturnOrderContributorTest
{

	/**
	 *
	 */
	private static final String DELIVERY_MODE = "bla";
	private static final String CURRENCY_ISO_CODE = "CurrencyIsoCode";
	private static final String CODE = "Code";

	private OmsDefaultReturnOrderContributor returnContributor;
	private ReturnRequestModel returnRequest;
	private Date date;

	@Before
	public void setUp()
	{
		returnContributor = new OmsDefaultReturnOrderContributor();
		CurrencyModel currency;
		currency = new CurrencyModel();
		currency.setIsocode(CURRENCY_ISO_CODE);

		final OrderModel preceedingDoc = new OrderModel();
		returnRequest = new ReturnRequestModel();

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setUid("STORE123");
		preceedingDoc.setStore(baseStoreModel);
		preceedingDoc.setCurrency(currency);
		returnRequest.setOrder(preceedingDoc);

		returnRequest.setCode(CODE);
		date = new Date();
		returnRequest.setCreationtime(date);
	}

	@Test
	public void testGetColumns()
	{
		final Set<String> columns = returnContributor.getColumns();

		assertTrue(columns.contains(OrderCsvColumns.ORDER_ID));
		assertTrue(columns.contains(OrderCsvColumns.DATE));
		assertTrue(columns.contains(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE));
		assertTrue(columns.contains(OrderCsvColumns.PAYMENT_MODE));
		assertTrue(columns.contains(OrderCsvColumns.DELIVERY_MODE));
	}

	@Test
	public void testCreateRow()
	{
		final DeliveryModeModel deliveryMode = new DeliveryModeModel();
		deliveryMode.setCode(DELIVERY_MODE);
		returnRequest.getOrder().setDeliveryMode(deliveryMode);

		final List<Map<String, Object>> rows = returnContributor.createRows(returnRequest);

		final Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(date, row.get(OrderCsvColumns.DATE));
		assertEquals(CURRENCY_ISO_CODE, row.get(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE));
		assertEquals(DELIVERY_MODE, row.get(OrderCsvColumns.DELIVERY_MODE));
		assertEquals("STORE123", row.get(OrderCsvColumns.BASE_STORE));
	}

	@Test
	public void testCreateRowWithShippingConditionNull()
	{

		final List<Map<String, Object>> rows = returnContributor.createRows(returnRequest);

		final Map<String, Object> row = rows.get(0);
		assertEquals(CODE, row.get(OrderCsvColumns.ORDER_ID));
		assertEquals(date, row.get(OrderCsvColumns.DATE));
		assertEquals(CURRENCY_ISO_CODE, row.get(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE));
	}

}
