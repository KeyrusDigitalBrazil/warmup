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
package de.hybris.platform.chinesetaxinvoiceservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ChineseTaxInvoiceDaoTest extends ServicelayerTransactionalTest
{

	@Resource(name = "chineseTaxInvoiceDao")
	private ChineseTaxInvoiceDao taxInvoiceDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	private TaxInvoiceModel taxInvoice;
	private OrderModel order;
	private CustomerModel customer;
	private CurrencyModel currency;

	private static final String SERIAL_CODE = "00000000001";
	private static final String ORDER_CODE = "00000000002";
	private static final String CUSTOMER_UID = "test@hybris.com";
	private String invoiceCode;
	private static final String SERIAL_CODE_NULL = "";
	private static final String ORDER_CODE_NULL = "";

	@Before
	public void prepare()
	{
		taxInvoice = new TaxInvoiceModel();
		taxInvoice.setSerialCode(SERIAL_CODE);

		currency = new CurrencyModel();
		currency.setName("USD", new Locale("en"));
		currency.setSymbol("$");
		currency.setIsocode("en");

		order = new OrderModel();
		order.setCode(ORDER_CODE);
		order.setCurrency(currency);
		order.setDate(new Date());

		customer = new CustomerModel();
		customer.setUid(CUSTOMER_UID);

		order.setTaxInvoice(taxInvoice);
		order.setUser(customer);
		modelService.save(order);

		invoiceCode = taxInvoice.getPk().toString();
	}

	@Test
	public void testFindInvoiceByCode()
	{
		final TaxInvoiceModel result = taxInvoiceDao.findInvoiceByCode(invoiceCode);
		Assert.assertEquals(taxInvoice, result);
	}

	@Test
	public void testFindInvoiceBySerialCode()
	{
		final TaxInvoiceModel result = taxInvoiceDao.findInvoiceBySerialCode(SERIAL_CODE);
		Assert.assertNotNull(result);
		Assert.assertEquals(taxInvoice.getSerialCode(), result.getSerialCode());
		final TaxInvoiceModel result2 = taxInvoiceDao.findInvoiceBySerialCode(SERIAL_CODE_NULL);
		Assert.assertNull(result2);
	}

	@Test
	public void testFindInvoiceByOrder()
	{
		final TaxInvoiceModel result = taxInvoiceDao.findInvoiceByOrder(ORDER_CODE);
		Assert.assertEquals(taxInvoice, result);
		final TaxInvoiceModel result2 = taxInvoiceDao.findInvoiceByOrder(ORDER_CODE_NULL);
		Assert.assertNull(result2);
	}

	@Test
	public void testFindInvoicesByCustomer()
	{
		final List<TaxInvoiceModel> invoices = taxInvoiceDao.findInvoicesByCustomer(customer);
		Assert.assertEquals(1, invoices.size());
		Assert.assertEquals(taxInvoice, invoices.get(0));
	}
}
