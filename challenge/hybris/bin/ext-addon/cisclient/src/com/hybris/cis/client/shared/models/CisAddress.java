/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.cis.client.shared.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Representation of an address.
 * 
 */
@XmlRootElement(name = "address")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisAddress
{
	/** Type of the address. */
	@XmlElement(name = "type")
	private CisAddressType type;

	/** Title. */
	@XmlElement(name = "title")
	private String title;

	/** First name. */
	@XmlElement(name = "firstName")
	private String firstName;

	/** Last name. */
	@XmlElement(name = "lastName")
	private String lastName;

	/** Email. */
	@XmlElement(name = "email")
	private String email;

	/** First address line. */
	@XmlElement(name = "addressLine1")
	private String addressLine1;

	/** Second address line. */
	@XmlElement(name = "addressLine2")
	private String addressLine2;

	/** Third address line. */
	@XmlElement(name = "addressLine3")
	private String addressLine3;

	/** Fourth address line. */
	@XmlElement(name = "addressLine4")
	private String addressLine4;

	/** Zip/Postal code. */
	@XmlElement(name = "zipCode")
	private String zipCode;

	/** City. */
	@XmlElement(name = "city")
	private String city;

	/** Second part of the ISO 3166-2 subdivision code (e.g. state or province code without country). */
	@XmlElement(name = "state")
	private String state;

	/** 2 letter ISO 3166-1 alpha-2 country code. */
	@XmlElement(name = "country")
	private String country;

	/** The phone number. */
	@XmlElement(name = "phone")
	private String phone;

	/** The company name. */
	@XmlElement(name = "company")
	private String company;

	/** Longitude of the address. Negative values are in the Western hemisphere. Positive values are in the Eastern hemisphere. */
	@XmlElement(name = "longitude")
	private String longitude;

	/** Latitude of the address. Negative values are in the Southern hemisphere. Positive values are in the Northern hemisphere. */
	@XmlElement(name = "latitude")
	private String latitude;

	/** the type of the facility. */
	@XmlElement(name = "facilityType")
	private String facilityType;

	/** The name of the facility. */
	@XmlElement(name = "facilityName")
	private String facilityName;

	/** The fax number belonging to this address. */
	@XmlElement(name = "faxNumber")
	private String faxNumber;

	/** The fax number belonging to this address. */
	@XmlElement(name = "stateAsTwoLetter")
	private String stateAsTwoLetter;


	/**
	 * Vendor specific values to pass in the request.
	 */
	@XmlElement(name = "vendorParameters")
	private AnnotationHashMap vendorParameters;

	/**
	 * Instantiates a new cis address.
	 */
	public CisAddress()
	{
		// required by jax xml bind
		super();
	}

	/**
	 * Instantiates a new cis address.
	 * 
	 * @param addressLine1 the address line1
	 * @param zipCode the zip code
	 * @param city the city
	 * @param state the state
	 * @param country the country
	 */
	public CisAddress(final String addressLine1, final String zipCode, final String city, final String state, final String country)
	{
		super();
		this.addressLine1 = addressLine1;
		this.zipCode = zipCode;
		this.city = city;
		this.state = state;
		this.country = country;
	}

	/**
	 * Gets the address line1.
	 * 
	 * @return the addressLine1
	 */
	public String getAddressLine1()
	{
		return this.addressLine1;
	}

	/**
	 * Sets the address line1.
	 * 
	 * @param addressLine1 the addressLine1 to set
	 */
	public void setAddressLine1(final String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	/**
	 * Gets the address line2.
	 * 
	 * @return the addressLine2
	 */
	public String getAddressLine2()
	{
		return this.addressLine2;
	}

	/**
	 * Sets the address line2.
	 * 
	 * @param addressLine2 the addressLine2 to set
	 */
	public void setAddressLine2(final String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	/**
	 * Gets the address line3.
	 * 
	 * @return the addressLine3
	 */
	public String getAddressLine3()
	{
		return this.addressLine3;
	}

	/**
	 * Sets the address line3.
	 * 
	 * @param addressLine3 the addressLine3 to set
	 */
	public void setAddressLine3(final String addressLine3)
	{
		this.addressLine3 = addressLine3;
	}

	/**
	 * Gets the address line4.
	 * 
	 * @return the addressLine4
	 */
	public String getAddressLine4()
	{
		return this.addressLine4;
	}

	/**
	 * Sets the address line4.
	 * 
	 * @param addressLine4 the addressLine4 to set
	 */
	public void setAddressLine4(final String addressLine4)
	{
		this.addressLine4 = addressLine4;
	}

	public String getLongitude()
	{
		return this.longitude;
	}

	public void setLongitude(final String longitude)
	{
		this.longitude = longitude;
	}

	public String getLatitude()
	{
		return this.latitude;
	}

	public void setLatitude(final String latitude)
	{
		this.latitude = latitude;
	}

	/**
	 * Gets the zip code.
	 * 
	 * @return the zipCode
	 */
	public String getZipCode()
	{
		return this.zipCode;
	}

	/**
	 * Sets the zip code.
	 * 
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(final String zipCode)
	{
		this.zipCode = zipCode;
	}

	/**
	 * Gets the city.
	 * 
	 * @return the city
	 */
	public String getCity()
	{
		return this.city;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city the city to set
	 */
	public void setCity(final String city)
	{
		this.city = city;
	}

//	/**
//	 * Returns the state in the two letter country code.
//	 *
//	 * @return the state in 2 letter format
//	 */
//	public String getStateAsTwoLetter()
//	{
//		if (this.state != null && this.state.length() > 2 && this.state.contains("-"))
//		{
//			return this.state.substring(3);
//		}
//
//		return this.state;
//	}
//
//	/**
//	 * Sets the state as two letter.
//	 *
//	 * @param state state as two letter code
//	 */
//	public void setStateAsTwoLetter(String state)
//	{
//		this.state = state;
//	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public String getState()
	{
		return this.state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state the state to set
	 */
	public void setState(final String state)
	{
		this.state = state;
	}

	/**
	 * Gets the country.
	 * 
	 * @return the country
	 */
	public String getCountry()
	{
		return this.country;
	}

	/**
	 * Sets the country.
	 * 
	 * @param country the country to set
	 */
	public void setCountry(final String country)
	{
		this.country = country;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public CisAddressType getType()
	{
		return this.type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type to set
	 */
	public void setType(final CisAddressType type)
	{
		this.type = type;
	}

	/**
	 * Gets the first name.
	 * 
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName the firstName to set
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 * 
	 * @return the lastName
	 */
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * Sets the last name.
	 * 
	 * @param lastName the lastName to set
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return this.phone;
	}

	public void setPhone(final String phone)
	{
		this.phone = phone;
	}

	public String getCompany()
	{
		return this.company;
	}

	public void setCompany(final String company)
	{
		this.company = company;
	}

	public String getFacilityType()
	{
		return this.facilityType;
	}

	public void setFacilityType(final String facilityType)
	{
		this.facilityType = facilityType;
	}

	public String getFacilityName()
	{
		return this.facilityName;
	}

	public void setFacilityName(final String facilityName)
	{
		this.facilityName = facilityName;
	}

	public String getFaxNumber()
	{
		return this.faxNumber;
	}

	public void setFaxNumber(final String faxNumber)
	{
		this.faxNumber = faxNumber;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}

	@Override
	public String toString()
	{
		return "CisAddress [type=" + this.type + ", title=" + this.title + ", firstName=" + this.firstName + ", lastName="
				+ this.lastName + ", email=" + this.email + ", addressLine1=" + this.addressLine1 + ", addressLine2="
				+ this.addressLine2 + ", addressLine3=" + this.addressLine3 + ", addressLine4=" + this.addressLine4 + ", zipCode="
				+ this.zipCode + ", city=" + this.city + ", state=" + this.state + ", country=" + this.country + ", company="
				+ this.company + ", facilityType=" + this.facilityType + ", facilityName=" + this.facilityName + ", faxNumber="
				+ this.faxNumber + ", phone=" + this.phone + ", longitude=" + this.longitude + ", latitude=" + this.latitude + "]";
	}

	public AnnotationHashMap getVendorParameters()
	{
		return this.vendorParameters;
	}

	public void setVendorParameters(final AnnotationHashMap vendorParameters)
	{
		this.vendorParameters = vendorParameters;
	}
}
