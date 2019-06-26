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
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Text Converter class for converting backend texts from ITF format into plain text format to be used within hybris.
 */
public interface TextConverterBase
{
	/**
	 * Removes all meta text elements (bold, underline, etc.) out of the long text value, provided by the knowledge base.
	 *
	 * @param formattedText
	 *           The text to be cleansed
	 * @return The converted text, which will be shown in the web frontend
	 */
	String convertLongText(String formattedText);
}
