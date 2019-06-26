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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;
import com.hybris.ymkt.recommendation.dao.OfferRecommendation;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationContext;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.ContextParam;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.Result;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.SAPOfferContentPosition;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationItemDataSourceType;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * This service provides offer recommendations and helper values from the CUAN_OFFER_DISCOVERY_SRV oData service
 */
public class OfferDiscoveryService
{
	protected static final String ACCEPT = "Accept";

	private static final Logger LOG = LoggerFactory.getLogger(OfferDiscoveryService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();
	protected static final String RECOMMENDATIONS = "Recommendations";

	protected CartService cartService;
	protected CommonI18NService commonI18NService;
	protected ODataService oDataService;
	protected RecentViewedItemsService recentViewedItemsService;
	protected RecommendationBufferService recommendationBufferService;
	protected UserContextService userContextService;

	protected Map<String, Object> convertMapBasketObject(final BasketObject bo)
	{
		final Map<String, Object> leadingObject = new HashMap<>();
		leadingObject.put("BasketObjectType", bo.getBasketObjectType());
		leadingObject.put("BasketObjectId", bo.getBasketObjectId());
		return leadingObject;
	}

	protected Map<String, Object> convertMapContextParams(final ContextParam cp)
	{
		final Map<String, Object> contextParam = new HashMap<>();
		contextParam.put("ContextId", cp.getContextId());
		contextParam.put("Name", cp.getName());
		contextParam.put("Value", cp.getValue());
		return contextParam;
	}

	protected Map<String, Object> convertMapLeadingObject(final LeadingObject lo)
	{
		final Map<String, Object> leadingObject = new HashMap<>();
		leadingObject.put("LeadingObjectType", lo.getLeadingObjectType());
		leadingObject.put("LeadingObjectId", lo.getLeadingObjectId());
		return leadingObject;
	}

	/**
	 * Represents one offer recommendation built from odata Result
	 *
	 * @param result
	 *           {@link Result}
	 * @return {@link OfferRecommendation}
	 */
	public OfferRecommendation createOfferRecommendation(final Result result)
	{
		final OfferRecommendation offerRecommendation = new OfferRecommendation();
		offerRecommendation.setOfferId(result.getOfferId());
		offerRecommendation.setTargetLink(result.getTargetLink());
		offerRecommendation.setTargetDescription(result.getTargetDescription());
		offerRecommendation.setContentId(result.getContentId());
		offerRecommendation.setContentSource(result.getContentSource());
		offerRecommendation.setContentDescription(result.getContentDescription());
		return offerRecommendation;
	}

	/**
	 * Build ContextParams, BasketObjects and LeadingObjects filters
	 *
	 * @param context
	 *           Request parameters and filters
	 * @return OfferRecommendationScenario
	 */
	protected OfferRecommendationScenario createOfferRecommendationScenario(final OfferRecommendationContext context)
	{
		final OfferRecommendationScenario offerRecommendationScenario = new OfferRecommendationScenario(
				this.userContextService.getUserId(), this.userContextService.getUserOrigin(), context.getScenarioId());

		// Last seen products or categories
		context.getLeadingItemId().stream() //
				.map(leadingObjectId -> new LeadingObject(context.getLeadingItemDSType(), leadingObjectId))//
				.forEach(offerRecommendationScenario.getLeadingObjects()::add);

		// Basket items
		if (StringUtils.isNotBlank(context.getCartItemDSType()))
		{
			for (final String basketObjectId : this.getCartItemsFromSession())
			{
				offerRecommendationScenario.getBasketObjects().add(new BasketObject(context.getCartItemDSType(), basketObjectId));
			}
		}

		// Basket items placed in the leading items
		if (context.isIncludeCart())
		{
			for (final String leadingObjectId : this.getCartItemsFromSession())
			{
				offerRecommendationScenario.getLeadingObjects()
						.add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
			}
		}

		// Last x viewed products or categories
		if (context.isIncludeRecent())
		{
			List<String> itemIds = Collections.emptyList();
			if (SapymktrecommendationConstants.PRODUCT.equals(context.getLeadingItemType()))
			{
				itemIds = this.recentViewedItemsService.getRecentViewedProducts();
			}
			else if (SapymktrecommendationConstants.CATEGORY.equals(context.getLeadingItemType()))
			{
				itemIds = this.recentViewedItemsService.getRecentViewedCategories();
			}
			for (final String leadingObjectId : itemIds)
			{
				offerRecommendationScenario.getLeadingObjects()
						.add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
			}
		}

		// Build context parameters
		final String currentLanguage = commonI18NService.getCurrentLanguage().getIsocode().toUpperCase(Locale.ENGLISH);
		offerRecommendationScenario.getContextParams().add(new ContextParam(1, "P_COMM_MEDIUM", "ONLINE_SHOP"));
		offerRecommendationScenario.getContextParams().add(new ContextParam(2, "P_LANGUAGE", currentLanguage));

		if (StringUtils.isNotBlank(context.getContentPosition()))
		{
			offerRecommendationScenario.getContextParams().add(new ContextParam(3, "P_POSITION", context.getContentPosition()));
		}

		return offerRecommendationScenario;
	}

	protected SAPOfferContentPosition createSAPOfferContentPosition(final ODataEntry entry)
	{
		final Map<String, Object> properties = entry.getProperties();
		final SAPOfferContentPosition contentPosition = new SAPOfferContentPosition((String) properties.get("ContentPositionId"));
		contentPosition.setCommunicationMediumName(((String) properties.get("CommunicationMediumName")));
		return contentPosition;
	}

	protected SAPRecommendationItemDataSourceType createSAPRecommendationItemDataSourceType(final ODataEntry entry)
	{
		final SAPRecommendationItemDataSourceType itemDSType = new SAPRecommendationItemDataSourceType();
		itemDSType.setId((String) entry.getProperties().get("ItemSourceObjectType"));
		itemDSType.setDescription((String) entry.getProperties().get("ItemSourceTypeDescription"));
		return itemDSType;
	}

	protected String createSAPRecommendationType(final ODataEntry entry)
	{
		return (String) entry.getProperties().get("OfferRecommendationScenarioId");
	}

	protected void executeOfferRecommendation(final OfferRecommendationScenario offerRecommendationScenario) throws IOException
	{
		try
		{
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST",
					this.oDataService.createURL(RECOMMENDATIONS));
			request.getRequestProperties().put(ACCEPT, MediaType.APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", MediaType.APPLICATION_JSON);

			//Build object for JSON payload
			final Map<String, Object> offerMap = new LinkedHashMap<>();

			offerMap.put("UserId", offerRecommendationScenario.getUserId());
			offerMap.put("UserOriginId", offerRecommendationScenario.getUserOriginId());
			offerMap.put("RecommendationScenarioId", offerRecommendationScenario.getRecommendationScenarioId());

			offerMap.put("LeadingObjects",
					offerRecommendationScenario.getLeadingObjects().stream() //
							.map(this::convertMapLeadingObject) //
							.collect(Collectors.toList()));

			offerMap.put("BasketObjects",
					offerRecommendationScenario.getBasketObjects().stream() //
							.map(this::convertMapBasketObject) //
							.collect(Collectors.toList()));

			offerMap.put("ContextParams",
					offerRecommendationScenario.getContextParams().stream() //
							.map(this::convertMapContextParams) //
							.collect(Collectors.toList()));

			offerMap.put("Results", Collections.emptyList());

			final byte[] payload = this.oDataService.convertMapToJSONPayload(RECOMMENDATIONS, offerMap);
			request.setPayload(payload);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet(RECOMMENDATIONS);
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataEntry oData = EntityProvider.readEntry(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			offerRecommendationScenario.update(oData.getProperties());
		}
		catch (final IOException | EntityProviderException e)
		{
			throw new IOException("Error reading offer recommendation scenario " + offerRecommendationScenario, e);

		}
	}

	protected List<String> getCartItemsFromSession()
	{
		return this.cartService.getSessionCart().getEntries().stream() //
				.map(AbstractOrderEntryModel::getProduct) //
				.map(ProductModel::getCode) //
				.collect(Collectors.toList());
	}

	public List<SAPOfferContentPosition> getContentPositionValues() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("ContentPositions", //
					"$filter", "CommunicationMediumId eq 'ONLINE_SHOP'");

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put(ACCEPT, MediaType.APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);
			final EdmEntitySet entitySet = this.oDataService.getEntitySet("ContentPositions");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			return feed.getEntries().stream() //
					.map(this::createSAPOfferContentPosition) //
					.collect(Collectors.toList());
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet OfferRecommendationScenarios.", e);
		}
	}

