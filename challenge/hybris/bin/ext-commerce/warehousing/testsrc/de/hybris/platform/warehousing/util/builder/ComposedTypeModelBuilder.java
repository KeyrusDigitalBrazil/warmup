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

import de.hybris.platform.core.model.type.ComposedTypeModel;


public class ComposedTypeModelBuilder
{
	private final ComposedTypeModel model;

	private ComposedTypeModelBuilder()
	{
		model = new ComposedTypeModel();
	}

	public static ComposedTypeModelBuilder aModel()
	{
		return new ComposedTypeModelBuilder();
	}

	private ComposedTypeModel getModel()
	{
		return this.model;
	}

	public ComposedTypeModel build()
	{
		return getModel();
	}

	public ComposedTypeModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public ComposedTypeModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}
}
