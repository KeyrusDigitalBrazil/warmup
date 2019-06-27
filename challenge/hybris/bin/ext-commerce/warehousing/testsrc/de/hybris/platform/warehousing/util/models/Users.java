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

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import de.hybris.platform.warehousing.util.builder.UserModelBuilder;

import org.springframework.beans.factory.annotation.Required;


public class Users extends AbstractItems<UserModel>
{
	public static final String UID_BOB = "bob";
	public static final String UID_NANCY = "nancy";
	public static final String UID_MANAGER_MONTREAL_DUKE = "mgr-montreal-duke";
	public static final String UID_MANAGER_MONTREAL_MAISONNEUVE = "mgr-montreal-maisonneuve";
	public static final String ADMIN_GRPOUP = "admingroup";
	public static final String WAREHOUSE_ADMINISTRATOR_GROUP = "warehouseadministratorgroup";
	public static final String WAREHOUSE_MANAGER_GRPOUP = "warehousemanagergroup";
	public static final String WAREHOUSE_AGENT_GRPOUP = "warehouseagentgroup";

	private UserDao userDao;

	public UserModel Bob()
	{
		return getOrCreateUser(UID_BOB);
	}

	public UserModel Nancy()
	{
		return getOrCreateUser(UID_NANCY);
	}

	public UserModel ManagerMontrealDuke()
	{
		return getOrCreateUser(UID_MANAGER_MONTREAL_DUKE);
	}

	public UserModel ManagerMontrealMaisonneuve()
	{
		return getOrCreateUser(UID_MANAGER_MONTREAL_MAISONNEUVE);
	}

	public UserModel AdminGroup()
	{
		return getOrCreateUser(ADMIN_GRPOUP);
	}

	public UserModel WarehouseAdministratorGroup()
	{
		return getOrCreateUser(WAREHOUSE_ADMINISTRATOR_GROUP);
	}

	public UserModel WarehouseManagerGroup()
	{
		return getOrCreateUser(WAREHOUSE_MANAGER_GRPOUP);
	}

	public UserModel WarehouseAgentGroup()
	{
		return getOrCreateUser(WAREHOUSE_AGENT_GRPOUP);
	}

	protected UserModel getOrCreateUser(final String uid)
	{
		return getOrSaveAndReturn(() -> getUserDao().findUserByUID(uid), () -> UserModelBuilder.aModel().withUid(uid)
				.build());
	}

	public UserDao getUserDao()
	{
		return userDao;
	}

	@Required
	public void setUserDao(final UserDao userDao)
	{
		this.userDao = userDao;
	}


}
