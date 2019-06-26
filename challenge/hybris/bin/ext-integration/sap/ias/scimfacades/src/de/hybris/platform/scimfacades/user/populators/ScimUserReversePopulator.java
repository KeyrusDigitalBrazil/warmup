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
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserEmail;
import de.hybris.platform.scimfacades.ScimUserGroup;
import de.hybris.platform.scimfacades.constants.ScimfacadesConstants;
import de.hybris.platform.scimfacades.utils.ScimUtils;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates employee model using SCIM User information
 */
public class ScimUserReversePopulator implements Populator<ScimUser, UserModel>
{

	private CommonI18NService commonI18NService;

	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;

	@Override
	public void populate(final ScimUser scimUser, final UserModel user)
	{
		if (user instanceof EmployeeModel)
		{
			final EmployeeModel employee = (EmployeeModel) user;
			final ScimUserEmail email = ScimUtils.getPrimaryEmail(scimUser.getEmails());
			final String emailValue = email != null ? email.getValue() : null;

			employee.setUid(StringUtils.defaultString(emailValue, employee.getUid()));
			employee.setScimUserId(StringUtils.defaultString(scimUser.getId(), employee.getScimUserId()));
			String name = null;
			if (scimUser.getName() != null)
			{
				name = getName(StringUtils.isNotEmpty(scimUser.getName().getGivenName()) ? scimUser.getName().getGivenName()
						: StringUtils.EMPTY, scimUser.getName().getFamilyName());
			}

			employee.setName(StringUtils.defaultString(name, employee.getName()));

			employee.setSessionCurrency(commonI18NService.getCurrentCurrency());
			employee.setSessionLanguage(getLanguageForCode(scimUser));

			if (scimUser.getActive() != null)
			{
				employee.setLoginDisabled(!scimUser.getActive().booleanValue());
			}

			updateGroups(scimUser, employee);
		}
	}

	/**
	 * Update groups in user model
	 *
	 * @param scimUser
	 * @param employee
	 */
	private void updateGroups(final ScimUser scimUser, final EmployeeModel employee)
	{
		// Groups are always replaced and not merged because if group is removed in IAS then it should be removed in Commerce as well
		if (CollectionUtils.isNotEmpty(scimUser.getGroups()))
		{
			employee.setGroups(getGroups(scimUser));
		}
	}

	/**
	 * Get groups for scim user
	 *
	 * @param scimUser
	 * @return groups
	 */
	private Set<PrincipalGroupModel> getGroups(final ScimUser scimUser)
	{
		Set<PrincipalGroupModel> groups = null;
		groups = new HashSet<>();
		for (final ScimUserGroup group : scimUser.getGroups())
		{
			final List<ScimUserGroupModel> scimUserGroups = scimUserGroupGenericDao
					.find(Collections.singletonMap(ScimUserGroupModel.SCIMUSERGROUP, group.getValue()));
			groups.addAll(scimUserGroups.stream().flatMap(item -> item.getUserGroups().stream()).collect(Collectors.toList()));
		}
		return groups;
	}

	/**
	 * Get language for code, if it doesn't exist get current language
	 *
	 * @param scimUser
	 * @return LanguageModel
	 */
	private LanguageModel getLanguageForCode(final ScimUser scimUser)
	{
		return StringUtils.isNotEmpty(scimUser.getPreferredLanguage())
				? commonI18NService.getLanguage(scimUser.getPreferredLanguage())
				: commonI18NService.getCurrentLanguage();
	}

	public String getName(final String firstName, final String lastName)
	{
		final String result = StringUtils.trimToEmpty(firstName) + ScimfacadesConstants.SEPARATOR_SPACE
				+ StringUtils.trimToEmpty(lastName);
		return StringUtils.trimToNull(result);
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public GenericDao<ScimUserGroupModel> getScimUserGroupGenericDao()
	{
		return scimUserGroupGenericDao;
	}

	public void setScimUserGroupGenericDao(final GenericDao<ScimUserGroupModel> scimUserGroupGenericDao)
	{
		this.scimUserGroupGenericDao = scimUserGroupGenericDao;
	}

}
