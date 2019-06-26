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

import de.hybris.platform.b2b.dao.B2BBookingLineEntryDao;
import de.hybris.platform.b2b.enums.B2BBookingLineStatus;
import de.hybris.platform.b2b.model.B2BBookingLineEntryModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link B2BBookingLineEntryDao}
 *
 *
 * @spring.bean b2bBookingLineEntryDao
 */
public class DefaultB2BBookingLineEntryDao extends DefaultGenericDao<B2BBookingLineEntryModel> implements B2BBookingLineEntryDao
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BBookingLineEntryDao.class);

	/**
	 * DefaultGenericDao is only usable when typecode is set.
	 */
	public DefaultB2BBookingLineEntryDao()
	{
		super(B2BBookingLineEntryModel._TYPECODE);
	}

	/**
	 * @deprecated Since 4.4. Use {@link #findTotalCostByCostCenterAndDate(B2BCostCenterModel,Date,Date)} instead
	 */
	@Deprecated
	@Override
	public Double getTotalCost(final B2BCostCenterModel costCenter, final Date startDate, final Date endDate)
	{
		return findTotalCostByCostCenterAndDate(costCenter, startDate, endDate);
	}

	@Override
	public Double findTotalCostByCostCenterAndDate(final B2BCostCenterModel costCenter, final Date startDate, final Date endDate)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(B2BBookingLineEntryModel.COSTCENTER, costCenter);
		attr.put("startDate", startDate);
		attr.put("endDate", endDate);
		final StringBuilder sql = new StringBuilder();
		sql.append("select sum({bl.amount}) from {").append(B2BBookingLineEntryModel._TYPECODE)
				.append(" as bl} where {bl.costCenter} = ?costCenter ")
				.append(" and {bl.bookingDate} >= ?startDate and {bl.bookingDate} <= ?endDate");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		query.setResultClassList(Collections.singletonList(Double.class));
		final SearchResult<Double> result = getFlexibleSearchService().search(query);
		final Double total = result.getResult().get(0);
		return (total == null ? new Double(0D) : total);
	}

	/**
	 * @deprecated Since 4.4. Use findOpenBookingLineEntries(List, Date)} instead
	 */
	@Deprecated
	@Override
	public List<B2BBookingLineEntryModel> getOpenBookingLineEntries(final List<B2BCostCenterModel> costCenters,
			final Date bookingDate)
	{
		return findOpenBookingLineEntriesByCostCenterAndDate(costCenters, bookingDate);
	}

	@Override
	public List<B2BBookingLineEntryModel> findOpenBookingLineEntriesByCostCenterAndDate(
			final List<B2BCostCenterModel> costCenters, final Date bookingDate)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(B2BBookingLineEntryModel.COSTCENTER, costCenters);
		attr.put(B2BBookingLineEntryModel.BOOKINGSTATUS, B2BBookingLineStatus.OPEN);
		attr.put(B2BBookingLineEntryModel.BOOKINGDATE, bookingDate);
		final StringBuilder sql = new StringBuilder();
		sql.append("select {bl.pk} from {").append(B2BBookingLineEntryModel._TYPECODE)
				.append(" as bl} where {bl.costCenter} in ( ?costCenter ) and ")
				.append(" {bl.bookingDate} <= ?bookingDate and {bl.bookingstatus} = ?bookingStatus order by {bl.bookingDate} DESC");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<B2BBookingLineEntryModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}
}
