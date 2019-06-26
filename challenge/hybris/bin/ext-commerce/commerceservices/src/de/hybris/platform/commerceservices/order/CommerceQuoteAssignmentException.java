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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Exception thrown if quote assignment fails
 */
public class CommerceQuoteAssignmentException extends SystemException
{
	final private String assignedUser;

	public CommerceQuoteAssignmentException(final String message, final String assignedUser)
	{
		super(message);
		this.assignedUser = assignedUser;
	}

	public String getAssignedUser()
	{
		return assignedUser;
	}
}
