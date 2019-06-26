/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.search;

/**
 * Symbols that indicate how to query the item in the hierarchy
 */
public enum ItemTypeMatch
{
	/**
	 * Symbol that indicate item with all subtypes - "*"
	 */
	ALL_SUB_AND_SUPER_TYPES("*"),

	/**
	 * Symbol that indicate one restricted type - "!"
	 */
	RESTRICT_TO_ITEM_TYPE("!"),

	/**
	 * Default item hierarchy
	 */
	ALL_SUBTYPES("");

	ItemTypeMatch(final String value)
	{
		this.value = value;
	}

	private String value;

	/**
	 * Internal value
	 *
	 * @return internal value
	 */
	public String getValue()
	{
		return value;
	}
}