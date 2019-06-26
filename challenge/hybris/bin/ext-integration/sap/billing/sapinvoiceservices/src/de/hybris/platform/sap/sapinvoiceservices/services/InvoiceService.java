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
package de.hybris.platform.sap.sapinvoiceservices.services;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * InvoiceService interface
 */
public interface InvoiceService
{
        /**
         * Method to get PDF data
         * @param billDocumentNumber DocumentNumber of bill in string format
         * @return byte array of PDF document
         * @throws BackendException Backend Exception
         */
	public byte[] getPDFData(final String billDocumentNumber) throws BackendException;
}
