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
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;

import java.io.Serializable;
import java.util.List;


/**
 * BO representation of an address.
 *
 */
public interface Address extends BusinessObject, Cloneable, Comparable<Address>, Serializable
{

	/**
	 * Address type corresponding to an organization
	 */
	String TYPE_ORGANISATION = "1";

	/**
	 * Address type corresponding to a business partner
	 */
	String TYPE_PERSON = "2";

	/**
	 * All (delta) attributes indicating that bean attributes have been changed are set to true
	 */
	void setAllXFields();

	/**
	 * Returns the attribute value for a given bean attribute
	 *
	 * @param field
	 *           Name of the field that should be returned
	 * @return attribute value
	 */
	String get(String field);

	/**
	 * Set the property id
	 *
	 * @param id
	 *           the id
	 */
	void setId(String id);

	/**
	 * Returns the property id
	 *
	 * @return id
	 */
	String getId();

	/**
	 * Set the property type
	 *
	 * @param type
	 *           the type
	 */
	void setType(String type);

	/**
	 * Returns the property type
	 *
	 * @return type
	 */
	String getType();

	/**
	 * @param titleKey
	 *           short ID of title
	 */
	void setTitleKey(String titleKey);

	/**
	 * @param firstName
	 *           the fist name
	 */
	void setFirstName(String firstName);

	/**
	 * @param lastName
	 *           the last name
	 */
	 void setLastName(String lastName);

	/**
	 * Setter for bean attribute
	 *
	 * @param name1
	 *           the name 1
	 */
	void setName1(String name1);

	/**
	 * Setter for bean attribute
	 *
	 * @param name2
	 *           the name 2
	 */
	void setName2(String name2);

	/**
	 * Setter for bean attribute
	 *
	 * @param city
	 *           the city
	 */
	void setCity(String city);

	/**
	 * Setter for bean attribute
	 *
	 * @param district
	 *           geographic entity for tax jurisdiction code determination
	 */
	void setDistrict(String district);

	/**
	 * Setter for bean attribute
	 *
	 * @param postlCod1
	 *           the postal code 1
	 */
	void setPostlCod1(String postlCod1);

	/**
	 * Setter for bean attribute
	 *
	 * @param postlCod2
	 *           the postal code 2
	 */
	void setPostlCod2(String postlCod2);

	/**
	 * Setter for bean attribute
	 *
	 * @param street
	 *           The street
	 */
	void setStreet(String street);

	/**
	 * Setter for bean attribute
	 *
	 * @param houseNo
	 *           The house no
	 */
	void setHouseNo(String houseNo);

	/**
	 * Setter for bean attribute
	 *
	 * @param country
	 *           The Country
	 */
	void setCountry(String country);


	/**
	 * Setter for bean attribute
	 *
	 * @param region
	 *           The region
	 */
	void setRegion(String region);

	/**
	 * Setter for bean attribute
	 *
	 * @param taxJurCode
	 *           tax jurisdiction code. Can be determined from full address or from district
	 */
	void setTaxJurCode(String taxJurCode);

	/**
	 * Setter for bean attribute
	 *
	 * @param tel1Numbr
	 *           the telephone number
	 */
	void setTel1Numbr(String tel1Numbr);

	/**
	 * Setter for bean attribute
	 *
	 * @param tel1Ext
	 *           the telephone extension
	 */
	void setTel1Ext(String tel1Ext);

	/**
	 * Setter for bean attribute
	 *
	 * @param faxNumber
	 *           The fax number
	 */
	void setFaxNumber(String faxNumber);

	/**
	 * Setter for bean attribute
	 *
	 * @param faxExtens
	 *           the fax number extension
	 */
	void setFaxExtens(String faxExtens);

	/**
	 * Setter for bean attribute
	 *
	 * @param email
	 *           the email address
	 */
	void setEmail(String email);

	/**
	 * Setter for bean attribute
	 *
	 * @param partner
	 *           business partner owning the address
	 */
	void setAddressPartner(String partner);

	/**
	 * @return respective bean attribute
	 */
	String getTitleKey();

	/**
	 * @return respective bean attribute
	 */
	String getFirstName();

	/**
	 * @return respective bean attribute
	 */
	String getLastName();

	/**
	 * @return respective bean attribute
	 */
	String getName1();

	/**
	 * @return respective bean attribute
	 */
	String getName2();

	/**
	 * @return respective bean attribute
	 */
	String getCity();

	/**
	 * @return geographic entity used for tax jurisdiction code determination
	 */
	String getDistrict();

	/**
	 * @return respective bean attribute
	 */
	String getPostlCod1();

	/**
	 * @return respective bean attribute
	 */
	String getPostlCod2();

	/**
	 * @return respective bean attribute
	 */
	String getStreet();

