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

import de.hybris.bootstrap.xml.TagListener;


/**
 * Parses the &lt;BooleanValue&gt; tag
 * 
 * 
 */
public class BooleanValueTagListener extends SimpleValueTagListener
{
	public BooleanValueTagListener(final TagListener parent, final String tagName)
	{
		super(parent, tagName);
	}

	public BooleanValueTagListener(final TagListener parent, final String tagName, final boolean typed)
	{
		super(parent, tagName, typed);
	}

	@Override
	public Object getValue()
	{
		final String chars = getCharacters();
		if (chars == null || chars.length() == 0)
		{
			return null;
		}
		return Boolean.valueOf(chars);
	}
}
