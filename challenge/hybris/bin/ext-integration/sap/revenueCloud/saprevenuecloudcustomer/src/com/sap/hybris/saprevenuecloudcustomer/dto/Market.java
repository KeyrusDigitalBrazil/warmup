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
package com.sap.hybris.saprevenuecloudcustomer.dto;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 */
@JsonPropertyOrder(
{ "marketId", "active" })
public class Market implements Serializable
{
	@JsonProperty("marketId")
	private String marketId;

	@JsonProperty("active")
	private String active;

	private static final Logger LOGGER = LogManager.getLogger(Market.class);

	@Override
	public String toString()
	{
		final ObjectMapper objectMapper = new ObjectMapper();
		String value = null;
		try
		{
			value = objectMapper.writeValueAsString(this);
		}
		catch (final JsonProcessingException e)
		{
			LOGGER.error(e);
		}
		return value;
	}

	/**
	 * @return the marketId
	 */
	@JsonProperty("marketId")
	public String getMarketId()
	{
		return marketId;
	}

	/**
	 * @param marketId
	 *           the marketId to set
	 */
	@JsonProperty("marketId")
	public void setMarketId(final String marketId)
	{
		this.marketId = marketId;
	}

	/**
	 * @return the active
	 */
	@JsonProperty("active")
	public String getActive()
	{
		return active;
	}

	/**
	 * @param active
	 *           the active to set
	 */
	@JsonProperty("active")
	public void setActive(final String active)
	{
		this.active = active;
	}
}
