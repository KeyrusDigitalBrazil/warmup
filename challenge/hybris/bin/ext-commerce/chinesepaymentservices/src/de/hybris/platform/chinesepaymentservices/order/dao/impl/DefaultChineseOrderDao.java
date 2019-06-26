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
package de.hybris.platform.chinesepaymentservices.order.dao.impl;

import de.hybris.platform.chinesepaymentservices.order.dao.ChineseOrderDao;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.daos.impl.DefaultOrderDao;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.task.TaskModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DefaultChineseOrderDao extends DefaultOrderDao implements ChineseOrderDao
{

	@Override
	public List<AbstractOrderModel> findUnpaidOrders(final long millisecond)
	{
		final String fsq = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.DATE
				+ "} < ?today  and {" + OrderModel.PAYMENTSTATUS + "} in ({{ select {pk} from {" + PaymentStatus._TYPECODE
				+ "} where {code}='NOTPAID'}}) and {" + OrderModel.STATUS + "} in ({{ select {pk} from {" + OrderStatus._TYPECODE
				+ "} where {code}!='CANCELLED'}})";
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final Date validTime = new Date(cal.getTimeInMillis() - millisecond);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter("today", validTime);
		final List<AbstractOrderModel> unpaidOrders = getFlexibleSearchService().<AbstractOrderModel> search(query).getResult();
		return unpaidOrders;
	}

	@Override
	public AbstractOrderModel findOrderByCode(final String orderCode)
	{
		final String fsq = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE
				+ "}=?orderCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter("orderCode", orderCode);
		final List<AbstractOrderModel> orders = getFlexibleSearchService().<AbstractOrderModel> search(query).getResult();
		if (orders != null && !orders.isEmpty())
		{
			return orders.get(0);
		}
		return null;
	}

	@Override
	public TaskModel findSubmitOrderEventTask(final String orderCode)
	{
		final String fsq = "SELECT {t:pk} FROM {" + TaskModel._TYPECODE + " as t left join " + OrderModel._TYPECODE
				+ " as o on {t.contextItem} = {o.pk}} where {o.code} = ?orderCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter("orderCode", orderCode);
		final List<TaskModel> results = getFlexibleSearchService().<TaskModel> search(query).getResult();
		if (results.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found " + results.size() + " tasks with the orderCode \'" + orderCode + "\'");
		}
		else
		{
			return results.isEmpty() ? null : results.get(0);
		}

	}

}
