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

import de.hybris.platform.apiregistryservices.dao.CredentialDao;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the interface {@link CredentialDao}
 */
public class DefaultCredentialDao extends AbstractItemDao implements CredentialDao
{
	protected static final String GET_ALL_EXPOSED_BY_CLIENT_QUERY = "select {" + ExposedOAuthCredentialModel.PK + "} from {"
			+ ExposedOAuthCredentialModel._TYPECODE + " as b1 JOIN " + OAuthClientDetailsModel._TYPECODE + " as b2 ON {b1:"
			+ ExposedOAuthCredentialModel.OAUTHCLIENTDETAILS + "}={b2:" + OAuthClientDetailsModel.PK + "}} where {b2:"
			+ OAuthClientDetailsModel.CLIENTID + "}=?" + OAuthClientDetailsModel.CLIENTID;

	@Override
	public List<ExposedOAuthCredentialModel> getAllExposedOAuthCredentialsByClientId(final String clientId)
	{
		final Map queryParams = new HashMap();
		queryParams.put(OAuthClientDetailsModel.CLIENTID, clientId);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_ALL_EXPOSED_BY_CLIENT_QUERY, queryParams);
		final SearchResult<ExposedOAuthCredentialModel> result = getFlexibleSearchService().search(query);

		return result.getResult();
	}
}
