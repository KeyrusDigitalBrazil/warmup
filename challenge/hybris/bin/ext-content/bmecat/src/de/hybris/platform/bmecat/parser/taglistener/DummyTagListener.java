/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.bmecat.parser.taglistener;

import de.hybris.platform.bmecat.parser.BMECatObjectProcessor;


/**
 * Parses the &lt;Dummy&gt; tag
 * 
 * 
 * 
 */
public class DummyTagListener extends DefaultBMECatTagListener
{
	public static final String TAGNAME = "DUMMY-TAG";

	public DummyTagListener()
	{
		super(null);
	}

	@Override
	public Object processEndElement(final BMECatObjectProcessor processor)
	{
		return null;
	}

	public String getTagName()
	{
		return TAGNAME;
	}
}
