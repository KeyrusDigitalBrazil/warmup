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
package de.hybris.platform.chinesepspwechatpayservices.dao.impl;

import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Optional;

import org.junit.Assert;


public class DefaultWeChatPayOrderDao extends DefaultGenericDao<OrderModel> implements WeChatPayOrderDao
{


	private static final String FIND_ORDERS_BY_CODE_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME
			+ "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE + "} = ?code AND {"
			+ OrderModel.VERSIONID + "} IS NULL";

	private static final String ORDER_CODE = "code";

	public DefaultWeChatPayOrderDao()
	{
		super(OrderModel._TYPECODE);
	}

	@Override
	public Optional<OrderModel> findOrderByCode(final String code)
	{
		Assert.assertNotNull(code, "Order code is null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_STORE_QUERY);
		query.addQueryParameter(ORDER_CODE, code);
		return Optional.ofNullable(getFlexibleSearchService().searchUnique(query));
	}

}
