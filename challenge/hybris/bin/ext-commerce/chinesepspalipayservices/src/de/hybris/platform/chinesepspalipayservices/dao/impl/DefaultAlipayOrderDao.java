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
package de.hybris.platform.chinesepspalipayservices.dao.impl;

import de.hybris.platform.chinesepspalipayservices.dao.AlipayOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import org.junit.Assert;


/**
 * Default implementation of {@link AlipayOrderDao}
 */
public class DefaultAlipayOrderDao extends DefaultGenericDao<OrderModel> implements AlipayOrderDao
{


	private static final String FIND_ORDERS_BY_CODE_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME
			+ "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE + "} = ?code AND {"
			+ OrderModel.VERSIONID + "} IS NULL";

	private static final String ORDER_CODE = "code";

	public DefaultAlipayOrderDao()
	{
		super(OrderModel._TYPECODE);
	}

	@Override
	public OrderModel findOrderByCode(final String code)
	{
		Assert.assertNotNull(code, "Order code is null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_STORE_QUERY);
		query.addQueryParameter(ORDER_CODE, code);
		return getFlexibleSearchService().searchUnique(query);
	}

}

