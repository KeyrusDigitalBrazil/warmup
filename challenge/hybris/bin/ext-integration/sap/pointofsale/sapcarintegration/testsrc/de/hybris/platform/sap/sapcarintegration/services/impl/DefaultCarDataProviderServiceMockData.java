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
package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.FeedMetadata;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.core.ep.feed.FeedMetadataImpl;
import org.apache.olingo.odata2.core.ep.feed.ODataDeltaFeedImpl;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;


public class DefaultCarDataProviderServiceMockData implements CarDataProviderService
{


	@Override
	public ODataFeed readLocaltionFeed(String location)
	{

		ODataFeed feed;

		try
		{

			final InputStream edmContent = getClass().getResourceAsStream("/test/services/serviceMetadata.xml");
			Edm edm = EntityProvider.readMetadata(edmContent, false);

			final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

			InputStream content = getClass().getResourceAsStream("/test/data/storeLocation.json");

			feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
					entityContainer.getEntitySet("RetailLocationQueryResults"), content, EntityProviderReadProperties.init().build());

		}
		catch (Exception e)
		{
			throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
		}

		return feed;
	}


	@Override
	public ODataFeed readHeaderFeed(String customerNumber, PaginationData paginationData)
	{
		ODataFeed feed;

		try
		{

			final InputStream edmContent = getClass().getResourceAsStream("/test/services/serviceMetadata.xml");
			Edm edm = EntityProvider.readMetadata(edmContent, false);

			final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

			InputStream content = getClass().getResourceAsStream("/test/data/carOrders.json");

			feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
					entityContainer.getEntitySet("POSSalesQueryResults"), content, EntityProviderReadProperties.init().build());

		}
		catch (Exception e)
		{

			throw new RuntimeException("Error while reading Header Feed", e); //NOPMD
		}

		// to comply withestraction process
		FeedMetadataImpl feedMetadata = new FeedMetadataImpl();
		feedMetadata.setInlineCount(10);

		return new ODataDeltaFeedImpl(feed.getEntries(), feedMetadata);
	}


	@Override
	public ODataFeed readHeaderFeed(String businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{

		ODataFeed feed;

		try
		{

			final InputStream edmContent = getClass().getResourceAsStream("/test/services/serviceMetadata.xml");
			Edm edm = EntityProvider.readMetadata(edmContent, false);

			final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

			InputStream content = getClass().getResourceAsStream("/test/data/carOrder.json");

			feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
					entityContainer.getEntitySet("POSSalesQueryResults"), content, EntityProviderReadProperties.init().build());



		}
		catch (Exception e)
		{
			throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
		}

		return feed;
	}


	@Override
	public ODataFeed readItemFeed(String businessDayDate, String storeId, Integer transactionIndex, String customerNumber)
	{
		ODataFeed feed;

		try
		{

			final InputStream edmContent = getClass().getResourceAsStream("/test/services/serviceMetadata.xml");
			Edm edm = EntityProvider.readMetadata(edmContent, false);

			final EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();

			InputStream content = getClass().getResourceAsStream("/test/data/carOrderItems.json");
			feed = EntityProvider.readFeed(SapcarintegrationConstants.APPLICATION_JSON,
					entityContainer.getEntitySet("POSSalesQueryResults"), content, EntityProviderReadProperties.init().build());
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error while reading Header Feed", e);//NOPMD
		}

		return feed;
	}




}
