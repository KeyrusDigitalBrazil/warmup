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

import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;


public class CommentTypeModelBuilder
{
	private final CommentTypeModel model;

	private CommentTypeModelBuilder()
	{
		model = new CommentTypeModel();
	}

	private CommentTypeModel getModel()
	{
		return this.model;
	}

	public static CommentTypeModelBuilder aModel()
	{
		return new CommentTypeModelBuilder();
	}

	public CommentTypeModel build()
	{
		return getModel();
	}

	public CommentTypeModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public CommentTypeModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public CommentTypeModelBuilder withDomain(final DomainModel domain)
	{
		getModel().setDomain(domain);
		return this;
	}

	public CommentTypeModelBuilder withMetaType(final ComposedTypeModel metaType)
	{
		getModel().setMetaType(metaType);
		return this;
	}
}
