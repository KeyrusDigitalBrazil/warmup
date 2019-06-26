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

import java.util.Collection;

public class DomainModelBuilder
{
	private final DomainModel model;

	private DomainModelBuilder()
	{
		model = new DomainModel();
	}

	private DomainModel getModel()
	{
		return this.model;
	}

	public static DomainModelBuilder aModel()
	{
		return new DomainModelBuilder();
	}

	public DomainModel build()
	{
		return getModel();
	}

	public DomainModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public DomainModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public DomainModelBuilder withCommentTypes(final Collection<CommentTypeModel> commentTypes)
	{
		getModel().setCommentTypes(commentTypes);
		return this;
	}
}
