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
import java.util.List;

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
{ "personalInfo", "customerType", "markets", "addresses", "revenueCloudId", "customerId" })
public class Customer implements Serializable
{
	private static final Logger LOGGER = LogManager.getLogger(Customer.class);

	@JsonProperty("personalInfo")
	private PersonalInfo personalInfo;

	@JsonProperty("customerType")
	private String customerType;

	@JsonProperty("markets")
	private List<Market> markets;

	@JsonProperty("addresses")
	private List<Address> addresses;

	@JsonProperty("revenueCloudId")
	private String revenueCloudId;

	@JsonProperty("customerId")
	private String customerId;

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
	 * @return the personalInfo
	 */
	@JsonProperty("personalInfo")
	public PersonalInfo getPersonalInfo()
	{
		return personalInfo;
	}

	/**
	 * @param personalInfo
	 *           the personalInfo to set
	 */
	@JsonProperty("personalInfo")
	public void setPersonalInfo(final PersonalInfo personalInfo)
	{
		this.personalInfo = personalInfo;
	}

	/**
	 * @return the customerType
	 */
	@JsonProperty("customerType")
	public String getCustomerType()
	{
		return customerType;
	}

	/**
	 * @param customerType
	 *           the customerType to set
	 */
	@JsonProperty("customerType")
	public void setCustomerType(final String customerType)
	{
		this.customerType = customerType;
	}

	/**
	 * @return the revenueCloudId
	 */
	@JsonProperty("revenueCloudId")
	public String getRevenueCloudId()
	{
		return revenueCloudId;
	}

	/**
	 * @param revenueCloudId
	 *           the revenueCloudId to set
	 */
	@JsonProperty("revenueCloudId")
	public void setRevenueCloudId(final String revenueCloudId)
	{
		this.revenueCloudId = revenueCloudId;
	}

	/**
	 * @return the markets
	 */
	public List<Market> getMarkets()
	{
		return markets;
	}

	/**
	 * @param markets
	 *           the markets to set
	 */
	public void setMarkets(final List<Market> markets)
	{
		this.markets = markets;
	}

	/**
	 * @return the addresses
	 */
	public List<Address> getAddresses()
	{
		return addresses;
	}

	/**
	 * @param addresses
	 *           the addresses to set
	 */
	public void setAddresses(final List<Address> addresses)
	{
		this.addresses = addresses;
	}

	/**
	 * @return the customerId
	 */
	public String getCustomerId()
	{
		return customerId;
	}

	/**
	 * @param customerId
	 *           the customerId to set
	 */
	public void setCustomerId(final String customerId)
	{
		this.customerId = customerId;
	}

}
