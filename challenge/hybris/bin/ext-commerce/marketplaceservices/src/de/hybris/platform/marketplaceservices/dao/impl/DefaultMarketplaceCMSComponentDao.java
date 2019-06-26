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
package de.hybris.platform.marketplaceservices.dao.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.daos.impl.DefaultCMSComponentDao;
import de.hybris.platform.marketplaceservices.dao.MarketplaceCMSComponentDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.FlexibleSearchUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class DefaultMarketplaceCMSComponentDao extends DefaultCMSComponentDao implements MarketplaceCMSComponentDao
{

	@Override
	public List<AbstractCMSComponentModel> findCMSComponentsByContentSlot(final String contentSlotId,
			final List<CatalogVersionModel> catalogVersions)
	{
		final StringBuilder queryBuilder = new StringBuilder();
		final Map<String, Object> queryParameters = new HashMap<>();

		queryBuilder.append("SELECT {component:" + AbstractCMSComponentModel.PK + "} " + " FROM " + "{"
				+ AbstractCMSComponentModel._TYPECODE + " as component " + " JOIN " + AbstractCMSComponentModel._ELEMENTSFORSLOT
				+ " AS rel ON {rel:target}={component:" + AbstractCMSComponentModel.PK + "} " + " JOIN " + ContentSlotModel._TYPECODE
				+ " AS slot ON {rel:source}={slot:" + ContentSlotModel.PK + "}} " + " WHERE {slot:" + ContentSlotModel.UID + "}=?"
				+ AbstractCMSComponentModel.SLOTS + "" + " AND ");
		queryBuilder.append(//
				FlexibleSearchUtils.buildOracleCompatibleCollectionStatement(//
						"{component:" + AbstractCMSComponentModel.CATALOGVERSION + "} in (?" + CATALOG_VERSIONS_QUERY_PARAM + ")", //
						CATALOG_VERSIONS_QUERY_PARAM, "OR", catalogVersions, queryParameters));

		queryParameters.put(AbstractCMSComponentModel.SLOTS, contentSlotId);

		final SearchResult<AbstractCMSComponentModel> result = search(queryBuilder.toString(), queryParameters);
		return result.getResult();
	}

}
