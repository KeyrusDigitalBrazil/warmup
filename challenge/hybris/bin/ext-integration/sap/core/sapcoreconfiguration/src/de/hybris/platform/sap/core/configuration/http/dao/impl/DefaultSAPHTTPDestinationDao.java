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
package de.hybris.platform.sap.core.configuration.http.dao.impl;

import java.util.List;

import de.hybris.platform.sap.core.configuration.http.dao.SAPHttpDestinationDao;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default implementation of the DAO for SAP HTTP Destinations.
 */
public class DefaultSAPHTTPDestinationDao extends AbstractItemDao implements SAPHttpDestinationDao {

    @Override
    public List<SAPHTTPDestinationModel> findHttpDestinations() {
        final String queryString = //
        "SELECT {p:" + SAPHTTPDestinationModel.PK + "} "//
                + "FROM {" + SAPHTTPDestinationModel._TYPECODE + " AS p} ";
        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        final SearchResult<SAPHTTPDestinationModel> result = search(query);
        return result.getResult();
    }

    @Override
    public List<SAPHTTPDestinationModel> findHttpDestinationsByName(final String destinationName) {
        final String queryString = //
        "SELECT {p:" + SAPHTTPDestinationModel.PK
                + "}" //
                + "FROM {" + SAPHTTPDestinationModel._TYPECODE
                + " AS p} "//
                + "WHERE " + "{p:" + SAPHTTPDestinationModel.HTTPDESTINATIONNAME
                + "}=?destinationName ";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        query.addQueryParameter("destinationName", destinationName);

        final SearchResult<SAPHTTPDestinationModel> result = search(query);
        return result.getResult();
    }

}
