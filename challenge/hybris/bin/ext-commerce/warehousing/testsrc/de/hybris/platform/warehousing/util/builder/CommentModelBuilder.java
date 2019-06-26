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

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.core.model.user.UserModel;


public class CommentModelBuilder
{
	private final CommentModel model;

	private CommentModelBuilder()
	{
		model = new CommentModel();
	}

	private CommentModel getModel()
	{
		return this.model;
	}

	public static CommentModelBuilder aModel()
	{
		return new CommentModelBuilder();
	}

	public CommentModel build()
	{
		return getModel();
	}

	public CommentModelBuilder withComponent(final ComponentModel component)
	{
		getModel().setComponent(component);
		return this;
	}

	public CommentModelBuilder withAuthor(final UserModel user)
	{
		getModel().setAuthor(user);
		return this;
	}

	public CommentModelBuilder withCommentType(final CommentTypeModel commentType)
	{
		getModel().setCommentType(commentType);
		return this;
	}

	public CommentModelBuilder withText(final String text)
	{
		getModel().setText(text);
		return this;
	}

}
