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
package com.hybris.ymkt.recommendation.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationItemDataSourceType;


/**
 * This service reads the ItemSourceTypes entity from the ODataService.
 */
public class RecommendationDataSourceTypeService
{
	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();

	protected ODataService oDataService;

	protected SAPRecommendationItemDataSourceType createSAPRecommendationItemDataSourceType(final ODataEntry entry)
	{
		final SAPRecommendationItemDataSourceType itemDSType = new SAPRecommendationItemDataSourceType();
		itemDSType.setId((String) entry.getProperties().get("ItemSourceObjectType"));
		itemDSType.setDescription((String) entry.getProperties().get("ItemSourceTypeDescription"));
		return itemDSType;
	}

	/**
	 * @return {@link List} of {@link SAPRecommendationItemDataSourceType} from the yMKT system.<br>
	 *         The list is sorted by {@link SAPRecommendationItemDataSourceType#getId()}.
	 * @throws IOException
	 */
	@Nonnull
	public List<SAPRecommendationItemDataSourceType> getItemDataSourceTypes() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("ItemSourceTypes", //
					"$select", "ItemSourceObjectType,ItemSourceTypeDescription");
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet("ItemSourceTypes");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			return feed.getEntries().stream() //
					.map(this::createSAPRecommendationItemDataSourceType) //
					.sorted(Comparator.comparing(SAPRecommendationItemDataSourceType::getId)) //
					.collect(Collectors.toList());
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet ItemSourceTypes.", e);
		}
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}
}
