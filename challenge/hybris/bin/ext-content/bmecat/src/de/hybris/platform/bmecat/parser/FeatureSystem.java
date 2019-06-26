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
 * Object which holds the value of a parsed &lt;FEATURESYSTEM&gt; tag
 * 
 * 
 * 
 */
public class FeatureSystem extends AbstractValueObject
{
	private String name;
	private String description;
	private Collection<FeatureGroup> groups;

	/**
	 * @return Returns the description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           The description to set.
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * @return Returns the group.
	 */
	public Collection<FeatureGroup> getGroups()
	{
		return this.groups;
	}

	/**
	 * @param groups
	 *           the group to set.
	 */
	public void setGroups(final Collection<FeatureGroup> groups)
	{
		this.groups = groups;
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
}
