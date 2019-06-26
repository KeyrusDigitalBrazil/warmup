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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Date;


/**
 * Helper for formatting characteristic values. The external format for UI Display is derived from the web-shops I18N
 * locale, which in turn depends on the browser's locale.
 */
public interface ValueFormatTranslator
{

	/**
	 * Converts a characteristic value into an internal format, which is used by the underlying API's.
	 *
	 * @param uiType UI type the characteristic value belongs to
	 * @param value  characteristic value in external format
	 * @return characteristic value in internal format
	 */
	String parse(UiType uiType, String value);


	/**
	 * Converts a characteristic value into an external format suitable for UI-Display.
	 *
	 * @param cstic characteristic the value belongs to
	 * @param value characteristic value in internal format
	 * @return characteristic value in external format
	 */
	String format(final CsticModel cstic, final String value);

	/**
	 * Checks the characteristic model whether it is either of type float or integer
	 *
	 * @param model characteristic to be checked
	 * @return true if characteristic is numeric
	 */
	boolean isNumericCsticType(final CsticModel model);

	/**
	 * Parses numerical value from UI to service
	 *
	 * @param value value to be parsed
	 * @return formatted numerical value
	 */
	String parseNumeric(final String value);

	/**
	 * Formats numerical value from service to UI
	 *
	 * @param value value to be parsed
	 * @return formatted numerical value
	 */
	String formatNumeric(String value);

	/**
	 * Format date value from service to UI
	 *
	 * @param date Date value which should be formatted
	 * @return the formatted date value
	 */
	String formatDate(Date date);
}
