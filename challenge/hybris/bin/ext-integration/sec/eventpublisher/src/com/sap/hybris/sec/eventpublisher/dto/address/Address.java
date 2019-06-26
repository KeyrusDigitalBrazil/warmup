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
package com.sap.hybris.sec.eventpublisher.dto.address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.hybris.sec.eventpublisher.dto.customer.Customer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "id",
    "contactName",
    "companyName",
    "street",
    "streetNumber",
    "streetAppendix",
    "extraLine1",
    "extraLine2",
    "extraLine3",
    "extraLine4",
    "zipCode",
    "city",
    "country",
    "state",
    "contactPhone",
    "tags",
    "isDefault",
    "hybrisCustomerId",
    "isDelete",
    "hybrisUid",
    "ownerPk",
    "ownerType"
})
public class Address {
	private static final Logger LOGGER = LogManager.getLogger(Address.class);

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("id")
    private String id;
    /**
     * 
     * (Required)
     * 
     */
     
	@JsonProperty("hybrisCustomerAddressId")
	private String hybrisCustomerAddressId;
    @JsonProperty("contactName")
    private String contactName;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("street")
    private String street;
    @JsonProperty("streetNumber")
    private String streetNumber;
    @JsonProperty("streetAppendix")
    private String streetAppendix;
    @JsonProperty("extraLine1")
    private String extraLine1;
    @JsonProperty("extraLine2")
    private String extraLine2;
    @JsonProperty("extraLine3")
    private String extraLine3;
    @JsonProperty("extraLine4")
    private String extraLine4;
    @JsonProperty("zipCode")
    private String zipCode;
    @JsonProperty("city")
    private String city;
    @JsonProperty("country")
    private String country;
    @JsonProperty("state")
    private String state;
    @JsonProperty("contactPhone")
    private String contactPhone;
    @JsonProperty("isDelete")
    private boolean isDelete;
    @JsonProperty("hybrisUid")
    private String hybrisUid;
    
    /**
     * Values like: Billing / Shipping or any other custom tag
     * 
     */
    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("isDefault")
    private Boolean isDefault;
    
    @JsonProperty("hybrisCustomerId")
    private String hybrisCustomerId;
    
    @JsonProperty("ownerType")
    private String ownerType;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("hybrisCustomerAddressId")
    public String getHybrisCustomerAddressId() {
		return hybrisCustomerAddressId;
	}

    @JsonProperty("hybrisCustomerAddressId")
	public void setHybrisCustomerAddressId(String hybrisCustomerAddressId) {
		this.hybrisCustomerAddressId = hybrisCustomerAddressId;
	}

	/**
     * 
     * (Required)
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * (Required)
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Address withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The contactName
     */
    @JsonProperty("contactName")
    public String getContactName() {
        return contactName;
    }

    /**
     * 
     * (Required)
     * 
     * @param contactName
     *     The contactName
     */
    @JsonProperty("contactName")
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Address withContactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    /**
     * 
     * @return
     *     The companyName
     */
    @JsonProperty("companyName")
    public String getCompanyName() {
        return companyName;
    }

