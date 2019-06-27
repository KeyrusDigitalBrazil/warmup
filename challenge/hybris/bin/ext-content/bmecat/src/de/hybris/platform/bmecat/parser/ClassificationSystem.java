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
 * 
 */
public class ClassificationSystem extends AbstractValueObject
{
	private String name;
	private String version;
	private Collection<ClassificationGroup> groups;

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(final String version)
	{
		this.version = version;
	}

	public Collection<ClassificationGroup> getGroups()
	{
		return groups;
	}

	public void setGroups(final Collection<ClassificationGroup> groups)
	{
		this.groups = groups;
	}
}
