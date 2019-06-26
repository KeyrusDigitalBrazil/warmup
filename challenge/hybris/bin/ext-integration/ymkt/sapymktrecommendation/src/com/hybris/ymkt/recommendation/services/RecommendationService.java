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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;
import com.hybris.ymkt.recommendation.dao.RecommendationContext;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;
import com.hybris.ymkt.recommendation.utils.RecommendationScenarioUtils;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * This service perform the 'get recommendation' actions, such as reading all scenario hashes of a user or read the
 * recommendation for a given scenario and context.
 */
public class RecommendationService
{
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();
	protected static final String RECOMMENDATION_SCENARIOS = "RecommendationScenarios";
	protected static final String GENERIC_RECOMMENDATION = "G";
	protected static final String PERSONALIZED_RECOMMENDATION = "P";
	protected static final String RESTRICTED_RECOMMENDATION = "R";

	protected CartService cartService;
	protected ODataService oDataService;
	protected RecentViewedItemsService recentViewedItemsService;
	protected RecommendationBufferService recommendationBufferService;
	protected UserContextService userContextService;
	protected int requestTimeoutThreshold;

	protected RecommendationScenario createRecommendationScenario(final RecommendationContext context)
	{
		final RecommendationScenario recoScenario = new RecommendationScenario(this.userContextService.getUserId(),
				this.userContextService.getUserOrigin());

		// Add User's cookie scenario hash matching the scenario Id
		final Scenario scenario = new Scenario(context.getScenarioId());

		// Last seen product or categories
		for (final String leadingObjectId : context.getLeadingItemId())
		{
			scenario.getLeadingObjects().add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
		}

		// Basket items
		if (StringUtils.isNotBlank(context.getCartItemDSType()))
		{
			for (final String basketObjectId : this.getCartItemsFromSession())
			{
				scenario.getBasketObjects().add(new BasketObject(context.getCartItemDSType(), basketObjectId));
			}
		}

		// Basket items placed in the leading items
		if (context.isIncludeCart())
		{
			for (final String leadingObjectId : this.getCartItemsFromSession())
			{
				scenario.getLeadingObjects().add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
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
				scenario.getLeadingObjects().add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
			}
		}

		recoScenario.getScenarios().add(scenario);

		return recoScenario;
	}

	/**
	 * Returns product currently in the cart
	 * 
	 * @return {@link List} of {@Link String}
	 */
	public List<String> getCartItemsFromSession()
	{
		return this.cartService.getSessionCart().getEntries().stream() //
				.map(AbstractOrderEntryModel::getProduct) //
				.map(ProductModel::getCode) //
				.collect(Collectors.toList());
	}

	private List<String> getFallbackRecommendation(final RecommendationScenario recoScenario)
	{
		final Scenario scenario = recoScenario.getScenarios().get(0);
		final String scenarioId = scenario.getScenarioId();
		final String userId = recoScenario.getUserId();
		final List<String> leadingItems = scenario.getLeadingObjects().stream().map(LeadingObject::getLeadingObjectId)
				.collect(Collectors.toList());

		SAPRecommendationBufferModel buffer = null;

		if (!userId.isEmpty())
		{
			buffer = this.getFallbackRecommendationByType(userId, scenarioId, leadingItems, PERSONALIZED_RECOMMENDATION);

			if (buffer == null)
			{
				buffer = this.getFallbackRecommendationByType(userId, scenarioId, leadingItems, RESTRICTED_RECOMMENDATION);
			}
		}

		if (buffer == null)
		{
			buffer = this.getFallbackRecommendationByType(userId, scenarioId, leadingItems, GENERIC_RECOMMENDATION);
		}

		return Optional.ofNullable(buffer) //
				.map(SAPRecommendationBufferModel::getRecoList) //
				.map(RecommendationScenarioUtils::convertBufferToList) //
				.orElse(Collections.emptyList());
	}

	protected SAPRecommendationBufferModel getFallbackRecommendationByType( //
			final String userId, //
			final String scenarioId, //
			final List<String> leadingItems, //
			final String recommendationType)
	{
		LOG.debug("Retrieving recommendation from buffer for userId='{}', scenarioId='{}', leadingItems='{}'", //
				userId, scenarioId, leadingItems);

		final List<String> subLeadingItems = new ArrayList<>(leadingItems);

		// Read buffer with subset of leading items
		while (subLeadingItems.size() > 1)
		{
			final String leadingItemsSorted = subLeadingItems.stream().sorted().collect(Collectors.joining(","));
			final SAPRecommendationBufferModel recommendation = this.getRecommendationOfAnyType(userId, scenarioId,
					leadingItemsSorted, recommendationType);
			if (recommendation != null)
			{
				return recommendation;
			}
			subLeadingItems.remove(0); // remove first element
		}

		// Read buffer for each leading item starting with most recent
		for (int i = leadingItems.size() - 1; i >= 0; i--)
		{
			final String leadingItem = leadingItems.get(i);
			final SAPRecommendationBufferModel recommendation = this.getRecommendationOfAnyType(userId, scenarioId, leadingItem,
					recommendationType);
			if (recommendation != null)
			{
				return recommendation;
			}
		}

		// Read buffer with empty leading items
		return this.getRecommendationOfAnyType(userId, scenarioId, "", recommendationType);
	}

