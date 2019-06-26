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
package com.sap.hybris.sec.eventpublisher.dto.customer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder(
{"hybrisUid", "hybrisCustomerId", "title", "firstName", "middleName", "lastName", "contactEmail", "contactPhone", "company",
		"preferredLanguage", "preferredCurrency", "preferredSite", "isDelete", "isGuest", "defaultAddressId"})
public class Customer
{
	private static final Logger LOGGER = LogManager.getLogger(Customer.class);

	@JsonProperty("hybrisUid")
	private String hybrisUid;
	@JsonProperty("hybrisCustomerId")
	private String hybrisCustomerId;
	@JsonProperty("title")
	private String title;
	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("middleName")
	private String middleName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("contactEmail")
	private String contactEmail;
	@JsonProperty("contactPhone")
	private String contactPhone;
	@JsonProperty("company")
	private String company;
	@JsonProperty("preferredLanguage")
	private String preferredLanguage = "en_US";
	@JsonProperty("preferredCurrency")
	private String preferredCurrency = "USD";
	@JsonProperty("preferredSite")
	private String preferredSite;
	@JsonProperty("isDelete")
	private boolean isDelete;
	@JsonProperty("isGuest")
	private boolean isGuest;
	@JsonProperty("isSealed")
	private boolean isSealed;
	@JsonProperty("defaultAddressId")
	private String defaultAddressId;
	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();


	/**
	 *
	 * @return The hybrisUid
	 */
	@JsonProperty("hybrisUid")
	public String getHybrisUid() {
		return hybrisUid;
	}

	/**
	 *
	 * @param hybrisUid
	 *           The hybrisUid
	 */
	@JsonProperty("hybrisUid")
	public void setHybrisUid(String hybrisUid) {
		this.hybrisUid = hybrisUid;
	}

	/**
	 *
	 * @return The hybrisCustomerId
	 */
	@JsonProperty("hybrisCustomerId")
	public String getHybrisCustomerId() {
		return hybrisCustomerId;
	}

	/**
	 *
	 * @param hybrisCustomerId
	 *           The hybrisCustomerId
	 */
	@JsonProperty("hybrisCustomerId")
	public void setHybrisCustomerId(String hybrisCustomerId) {
		this.hybrisCustomerId = hybrisCustomerId;
	}

	/**
	 *
	 * @return The title
	 */
	@JsonProperty("title")
	public String getTitle()
	{
		return title;
	}

	/**
	 *
	 * @param title
	 *           The title
	 */
	@JsonProperty("title")
	public void setTitle(final String title)
	{
		this.title = title;
	}

	public Customer withTitle(final String title)
	{
		this.title = title;
		return this;
	}

	/**
	 *
	 * @return The firstName
	 */
	@JsonProperty("firstName")
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 *
	 * @param firstName
	 *           The firstName
	 */
	@JsonProperty("firstName")
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	public Customer withFirstName(final String firstName)
	{
		this.firstName = firstName;
		return this;
	}

	/**
	 *
	 * @return The middleName
	 */
	@JsonProperty("middleName")
	public String getMiddleName()
	{
		return middleName;
	}

	/**
	 *
	 * @param middleName
	 *           The middleName
	 */
	@JsonProperty("middleName")
	public void setMiddleName(final String middleName)
	{
		this.middleName = middleName;
	}

	public Customer withMiddleName(final String middleName)
	{
		this.middleName = middleName;
		return this;
	}

	/**
	 *
	 * @return The lastName
	 */
	@JsonProperty("lastName")
	public String getLastName()
	{
		return lastName;
	}

	/**
	 *
	 * @param lastName
	 *           The lastName
	 */
	@JsonProperty("lastName")
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	public Customer withLastName(final String lastName)
	{
		this.lastName = lastName;
		return this;
	}

	/**
	 *
	 * @return The contactEmail
	 */
	@JsonProperty("contactEmail")
	public String getContactEmail()
	{
		return contactEmail;
	}

