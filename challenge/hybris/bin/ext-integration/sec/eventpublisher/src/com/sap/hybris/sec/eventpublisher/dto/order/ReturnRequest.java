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
{ "code", "rma", "status", "lastStatusChange", "created", "refundDeliveryCost", "customer","order",
		"cancellable", "eventType" })
public class ReturnRequest
{
	private static final Logger LOGGER = LogManager.getLogger(ReturnRequest.class);

	@JsonProperty("code")
	private String code;
	
	@JsonProperty("rma")
	private String rma;
	
	@JsonProperty("status")
	private String status;

	@JsonProperty("lastStatusChange")
	private String modifiedTime;

	@JsonProperty("created")
	private String createdTime;

	@JsonProperty("refundDeliveryCost")
	private Boolean refundDeliveryCost;

	@JsonProperty("customer")
	private OrderCustomer customer;
	
	@JsonProperty("order")
	private OrderIndex order;
	
	@JsonProperty("cancellable")
	private boolean cancellable;
	
	@JsonProperty("eventType")
	private String eventStatus;
	


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
	 * @return the modifiedTime
	 */
	public String getModifiedTime()
	{
		return modifiedTime;
	}

	/**
	 * @param modifiedTime
	 *           the modifiedTime to set
	 */
	public void setModifiedTime(final String modifiedTime)
	{
		this.modifiedTime = modifiedTime;
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



	/**
	 * @return the customer
	 */
	public OrderCustomer getCustomer()
	{
		return customer;
	}

	/**
	 * @param customer
	 *           the customer to set
	 */
	public void setCustomer(final OrderCustomer customer)
	{
		this.customer = customer;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getRma() {
		return rma;
	}


	public void setRma(String rma) {
		this.rma = rma;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Boolean getRefundDeliveryCost() {
		return refundDeliveryCost;
	}


	public void setRefundDeliveryCost(Boolean refundDeliveryCost) {
		this.refundDeliveryCost = refundDeliveryCost;
	}


	public OrderIndex getOrder() {
		return order;
	}


	public void setOrder(OrderIndex order) {
		this.order = order;
	}


	public boolean isCancellable() {
		return cancellable;
	}


	public void setCancellable(boolean cancellable) {
		this.cancellable = cancellable;
	}
	
	/**
	 * @return the eventStatus
	 */
	public String getEventStatus()
	{
		return eventStatus;
	}

	/**
	 * @param eventStatus
	 *           the eventStatus to set
	 */
	public void setEventStatus(final String eventStatus)
	{
		this.eventStatus = eventStatus;
	}



}
