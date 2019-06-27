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
package de.hybris.platform.sap.productconfig.runtime.cps;

/**
 * Parses conflict texts from configuration engine into its display ready form
 */
public interface CPSConflictTextParser
{
	/**
	 * Parses conflict texts from CPS service
	 * 
	 * @param rawText
	 *           text from CPS service
	 * @return parsed text
	 */
	String parseConflictText(final String rawText);
}
