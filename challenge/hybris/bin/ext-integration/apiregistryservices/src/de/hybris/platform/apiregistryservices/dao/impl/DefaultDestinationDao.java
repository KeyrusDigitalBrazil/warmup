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

import de.hybris.platform.apiregistryservices.dao.DestinationDao;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link DestinationDao}
 *
 * @param <T>
 *           the type parameter which extends the {@link AbstractDestinationModel} type
 */
public class DefaultDestinationDao<T extends AbstractDestinationModel> extends AbstractItemDao implements DestinationDao
{
	protected static final String ID_PARAMETER = "id";
	protected static final String TARGET_PARAMETER = "destinationTargetId";
	protected static final String CHANNEL_PARAMETER = "channel";
	protected static final String ACTIVE_PARAMETER = "active";

	protected static final String AND = " AND {";
	protected static final String GET_ALL_D_QUERY = "select {pk} from {" + AbstractDestinationModel._TYPECODE + "} ";


	protected static final String GET_ALL_D_BY_TARGET = "SELECT {pk} FROM {" + AbstractDestinationModel._TYPECODE + " AS d JOIN "
			+ DestinationTargetModel._TYPECODE + " AS t ON {d:" + AbstractDestinationModel.DESTINATIONTARGET + "}={t:"
			+ DestinationTargetModel.PK + "}} WHERE {t:" + DestinationTargetModel.ID + "}=?destinationTargetId";

	protected static final String ID_CLAUSE = "WHERE {" + AbstractDestinationModel.ID + "}=?" + ID_PARAMETER;
	protected static final String AND_ACTIVE_CLAUSE = AND + "d:" + AbstractDestinationModel.ACTIVE + "}=?" + ACTIVE_PARAMETER;

	protected static final String GET_ALL_D_BY_CHANNEL = "SELECT {pk} FROM {" + AbstractDestinationModel._TYPECODE + " AS d JOIN "
			+ DestinationTargetModel._TYPECODE + " AS t ON {d:" + AbstractDestinationModel.DESTINATIONTARGET + "}={t:"
			+ DestinationTargetModel.PK + "}} WHERE {t:" + DestinationTargetModel.DESTINATIONCHANNEL + "}=?" + CHANNEL_PARAMETER;


	protected static final String GET_ALL_EXPOSED_BY_CLIENT_QUERY = "select {" + AbstractDestinationModel.PK + "} " + "from {"
			+ AbstractDestinationModel._TYPECODE + " as destination " + "JOIN " + ExposedOAuthCredentialModel._TYPECODE
			+ " as credential ON {destination:" + AbstractDestinationModel.CREDENTIAL + "}={credential:"
			+ ExposedOAuthCredentialModel.PK + "} " + "JOIN " + OAuthClientDetailsModel._TYPECODE + " as oauth ON {credential:"
			+ ExposedOAuthCredentialModel.OAUTHCLIENTDETAILS + "}={oauth:" + OAuthClientDetailsModel.PK + "}} " + "where {oauth:"
			+ OAuthClientDetailsModel.CLIENTID + "}=?" + OAuthClientDetailsModel.CLIENTID + AND + " destination:"
			+ AbstractDestinationModel.ACTIVE + "}=?" + ACTIVE_PARAMETER;

	@Override
	public List<T> getDestinationsByChannel(final DestinationChannel channel)
	{
		final Map queryParams = new HashMap();
		queryParams.put(CHANNEL_PARAMETER, channel);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_D_BY_CHANNEL, queryParams);
		final SearchResult<T> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	@Override
	public List<T> getDestinationsByDestinationTargetId(final String destinationTargetId)
	{
		final Map queryParams = new HashMap();
		queryParams.put(TARGET_PARAMETER, destinationTargetId);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_D_BY_TARGET, queryParams);
		final SearchResult<T> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	@Override
	public T getDestinationById(String id)
	{
		final Map queryParams = new HashMap();
		queryParams.put(ID_PARAMETER, id);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_D_QUERY + ID_CLAUSE, queryParams);
		final SearchResult<T> result = getFlexibleSearchService().search(query);
		return result.getResult().isEmpty() ? null : result.getResult().get(0);
	}

	@Override
	public List<T> findAllDestinations()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_D_QUERY);
		return getFlexibleSearchService().<T> search(query).getResult();
	}


	@Override
	public List<ExposedDestinationModel> findActiveExposedDestinationsByClientId(final String clientId)
	{
		final Map queryParams = new HashMap();
		queryParams.put(OAuthClientDetailsModel.CLIENTID, clientId);
		queryParams.put(ACTIVE_PARAMETER, true);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_EXPOSED_BY_CLIENT_QUERY, queryParams);
		final SearchResult<ExposedDestinationModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	@Override
	public List<ExposedDestinationModel> findActiveExposedDestinationsByChannel(final DestinationChannel channel)
	{
		final Map queryParams = new HashMap();
		queryParams.put(CHANNEL_PARAMETER, channel);
		queryParams.put(ACTIVE_PARAMETER, true);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_D_BY_CHANNEL + AND_ACTIVE_CLAUSE, queryParams);
		final SearchResult<ExposedDestinationModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

}
