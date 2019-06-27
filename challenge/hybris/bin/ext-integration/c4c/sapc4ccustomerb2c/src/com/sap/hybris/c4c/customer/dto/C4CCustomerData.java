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
 * Data transfer object definition for Customer
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(
{ "customerId", "categoryCode", "releasedIndicator", "blockedIndicator", "deletedIndicator", "firstName", "lastName", "gender",
		"roleCode", "addresses" })
public class C4CCustomerData implements Serializable
{

	private static final Logger LOG = Logger.getLogger(C4CCustomerData.class);

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("categoryCode")
	private String categoryCode;

	@JsonProperty("releasedIndicator")
	private String releasedIndicator;

	@JsonProperty("blockedIndicator")
	private String blockedIndicator;

	@JsonProperty("deletedIndicator")
	private String deletedIndicator;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("roleCode")
	private String roleCode;

	@JsonProperty("addresses")
	private C4CAddressData[] addresses;

	@JsonProperty("header")
	private C4CHeaderData header;

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
	 * @return the customerId
	 */
	@JsonProperty("customerId")
	public String getCustomerId()
	{
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	@JsonProperty("customerId")
	public void setCustomerId(final String customerId)
	{
		this.customerId = customerId;
	}

	/**
	 * @return the categoryCode
	 */
	@JsonProperty("categoryCode")
	public String getCategoryCode()
	{
		return categoryCode;
	}

	/**
	 * @param categoryCode the categoryCode to set
	 */
	@JsonProperty("categoryCode")
	public void setCategoryCode(final String categoryCode)
	{
		this.categoryCode = categoryCode;
	}

	/**
	 * @return the releasedIndicator
	 */
	@JsonProperty("releasedIndicator")
	public String getReleasedIndicator()
	{
		return releasedIndicator;
	}

	/**
	 * @param releasedIndicator
	 *           the releasedIndicator to set
	 */
	@JsonProperty("releasedIndicator")
	public void setReleasedIndicator(final String releasedIndicator)
	{
		this.releasedIndicator = releasedIndicator;
	}

	/**
	 * @return the blockedIndicator
	 */
	@JsonProperty("blockedIndicator")
	public String getBlockedIndicator()
	{
		return blockedIndicator;
	}

	/**
	 * @param blockedIndicator
	 *           the blockedIndicator to set
	 */
	@JsonProperty("blockedIndicator")
	public void setBlockedIndicator(final String blockedIndicator)
	{
		this.blockedIndicator = blockedIndicator;
	}

	/**
	 * @return the deletedIndicator
	 */
	@JsonProperty("deletedIndicator")
	public String getDeletedIndicator()
	{
		return deletedIndicator;
	}

	/**
	 * @param deletedIndicator
	 *           the deletedIndicator to set
	 */
	@JsonProperty("deletedIndicator")
	public void setDeletedIndicator(final String deletedIndicator)
	{
		this.deletedIndicator = deletedIndicator;
	}

	/**
	 * @return the firstName
	 */
	@JsonProperty("firstName")
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	@JsonProperty("firstName")
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	@JsonProperty("lastName")
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *           the lastName to set
	 */
	@JsonProperty("lastName")
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the gender
	 */
	@JsonProperty("gender")
	public String getGender()
	{
		return gender;
	}

	/**
	 * @param gender
	 *           the gender to set
	 */
	@JsonProperty("gender")
	public void setGender(final String gender)
	{
		this.gender = gender;
	}

	/**
	 * @return the roleCode
	 */
	@JsonProperty("roleCode")
	public String getRoleCode()
	{
		return roleCode;
	}

	/**
	 * @param roleCode
	 *           the roleCode to set
	 */
	@JsonProperty("roleCode")
	public void setRoleCode(final String roleCode)
	{
		this.roleCode = roleCode;
	}

	/**
	 * @return the addresses
	 */
	@JsonProperty("addresses")
	public C4CAddressData[] getAddresses()
	{
		return addresses;
	}

	/**
	 * @param addresses
	 *           the addresses to set
	 */
	@JsonProperty("addresses")
	public void setAddresses(final C4CAddressData[] addresses)
	{
		this.addresses = addresses;
	}

	/**
	 * @return the header
	 */
	@JsonProperty("header")
	public C4CHeaderData getHeader()
	{
		return header;
	}

	/**
	 * @param header the header to set
	 */
	@JsonProperty("header")
	public void setHeader(final C4CHeaderData header)
	{
		this.header = header;
	}



}