    /**
     * 
     * @param companyName
     *     The companyName
     */
    @JsonProperty("companyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address withCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    /**
     * 
     * @return
     *     The street
     */
    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    /**
     * 
     * @param street
     *     The street
     */
    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    public Address withStreet(String street) {
        this.street = street;
        return this;
    }

    /**
     * 
     * @return
     *     The streetNumber
     */
    @JsonProperty("streetNumber")
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * 
     * @param streetNumber
     *     The streetNumber
     */
    @JsonProperty("streetNumber")
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Address withStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

    /**
     * 
     * @return
     *     The streetAppendix
     */
    @JsonProperty("streetAppendix")
    public String getStreetAppendix() {
        return streetAppendix;
    }

    /**
     * 
     * @param streetAppendix
     *     The streetAppendix
     */
    @JsonProperty("streetAppendix")
    public void setStreetAppendix(String streetAppendix) {
        this.streetAppendix = streetAppendix;
    }

    public Address withStreetAppendix(String streetAppendix) {
        this.streetAppendix = streetAppendix;
        return this;
    }

    /**
     * 
     * @return
     *     The extraLine1
     */
    @JsonProperty("extraLine1")
    public String getExtraLine1() {
        return extraLine1;
    }

    /**
     * 
     * @param extraLine1
     *     The extraLine1
     */
    @JsonProperty("extraLine1")
    public void setExtraLine1(String extraLine1) {
        this.extraLine1 = extraLine1;
    }

    public Address withExtraLine1(String extraLine1) {
        this.extraLine1 = extraLine1;
        return this;
    }

    /**
     * 
     * @return
     *     The extraLine2
     */
    @JsonProperty("extraLine2")
    public String getExtraLine2() {
        return extraLine2;
    }

    /**
     * 
     * @param extraLine2
     *     The extraLine2
     */
    @JsonProperty("extraLine2")
    public void setExtraLine2(String extraLine2) {
        this.extraLine2 = extraLine2;
    }

    public Address withExtraLine2(String extraLine2) {
        this.extraLine2 = extraLine2;
        return this;
    }

    /**
     * 
     * @return
     *     The extraLine3
     */
    @JsonProperty("extraLine3")
    public String getExtraLine3() {
        return extraLine3;
    }

    /**
     * 
     * @param extraLine3
     *     The extraLine3
     */
    @JsonProperty("extraLine3")
    public void setExtraLine3(String extraLine3) {
        this.extraLine3 = extraLine3;
    }

    public Address withExtraLine3(String extraLine3) {
        this.extraLine3 = extraLine3;
        return this;
    }

    /**
     * 
     * @return
     *     The extraLine4
     */
    @JsonProperty("extraLine4")
    public String getExtraLine4() {
        return extraLine4;
    }

    /**
     * 
     * @param extraLine4
     *     The extraLine4
     */
    @JsonProperty("extraLine4")
    public void setExtraLine4(String extraLine4) {
        this.extraLine4 = extraLine4;
    }

    public Address withExtraLine4(String extraLine4) {
        this.extraLine4 = extraLine4;
        return this;
    }

    /**
     * 
     * @return
     *     The zipCode
     */
    @JsonProperty("zipCode")
    public String getZipCode() {
        return zipCode;
    }

    /**
     * 
     * @param zipCode
     *     The zipCode
     */
    @JsonProperty("zipCode")
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Address withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    /**
     * 
     * @return
     *     The city
     */
    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    public Address withCity(String city) {
        this.city = city;
        return this;
    }

    /**
     * 
     * @return
     *     The country
     */
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    public Address withCountry(String country) {
        this.country = country;
        return this;
    }

    /**
     * 
     * @return
     *     The state
     */
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    public Address withState(String state) {
        this.state = state;
        return this;
    }

    /**
     * 
     * @return
     *     The contactPhone
     */
    @JsonProperty("contactPhone")
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * 
     * @param contactPhone
     *     The contactPhone
     */
    @JsonProperty("contactPhone")
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Address withContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        return this;
    }

    /**
     * Values like: Billing / Shipping or any other custom tag
     * 
     * @return
     *     The tags
     */
    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    /**
     * Values like: Billing / Shipping or any other custom tag
     * 
     * @param tags
     *     The tags
     */
    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Address withTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The isDefault
     */
    @JsonProperty("isDefault")
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * 
     * (Required)
     * 
     * @param isDefault
     *     The isDefault
     */
    @JsonProperty("isDefault")
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Address withIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Address withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

	/**
	 * @return the hybrisCustomerId
	 */
	public String getHybrisCustomerId()
	{
		return hybrisCustomerId;
	}

	/**
	 * @param hybrisCustomerId the hybrisCustomerId to set
	 */
	public void setHybrisCustomerId(String hybrisCustomerId)
	{
		this.hybrisCustomerId = hybrisCustomerId;
	}
	public boolean isDelete() {
		return isDelete;
	}

	@JsonProperty("isDelete")
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	
	public String getHybrisUid() {
		return hybrisUid;
	}

	@JsonProperty("hybrisUid")
	public void setHybrisUid(String hybrisUid) {
		this.hybrisUid = hybrisUid;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}


}
