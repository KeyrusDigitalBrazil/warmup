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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CPSConflictTextParser;


/**
 * Default implementation of {@link CPSConflictTextParser}
 */
public class CPSConflictTextParserImpl implements CPSConflictTextParser
{

	@Override
	public String parseConflictText(final String rawText)
	{
		return rawText.replaceAll("\\x26[a-zA-Z]*\\x26", "");
	}

}
