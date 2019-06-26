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
package com.hybris.merchandising.context.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.hybris.merchandising.context.ContextRepository;
import com.hybris.merchandising.model.ContextMap;


/**
 * Default implementation of a way to store context information.
 *
 */
public class DefaultContextRepository implements ContextRepository, Serializable
{
	private static final long serialVersionUID = 1L;
	ConcurrentHashMap<String, ContextMap> data;

	/**
	 * Creates a new instance of the context store, backed by a ConcurrentHashMap.
	 */
	public DefaultContextRepository()
	{
		this.data = new ConcurrentHashMap<String, ContextMap>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.merchandising.context.ContextRepository#get(java.lang.String)
	 */
	@Override
	public ContextMap get(final String name)
	{
		return this.data.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.merchandising.context.ContextRepository#put(java.lang.String,
	 * com.hybris.merchandising.model.ContextMap)
	 */
	@Override
	public void put(final String name, final ContextMap context)
	{
		this.data.put(name, context);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.convert.hybrisconvert.addon.data.IContextStore#clear()
	 */
	@Override
	public void clear()
	{
		this.data.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.hybris.convert.hybrisconvert.addon.data.IContextStore#size()
	 */
	@Override
	public int size()
	{
		return this.data.size();
	}

	@Override
	public Enumeration<String> keys()
	{
		return this.data.keys();
	}

}
