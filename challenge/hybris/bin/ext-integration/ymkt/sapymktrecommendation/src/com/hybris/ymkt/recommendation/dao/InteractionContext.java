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

/**
 *
 * data structure that holds the interaction data context
 *
 */
public class InteractionContext
{
	private String productId;
	private String productType;
	private String scenarioId;
	private String sourceObjectId;

	/**
	 * @return productId
	 */
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @return productType
	 */
	public String getProductType()
	{
		return productType;
	}

	/**
	 * @return scenarioId
	 */
	public String getScenarioId()
	{
		return scenarioId;
	}

	/**
	 * @return the sourceObjectId
	 */
	public String getSourceObjectId()
	{
		return sourceObjectId;
	}

	/**
	 * @param productId
	 */
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @param productType
	 */
	public void setProductType(final String productType)
	{
		this.productType = productType;
	}

	/**
	 * @param scenarioId
	 */
	public void setScenarioId(final String scenarioId)
	{
		this.scenarioId = scenarioId;
	}

	/**
	 * @param sourceObjectId
	 *           the sourceObjectId to set
	 */
	public void setSourceObjectId(final String sourceObjectId)
	{
		this.sourceObjectId = sourceObjectId;
	}

}
