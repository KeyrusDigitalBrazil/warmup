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

import java.util.Map;

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
{ "bdtType", "eventType", "status", "id", "lastStatusChange", "created", "totalPrice", "currency", "subTotalPrice", "customer",
		"indexOrderData" })
public class OrderIndex
{
	private static final Logger LOGGER = LogManager.getLogger(OrderCustomer.class);

	@JsonProperty("bdtType")
	private String bdtType;

	@JsonProperty("eventType")
	private String eventStatus;

	@JsonProperty("status")
	private String orderStatus;

	@JsonProperty("id")
	private String orderId;

	@JsonProperty("lastStatusChange")
	private String modifiedTime;

	@JsonProperty("created")
	private String createdTime;

	@JsonProperty("totalPrice")
	private String totalPrice;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("subTotalPrice")
	private String subTotalPrice;


	@JsonProperty("indexOrderData")
	private Map<String, String> indexOrderData;

	@JsonProperty("customer")
	private OrderCustomer customer;


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

	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus()
	{
		return orderStatus;
	}

	/**
	 * @param orderStatus
	 *           the orderStatus to set
	 */
	public void setOrderStatus(final String orderStatus)
	{
		this.orderStatus = orderStatus;
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
	 * @return the totalPrice
	 */
	public String getTotalPrice()
	{
		return totalPrice;
	}

	/**
	 * @param totalPrice
	 *           the totalPrice to set
	 */
	public void setTotalPrice(final String totalPrice)
	{
		this.totalPrice = totalPrice;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency
	 *           the currency to set
	 */
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}


	/**
	 * @return the subTotalPrice
	 */
	public String getSubTotalPrice()
	{
		return subTotalPrice;
	}

	/**
	 * @param subTotalPrice
	 *           the subTotalPrice to set
	 */
	public void setSubTotalPrice(final String subTotalPrice)
	{
		this.subTotalPrice = subTotalPrice;
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

	/**
	 * @return the indexOrderData
	 */
	public Map<String, String> getIndexOrderData()
	{
		return indexOrderData;
	}

	/**
	 * @param indexOrderData
	 *           the indexOrderData to set
	 */
	public void setIndexOrderData(final Map<String, String> indexOrderData)
	{
		this.indexOrderData = indexOrderData;
	}




}
