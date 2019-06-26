/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.model;

import java.io.Serializable;
import java.util.HashMap;


/**
 * A key-value Map to store various Merchandising related contextual data.
 *
 */
public class ContextMap implements Serializable
{
	private static final long serialVersionUID = 1L;
	protected HashMap<String, Object> properties;

	public ContextMap()
	{
		this.properties = new HashMap<>();
	}

	public void removeProperty(final String propertyName)
	{
		this.properties.remove(propertyName);
	}

	public void addProperty(final String propertyName, final Object propertyValue)
	{
		this.properties.put(propertyName, propertyValue);
	}

	public Object getProperty(final String propertyName)
	{
		return this.properties.get(propertyName);
	}

	public HashMap<String, Object> getProperties()
	{
		return this.properties;
	}
}