	/**
	 *
	 * @param contactEmail
	 *           The contactEmail
	 */
	@JsonProperty("contactEmail")
	public void setContactEmail(final String contactEmail)
	{
		this.contactEmail = contactEmail;
	}

	public Customer withContactEmail(final String contactEmail)
	{
		this.contactEmail = contactEmail;
		return this;
	}

	/**
	 *
	 * @return The contactPhone
	 */
	@JsonProperty("contactPhone")
	public String getContactPhone()
	{
		return contactPhone;
	}

	/**
	 *
	 * @param contactPhone
	 *           The contactPhone
	 */
	@JsonProperty("contactPhone")
	public void setContactPhone(final String contactPhone)
	{
		this.contactPhone = contactPhone;
	}

	public Customer withContactPhone(final String contactPhone)
	{
		this.contactPhone = contactPhone;
		return this;
	}

	/**
	 *
	 * @return The company
	 */
	@JsonProperty("company")
	public String getCompany()
	{
		return company;
	}

	/**
	 *
	 * @param company
	 *           The company
	 */
	@JsonProperty("company")
	public void setCompany(final String company)
	{
		this.company = company;
	}

	public Customer withCompany(final String company)
	{
		this.company = company;
		return this;
	}

	/**
	 *
	 * @return The preferredLanguage
	 */
	@JsonProperty("preferredLanguage")
	public String getPreferredLanguage()
	{
		return preferredLanguage;
	}

	/**
	 *
	 * @param preferredLanguage
	 *           The preferredLanguage
	 */
	@JsonProperty("preferredLanguage")
	public void setPreferredLanguage(final String preferredLanguage)
	{
		this.preferredLanguage = preferredLanguage;
	}

	public Customer withPreferredLanguage(final String preferredLanguage)
	{
		this.preferredLanguage = preferredLanguage;
		return this;
	}

	/**
	 *
	 * @return The preferredCurrency
	 */
	@JsonProperty("preferredCurrency")
	public String getPreferredCurrency()
	{
		return preferredCurrency;
	}

	/**
	 *
	 * @param preferredCurrency
	 *           The preferredCurrency
	 */
	@JsonProperty("preferredCurrency")
	public void setPreferredCurrency(final String preferredCurrency)
	{
		this.preferredCurrency = preferredCurrency;
	}

	public Customer withPreferredCurrency(final String preferredCurrency)
	{
		this.preferredCurrency = preferredCurrency;
		return this;
	}

	/**
	 *
	 * @return The preferredSite
	 */
	@JsonProperty("preferredSite")
	public String getPreferredSite()
	{
		return preferredSite;
	}

	/**
	 *
	 * @param preferredSite
	 *           The preferredSite
	 */
	@JsonProperty("preferredSite")
	public void setPreferredSite(final String preferredSite)
	{
		this.preferredSite = preferredSite;
	}

	public Customer withPreferredSite(final String preferredSite)
	{
		this.preferredSite = preferredSite;
		return this;
	}
	
	
	
	


	@Override
	public String toString()
	{
		//return ToStringBuilder.reflectionToString(this);
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

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(final String name, final Object value)
	{
		this.additionalProperties.put(name, value);
	}

	public Customer withAdditionalProperty(final String name, final Object value)
	{
		this.additionalProperties.put(name, value);
		return this;
	}
	
	public boolean isDelete() {
		return isDelete;
	}

	@JsonProperty("isDelete")
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	
	public boolean isSealed() {
		return isSealed;
	}

	@JsonProperty("isSealed")
	public void setSealed(boolean isSealed) {
		this.isSealed = isSealed;
	}
	
	public boolean isGuest() {
		return isGuest;
	}

	@JsonProperty("isGuest")
	public void setGuest(boolean isGuest) {
		this.isGuest = isGuest;
	}

	public String getDefaultAddressId() {
		return defaultAddressId;
	}

	public void setDefaultAddressId(String defaultAddressId) {
		this.defaultAddressId = defaultAddressId;
	}


	
}
