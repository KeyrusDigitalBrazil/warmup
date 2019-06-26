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

import de.hybris.platform.comments.model.ComponentModel;

import de.hybris.platform.comments.services.CommentService;
import de.hybris.platform.warehousing.util.builder.ComponentModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class Components extends AbstractItems<ComponentModel>
{
	public static final String COMPONENT_CODE = "warehousing";
	public static final String COMPONENT_NAME = "Warehousing";
	public static final String TICKET_COMPONENT = "ticketSystem";

	private CommentService commentService;
	private Domains domains;

	public ComponentModel warehousingComponent()
	{
		return getOrSaveAndReturn(() -> getCommentService().getComponentForCode(getDomains().warehousingDomain(), COMPONENT_CODE),
				() -> ComponentModelBuilder.aModel()
						.withCode(COMPONENT_CODE)
						.withName(COMPONENT_NAME)
						.withDomain(getDomains().warehousingDomain())
						.build());
	}

	public ComponentModel ticketComponent()
	{
		return getOrSaveAndReturn(() -> getCommentService().getComponentForCode(getDomains().ticketSystem(), TICKET_COMPONENT),
				() -> ComponentModelBuilder.aModel()
						.withCode(TICKET_COMPONENT)
						.withDomain(getDomains().ticketSystem())
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

	public Domains getDomains()
	{
		return domains;
	}

	@Required
	public void setDomains(Domains domains)
	{
		this.domains = domains;
	}
}
