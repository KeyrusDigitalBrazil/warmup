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

import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendation.utils.RecentViewedItemsCollection;


/**
 * Manage 2 session {@link RecentViewedItemsCollection} for products and categories.
 */
public class RecentViewedItemsService
{
	protected static final String VISITED_PRODUCT_KEY = "RECENT_VIEWED_PRODUCTS";
	protected static final String VISITED_CATEGORY_KEY = "RECENT_VIEWED_CATEGORIES";

	protected int maxRecentViewedItems;
	protected SessionService sessionService;

	public List<String> getRecentViewedCategories()
	{
		return this.getRecentViewedItemsCollection(VISITED_CATEGORY_KEY).getCodes();
	}

	@Nonnull
	protected RecentViewedItemsCollection getRecentViewedItemsCollection(String key)
	{
		return sessionService.getOrLoadAttribute(key, () -> new RecentViewedItemsCollection(this.maxRecentViewedItems));
	}

	public List<String> getRecentViewedProducts()
	{
		return this.getRecentViewedItemsCollection(VISITED_PRODUCT_KEY).getCodes();
	}

	/**
	 * Register visited product and category.
	 *
	 * @param productCode
	 *           Product code.
	 * @param categoryCode
	 *           Category code.
	 */
	public void productVisited(final String productCode, final String categoryCode)
	{
		this.getRecentViewedItemsCollection(VISITED_PRODUCT_KEY).addCode(productCode);
		this.getRecentViewedItemsCollection(VISITED_CATEGORY_KEY).addCode(categoryCode);
	}

	@Required
	public void setMaxRecentViewedItems(final int maxRecentViewedItems)
	{
		this.maxRecentViewedItems = maxRecentViewedItems;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
