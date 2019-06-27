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
 * Data transfer object definition for Address
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(
{ "emailId", "emailUsageCode", "addressUsageCodes", "streetName", "streetNumber", "town", "country", "postalCode", "fax",
		"faxUsageCode", "district", "pobox", "phoneNumbers" })
public class C4CAddressData implements Serializable
{

	private static final Logger LOG = Logger.getLogger(C4CAddressData.class);

	@JsonProperty("emailId")
	public String emailId;

	@JsonProperty("emailUsageCode")
	private String emailUsageCode;

	@JsonProperty("addressUsageCodes")
	private String[] addressUsageCodes;

	@JsonProperty("streetName")
	private String streetName;

	@JsonProperty("streetNumber")
	private String streetNumber;

	@JsonProperty("town")
	private String town;

	@JsonProperty("country")
	private String country;

	@JsonProperty("postalCode")
	private String postalCode;

	@JsonProperty("fax")
	private String fax;

	@JsonProperty("faxUsageCode")
	private String faxUsageCode;

	@JsonProperty("district")
	private String district;

	@JsonProperty("pobox")
	private String pobox;

	@JsonProperty("phoneNumbers")
	private C4CAddressPhoneData[] phoneNumbers;

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
	 * @return the emailId
	 */
	@JsonProperty("emailId")
	public String getEmailId()
	{
		return emailId;
	}

	/**
	 * @param emailId
	 *           the emailId to set
	 */
	@JsonProperty("emailId")
	public void setEmailId(final String emailId)
	{
		this.emailId = emailId;
	}

	/**
	 * @return the emailUsageCode
	 */
	@JsonProperty("emailUsageCode")
	public String getEmailUsageCode()
	{
		return emailUsageCode;
	}

	/**
	 * @param emailUsageCode
	 *           the emailUsageCode to set
	 */
	@JsonProperty("emailUsageCode")
	public void setEmailUsageCode(final String emailUsageCode)
	{
		this.emailUsageCode = emailUsageCode;
	}

	/**
	 * @return the streetName
	 */
	@JsonProperty("streetName")
	public String getStreetName()
	{
		return streetName;
	}

	/**
	 * @param streetName
	 *           the streetName to set
	 */
	@JsonProperty("streetName")
	public void setStreetName(final String streetName)
	{
		this.streetName = streetName;
	}

	/**
	 * @return the streetNumber
	 */
	@JsonProperty("streetNumber")
	public String getStreetNumber()
	{
		return streetNumber;
	}

	/**
	 * @param streetNumber
	 *           the streetNumber to set
	 */
	@JsonProperty("streetNumber")
	public void setStreetNumber(final String streetNumber)
	{
		this.streetNumber = streetNumber;
	}

	/**
	 * @return the town
	 */
	@JsonProperty("town")
	public String getTown()
	{
		return town;
	}

	/**
	 * @param town
	 *           the town to set
	 */
	@JsonProperty("town")
	public void setTown(final String town)
	{
		this.town = town;
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
	 * @return the fax
	 */
	@JsonProperty("fax")
	public String getFax()
	{
		return fax;
	}

	/**
	 * @param fax
	 *           the fax to set
	 */
	@JsonProperty("fax")
	public void setFax(final String fax)
	{
		this.fax = fax;
	}

	/**
	 * @return the faxUsageCode
	 */
	@JsonProperty("faxUsageCode")
	public String getFaxUsageCode()
	{
		return faxUsageCode;
	}

	/**
	 * @param faxUsageCode
	 *           the faxUsageCode to set
	 */
	@JsonProperty("faxUsageCode")
	public void setFaxUsageCode(final String faxUsageCode)
	{
		this.faxUsageCode = faxUsageCode;
	}

	/**
	 * @return the district
	 */
	@JsonProperty("district")
	public String getDistrict()
	{
		return district;
	}

	/**
	 * @param district
	 *           the district to set
	 */
	@JsonProperty("district")
	public void setDistrict(final String district)
	{
		this.district = district;
	}

	/**
	 * @return the pobox
	 */
	@JsonProperty("pobox")
	public String getPobox()
	{
		return pobox;
	}

	/**
	 * @param pobox
	 *           the pobox to set
	 */
	@JsonProperty("pobox")
	public void setPobox(final String pobox)
	{
		this.pobox = pobox;
	}

	/**
	 * @return the phoneNumbers
	 */
	@JsonProperty("phoneNumbers")
	public C4CAddressPhoneData[] getPhoneNumbers()
	{
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers
	 *           the phoneNumbers to set
	 */
	@JsonProperty("phoneNumbers")
	public void setPhoneNumbers(final C4CAddressPhoneData[] phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * @return the addressUsageCodes
	 */
	@JsonProperty("addressUsageCodes")
	public String[] getAddressUsageCodes()
	{
		return addressUsageCodes;
	}

	/**
	 * @param addressUsageCodes
	 *           the addressUsageCodes to set
	 */
	@JsonProperty("addressUsageCodes")
	public void setAddressUsageCodes(final String[] addressUsageCodes)
	{
		this.addressUsageCodes = addressUsageCodes;
	}


}
