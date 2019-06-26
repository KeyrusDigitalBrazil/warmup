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
package com.hybris.ymkt.recommendation.dao;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;


/**
 * Recommendation Context
 */
public class RecommendationContext
{
	protected String cartItemDSType;
	protected boolean includeCart;
	protected boolean includeRecent;
	protected String leadingItemDSType;
	protected String leadingItemType;
	protected String leadingProductId;
	protected String scenarioId;

	public String getCartItemDSType()
	{
		return cartItemDSType;
	}

	public String getLeadingItemDSType()
	{
		return leadingItemDSType;
	}

	/**
	 * @return list of leading item ids
	 */
	public List<String> getLeadingItemId()
	{
		if (SapymktrecommendationConstants.PRODUCT.equals(this.leadingItemType) && //
				StringUtils.isNotEmpty(this.getLeadingProductId()))
		{
			return Collections.singletonList(this.getLeadingProductId());
		}

		return Collections.emptyList();
	}

	public String getLeadingItemType()
	{
		return leadingItemType;
	}

	public String getLeadingProductId()
	{
		return leadingProductId;
	}

	public String getScenarioId()
	{
		return scenarioId;
	}

	public boolean isIncludeCart()
	{
		return includeCart;
	}

	public boolean isIncludeRecent()
	{
		return includeRecent;
	}

	public void setCartItemDSType(final String cartItemDSType)
	{
		this.cartItemDSType = cartItemDSType;
	}

	public void setIncludeCart(final boolean includeCart)
	{
		this.includeCart = includeCart;
	}

	public void setIncludeRecent(final boolean includeRecent)
	{
		this.includeRecent = includeRecent;
	}

	public void setLeadingItemDSType(final String leadingItemDSType)
	{
		this.leadingItemDSType = leadingItemDSType;
	}

	public void setLeadingItemType(final String leadingItemType)
	{
		this.leadingItemType = leadingItemType;
	}

	public void setLeadingProductId(final String leadingProductId)
	{
		this.leadingProductId = leadingProductId;
	}

	public void setScenarioId(final String scenarioId)
	{
		this.scenarioId = scenarioId;
	}
}
