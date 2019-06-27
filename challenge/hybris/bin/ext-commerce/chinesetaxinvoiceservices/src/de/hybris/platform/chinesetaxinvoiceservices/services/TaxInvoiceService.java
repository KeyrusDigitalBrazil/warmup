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
package de.hybris.platform.chinesetaxinvoiceservices.services;

import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;


/**
 * Tax invoice facade interface. Its main purpose is to retrieve chinese tax invoice related DTOs using existing
 * services.
 */
public interface TaxInvoiceService
{

	/**
	 * Query TaxInvoiceModel for PK.
	 *
	 * @param code
	 *           PK of tax invoice
	 * @return tax invoice model
	 */
	TaxInvoiceModel getTaxInvoiceForCode(String code);

	/**
	 * To create a TaxInvoiceModel use a TaxInvoiceData
	 *
	 * @param data
	 *           A DTO instance of TaxInvoiceData.
	 * @return tax invoice model
	 */
	TaxInvoiceModel createTaxInvoice(TaxInvoiceData data);
}
