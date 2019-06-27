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

import java.util.Set;


/**
 * Object which holds the value of a parsed &lt;NODE&gt; tag
 * 
 * 
 */
public class Node extends AbstractValueObject
{
	private Set categories;

	/**
	 * @return Returns the categories.
	 */
	public Set getCategories()
	{
		return categories;
	}

	/**
	 * @param categories
	 *           The categories to set.
	 */
	public void setCategories(final Set categories)
	{
		this.categories = categories;
	}
}
