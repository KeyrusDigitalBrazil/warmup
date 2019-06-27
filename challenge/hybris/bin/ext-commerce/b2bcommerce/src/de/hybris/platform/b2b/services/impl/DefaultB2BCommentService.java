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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.B2BCommentDao;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BCommentService}
 */
public class DefaultB2BCommentService implements B2BCommentService<AbstractOrderModel>
{
	private B2BCommentDao b2bCommentDao;

	private ModelService modelService;

	@Override
	public List<B2BCommentModel> getComments(final AbstractOrderModel model)
	{
		return getB2bCommentDao().findCommentsByOrder(model);
	}

	@Override
	public List<B2BCommentModel> getB2BCommentsForUser(final AbstractOrderModel model, final UserModel user)
	{
		return getB2bCommentDao().findCommentsByUser(user, model);
	}

	@Override
	public void addComment(final AbstractOrderModel model, final B2BCommentModel comment)
	{
		final Collection<B2BCommentModel> b2bcomments = new ArrayList<B2BCommentModel>(model.getB2bcomments());
		b2bcomments.add(comment);
		model.setB2bcomments(b2bcomments);
		this.getModelService().save(model);
	}

	@Required
	public void setB2bCommentDao(final B2BCommentDao b2bCommentDao)
	{
		this.b2bCommentDao = b2bCommentDao;
	}

	protected B2BCommentDao getB2bCommentDao()
	{
		return b2bCommentDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
