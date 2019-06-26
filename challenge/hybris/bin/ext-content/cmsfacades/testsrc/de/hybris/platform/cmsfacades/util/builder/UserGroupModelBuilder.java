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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.Locale;


public class UserGroupModelBuilder
{
	private final UserGroupModel model;

	private UserGroupModelBuilder()
	{
		this.model = new UserGroupModel();
	}

	private UserGroupModelBuilder(final UserGroupModel model)
	{
		this.model = model;
	}

	protected UserGroupModel getModel()
	{
		return this.model;
	}

	public static UserGroupModelBuilder aModel()
	{
		return new UserGroupModelBuilder();
	}

	public static UserGroupModelBuilder fromModel(final UserGroupModel model)
	{
		return new UserGroupModelBuilder(model);
	}

	public UserGroupModel build()
	{
		return getModel();
	}

	public UserGroupModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setLocName(name, locale);
		return this;
	}

	public UserGroupModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}
}
