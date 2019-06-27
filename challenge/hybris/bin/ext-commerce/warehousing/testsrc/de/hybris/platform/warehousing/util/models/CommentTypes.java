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

import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.warehousing.util.builder.CommentTypeModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class CommentTypes extends AbstractItems<CommentTypeModel>
{
	public static final String COMMENT_TYPE_CODE = "stockLevelAdjustmentNote";
	public static final String COMMENT_TYPE_NAME = "Stock Level Adjustment Note";
	public static final String TICKET_CREATION_EVENT_COMMENT = "ticketCreationEvent";

	private CommentService commentService;
	private Components components;
	private Domains domains;
	private ComposedTypes composedTypes;

	public CommentTypeModel adjustmentNote()
	{
		return getOrSaveAndReturn(() -> getCommentService().getCommentTypeForCode(getComponents().warehousingComponent(), COMMENT_TYPE_CODE),
				() -> CommentTypeModelBuilder.aModel()
						.withCode(COMMENT_TYPE_CODE)
						.withName(COMMENT_TYPE_NAME)
						.withDomain(getDomains().warehousingDomain())
						.build());
	}

	public CommentTypeModel ticketCreationEvent()
	{
		return getOrSaveAndReturn(() -> getCommentService().getCommentTypeForCode(getComponents().ticketComponent(), TICKET_CREATION_EVENT_COMMENT),
				() -> CommentTypeModelBuilder.aModel()
						.withCode(TICKET_CREATION_EVENT_COMMENT)
						.withDomain(getDomains().ticketSystem())
						.withMetaType(getComposedTypes().customerEvent())
						.build());
	}

	protected CommentService getCommentService()
	{
		return commentService;
	}

	@Required
	public void setCommentService(CommentService commentService)
	{
		this.commentService = commentService;
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

	public Domains getDomains()
	{
		return domains;
	}

	@Required
	public void setDomains(Domains domains)
	{
		this.domains = domains;
	}

	public ComposedTypes getComposedTypes()
	{
		return composedTypes;
	}

	@Required
	public void setComposedTypes(final ComposedTypes composedTypes)
	{
		this.composedTypes = composedTypes;
	}
}
