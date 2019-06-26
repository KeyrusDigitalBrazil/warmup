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
package de.hybris.platform.chinesetaxinvoiceservices.services.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoiceservices.daos.TaxInvoiceDao;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseTaxInvoiceServiceTest
{

	private ChineseTaxInvoiceService service;

	@Mock
	private TaxInvoiceDao taxInvoiceDao;
	@Mock
	private ModelService modelService;
	@Mock
	private TaxInvoiceData invoiceData;
	@Mock
	private TaxInvoiceModel invoiceModel;

	private static final String INVOICE_CODE = "00001";

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		service = new ChineseTaxInvoiceService();
		service.setTaxInvoiceDao(taxInvoiceDao);
		service.setModelService(modelService);

		given(taxInvoiceDao.findInvoiceByCode(INVOICE_CODE)).willReturn(invoiceModel);
	}

	@Test
	public void testGetTaxInvoiceForCode()
	{
		final TaxInvoiceModel result = service.getTaxInvoiceForCode(INVOICE_CODE);
		Assert.assertEquals(invoiceModel, result);
	}

	@Test
	public void testCreateTaxInvoiceForExisting()
	{
		given(invoiceData.getId()).willReturn(INVOICE_CODE);
		given(service.getTaxInvoiceForCode(INVOICE_CODE)).willReturn(invoiceModel);
		final TaxInvoiceModel result = service.createTaxInvoice(invoiceData);
		Assert.assertEquals(invoiceModel, result);
	}

	@Test
	public void testCreateTaxInvoiceForNew()
	{
		given(invoiceData.getId()).willReturn(null);
		given(modelService.create(TaxInvoiceModel.class)).willReturn(invoiceModel);
		final TaxInvoiceModel result = service.createTaxInvoice(invoiceData);
		Assert.assertEquals(invoiceModel, result);
	}
}
