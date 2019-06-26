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
package de.hybris.platform.odata2services.odata.persistence.lookup;

/**
 * Exception for cases when a navigation property does not exist in the DB during lookup.
 */
public class ItemNotFoundForKeyNavigationPropertyException extends RuntimeException
{
	private final String propertyName;
	private final String typeName;

	public ItemNotFoundForKeyNavigationPropertyException(final String typeName, final String propertyName)
	{
		this.typeName = typeName;
		this.propertyName = propertyName;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public String getTypeName()
	{
		return typeName;
	}
}
