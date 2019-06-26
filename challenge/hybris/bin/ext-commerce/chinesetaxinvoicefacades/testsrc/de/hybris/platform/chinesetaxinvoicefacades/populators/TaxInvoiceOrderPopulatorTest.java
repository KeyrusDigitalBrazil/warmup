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
package de.hybris.platform.chinesetaxinvoicefacades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceCategory;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceRecipientType;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class TaxInvoiceOrderPopulatorTest
{
	private AbstractPopulatingConverter<OrderModel, OrderData> taxInvoiceConverter;
	private TaxInvoiceOrderPopulator taxInvoiceOrderPopulator;

	@Before
	public void setUp()
	{
		taxInvoiceOrderPopulator = new TaxInvoiceOrderPopulator();
		taxInvoiceConverter = new ConverterFactory<OrderModel, OrderData, TaxInvoiceOrderPopulator>().create(OrderData.class,
				taxInvoiceOrderPopulator);

	}

	@Test
	public void testTaxInvoiceModelNull()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		given(orderModel.getTaxInvoice()).willReturn(null);
		final OrderData orderData = taxInvoiceConverter.convert(orderModel);
		Assert.assertNull(orderData.getTaxInvoice());
	}

	@Test
	public void testTaxInvoiceModelNotNull()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		final PK pk = PK.parse("123");
		final TaxInvoiceModel taxInvoiceModel = mock(TaxInvoiceModel.class);
		given(orderModel.getTaxInvoice()).willReturn(taxInvoiceModel);
		given(taxInvoiceModel.getPk()).willReturn(pk);
		given(taxInvoiceModel.getCategory()).willReturn(InvoiceCategory.GENERAL);
		given(taxInvoiceModel.getRecipient()).willReturn("testRecipient");
		given(taxInvoiceModel.getRecipientType()).willReturn(InvoiceRecipientType.INDIVIDUAL);
		final OrderData orderData = taxInvoiceConverter.convert(orderModel);
		Assert.assertNotNull(orderData.getTaxInvoice());
		Assert.assertEquals(InvoiceCategory.GENERAL.getCode(), orderData.getTaxInvoice().getCategory());
		Assert.assertEquals("testRecipient", orderData.getTaxInvoice().getRecipient());
		Assert.assertEquals(InvoiceRecipientType.INDIVIDUAL.getCode(), orderData.getTaxInvoice().getRecipientType());
	}
}
