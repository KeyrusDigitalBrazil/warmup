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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.core.model.user.UserModel;


public class UserModelBuilder
{
	private final UserModel model;

	private UserModelBuilder()
	{
		model = new UserModel();
	}

	private UserModel getModel()
	{
		return this.model;
	}

	public static UserModelBuilder aModel()
	{
		return new UserModelBuilder();
	}

	public UserModel build()
	{
		return getModel();
	}

	public UserModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

}
