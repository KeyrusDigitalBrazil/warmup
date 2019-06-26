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
package de.hybris.platform.sap.sapinvoicefacades.facade;

import de.hybris.platform.sap.sapinvoicefacades.exception.UnableToRetrieveInvoiceException;




/**
 * InvoiceFacade interface
 */
public interface InvoiceFacade
{

	/**
	 * Method to generate Pdf
	 * @param billDocumentNumber number of bill in string format
	 * @return byte array of PDF document
	 * @throws UnableToRetrieveInvoiceException Exception
	 */
	byte[] generatePdf(String billDocumentNumber) throws UnableToRetrieveInvoiceException;
}