	/**
	 * Main method to trigger offer recommendation retrieval
	 *
	 * @param context
	 *           Request parameters and filters
	 * @return {@link List} of OfferRecommendation
	 */
	public List<OfferRecommendation> getOfferRecommendations(final OfferRecommendationContext context)
	{
		try
		{
			//Collect data needed to make offer request
			final OfferRecommendationScenario offerRecommendationScenario = this.createOfferRecommendationScenario(context);

			//trigger backend request
			this.executeOfferRecommendation(offerRecommendationScenario);

			//convert odata results to list for UI display
			return offerRecommendationScenario.getResults().stream() //
					.map(this::createOfferRecommendation) //
					.collect(Collectors.toList());
		}
		catch (final IOException e)
		{
			LOG.error("Error reading offer recommendations from backend using scenarioId {} and context {}", context.getScenarioId(),
					context, e);
			return Collections.emptyList();
		}
	}

	/**
	 * @return {@link List} of {@link SAPRecommendationType} from the yMKT system.<br>
	 *         The list is sorted by {@link SAPRecommendationType#getId()}.
	 * @throws IOException
	 */
	@Nonnull
	public List<String> getOfferRecommendationScenarios() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("OfferRecommendationScenarios", //
					"$orderby", "OfferRecommendationScenarioId", //
					"$select", "OfferRecommendationScenarioId,OfferRecommendationScenarioName");

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put(ACCEPT, MediaType.APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet("OfferRecommendationScenarios");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(MediaType.APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			return feed.getEntries().stream() //
					.map(this::createSAPRecommendationType) //
					.collect(Collectors.toList());
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet OfferRecommendationScenarios.", e);
		}
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	@Required
	public void setRecentViewedItemsService(final RecentViewedItemsService recentViewedItemsService)
	{
		this.recentViewedItemsService = recentViewedItemsService;
	}

	@Required
	public void setRecommendationBufferService(final RecommendationBufferService recommendationBufferService)
	{
		this.recommendationBufferService = recommendationBufferService;
	}

	@Required
	public void setUserContextService(UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}

}
