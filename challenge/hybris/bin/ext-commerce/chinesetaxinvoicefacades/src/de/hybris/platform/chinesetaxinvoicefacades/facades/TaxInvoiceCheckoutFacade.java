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
package de.hybris.platform.chinesetaxinvoicefacades.facades;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;


/**
 * Implementation for {@link AcceleratorCheckoutFacade}. Delivers main functionality for chinese tax invoice checkout.
 */
public interface TaxInvoiceCheckoutFacade extends AcceleratorCheckoutFacade
{

	/**
	 * Save TaxInvoice in AbstractOrderModel.
	 *
	 * @param data
	 *           TaxInvoice data.
	 * @return true if set TaxInvoiceData successfully, false otherwise
	 */
	boolean setTaxInvoice(TaxInvoiceData data);

	/**
	 * Remove a TaxInvoiceModel for PK.
	 *
	 * @param code
	 *           TaxInvoice code(PK)
	 * @return remove TaxInvoiceData successfully or not
	 */
	boolean removeTaxInvoice(String code);

	/**
	 * Check if the current CartModel has an TaxInvoice.
	 *
	 * @return true if the current CartModel has an tax invoice, false otherwise
	 */
	boolean hasTaxInvoice();
}
