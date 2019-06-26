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
package de.hybris.platform.commerceservices.organization.services.impl;

import de.hybris.platform.commerceservices.jalo.OrgUnit;


/**
 * Exception indicating that an operation updating the <code>path</code> value of an {@link OrgUnit} failed, e.g. for
 * inconsistencies discovered during the update.
 */
public class OrgUnitHierarchyException extends RuntimeException
{
	/**
	 * Constructs an {@link OrgUnitHierarchyException} without any parameters.
	 */
	public OrgUnitHierarchyException()
	{
		super();
	}

	/**
	 * Constructs an {@link OrgUnitHierarchyException} setting a message.
	 *
	 * @param message
	 *           the exception message
	 */
	public OrgUnitHierarchyException(final String message)
	{
		super(message);
	}

	/**
	 * Constructs an {@link OrgUnitHierarchyException} setting the cause of the exception.
	 *
	 * @param t
	 *           the cause of the exception
	 */
	public OrgUnitHierarchyException(final Throwable t)
	{
		super(t);
	}

	/**
	 * Constructs an {@link OrgUnitHierarchyException} setting a message as well as the cause of the exception.
	 *
	 * @param message
	 *           the exception message
	 * @param t
	 *           the cause of the exception
	 */
	public OrgUnitHierarchyException(final String message, final Throwable t)
	{
		super(message, t);
	}
}
