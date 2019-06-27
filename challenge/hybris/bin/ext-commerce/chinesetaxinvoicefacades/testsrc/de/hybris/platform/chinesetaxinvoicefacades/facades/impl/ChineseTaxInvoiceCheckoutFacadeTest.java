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
package de.hybris.platform.chinesetaxinvoicefacades.facades.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.chinesetaxinvoiceservices.services.TaxInvoiceService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseTaxInvoiceCheckoutFacadeTest
{

	private ChineseTaxInvoiceCheckoutFacade taxInvoiceCheckoutFacade;

	@Mock
	private TaxInvoiceService taxInvoiceService;
	@Mock
	private Converter<TaxInvoiceData, TaxInvoiceModel> taxInvoiceReverseConverter;
	@Mock
	private CartService cartService;
	@Mock
	private ModelService modelService;
	@Mock
	private CartFacade cartFacade;

	@Mock
	private CartModel cartModel;
	@Mock
	private CartData cartData;
	@Mock
	private TaxInvoiceModel taxInvoiceModel;
	@Mock
	private TaxInvoiceData taxInvoiceData;

	private static final String INVOICE_CODE = "000001";


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		taxInvoiceCheckoutFacade = Mockito.spy(new ChineseTaxInvoiceCheckoutFacade());
		taxInvoiceCheckoutFacade.setTaxInvoiceService(taxInvoiceService);
		taxInvoiceCheckoutFacade.setTaxInvoiceReverseConverter(taxInvoiceReverseConverter);
		taxInvoiceCheckoutFacade.setCartService(cartService);
		taxInvoiceCheckoutFacade.setModelService(modelService);
		taxInvoiceCheckoutFacade.setCartFacade(cartFacade);
	}

	@Test
	public void testSetTaxInvoice()
	{
		given(taxInvoiceCheckoutFacade.hasCheckoutCart()).willReturn(true);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(taxInvoiceService.createTaxInvoice(taxInvoiceData)).willReturn(taxInvoiceModel);
		doNothing().when(cartModel).setTaxInvoice(taxInvoiceModel);

		final boolean result = taxInvoiceCheckoutFacade.setTaxInvoice(taxInvoiceData);
		verify(modelService, times(1)).save(cartModel);
		verify(modelService, times(1)).refresh(cartModel);
		assertTrue(result);
	}

	@Test
	public void testRemoveTaxInvoice()
	{
		given(taxInvoiceService.getTaxInvoiceForCode(INVOICE_CODE)).willReturn(taxInvoiceModel);
		doNothing().when(modelService).remove(taxInvoiceModel);
		given(taxInvoiceCheckoutFacade.hasCheckoutCart()).willReturn(true);
		given(cartService.getSessionCart()).willReturn(cartModel);
		doNothing().when(cartModel).setTaxInvoice(null);

		final boolean result = taxInvoiceCheckoutFacade.removeTaxInvoice(INVOICE_CODE);

		verify(modelService, times(1)).save(cartModel);
		verify(modelService, times(1)).refresh(cartModel);
		assertTrue(result);
	}
}
