/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.comments.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.comments.model.CommentTypeModel;
import de.hybris.platform.comments.model.ComponentModel;
import de.hybris.platform.comments.model.DomainModel;
import de.hybris.platform.comments.services.impl.DefaultCommentService;
import de.hybris.platform.commerceservices.comments.CommerceCommentService;
import de.hybris.platform.commerceservices.service.data.CommerceCommentParameter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link CommerceCommentService}.
 */
public class DefaultCommerceCommentService extends DefaultCommentService implements CommerceCommentService
{
	private transient KeyGenerator commentsKeyGenerator;

	@Override
	public void addComment(final CommerceCommentParameter parameter)
	{
		validateCommentParameter(parameter);
		final DomainModel domainModel = getDomainForCode(parameter.getDomainCode());
		final ComponentModel componentModel = getComponentForCode(domainModel, parameter.getComponentCode());
		final CommentTypeModel commentTypeModel = getCommentTypeForCode(componentModel, parameter.getCommentTypeCode());

		final CommentModel commentModel = getModelService().create(CommentModel.class);
		commentModel.setCode((String) getCommentsKeyGenerator().generate());
		commentModel.setText(parameter.getText());
		commentModel.setAuthor(parameter.getAuthor());
		commentModel.setComponent(componentModel);
		commentModel.setCommentType(commentTypeModel);

		final ItemModel model = parameter.getItem();

		// need to redeclare list as default list on model is immutable
		final List<CommentModel> comments = new ArrayList<CommentModel>(model.getComments());
		comments.add(commentModel);
		model.setComments(comments);

		getModelService().save(model);
	}

	protected void validateCommentParameter(final CommerceCommentParameter parameter)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("parameter", parameter);
		ServicesUtil.validateParameterNotNullStandardMessage("item", parameter.getItem());
		ServicesUtil.validateParameterNotNullStandardMessage("author", parameter.getAuthor());
		checkArgument(isNotEmpty(parameter.getText()), "Text cannot not be empty");
		checkArgument(isNotEmpty(parameter.getDomainCode()), "Domain cannot not be empty");
		checkArgument(isNotEmpty(parameter.getComponentCode()), "Component cannot not be empty");
		checkArgument(isNotEmpty(parameter.getCommentTypeCode()), "CommentType cannot not be empty");
	}


	protected KeyGenerator getCommentsKeyGenerator()
	{
		return commentsKeyGenerator;
	}

	@Required
	public void setCommentsKeyGenerator(final KeyGenerator commentsKeyGenerator)
	{
		this.commentsKeyGenerator = commentsKeyGenerator;
	}


}
