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

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoicefacades.facades.TaxInvoiceCheckoutFacade;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.chinesetaxinvoiceservices.services.TaxInvoiceService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation for {@link ChineseCustomerAccountService}. Its main purpose is to retrieve chinese tax invoice checkout related DTOs using existing services.
 */
public class ChineseTaxInvoiceCheckoutFacade extends DefaultAcceleratorCheckoutFacade implements TaxInvoiceCheckoutFacade
{

	private TaxInvoiceService taxInvoiceService;
	private Converter<TaxInvoiceData, TaxInvoiceModel> taxInvoiceReverseConverter;

	@Override
	public boolean setTaxInvoice(final TaxInvoiceData data)
	{

		final CartModel cartModel = getCart();

		if (cartModel != null)
		{

			final TaxInvoiceModel taxInvoiceModel = taxInvoiceService.createTaxInvoice(data);
			getTaxInvoiceReverseConverter().convert(data, taxInvoiceModel);
			getModelService().save(taxInvoiceModel);
			getModelService().refresh(taxInvoiceModel);

			cartModel.setTaxInvoice(taxInvoiceModel);
			getModelService().save(cartModel);
			getModelService().refresh(cartModel);

			return true;
		}

		return false;
	}

	@Override
	public boolean removeTaxInvoice(final String code)
	{

		final TaxInvoiceModel invoiceModel = taxInvoiceService.getTaxInvoiceForCode(code);

		if (invoiceModel != null)
		{

			getModelService().remove(invoiceModel);

			final CartModel cartModel = getCart();
			cartModel.setTaxInvoice(null);
			getModelService().save(cartModel);
			getModelService().refresh(cartModel);

			return true;
		}

		return false;
	}

	@Override
	public boolean hasTaxInvoice()
	{

		return getCart().getTaxInvoice() != null;
	}

	protected TaxInvoiceService getTaxInvoiceService()
	{
		return taxInvoiceService;
	}

	@Required
	public void setTaxInvoiceService(final TaxInvoiceService taxInvoiceService)
	{
		this.taxInvoiceService = taxInvoiceService;
	}

	protected Converter<TaxInvoiceData, TaxInvoiceModel> getTaxInvoiceReverseConverter()
	{
		return taxInvoiceReverseConverter;
	}

	@Required
	public void setTaxInvoiceReverseConverter(final Converter<TaxInvoiceData, TaxInvoiceModel> taxInvoiceReverseConverter)
	{
		this.taxInvoiceReverseConverter = taxInvoiceReverseConverter;
	}
}
