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
package de.hybris.platform.b2bacceleratorservices.strategies.impl;

import de.hybris.platform.b2bacceleratorservices.strategies.B2BUserGroupsLookUpStrategy;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.strategies.impl.DefaultB2BUserGroupsLookUpStrategy} instead.
 */
@Deprecated
public class DefaultB2BUserGroupsLookUpStrategy implements B2BUserGroupsLookUpStrategy
{
	private List<String> groups;

	@Override
	public List<String> getUserGroups()
	{
		return getGroups();
	}

	protected List<String> getGroups()
	{
		return groups;
	}

	@Required
	public void setGroups(final List<String> groups)
	{
		this.groups = groups;
	}
}
