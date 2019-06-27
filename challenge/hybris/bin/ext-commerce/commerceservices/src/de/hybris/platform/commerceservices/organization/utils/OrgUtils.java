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
package de.hybris.platform.commerceservices.organization.utils;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.organization.services.OrgUnitMemberParameter;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;


/**
 * Organization utils class.
 *
 */
public final class OrgUtils
{
	private OrgUtils()
	{
		// private constructor to avoid instantiation
	}

	/**
	 * Gets the role uids that are configured in project.properties file.
	 *
	 * @return a list of role uids
	 */
	public static List<String> getRoleUids()
	{
		final String roles = Config.getString(CommerceServicesConstants.ORGANIZATION_ROLES, StringUtils.EMPTY);
		if (StringUtils.isBlank(roles))
		{
			throw new IllegalStateException(
					"Property is empty or not configured. Property name: " + CommerceServicesConstants.ORGANIZATION_ROLES);
		}
		return Arrays.asList(StringUtils.split(roles, ","));
	}

	/**
	 * Checks whether the given employee is the admin of an organizational unit.
	 *
	 * @param employee
	 *           the employee to check
	 * @return <code>true</code> if the employee is an admin, <code>false</code> otherwise
	 */
	public static boolean isAdmin(final EmployeeModel employee)
	{
		return containsOrgAdminGroup(employee.getOrganizationRoles());
	}

	/**
	 * Checks whether the a collection of principal groups contains a group that has the organizational admin role
	 * assigned.
	 *
	 * @param groups
	 *           the collection of groups to check
	 * @return <code>true</code> if the collection contains an admin group, <code>false</code> otherwise
	 */
	public static boolean containsOrgAdminGroup(final Collection<? extends PrincipalGroupModel> groups)
	{
		for (final PrincipalGroupModel orgRole : groups)
		{
			if (isAdminRole(orgRole.getUid()))
			{
				return true;
			}
		}
		return false;
	}

	protected static boolean isAdminRole(final String roleUid)
	{
		final String roles = Config.getString(CommerceServicesConstants.ORGANIZATION_ROLES_ADMIN_GROUPS, StringUtils.EMPTY);
		if (StringUtils.isBlank(roles))
		{
			return false;
		}
		return Arrays.asList(StringUtils.split(roles, ",")).contains(roleUid);
	}

	/**
	 * Creates the org unit member parameter.
	 *
	 * @param <T>
	 *           the generic type
	 * @param uid
	 *           the uid
	 * @param members
	 *           the members
	 * @param type
	 *           the type
	 * @param pageableData
	 *           the pageable data
	 * @return the org unit member parameter
	 */
	public static <T extends PrincipalModel> OrgUnitMemberParameter<T> createOrgUnitMemberParameter(final String uid,
			final Set<T> members, final Class<T> type, final PageableData pageableData)
	{
		final OrgUnitMemberParameter parameter = new OrgUnitMemberParameter<>();
		parameter.setUid(uid);
		parameter.setType(type);
		parameter.setMembers(members);
		parameter.setPageableData(pageableData);
		return parameter;
	}
}
