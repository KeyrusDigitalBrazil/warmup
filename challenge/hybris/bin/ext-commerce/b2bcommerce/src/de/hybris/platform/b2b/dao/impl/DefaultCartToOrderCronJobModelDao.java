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

import de.hybris.platform.b2b.dao.CartToOrderCronJobModelDao;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the {@link CartToOrderCronJobModelDao}
 * 
 * 
 * @spring.bean cartToOrderCronJobModelDao
 */
public class DefaultCartToOrderCronJobModelDao extends DefaultGenericDao<CartToOrderCronJobModel> implements
		CartToOrderCronJobModelDao
{
	/**
	 * DefaultGenericDao is only usable when typecode is set.
	 */
	public DefaultCartToOrderCronJobModelDao()
	{
		super(CartToOrderCronJobModel._TYPECODE);
	}

	@Override
	public CartToOrderCronJobModel findCartToOrderCronJob(final String code)
	{
		final List<CartToOrderCronJobModel> jobs = this.find(Collections.singletonMap(CartToOrderCronJobModel.CODE, code));
		return (jobs.iterator().hasNext() ? jobs.iterator().next() : null);
	}

	@Override
	public List<CartToOrderCronJobModel> findCartToOrderCronJobs(final UserModel user)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.USER, user);
		attr.put(CartToOrderCronJobModel.ACTIVE, Boolean.TRUE);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {soj:pk} FROM { ").append(CartToOrderCronJobModel._TYPECODE).append(" as soj JOIN ")
				.append(CartModel._TYPECODE).append(" as c ON {soj.cart} = {c:pk} } ")
				.append(" WHERE {soj:active} = ?active and {c:user} = ?user ORDER BY {c.date} DESC");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<CartToOrderCronJobModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}
}
