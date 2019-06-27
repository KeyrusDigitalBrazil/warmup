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
package de.hybris.platform.sapdigitalpaymentaddon.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;


/**
 *
 * Billing address details form
 *
 */
public class BillingAddressDetailsForm
{

	private AddressForm billingAddress;
	private boolean useDeliveryAddress;
	private String billTo_city;
	private String billTo_country;
	private String billTo_firstName;
	private String billTo_lastName;
	private String billTo_phoneNumber;
	private String billTo_postalCode;
	private String billTo_titleCode;
	private String billTo_state;
	private String billTo_street1;
	private String billTo_street2;



	/**
	 * @return the billTo_country
	 */
	public String getBillTo_country()
	{
		return billTo_country;
	}

	/**
	 * @param billTo_country
	 *           the billTo_country to set
	 */
	public void setBillTo_country(final String billTo_country)
	{
		this.billTo_country = billTo_country;
	}

	/**
	 * @return the billingAddress
	 */
	public AddressForm getBillingAddress()
	{
		return billingAddress;
	}

	/**
	 * @param billingAddress
	 *           the billingAddress to set
	 */
	public void setBillingAddress(final AddressForm billingAddress)
	{
		this.billingAddress = billingAddress;
	}


	/**
	 * @return the useDeliveryAddress
	 */
	public boolean isUseDeliveryAddress()
	{
		return useDeliveryAddress;
	}

	/**
	 * @param useDeliveryAddress
	 *           the useDeliveryAddress to set
	 */
	public void setUseDeliveryAddress(final boolean useDeliveryAddress)
	{
		this.useDeliveryAddress = useDeliveryAddress;
	}

	/**
	 * @return the billTo_city
	 */
	public String getBillTo_city()
	{
		return billTo_city;
	}

	/**
	 * @param billTo_city
	 *           the billTo_city to set
	 */
	public void setBillTo_city(final String billTo_city)
	{
		this.billTo_city = billTo_city;
	}

	/**
	 * @return the billTo_firstName
	 */
	public String getBillTo_firstName()
	{
		return billTo_firstName;
	}

	/**
	 * @param billTo_firstName
	 *           the billTo_firstName to set
	 */
	public void setBillTo_firstName(final String billTo_firstName)
	{
		this.billTo_firstName = billTo_firstName;
	}

	/**
	 * @return the billTo_lastName
	 */
	public String getBillTo_lastName()
	{
		return billTo_lastName;
	}

	/**
	 * @param billTo_lastName
	 *           the billTo_lastName to set
	 */
	public void setBillTo_lastName(final String billTo_lastName)
	{
		this.billTo_lastName = billTo_lastName;
	}

	/**
	 * @return the billTo_phoneNumber
	 */
	public String getBillTo_phoneNumber()
	{
		return billTo_phoneNumber;
	}

	/**
	 * @param billTo_phoneNumber
	 *           the billTo_phoneNumber to set
	 */
	public void setBillTo_phoneNumber(final String billTo_phoneNumber)
	{
		this.billTo_phoneNumber = billTo_phoneNumber;
	}

	/**
	 * @return the billTo_postalCode
	 */
	public String getBillTo_postalCode()
	{
		return billTo_postalCode;
	}

	/**
	 * @param billTo_postalCode
	 *           the billTo_postalCode to set
	 */
	public void setBillTo_postalCode(final String billTo_postalCode)
	{
		this.billTo_postalCode = billTo_postalCode;
	}

	/**
	 * @return the billTo_titleCode
	 */
	public String getBillTo_titleCode()
	{
		return billTo_titleCode;
	}

	/**
	 * @param billTo_titleCode
	 *           the billTo_titleCode to set
	 */
	public void setBillTo_titleCode(final String billTo_titleCode)
	{
		this.billTo_titleCode = billTo_titleCode;
	}

	/**
	 * @return the billTo_state
	 */
	public String getBillTo_state()
	{
		return billTo_state;
	}

	/**
	 * @param billTo_state
	 *           the billTo_state to set
	 */
	public void setBillTo_state(final String billTo_state)
	{
		this.billTo_state = billTo_state;
	}

	/**
	 * @return the billTo_street1
	 */
	public String getBillTo_street1()
	{
		return billTo_street1;
	}

	/**
	 * @param billTo_street1
	 *           the billTo_street1 to set
	 */
	public void setBillTo_street1(final String billTo_street1)
	{
		this.billTo_street1 = billTo_street1;
	}

	/**
	 * @return the billTo_street2
	 */
	public String getBillTo_street2()
	{
		return billTo_street2;
	}

	/**
	 * @param billTo_street2
	 *           the billTo_street2 to set
	 */
	public void setBillTo_street2(final String billTo_street2)
	{
		this.billTo_street2 = billTo_street2;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		// YTODO Auto-generated method stub
		return super.hashCode();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		// YTODO Auto-generated method stub
		return super.equals(obj);
	}

}
