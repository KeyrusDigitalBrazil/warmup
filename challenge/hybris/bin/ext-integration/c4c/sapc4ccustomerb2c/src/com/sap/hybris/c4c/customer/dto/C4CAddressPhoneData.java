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
 * Data transfer object definition for Phone Numbers in Address
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(
{ "phoneNumber", "mobileNumberIndicator", "phoneNumberUsageCode" })
public class C4CAddressPhoneData implements Serializable
{
	private static final Logger LOG = Logger.getLogger(C4CAddressPhoneData.class);

	@JsonProperty("phoneNumber")
	private String phoneNumber;

	@JsonProperty("mobileNumberIndicator")
	private String mobileNumberIndicator;

	@JsonProperty("phoneNumberUsageCode")
	private String phoneNumberUsageCode;


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
	 * @return the phoneNumber
	 */
	@JsonProperty("phoneNumber")
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	@JsonProperty("phoneNumber")
	public void setPhoneNumber(final String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the mobileNumberIndicator
	 */
	@JsonProperty("mobileNumberIndicator")
	public String getMobileNumberIndicator()
	{
		return mobileNumberIndicator;
	}

	/**
	 * @param mobileNumberIndicator the mobileNumberIndicator to set
	 */
	@JsonProperty("mobileNumberIndicator")
	public void setMobileNumberIndicator(final String mobileNumberIndicator)
	{
		this.mobileNumberIndicator = mobileNumberIndicator;
	}

	/**
	 * @return the phoneNumberUsageCode
	 */
	@JsonProperty("phoneNumberUsageCode")
	public String getPhoneNumberUsageCode()
	{
		return phoneNumberUsageCode;
	}

	/**
	 * @param phoneNumberUsageCode the phoneNumberUsageCode to set
	 */
	@JsonProperty("phoneNumberUsageCode")
	public void setPhoneNumberUsageCode(final String phoneNumberUsageCode)
	{
		this.phoneNumberUsageCode = phoneNumberUsageCode;
	}



}
