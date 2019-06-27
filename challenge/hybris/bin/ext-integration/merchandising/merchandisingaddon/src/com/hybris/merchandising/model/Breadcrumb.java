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

/**
 * Represents a trail of categories through the site.
 *
 */
public class Breadcrumb implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String url;
	private String name;

	public Breadcrumb()
	{
		//Default constructor
	}

	/**
	 * Create a new Breadcrumb.
	 * 
	 * @param url
	 *           the category url.
	 * @param name
	 *           the category name.
	 */
	public Breadcrumb(final String url, final String name)
	{
		this.url = url;
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
