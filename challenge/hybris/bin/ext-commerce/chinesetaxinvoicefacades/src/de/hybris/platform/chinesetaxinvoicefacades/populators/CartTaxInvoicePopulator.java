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

import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceCategory;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceRecipientType;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;

import org.springframework.stereotype.Component;


@Component("cartTaxInvoicePopulator")
public class CartTaxInvoicePopulator implements Populator<CartModel, CartData>
{

	@Override
	public void populate(final CartModel source, final CartData target)
	{

		final TaxInvoiceModel model = source.getTaxInvoice();
		if (model != null)
		{
			final TaxInvoiceData data = new TaxInvoiceData();
			data.setId(model.getPk().toString());
			data.setCategory(model.getCategory() == null ? InvoiceCategory.GENERAL.getCode() : model.getCategory().getCode());
			data.setRecipient(model.getRecipient());
			data.setRecipientType(model.getRecipientType() == null ? InvoiceRecipientType.INDIVIDUAL.getCode() : model
					.getRecipientType().getCode());

			target.setTaxInvoice(data);
		}
	}
}
