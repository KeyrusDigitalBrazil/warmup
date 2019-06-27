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
import java.util.List;
import java.util.stream.Collectors;

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


/**
 * This service reads the ProductRecoScenarios (ScenarioId) entity from the ODataService.
 */
public class RecommendationScenarioService
{
	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();

	protected ODataService oDataService;

	protected String createSAPRecommendationType(final ODataEntry entry)
	{
		return (String) entry.getProperties().get("ScenarioId");
	}

	/**
	 * @return {@link List} of {@link SAPRecommendationType} from the yMKT system.<br>
	 *         The list is sorted by {@link SAPRecommendationType#getId()}.
	 * @throws IOException
	 */
	public List<String> getRecommendationScenarios() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("ProductRecoScenarios", //
					"$orderby", "ScenarioId", //
					"$select", "ScenarioId,ScenarioDescription");
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet("ProductRecoScenarios");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			return feed.getEntries().stream() //
					.map(this::createSAPRecommendationType) //
					.collect(Collectors.toList());
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet ProductRecoScenarios.", e);
		}
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}
}
