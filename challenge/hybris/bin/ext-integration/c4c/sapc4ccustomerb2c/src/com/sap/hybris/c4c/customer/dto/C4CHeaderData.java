/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.dto;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Data transfer object definition for Header Configurations
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(
{ "id", "uuid", "senderParty", "recipientParty", "timestamp" })
public class C4CHeaderData implements Serializable
{

	private static final Logger LOG = Logger.getLogger(C4CHeaderData.class);

	@JsonProperty("id")
	private String id;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("senderParty")
	private String senderParty;

	@JsonProperty("recipientParty")
	private String recipientParty;

	@JsonProperty("timestamp")
	private String timestamp;

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
			LOG.error(e);
		}
		return value;
	}


	/**
	 * @return the id
	 */
	@JsonProperty("id")
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	@JsonProperty("id")
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the uuid
	 */
	@JsonProperty("uuid")
	public String getUuid()
	{
		return uuid;
	}

	/**
	 * @param uuid
	 *           the uuid to set
	 */
	@JsonProperty("uuid")
	public void setUuid(final String uuid)
	{
		this.uuid = uuid;
	}

	/**
	 * @return the senderParty
	 */
	@JsonProperty("senderParty")
	public String getSenderParty()
	{
		return senderParty;
	}

	/**
	 * @param senderParty
	 *           the senderParty to set
	 */
	@JsonProperty("senderParty")
	public void setSenderParty(final String senderParty)
	{
		this.senderParty = senderParty;
	}

	/**
	 * @return the recipientParty
	 */
	@JsonProperty("recipientParty")
	public String getRecipientParty()
	{
		return recipientParty;
	}

	/**
	 * @param recipientParty
	 *           the recipientParty to set
	 */
	@JsonProperty("recipientParty")
	public void setRecipientParty(final String recipientParty)
	{
		this.recipientParty = recipientParty;
	}

	/**
	 * @return the timestamp
	 */
	@JsonProperty("timestamp")
	public String getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp
	 *           the timestamp to set
	 */
	@JsonProperty("timestamp")
	public void setTimestamp(final String timestamp)
	{
		this.timestamp = timestamp;
	}
}
