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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;


/**
 * A dao around {@link B2BCommentModel}
 * 
 * @param < C, T>
 */
public interface B2BCommentDao<C extends B2BCommentModel, T extends AbstractOrderModel>
{

	/**
	 * Find all comments owned by user and associated to an order
	 * 
	 * @param user
	 *           The owner of a {@link B2BCommentModel}
	 * @param order
	 *           An order or cart.
	 * @return All comments associated to an order or cart owned by user.
	 */
	List<C> findCommentsByUser(final UserModel user, final T order);

	/**
	 * Find all comments and associated to an order
	 * 
	 * @param order
	 *           An order or cart.
	 * @return All comments associated to an order.
	 */
	List<C> findCommentsByOrder(T order);


}
