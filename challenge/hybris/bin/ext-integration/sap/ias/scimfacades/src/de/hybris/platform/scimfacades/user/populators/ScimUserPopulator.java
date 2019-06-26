/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimfacades.user.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserEmail;
import de.hybris.platform.scimfacades.ScimUserGroup;
import de.hybris.platform.scimfacades.ScimUserName;
import de.hybris.platform.scimfacades.constants.ScimfacadesConstants;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class ScimUserPopulator implements Populator<UserModel, ScimUser>
{

	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;

	@Override
	public void populate(final UserModel userModel, final ScimUser scimUser)
	{
		if (userModel instanceof EmployeeModel)
		{
			final EmployeeModel employee = (EmployeeModel) userModel;
			scimUser.setId(employee.getScimUserId());
			final ScimUserName scimUserName = new ScimUserName();
			final String[] names = splitName(employee.getName());
			if (ArrayUtils.isNotEmpty(names))
			{
				scimUserName.setGivenName(names[0]);
				if (names.length > 1)
				{
					scimUserName.setFamilyName(names[1]);
				}
			}
			scimUser.setName(scimUserName);

			final ScimUserEmail email = new ScimUserEmail();
			email.setValue(employee.getUid());
			email.setPrimary(true);
			scimUser.setEmails(Collections.singletonList(email));

			final boolean active = !employee.isLoginDisabled();
			scimUser.setActive(active);
			scimUser.setVerified(active);

			scimUser.setUserType("employee");
			scimUser.setPreferredLanguage(employee.getSessionLanguage() != null ? employee.getSessionLanguage().getIsocode() : null);

			populateGroups(userModel, scimUser);
		}
	}

	/**
	 * Populate ScimUserGroups for the groups in user model
	 *
	 * @param userModel
	 * @param scimUser
	 */
	private void populateGroups(final UserModel userModel, final ScimUser scimUser)
	{
		final Set<PrincipalGroupModel> groups = userModel.getGroups();
		final List<ScimUserGroupModel> userGroups = scimUserGroupGenericDao.find();
		if (CollectionUtils.isNotEmpty(userGroups) && CollectionUtils.isNotEmpty(groups))
		{
			final List<ScimUserGroupModel> validGroups = userGroups.stream()
					.filter(item -> !Collections.disjoint(item.getUserGroups(), groups)).collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(userGroups))
			{
				final List<ScimUserGroup> scimgroups = new ArrayList<>();
				validGroups.forEach(item -> {
					final ScimUserGroup group = new ScimUserGroup();
					group.setValue(item.getScimUserGroup());
					scimgroups.add(group);
				});
				scimUser.setGroups(scimgroups);
			}
		}
	}

	private String[] splitName(final String name)
	{
		final String trimmedName = StringUtils.trimToNull(name);
		return new String[]
		{ StringUtils.substringBeforeLast(trimmedName, ScimfacadesConstants.SEPARATOR_SPACE),
				StringUtils.substringAfterLast(trimmedName, ScimfacadesConstants.SEPARATOR_SPACE) };
	}

	public GenericDao<ScimUserGroupModel> getScimUserGroupGenericDao()
	{
		return scimUserGroupGenericDao;
	}

	@Required
	public void setScimUserGroupGenericDao(final GenericDao<ScimUserGroupModel> scimUserGroupGenericDao)
	{
		this.scimUserGroupGenericDao = scimUserGroupGenericDao;
	}

}
