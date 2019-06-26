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
package de.hybris.platform.sap.sapcarintegration.services;

import java.util.List;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarStoreAddress;


/**
 * Interface providing access to methods to extract relevant order information for a given CAR instance via OData
 */
public interface CarOrderHistoryExtractorService
{

	/**
	 * extract the address information from store location
	 * 
	 * @param feed
	 * @return
	 */
	abstract CarStoreAddress extractStoreLocation(final ODataFeed feed);

	/**
	 * 
	 * @param feed
	 * @return
	 */
	abstract CarOrderHistoryData extractOrder(ODataFeed feed);

	/**
	 * Extract order item entries
	 * 
	 * @param order
	 * @param readItemFeed
	 */
	abstract void extractOrderEntries(CarOrderHistoryData order, ODataFeed readItemFeed);

	/**
	 * 
	 * @param feed
	 * @param paginationData
	 * @return
	 */
	abstract List<CarOrderHistoryData> extractOrders(ODataFeed feed, PaginationData paginationData);
}
