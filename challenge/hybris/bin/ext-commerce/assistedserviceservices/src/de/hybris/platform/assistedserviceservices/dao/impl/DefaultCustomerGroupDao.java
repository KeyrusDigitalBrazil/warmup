/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedserviceservices.dao.impl;

import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedserviceservices.dao.CustomerGroupDao;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.Config;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Concrete implementation for the customer group Dao
 */
public class DefaultCustomerGroupDao extends DefaultPagedGenericDao<CustomerModel> implements CustomerGroupDao
{
	protected static final String SORT_BY_NAME_ASC = "byNameAsc";
	protected static final String SORT_BY_NAME_DESC = "byNameDesc";

	protected static final String SORT_BY_ODER_DATE_ASC = "byOrderDateAsc";
	protected static final String SORT_BY_ORDER_DATE_DESC = "byOrderDateDesc";

	protected static final String SORT_BY_CREATIONTIME_DESC = "byCreationTimeDesc";

	protected static final String GROUPS_UID = "groupsUid";
	protected static final String CURRENTDATE = "currentDate";
	protected static final String DELIVERY_STATUS = "READY_FOR_PICKUP";
	protected static final String LOGINDISABLED_PARAMETER = "loginDisabled";

	private TimeService timeService;

	private static final String CUSTOMERS_PER_STORE = "SELECT {m." + CustomerModel.PK + "}, {m." + CustomerModel.NAME + "}"
		+ " FROM {PrincipalGroupRelation as pg JOIN PrincipalGroup as p ON {pg.target} = {p.pk}"
		+ " JOIN " + CustomerModel._TYPECODE + " as m  ON {pg.source} = {m." + CustomerModel.PK + "}}"
		+ " WHERE  {p.pk} in ( ?" + GROUPS_UID + " ) "
		+ " AND {m:" + CustomerModel.LOGINDISABLED + "} = ?" + LOGINDISABLED_PARAMETER
		+ " AND ({m:" + CustomerModel.DEACTIVATIONDATE + "} IS NULL"
		+ " OR {m:" + CustomerModel.DEACTIVATIONDATE + "} > ?" + CURRENTDATE + ") ";

	private final String CUSTOMERS_REP_CONSIGNMENT = "SELECT {cu." + CustomerModel.PK + "}, MAX({co." + ConsignmentModel.CREATIONTIME + "})"
		+ " FROM {" + ConsignmentModel._TYPECODE + " as co "
		+ " JOIN " + OrderModel._TYPECODE + " as o ON {o:" + OrderModel.PK + "} = {co:" + ConsignmentModel.ORDER + "} "
		+ " JOIN " + CustomerModel._TYPECODE + " as cu ON {o:" + OrderModel.USER + "} = {cu:" + CustomerModel.PK + "} "
		+ " JOIN " + ConsignmentStatus._TYPECODE + " as cs ON {co:" + ConsignmentModel.STATUS + "} = {cs:pk}} "
		+ " WHERE {cs.code} = '"+ getDeliveryStatus() + "'"
		+ " AND { co." + ConsignmentModel.DELIVERYPOINTOFSERVICE + "} in ( ?" + GROUPS_UID + " ) "
		+ " AND {cu:" + CustomerModel.LOGINDISABLED + "} = ?" + LOGINDISABLED_PARAMETER 
		+ " AND ({cu:" + CustomerModel.DEACTIVATIONDATE + "} IS NULL"
		+ " OR {cu:" + CustomerModel.DEACTIVATIONDATE + "} > ?" + CURRENTDATE + ") "
		+ " GROUP BY {cu.pk}";

	private static final String SORT_CUSTOMERS_BY_NAME_ASC = " ORDER BY {m." + CustomerModel.NAME + "} ASC";

	private static final String SORT_CUSTOMERS_BY_NAME_DESC = " ORDER BY {m." + CustomerModel.NAME + "} DESC";


	private static final String SORT_ORDERS_BY_ASC = " ORDER BY MAX({co." + ConsignmentModel.CREATIONTIME + "}) ASC";

	private static final String SORT_ORDERS_BY_DESC = " ORDER BY MAX({co." + ConsignmentModel.CREATIONTIME + "}) DESC";


	public DefaultCustomerGroupDao(final String typeCode)
	{
		super(typeCode);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByGroups(final List<UserGroupModel> groups,
			final PageableData pageableData)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(CURRENTDATE, getTimeService().getCurrentTime());
		params.put(GROUPS_UID, groups);
		params.put(LOGINDISABLED_PARAMETER, Boolean.FALSE);
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_BY_NAME_ASC, createQuery(CUSTOMERS_PER_STORE, SORT_CUSTOMERS_BY_NAME_ASC)),
				createSortQueryData(SORT_BY_NAME_DESC, createQuery(CUSTOMERS_PER_STORE, SORT_CUSTOMERS_BY_NAME_DESC)));
		return getPagedFlexibleSearchService().search(sortQueries, SORT_BY_NAME_ASC, params, pageableData);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByConsignmentsInPointOfServices(
			final List<PointOfServiceModel> poses, final PageableData pageableData)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(CURRENTDATE, getTimeService().getCurrentTime());
		params.put(GROUPS_UID, poses);
		params.put(LOGINDISABLED_PARAMETER, Boolean.FALSE);

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_BY_ODER_DATE_ASC, createQuery(CUSTOMERS_REP_CONSIGNMENT, SORT_ORDERS_BY_ASC)),
				createSortQueryData(SORT_BY_ORDER_DATE_DESC, createQuery(CUSTOMERS_REP_CONSIGNMENT, SORT_ORDERS_BY_DESC)));

		return getPagedFlexibleSearchService().search(sortQueries, SORT_BY_ORDER_DATE_DESC, params, pageableData);
	}

	protected String createQuery(final String... queryClauses)
	{
		final StringBuilder queryBuilder = new StringBuilder();

		for (final String queryClause : queryClauses)
		{
			queryBuilder.append(queryClause);
		}

		return queryBuilder.toString();
	}

	protected String getDeliveryStatus()
	{
		return Config.getString(AssistedserviceservicesConstants.DEFAULT_BOPIS_STATUS, DELIVERY_STATUS);
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(TimeService timeService)
	{
		this.timeService = timeService;
	}
}
