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

import java.util.Collection;


/**
 * Object which holds the value of a parsed &lt;FEATUREGROUP&gt; tag
 * 
 * 
 * 
 */
public class FeatureGroup extends AbstractValueObject
{
	private String id;
	private String name;
	private String descr;
	private Collection<FeatureTemplate> templates;

	/**
	 * @return Returns the description
	 */
	public String getDescription()
	{
		return descr;
	}

	/**
	 * @param descr
	 *           The description to set.
	 */
	public void setDescription(final String descr)
	{
		this.descr = descr;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           The id to set.
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           The name to set.
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the template.
	 */
	public Collection<FeatureTemplate> getTemplates()
	{
		return this.templates;
	}

	/**
	 * @param templates
	 *           The template to set.
	 */
	public void setTemplates(final Collection<FeatureTemplate> templates)
	{
		this.templates = templates;
	}
}
