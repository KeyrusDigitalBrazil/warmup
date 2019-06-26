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
package de.hybris.platform.sap.sapinvoiceaddon.facade;

import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoiceaddon.exception.SapInvoiceException;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;



/**
 * B2BInvoiceFacade interface
 */
public interface B2BInvoiceFacade
{

	/**
	 * Gets order for invoiceDocumentNumber
	 * @param invoiceDocumentNumber invoice Document Number
	 * @return byte array of PDF document
	 * @throws SapInvoiceException exception
	 */
	public abstract SapB2BDocumentModel getOrderForCode(String invoiceDocumentNumber) throws SapInvoiceException;
	
	/**
	 * Convert Invoice Data
     * @param invoice SapB2BDocumentModel
     * @return B2BDocumentData for given invoice
     * @throws SapInvoiceException exception
     */
	public abstract B2BDocumentData convertInvoiceData(SapB2BDocumentModel invoice) throws SapInvoiceException;


}