	/**
	 * @return respective bean attribute
	 */
	String getHouseNo();

	/**
	 * @return respective bean attribute
	 */
	String getCountry();

	/**
	 * @return respective bean attribute
	 */
	String getRegion();

	/**
	 * @return tax jurisdiction which can be derived from complete address or from country, regions city, street and
	 *         district
	 */
	String getTaxJurCode();

	/**
	 * @return respective bean attribute
	 */
	String getTel1Numbr();

	/**
	 * @return respective bean attribute
	 */
	String getTel1Ext();

	/**
	 * @return respective bean attribute
	 */
	String getFaxNumber();

	/**
	 * @return respective bean attribute
	 */
	String getFaxExtens();

	/**
	 * @return respective bean attribute
	 */
	String getEmail();

	/**
	 * @return partner who owns this address
	 */
	String getAddressPartner();

	/**
	 * Sets operation mode on current address
	 *
	 * @param operation
	 *           the address operation
	 * @see Operation
	 */
	void setOperation(Operation operation);

	/**
	 * The operation which is possible on an address
	 *
	 */
	public enum Operation
	{
		/**
		 * Default value. Assigned if actual operation cannot be determined
		 */
		NONE,
		/**
		 * Add a new address
		 */
		ADD,
		/**
		 * Change an address
		 */
		CHANGE,
		/**
		 * Delete an address
		 */
		DELETE
	}

	/**
	 * @return current operation
	 * @see Operation
	 */
	Operation getOperation();

	/**
	 * @return respective bean attribute
	 */
	String getTelmob1();

	/**
	 * Sets first mobile number
	 *
	 * @param telmob1
	 *           the telmob1
	 */
	void setTelmob1(String telmob1);

	/**
	 * @return address number from CRM or ERP backend if available
	 */
	String getAddrnum();

	/**
	 * Sets address number which is available in the CRM or ERP backend
	 *
	 * @param addrnum
	 *           the address number (BAS)
	 */
	void setAddrnum(String addrnum);

	/**
	 * @return guid of address, only available in CRM case
	 */
	String getAddrguid();

	/**
	 * Sets address guid (only relevant for CRM backend)
	 *
	 * @param addrguid
	 *           the address guid
	 */
	void setAddrguid(String addrguid);

	/**
	 * @return list of available counties. Relevant for tax jurisdiction code determination
	 */
	List<County> getCountyList();

	/**
	 * Sets lists of available counties. Relevant for tax jurisdiction code determination
	 *
	 * @param countyList
	 *           the county list
	 */
	void setCountyList(List<County> countyList);

	/**
	 * @return respective bean change attribute
	 */
	boolean getLastNameX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getFirstNameX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getName1X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getName2X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getCityX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getDistrictX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getCountryX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getStreetX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getRegionX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getEmailX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTelmob1X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getFaxNumberX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getHouseNoX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getFaxExtensX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getPostlCod1X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getPostlCod2X();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTel1NumbrX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTel1ExtX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTitleKeyX();

	/**
	 * @return respective bean change attribute
	 */
	boolean getTaxJurCodeX();

	/**
	 * @return respective bean attribute
	 */
	String getCompanyName();

	/**
	 * Sets company name
	 *
	 * @param companyName
	 *           the company name
	 */
	void setCompanyName(String companyName);

	/**
	 * @return respective bean change attribute
	 */
	boolean getCompanyNameX();

	/**
	 * <b>clearX</b> This method is to dynamically clear the values of all the <i>"X"</i> variables except the ones in
	 * the except array. Dynamically fetches the fields names and clears their values.
	 */
	void clearX();

	/**
	 * @return has this address been changed
	 */
	boolean isChanged();

	/**
	 * @return respective bean attribute
	 */
	String getTelmob1Seq();

	/**
	 * Sets respective bean attribute
	 *
	 * @param telmob1Seq
	 *           the telmob1 sequence number
	 */
	void setTelmob1Seq(String telmob1Seq);

	/**
	 * Sets address in short format
	 *
	 * @param string
	 *           the address string
	 */
	void setAddressString(String string);

	/**
	 * @return respective bean attribute
	 */
	String getName();

	/**
	 * @return address in string format
	 */
	String getAddressString();

	/**
	 * Sets address string including name
	 *
	 * @param addressStringC
	 *           address in string format, including name
	 */
	void setAddressStringC(String addressStringC);

	/**
	 * @return address string including name
	 */
	String getAddressStringC();

	/**
	 * Compares all address content fields
	 *
	 * @param address
	 *           the address to be compared
	 *
	 * @return true, if all address content fields are equal
	 */
	boolean isAddressfieldsEqualTo(Address address);
	
	@SuppressWarnings("squid:S1161")
	Address clone();
}