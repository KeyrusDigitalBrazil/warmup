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
package de.hybris.platform.sap.sapinvoicebol.businessobject;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;




/**
 *
 */
public interface SapInvoiceBO
{
	/**
	 * get byte for PDF for billingDOCNumber
	 *
	 * @param billingDocNumber
	 * @return byte of PDF for billingDOCNumber
	 */
	abstract byte[] getPDF(final String billingDocNumber) throws BackendException;
}