	/**
	 * Read {@link ProductRecommendationData}s according to {@link RecommendationContext}.
	 *
	 * @param context
	 *           Parameters of the recommendations to read.
	 * @return {@link List} of {@link ProductRecommendationData}
	 */
	public List<String> getProductRecommendation(final RecommendationContext context)
	{
		final RecommendationScenario recoScenario = this.createRecommendationScenario(context);
		final String userId = recoScenario.getUserId();
		final String scenarioId = context.getScenarioId();
		final Scenario scenario = recoScenario.getScenarios().get(0);
		final String leadingItems = scenario.getLeadingObjects().stream() //
				.map(LeadingObject::getLeadingObjectId) //
				.sorted() //
				.collect(Collectors.joining(","));

		SAPRecommendationBufferModel buffer;

		// Read buffered results
		if (userId.isEmpty())
		{
			buffer = recommendationBufferService.getGenericRecommendation(scenarioId, leadingItems);
		}
		else
		{
			buffer = recommendationBufferService.getPersonalizedRecommendation(userId, scenarioId, leadingItems);
		}

		// Recommendation found in buffer
		if (buffer != null)
		{
			// If expired, asynchronously fill the buffer for next time
			if (recommendationBufferService.isRecommendationExpired(buffer))
			{
				this.createStartRunnable(recoScenario);
			}

			// Return the expired recommendation
			return RecommendationScenarioUtils.convertBufferToList(buffer.getRecoList());
		}

		// No recommendation found in buffer, try to get one from backend
		final List<String> recoListFromBackEnd = getRecommendationFromBackendWithinThreshold(recoScenario);
		if (!recoListFromBackEnd.isEmpty())
		{
			return recoListFromBackEnd;
		}

		// No response from backend or threshold expired, try to get a best possible match from buffer
		return this.getFallbackRecommendation(recoScenario);

	}

	private List<String> getRecommendationFromBackendWithinThreshold(final RecommendationScenario recoScenario)
	{
		final RunnablePopulateRecommendationBuffer runnable = this.createStartRunnable(recoScenario);
		final long stopTime = System.currentTimeMillis() + this.requestTimeoutThreshold;

		do
		{
			try
			{
				Thread.sleep(5);
			}
			catch (InterruptedException e)
			{
				LOG.error("Exception sleeping.", e);
			}

			final List<String> recoList = runnable.getRecommendationList();
			if (recoList != null)
			{
				return recoList;
			}
		}
		while (System.currentTimeMillis() < stopTime);

		return Collections.emptyList();
	}

	private SAPRecommendationBufferModel getRecommendationOfAnyType(final String userId, final String scenarioId,
			final String leadingItemsSorted, final String recommendationType)
	{
		switch (recommendationType)
		{
			case PERSONALIZED_RECOMMENDATION:
				return recommendationBufferService.getPersonalizedRecommendation(userId, scenarioId, leadingItemsSorted);

			case RESTRICTED_RECOMMENDATION:
				return recommendationBufferService.getRestrictedRecommendation(scenarioId, leadingItemsSorted);

			case GENERIC_RECOMMENDATION:
				return recommendationBufferService.getGenericRecommendation(scenarioId, leadingItemsSorted);

			default:
				return null;
		}
	}

	private RunnablePopulateRecommendationBuffer createStartRunnable(final RecommendationScenario recoScenario)
	{
		final RunnablePopulateRecommendationBuffer runnableBuffer = new RunnablePopulateRecommendationBuffer();
		runnableBuffer.setoDataService(oDataService);
		runnableBuffer.setRecommendationBufferService(recommendationBufferService);
		runnableBuffer.setRecommendationScenario(new RecommendationScenario(recoScenario));

		final Tenant myTenant = Registry.getCurrentTenant();
		final TenantAwareThreadFactory threadFactory = new TenantAwareThreadFactory(myTenant);
		final Thread workerThread = myTenant.createAndRegisterBackgroundThread(runnableBuffer, threadFactory);
		workerThread.setName("RunnablePopulateRecommendationBuffer-" + myTenant.getTenantID() + "-" + workerThread.getId());
		workerThread.start();
		return runnableBuffer;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
	public void setRequestTimeoutThreshold(final int requestTimeoutThreshold)
	{
		LOG.debug("requestTimeoutThreshold={}", requestTimeoutThreshold);
		this.requestTimeoutThreshold = requestTimeoutThreshold;
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}

}
