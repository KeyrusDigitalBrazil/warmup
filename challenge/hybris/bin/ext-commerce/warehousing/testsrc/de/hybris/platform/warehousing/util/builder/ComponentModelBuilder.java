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

import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;


public class ComponentModelBuilder
{
	private final ComponentModel model;

	private ComponentModelBuilder()
	{
		model = new ComponentModel();
	}

	private ComponentModel getModel()
	{
		return this.model;
	}

	public static ComponentModelBuilder aModel()
	{
		return new ComponentModelBuilder();
	}

	public ComponentModel build()
	{
		return getModel();
	}

	public ComponentModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public ComponentModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public ComponentModelBuilder withDomain(final DomainModel domain)
	{
		getModel().setDomain(domain);
		return this;
	}
}
