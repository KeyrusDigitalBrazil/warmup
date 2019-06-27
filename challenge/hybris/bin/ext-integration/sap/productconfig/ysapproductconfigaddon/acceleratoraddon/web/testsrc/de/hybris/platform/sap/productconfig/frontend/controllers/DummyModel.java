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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.ui.Model;


public class DummyModel implements Model
{

	private final Map modelMap = new HashMap();

	@Override
	public Model addAttribute(final String paramString, final Object paramObject)
	{
		modelMap.put(paramString, paramObject);
		return this;
	}

	@Override
	public Model addAttribute(final Object paramObject)
	{
		modelMap.put(paramObject.getClass().getName(), paramObject);
		return this;
	}

	@Override
	public Model addAllAttributes(final Collection<?> paramCollection)
	{
		for (final Object obj : paramCollection)
		{
			addAttribute(obj);
		}
		return this;
	}

	@Override
	public Model addAllAttributes(final Map<String, ?> paramMap)
	{
		for (final Entry<String, ?> entry : paramMap.entrySet())
		{
			addAttribute(entry.getKey(), entry.getValue());
		}
		return this;
	}

	@Override
	public Model mergeAttributes(final Map<String, ?> paramMap)
	{
		addAllAttributes(paramMap);
		return this;
	}

	@Override
	public boolean containsAttribute(final String paramString)
	{
		return modelMap.containsKey(paramString);
	}

	@Override
	public Map<String, Object> asMap()
	{
		return modelMap;
	}


}
