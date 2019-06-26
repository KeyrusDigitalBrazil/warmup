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
package com.sap.hybris.sec.eventpublisher.dto.order;

import javax.annotation.Generated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder(
{ "bdtType", "orderId", "createdTime",
		"customerId", "agentId", "status","modificationType" })
public class OrderWS
{
	private static final Logger LOGGER = LogManager.getLogger(OrderWS.class);

	@JsonProperty("bdtType")
	private String bdtType;

	@JsonProperty("orderId")
	private String orderId;

	@JsonProperty("createdTime")
	private String createdTime;

	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("agentId")
	private String agentId;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("modificationType")
	private String modificationType;



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
			LOGGER.info(e);
		}
		return value;
	}

	/**
	 * @return the bdtType
	 */
	public String getBdtType()
	{
		return bdtType;
	}

	/**
	 * @param bdtType
	 *           the bdtType to set
	 */
	public void setBdtType(final String bdtType)
	{
		this.bdtType = bdtType;
	}


	/**
	 * @return the orderId
	 */
	public String getOrderId()
	{
		return orderId;
	}

	/**
	 * @param orderId
	 *           the orderId to set
	 */
	public void setOrderId(final String orderId)
	{
		this.orderId = orderId;
	}


	/**
	 * @return the createdTime
	 */
	public String getCreatedTime()
	{
		return createdTime;
	}

	/**
	 * @param createdTime
	 *           the createdTime to set
	 */
	public void setCreatedTime(final String createdTime)
	{
		this.createdTime = createdTime;
	}


	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getModificationType() {
		return modificationType;
	}

	public void setModificationType(String modificationType) {
		this.modificationType = modificationType;
	}

}
