/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */

package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultUserGroupDao;
import de.hybris.platform.warehousing.util.builder.UserGroupModelBuilder;

import org.springframework.beans.factory.annotation.Required;


public class UserGroups extends AbstractItems<UserGroupModel>
{
	public static final String ADMIN_GRPOUP = "admingroup";
	public static final String WAREHOUSE_ADMINISTRATOR_GROUP = "warehouseadministratorgroup";
	public static final String WAREHOUSE_MANAGER_GRPOUP = "warehousemanagergroup";
	public static final String WAREHOUSE_AGENT_GRPOUP = "warehouseagentgroup";
	public static final String CUSTOMER_GRPOUP = "customergroup";

	private DefaultUserGroupDao userGroupDao;

	public UserGroupModel AdminGroup()
	{
		return getOrCreateUserGroup(ADMIN_GRPOUP);
	}

	public UserGroupModel WarehouseAdministratorGroup()
	{
		return getOrCreateUserGroup(WAREHOUSE_ADMINISTRATOR_GROUP);
	}

	public UserGroupModel WarehouseManagerGroup()
	{
		return getOrCreateUserGroup(WAREHOUSE_MANAGER_GRPOUP);
	}

	public UserGroupModel WarehouseAgentGroup()
	{
		return getOrCreateUserGroup(WAREHOUSE_AGENT_GRPOUP);
	}

	public UserGroupModel CustomerGroup()
	{
		return getOrCreateUserGroup(CUSTOMER_GRPOUP);
	}

	protected UserGroupModel getOrCreateUserGroup(final String uid)
	{
		return getOrSaveAndReturn(() -> getUserGroupDao().findUserGroupByUid(uid),
				() -> UserGroupModelBuilder.aModel().withUid(uid).build());
	}

	protected DefaultUserGroupDao getUserGroupDao()
	{
		return userGroupDao;
	}

	@Required
	public void setUserGroupDao(final DefaultUserGroupDao userGroupDao)
	{
		this.userGroupDao = userGroupDao;
	}
}
