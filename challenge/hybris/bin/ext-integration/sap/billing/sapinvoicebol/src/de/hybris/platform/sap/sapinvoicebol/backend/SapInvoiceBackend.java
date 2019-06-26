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
package de.hybris.platform.sap.sapinvoicebol.backend;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 *
 */
public interface SapInvoiceBackend extends BackendBusinessObject
{
	/**
	 * get the invoice in byte format
	 *
	 * @param billingDocNumber
	 * @return byte array
	 * @throws BackendException
	 */
	public byte[] getInvoiceInByte(final String billingDocNumber) throws BackendException;
}
