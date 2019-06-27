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
package de.hybris.platform.apiregistryservices.dao.impl;

import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the interface {@link EventConfigurationDao}
 */
public class DefaultEventConfigurationDao extends AbstractItemDao implements EventConfigurationDao
{
	protected static final String EXPORTFLAG_PARAMETER = "exportFlag";
	protected static final String EVENTCLASS_PARAMETER = "eventClass";
	protected static final String EVENTDESTINATION_PARAMETER = "destinationTargetId";
	protected static final String EVENTCHANNEL_PARAMETER = "channel";

	protected static final String GET_ALL_EC_QUERY = "select {pk} from {" + EventConfigurationModel._TYPECODE + "}" + " WHERE {"
			+ EventConfigurationModel.EXPORTFLAG + "}=?" + EXPORTFLAG_PARAMETER;

	protected static final String GET_ALL_EC_BY_TARGET = "SELECT {pk} FROM {" + EventConfigurationModel._TYPECODE + " AS e JOIN "
			+ DestinationTargetModel._TYPECODE + " AS t ON {e:" + EventConfigurationModel.DESTINATIONTARGET + "}={t:"
			+ DestinationTargetModel.PK + "}} WHERE {t:" + DestinationTargetModel.ID + "}=?" + EVENTDESTINATION_PARAMETER + " AND {e:"
			+ EventConfigurationModel.EXPORTFLAG + "}=?" + EXPORTFLAG_PARAMETER;

	protected static final String GET_ALL_EC_BY_CHANNEL = "SELECT {pk} FROM {" + EventConfigurationModel._TYPECODE + " AS e JOIN "
			+ DestinationTargetModel._TYPECODE + " AS t ON {e:" + EventConfigurationModel.DESTINATIONTARGET + "}={t:"
			+ DestinationTargetModel.PK + "}} WHERE {t:" + DestinationTargetModel.DESTINATIONCHANNEL + "}=?" + EVENTCHANNEL_PARAMETER + " AND {e:"
			+ EventConfigurationModel.EXPORTFLAG + "}=?" + EXPORTFLAG_PARAMETER;

	protected static final String WHERE_EVENT_CLAUSE = " AND {" + EventConfigurationModel.EVENTCLASS + "}=?"
			+ EVENTCLASS_PARAMETER;

	@Override
	public List<EventConfigurationModel> findActiveEventConfigsByClass(final String eventClass)
	{
		final Map queryParams = new HashMap();
		queryParams.put(EXPORTFLAG_PARAMETER, true);
		queryParams.put(EVENTCLASS_PARAMETER, eventClass);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_EC_QUERY + WHERE_EVENT_CLAUSE, queryParams);
		final SearchResult<EventConfigurationModel> searchResult = getFlexibleSearchService().search(query);
		return searchResult.getResult();
	}

	@Override
	public List<EventConfigurationModel> findActiveEventConfigsByDestinationTargetId(final String destinationTargetId)
	{
		final Map queryParams = new HashMap();
		queryParams.put(EXPORTFLAG_PARAMETER, true);
		queryParams.put(EVENTDESTINATION_PARAMETER, destinationTargetId);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_EC_BY_TARGET, queryParams);
		final SearchResult<EventConfigurationModel> searchResult = getFlexibleSearchService().search(query);
		return searchResult.getResult();
	}

	@Override
	public List<EventConfigurationModel> findActiveEventConfigsByChannel(final DestinationChannel channel)
	{
		final Map queryParams = new HashMap();
		queryParams.put(EXPORTFLAG_PARAMETER, true);
		queryParams.put(EVENTCHANNEL_PARAMETER, channel);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_EC_BY_CHANNEL, queryParams);
		final SearchResult<EventConfigurationModel> searchResult = getFlexibleSearchService().search(query);
		return searchResult.getResult();
	}
}
