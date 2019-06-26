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
{ "email", "phone", "street", "houseNumber", "city", "state", "postalCode", "country", "isDefault" })
public class Address implements Serializable
{
	@JsonProperty("email")
	private String email;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("street")
	private String street;

	@JsonProperty("houseNumber")
	private String houseNumber;

	@JsonProperty("city")
	private String city;

	@JsonProperty("state")
	private String state;

	@JsonProperty("postalCode")
	private String postalCode;

	@JsonProperty("country")
	private String country;

	@JsonProperty("isDefault")
	private String isDefault;

	private static final Logger LOGGER = LogManager.getLogger(Address.class);


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
	 * @return the email
	 */
	@JsonProperty("email")
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	@JsonProperty("email")
	public void setEmail(final String email)
	{
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	@JsonProperty("phone")
	public String getPhone()
	{
		return phone;
	}

	/**
	 * @param phone
	 *           the phone to set
	 */
	@JsonProperty("phone")
	public void setPhone(final String phone)
	{
		this.phone = phone;
	}

	/**
	 * @return the street
	 */
	@JsonProperty("street")
	public String getStreet()
	{
		return street;
	}

	/**
	 * @param street
	 *           the street to set
	 */
	@JsonProperty("street")
	public void setStreet(final String street)
	{
		this.street = street;
	}

	/**
	 * @return the houseNumber
	 */
	@JsonProperty("houseNumber")
	public String getHouseNumber()
	{
		return houseNumber;
	}

	/**
	 * @param houseNumber
	 *           the houseNumber to set
	 */
	@JsonProperty("houseNumber")
	public void setHouseNumber(final String houseNumber)
	{
		this.houseNumber = houseNumber;
	}

	/**
	 * @return the city
	 */
	@JsonProperty("city")
	public String getCity()
	{
		return city;
	}

	/**
	 * @param city
	 *           the city to set
	 */
	@JsonProperty("city")
	public void setCity(final String city)
	{
		this.city = city;
	}

	/**
	 * @return the state
	 */
	@JsonProperty("state")
	public String getState()
	{
		return state;
	}

	/**
	 * @param state
	 *           the state to set
	 */
	@JsonProperty("state")
	public void setState(final String state)
	{
		this.state = state;
	}

	/**
	 * @return the postalCode
	 */
	@JsonProperty("postalCode")
	public String getPostalCode()
	{
		return postalCode;
	}

	/**
	 * @param postalCode
	 *           the postalCode to set
	 */
	@JsonProperty("postalCode")
	public void setPostalCode(final String postalCode)
	{
		this.postalCode = postalCode;
	}

	/**
	 * @return the country
	 */
	@JsonProperty("country")
	public String getCountry()
	{
		return country;
	}

	/**
	 * @param country
	 *           the country to set
	 */
	@JsonProperty("country")
	public void setCountry(final String country)
	{
		this.country = country;
	}

	/**
	 * @return the isDefault
	 */
	@JsonProperty("isDefault")
	public String getIsDefault()
	{
		return isDefault;
	}

	/**
	 * @param isDefault
	 *           the isDefault to set
	 */
	@JsonProperty("isDefault")
	public void setIsDefault(final String isDefault)
	{
		this.isDefault = isDefault;
	}
}
