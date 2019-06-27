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
package de.hybris.platform.commerceservices.order.dao;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;


/**
 * Commerce DAO interface for handling {@link OrderModel}
 */
public interface CommerceOrderDao extends GenericDao<OrderModel>
{

	/**
	 * Retrieves an order associated to a quote if any, else returns null
	 *
	 * @param quote
	 *           quote model
	 * @return order
	 */
	OrderModel findOrderByQuote(QuoteModel quote);
}
