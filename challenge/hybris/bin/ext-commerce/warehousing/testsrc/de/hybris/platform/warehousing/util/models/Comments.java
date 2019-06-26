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

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.warehousing.util.builder.CommentModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class Comments extends AbstractItems<CommentModel>
{
	public static final String COMMENT_TEXT = "Test comment";

	private Components components;
	private Users users;
	private CommentTypes commentTypes;

	public CommentModel commentAdjustmentNote()
	{
		CommentModel model = CommentModelBuilder.aModel()
				.withText(COMMENT_TEXT)
				.withComponent(getComponents().warehousingComponent())
				.withAuthor(getUsers().Bob())
				.withCommentType(getCommentTypes().adjustmentNote())
				.build();
		getModelService().save(model);
		return model;
	}

	public Components getComponents()
	{
		return components;
	}

	@Required
	public void setComponents(Components components)
	{
		this.components = components;
	}

	public Users getUsers()
	{
		return users;
	}

	@Required
	public void setUsers(Users users)
	{
		this.users = users;
	}

	public CommentTypes getCommentTypes()
	{
		return commentTypes;
	}

	@Required
	public void setCommentTypes(CommentTypes commentTypes)
	{
		this.commentTypes = commentTypes;
	}
}
