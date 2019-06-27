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
package de.hybris.platform.commercefacades.customergroups.exceptions;

import de.hybris.platform.core.model.user.UserGroupModel;


/**
 *
 * 
 */
public class InvalidCustomerGroupException extends RuntimeException
{

	private final UserGroupModel group;

	public InvalidCustomerGroupException(final UserGroupModel group)
	{
		super("UserGroup [" + group.getUid() + "] is not member of customergroup");
		this.group = group;
	}

	/**
	 * @return the group
	 */
	public UserGroupModel getGroup()
	{
		return group;
	}
}
