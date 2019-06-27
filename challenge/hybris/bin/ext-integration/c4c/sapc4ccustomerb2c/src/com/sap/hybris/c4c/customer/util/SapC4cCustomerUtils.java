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
package com.sap.hybris.c4c.customer.util;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CAddressData;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;


/**
 * Utility Class for creating Data Transfer objects
 */
public class SapC4cCustomerUtils
{
	private Populator addressPopulator;
	private Populator customerPopulator;
	private ConfigurationService configurationService;

	/**
	 * Returns C4CCustomerData for given customerModel and list of addressModel
	 *
	 * @param customerModel
	 * @param addressModels
	 * @return c4cCustomerData
	 */
	public C4CCustomerData getCustomerDataForCustomer(final CustomerModel customerModel,
			final Collection<AddressModel> addressModels)
	{

		final C4CCustomerData customerData = new C4CCustomerData();
		getCustomerPopulator().populate(customerModel, customerData);

		List<C4CAddressData> addressList;

		if (addressModels == null || addressModels.isEmpty())
		{
			final C4CAddressData addressData = getAddressWithEmail(customerModel.getUid());
			addressList = new ArrayList<>();
			addressList.add(addressData);
		}
		else
		{
			addressList = getAdressListForCustomer(addressModels);
			if (customerModel.getDefaultShipmentAddress() == null)
			{
				final C4CAddressData addressData = getAddressWithEmail(customerModel.getUid());
				final String[] defaultAddressUsage = new String[1];
				defaultAddressUsage[0] = getConfigurationService().getConfiguration()
						.getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_ADDRESS_USAGE_CODE);
				addressData.setAddressUsageCodes(defaultAddressUsage);
				addressList.add(addressData);
			}
		}

		C4CAddressData[] addresses = new C4CAddressData[addressList.size()];
		addresses = addressList.toArray(addresses);

		customerData.setAddresses(addresses);

		return customerData;
	}

	/**
	 * Returns List of C4CAddressData for given list of addressModel
	 *
	 * @param addressModels
	 * @return list of C4CAddressData
	 */
	public List<C4CAddressData> getAdressListForCustomer(final Collection<AddressModel> addressModels)
	{
		final List<C4CAddressData> addressList = new ArrayList<>();
		boolean isCustomerEmailUsed = false;
		for (final AddressModel addressModel : addressModels)
		{
			final C4CAddressData addressData = new C4CAddressData();
			if (null == addressModel.getEmail())
			{
				isCustomerEmailUsed = true;
			}
			getAddressPopulator().populate(addressModel, addressData);
			addressList.add(addressData);
		}
		if (!isCustomerEmailUsed)
		{
			final CustomerModel customerModel = (CustomerModel) addressModels.iterator().next().getOwner();
			addressList.add(0, getAddressWithEmail(customerModel.getUid()));
		}
		return addressList;
	}

	/**
	 * Returns C4CAddressData populated with given email
	 *
	 * @param email
	 * @return C4CAddressData
	 */
	public C4CAddressData getAddressWithEmail(final String email)
	{

		final C4CAddressData address = new C4CAddressData();
		address.setEmailId(email);
		address.setEmailUsageCode(
				getConfigurationService().getConfiguration().getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DEFAULT_USAGE_CODE));
		return address;
	}

	/**
	 * @return the addressPopulator
	 */
	public Populator getAddressPopulator()
	{
		return addressPopulator;
	}

	/**
	 * @param addressPopulator
	 *           the addressPopulator to set
	 */
	public void setAddressPopulator(final Populator addressPopulator)
	{
		this.addressPopulator = addressPopulator;
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

	/**
	 * @return the customerPopulator
	 */
	public Populator getCustomerPopulator()
	{
		return customerPopulator;
	}

	/**
	 * @param customerPopulator
	 *           the customerPopulator to set
	 */
	public void setCustomerPopulator(final Populator customerPopulator)
	{
		this.customerPopulator = customerPopulator;
	}
}
