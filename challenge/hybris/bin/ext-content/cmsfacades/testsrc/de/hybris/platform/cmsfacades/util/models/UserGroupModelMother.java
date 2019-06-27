/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.cmsfacades.util.builder.UserGroupModelBuilder;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.daos.UserGroupDao;

import java.util.*;


public class UserGroupModelMother extends AbstractModelMother<UserGroupModel>
{
	public static final String CUSTOMER_GROUP = "customer-group";
	public static final String CMSMANAGER_GROUP = "cmsmanager-group";
	public static final String CUSTOMER_GROUP_ID = "customer-group-id";
	public static final String CMSMANAGER_GROUP_ID = "cmsmanager-group-id";

	private UserGroupDao userGroupDao;

	protected UserGroupModel createGroupModel(final String nameCode, final String uid)
	{
		UserGroupModel userGroupModel = getUserGroupDao().findUserGroupByUid(nameCode);
		List<UserGroupModel> list = (userGroupModel == null ? new ArrayList<>() : Arrays.asList(userGroupModel));
		return getFromCollectionOrSaveAndReturn(() -> list,
				() -> UserGroupModelBuilder.aModel() //
						.withName(nameCode, Locale.ENGLISH) //
						.withUid(uid) //
						.build());
	}

	public UserGroupModel userGroup02()
	{
		return createGroupModel(CMSMANAGER_GROUP, CMSMANAGER_GROUP_ID);
	}

	public UserGroupModel userGroup01()
	{
		return createGroupModel(CUSTOMER_GROUP, CUSTOMER_GROUP_ID);
	}

	public UserGroupDao getUserGroupDao()
	{
		return userGroupDao;
	}

	public void setUserGroupDao(UserGroupDao userGroupDao)
	{
		this.userGroupDao = userGroupDao;
	}
}
