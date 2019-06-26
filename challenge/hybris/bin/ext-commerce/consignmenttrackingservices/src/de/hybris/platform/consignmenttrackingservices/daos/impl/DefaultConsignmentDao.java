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
package de.hybris.platform.consignmenttrackingservices.daos.impl;

import de.hybris.platform.consignmenttrackingservices.daos.ConsignmentDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;
import java.util.Optional;


/**
 * The default implementation of ConsignmentDao
 */
public class DefaultConsignmentDao extends DefaultGenericDao<ConsignmentModel> implements ConsignmentDao
{

	private static final String CONSIGNMENT_CODE = "consignmentCode";

	private static final String ORDER_CODE = "orderCode";

	private static final String FQL = "SELECT {c:" + ConsignmentModel.PK + "} FROM {" + ConsignmentModel._TYPECODE + " AS c JOIN "
			+ OrderModel._TYPECODE + " AS o ON {c:" + ConsignmentModel.ORDER + "} = {o:" + OrderModel.PK + "}} WHERE {o:"
			+ OrderModel.CODE + "} = ?orderCode AND {c:" + ConsignmentModel.CODE + "} = ?consignmentCode";

	private static final String FQL_ORDER = "SELECT {c:" + ConsignmentModel.PK + "} FROM {" + ConsignmentModel._TYPECODE
			+ " AS c JOIN " + OrderModel._TYPECODE + " AS o ON {c:" + ConsignmentModel.ORDER + "} = {o:" + OrderModel.PK
			+ "}} WHERE {o:" + OrderModel.CODE + "} = ?orderCode";


	public DefaultConsignmentDao()
	{
		super(ConsignmentModel._TYPECODE);
	}

	@Override
	public Optional<ConsignmentModel> findConsignmentByCode(final String orderCode, final String consignmentCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FQL);
		query.addQueryParameter(CONSIGNMENT_CODE, consignmentCode);
		query.addQueryParameter(ORDER_CODE, orderCode);

		return Optional.ofNullable(getFlexibleSearchService().searchUnique(query));
	}

	@Override
	public List<ConsignmentModel> findConsignmentsByOrder(final String orderCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FQL_ORDER);
		query.addQueryParameter(ORDER_CODE, orderCode);

		return getFlexibleSearchService().<ConsignmentModel> search(query).getResult();

	}

}
