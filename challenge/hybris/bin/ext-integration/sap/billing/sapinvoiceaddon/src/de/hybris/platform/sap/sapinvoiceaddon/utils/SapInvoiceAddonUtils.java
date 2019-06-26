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
package de.hybris.platform.sap.sapinvoiceaddon.utils;

import org.apache.commons.lang3.StringUtils;

public class SapInvoiceAddonUtils {
    
        private SapInvoiceAddonUtils(){
            //private constructor to hide public constructor
        }

	public static String filter(final String value) throws NumberFormatException {

		final String encodedInvoiceCode = StringUtils.isNotBlank(value) ? filterString(value) : value;
		boolean numberFormatCheck = false;
		if(null!=encodedInvoiceCode)
		{
		 numberFormatCheck = StringUtils.isNotBlank(encodedInvoiceCode) ? checkNumberFormat(encodedInvoiceCode): false;
		}
		if (numberFormatCheck) {
			return encodedInvoiceCode;
		} else {
			throw new NumberFormatException("Invoice code not valid");
		}
	}

	public static boolean checkNumberFormat(String encodedInvoiceCode) {
		String numbetRegex = "[0-9]+";
		return encodedInvoiceCode.matches(numbetRegex);
	}
	
	
	private static String filterString(final String value)
	{
		if (value == null)
		{
			return null;
		}
		String sanitized = value;
		sanitized = sanitized.replaceAll("eval\\((.*)\\)", "");
		sanitized = sanitized.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		return sanitized;
	}
}
