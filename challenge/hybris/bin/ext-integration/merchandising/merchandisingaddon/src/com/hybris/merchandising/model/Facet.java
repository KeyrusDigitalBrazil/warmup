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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This object is the list of facets that the user has selected in the UI to narrow down the search of products
 *
 */
public class Facet implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private List<String> values;

	private static final Logger LOG = Logger.getLogger(Facet.class);

	/**
	 * Creates a new instance of an ItemFacet with an empty list of values.
	 */
	public Facet()
	{
		values = new ArrayList<String>();
	}

	/**
	 * Creates a new instance of an ItemFacet with a specified code and name.
	 *
	 * @param code
	 *           a String containing the code for the specific facet.
	 * @param name
	 *           the name of the specific facet.
	 */
	public Facet(final String code, final String name)
	{
		this();
		this.code = code;
		this.name = name;
	}

	/**
	 * Creates a new instance of an ItemFacet with a specific code, name and initial values.
	 *
	 * @param code
	 *           a String containing the code for the specific facet.
	 * @param name
	 *           the name of the specific facet.
	 * @param values
	 *           a list of the values we want to store against this facet.
	 */
	public Facet(final String code, final String name, final List<String> values)
	{
		this(code, name);
		this.values = values;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return this.code;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public void setValues(final List<String> values)
	{
		this.values = values;
	}

	/**
	 * Adds a specified facet value to the list of values represented by this current facet.
	 *
	 * @param value
	 *           the facet value to add to the list.
	 */
	public void addValue(final String value)
	{
		this.values.add(value);
	}

	public List<String> getValues()
	{
		return this.values;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object object)
	{
		final boolean matched = false;
		if (object != null && this.getClass() == object.getClass())
		{
			final Facet itemFacet = (Facet) object;

			return itemFacet.getCode().equals(this.getCode()) && itemFacet.getValues().equals(this.getValues());
		}

		return matched;
	}


	@Override
	public String toString()
	{
		final ObjectMapper mapper = new ObjectMapper();
		try
		{
			return mapper.writeValueAsString(this);
		}
		catch (final IOException e)
		{
			LOG.error("Exception thrown when running toString()", e);
		}
		return super.toString();
	}

}
