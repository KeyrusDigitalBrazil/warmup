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
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;


/**
 * A service around {@link B2BCommentModel}
 * 
 * @param <T>
 */
public interface B2BCommentService<T extends AbstractOrderModel>
{

	/**
	 * Retrives a list of {@link de.hybris.platform.b2b.jalo.B2BComment} associated to the order
	 * 
	 * @param model
	 *           A generic type currently only {@link AbstractOrderModel} is supported
	 * @return A collection of {@link B2BCommentModel} associated to the {@link AbstractOrderModel}
	 */
	List<B2BCommentModel> getComments(final T model);

	/**
	 * Retrives a list of {@link de.hybris.platform.b2b.jalo.B2BComment} associated to the order owned by a particular
	 * user
	 * 
	 * @param model
	 *           A generic type currently only {@link AbstractOrderModel} is supported
	 * @param user
	 *           The owner of the {@link B2BCommentModel}
	 * @return A collection of {@link B2BCommentModel} associated to the {@link AbstractOrderModel}
	 */
	List<B2BCommentModel> getB2BCommentsForUser(final T model, final UserModel user);

	/**
	 * Adds a {@link B2BCommentModel} to {@link AbstractOrderModel}
	 * 
	 * @param model
	 *           An order or cart
	 * @param comment
	 *           A {@link B2BCommentModel} to associated to the order
	 */
	void addComment(final T model, final B2BCommentModel comment);
}
