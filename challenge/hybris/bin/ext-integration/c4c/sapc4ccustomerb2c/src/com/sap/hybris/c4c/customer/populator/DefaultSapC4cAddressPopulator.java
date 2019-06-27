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
package com.sap.hybris.c4c.customer.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CAddressData;
import com.sap.hybris.c4c.customer.dto.C4CAddressPhoneData;


/**
 * Populates C4CAddressData DTO
 */
public class DefaultSapC4cAddressPopulator implements Populator<AddressModel, C4CAddressData>
{

	private ConfigurationService configurationService;

	@Override
	public void populate(final AddressModel addressModel, final C4CAddressData addressData) throws ConversionException
	{

		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();

		if (addressModel.getEmail() != null && !addressModel.getEmail().isEmpty())
		{
			addressData.setEmailId(addressModel.getEmail());
		}
		else
		{
			addressData.setEmailId(customerModel.getUid());
		}

		addressData.setEmailUsageCode(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_USAGE_CODE));

		addressData.setStreetName(addressModel.getStreetname());

		addressData.setStreetNumber(addressModel.getStreetnumber());
		addressData.setTown(addressModel.getTown());
		if (addressModel.getCountry() != null)
		{
			addressData.setCountry(addressModel.getCountry().getIsocode());
		}
		addressData.setPostalCode(addressModel.getPostalcode());
		addressData.setDistrict(addressModel.getDistrict());
		addressData.setPobox(addressModel.getPobox());

		if (addressModel.getFax() != null)
		{
			addressData.setFax(addressModel.getFax());
			addressData.setFaxUsageCode(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_USAGE_CODE));
		}


		final List<C4CAddressPhoneData> phoneNumberList = populatePhoneNumbers(addressModel);

		if (phoneNumberList != null && !phoneNumberList.isEmpty())
		{
			final C4CAddressPhoneData[] phoneNumbers = new C4CAddressPhoneData[phoneNumberList.size()];
			addressData.setPhoneNumbers(phoneNumberList.toArray(phoneNumbers));
		}

		final List<String> addressUsageCodeList = populateAddressUsageCodes(customerModel, addressModel);
		if (addressUsageCodeList != null && !addressUsageCodeList.isEmpty())
		{
			final String[] addressUsageCodes = new String[addressUsageCodeList.size()];
			addressData.setAddressUsageCodes(addressUsageCodeList.toArray(addressUsageCodes));
		}

	}

	/**
	 * Creates and populates address usage data
	 */
	protected List<String> populateAddressUsageCodes(final CustomerModel customerModel, final AddressModel addressModel)
	{
		final List<String> addressUsageCodeList = new ArrayList<>();
		if (null != customerModel.getDefaultShipmentAddress() && addressModel.equals(customerModel.getDefaultShipmentAddress()))
		{
			addressUsageCodeList.add(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_ADDRESS_USAGE_CODE));
			addressUsageCodeList.add(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_SHIPTO_ADDRESS_USAGE_CODE));
		}
		if (null != customerModel.getDefaultPaymentAddress() && addressModel.equals(customerModel.getDefaultPaymentAddress()))
		{
			addressUsageCodeList.add(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_BILLTO_ADDRESS_USAGE_CODE));
		}
		return addressUsageCodeList;
	}

	/**
	 * Creates and populates phone number data list
	 */
	protected List<C4CAddressPhoneData> populatePhoneNumbers(final AddressModel addressModel)
	{

		final List<C4CAddressPhoneData> phoneNumbers = new ArrayList<>();
		C4CAddressPhoneData phoneNumber;
		if (addressModel.getPhone1() != null && !addressModel.getPhone1().isEmpty())
		{
			phoneNumber = populatePhoneNumber(addressModel.getPhone1(), Boolean.FALSE,
					getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_USAGE_CODE));
			phoneNumbers.add(phoneNumber);

		}

		if (addressModel.getPhone2() != null && !addressModel.getPhone2().isEmpty())
		{
			phoneNumber = populatePhoneNumber(addressModel.getPhone2(), Boolean.FALSE,
					getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_USAGE_CODE));
			phoneNumbers.add(phoneNumber);

		}

		if (addressModel.getCellphone() != null && !addressModel.getCellphone().isEmpty())
		{
			phoneNumber = populatePhoneNumber(addressModel.getCellphone(), Boolean.TRUE,
					getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_MOBILE_USAGE_CODE));
			phoneNumbers.add(phoneNumber);

		}
		return phoneNumbers;
	}

	/**
	 * Reads configuration from the properties file for given propertyKey
	 */
	protected String getConfigurationProperty(final String propertyKey)
	{
		if (propertyKey != null && !propertyKey.isEmpty())
		{
			return getConfigurationService().getConfiguration().getString(propertyKey);
		}
		return null;
	}

	/**
	 * Creates and populates phone number data
	 */
	protected C4CAddressPhoneData populatePhoneNumber(final String phoneNumber, final Boolean mobileNumberIndicator,
			final String usageCode)
	{
		final C4CAddressPhoneData phoneNumberData = new C4CAddressPhoneData();
		phoneNumberData.setPhoneNumber(phoneNumber);
		phoneNumberData.setMobileNumberIndicator(mobileNumberIndicator.toString());
		if (usageCode != null)
		{
			phoneNumberData.setPhoneNumberUsageCode(usageCode);
		}

		return phoneNumberData;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


}
