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

import de.hybris.platform.core.model.user.CustomerModel;


public class CustomerModelBuilder
{
	private final CustomerModel model;

	private CustomerModelBuilder()
	{
		model = new CustomerModel();
	}

	private CustomerModel getModel()
	{
		return this.model;
	}

	public static CustomerModelBuilder aModel()
	{
		return new CustomerModelBuilder();
	}

	public CustomerModel build()
	{
		return getModel();
	}

	public CustomerModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public CustomerModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}
}
