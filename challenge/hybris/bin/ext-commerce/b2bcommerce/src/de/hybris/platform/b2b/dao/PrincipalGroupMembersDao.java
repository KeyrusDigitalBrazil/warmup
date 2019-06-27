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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.List;



public interface PrincipalGroupMembersDao
{

	/**
	 * Finds all members of a Principal Group of a given type. FlexibleSearch filters non specified type members so as
	 * not to have to iterate and instantiate entire collection to filter undesired types
	 */
	<T extends PrincipalModel> List<T> findAllMembersByType(final UserGroupModel parent, final Class<T> memberType);

	/**
	 * Finds members of a Principal Group of a given type. FlexibleSearch filters non specified type members so as not to
	 * have to iterate and instantiate entire collection to filter undesired types *
	 */
	<T extends PrincipalModel> List<T> findMembersByType(final UserGroupModel parent, final Class<T> memberType, final int count,
			final int start);

}
