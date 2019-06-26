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
package de.hybris.platform.bmecat.parser;

import de.hybris.bootstrap.xml.AbstractValueObject;


/**
 * Object which holds the value of a parsed &lt;Abort&gt; tag
 * 
 * 
 */
public class Abort extends AbstractValueObject
{
	private final String type;

	public Abort(final String type)
	{
		super();
		this.type = type;
	}

	public String getType()
	{
		return type;
	}
}
