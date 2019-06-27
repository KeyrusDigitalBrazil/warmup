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

package de.hybris.platform.marketplaceservices.interceptor;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class assigned the {@link UserGroupModel} type group to the VendorUser group.
 */
@SuppressWarnings("rawtypes")
public class VendorUserInitDefaultsInterceptor implements InitDefaultsInterceptor
{
	private UserService userService;
	private ModelService modelService;

	private static final String VENDERADMINGROUP_UID = "vendoradministratorgroup";
	private static final String MERCHANTOPRATORGROUP_UID = "merchantoperatorgroup";
	private static final String VENDERADMINGROUP_NAME = "Vendor Administrator Group";
	private static final String CUSTOMERGROUP_UID = "customergroup";

	@Override
	public void onInitDefaults(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof VendorUserModel)
		{
			final UserModel currentUser = userService.getCurrentUser();

			final List<PrincipalGroupModel> groupList = currentUser.getAllGroups().stream()
					.filter(userGroup -> userGroup.getUid().equals(MERCHANTOPRATORGROUP_UID)).collect(Collectors.toList());

			final VendorUserModel vendorUser = (VendorUserModel) model;
			if (!groupList.isEmpty())
			{
				final UserGroupModel userGroup = getOrCreateGroup(VENDERADMINGROUP_UID, VENDERADMINGROUP_NAME);
				vendorUser.setGroups(Collections.<PrincipalGroupModel> singleton(userGroup));
			}
			else
			{
				vendorUser.setGroups(null);
			}
		}
	}

	/**
	 * Get user group by given UID or create it with given name (not locName) if the group doesn't exist
	 *
	 * @param groupUid
	 *           given UID of this group
	 * @param groupName
	 *           given name of new group
	 * @return the user group
	 */
	protected UserGroupModel getOrCreateGroup(final String groupUid, final String groupName)
	{
		UserGroupModel userGroup;
		try
		{
			userGroup = getUserService().getUserGroupForUID(groupUid);
		}
		catch (final UnknownIdentifierException e)
		{
			userGroup = getModelService().create(UserGroupModel.class);
			userGroup.setUid(groupUid);
			userGroup.setName(groupName);
			userGroup.setGroups(Collections.<PrincipalGroupModel> singleton(getUserService().getUserGroupForUID(CUSTOMERGROUP_UID)));
			getModelService().save(userGroup);
		}
		return userGroup;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}