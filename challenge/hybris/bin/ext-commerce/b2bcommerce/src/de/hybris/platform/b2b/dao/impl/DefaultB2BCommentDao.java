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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.b2b.dao.B2BCommentDao;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A default implementation to retrive {@link B2BCommentModel} related to cart or order.
 * 
 */

public class DefaultB2BCommentDao extends DefaultGenericDao<B2BCommentModel> implements
		B2BCommentDao<B2BCommentModel, AbstractOrderModel>
{

	public DefaultB2BCommentDao()
	{
		super(B2BCommentModel._TYPECODE);
	}

	@Override
	public List<B2BCommentModel> findCommentsByUser(final UserModel user, final AbstractOrderModel order)
	{

		final Map<String, Object> attr = new HashMap<String, Object>(2);
		attr.put(B2BCommentModel.ORDER, order);
		attr.put(B2BCommentModel.OWNER, user);
		return find(attr, SortParameters.singletonDescending(B2BCommentModel.CREATIONTIME));
	}

	@Override
	public List<B2BCommentModel> findCommentsByOrder(final AbstractOrderModel order)
	{
		final Map<String, Object> attr = new HashMap<String, Object>(1);
		attr.put(B2BCommentModel.ORDER, order);
		return find(attr, SortParameters.singletonDescending(B2BCommentModel.CREATIONTIME));
	}
}
