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
package com.sap.hybris.saprevenuecloudcustomer.populator;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.saprevenuecloudcustomer.dto.Address;
import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.dto.PersonalInfo;
import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;


/**
 *
 */
public class DefaultSapRevenueCloudCustomerPopulator implements Populator<CustomerModel, Customer>
{
	private CustomerNameStrategy customerNameStrategy;
	private Populator addressPopulator;
	private GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao;


	@Override
	public void populate(final CustomerModel customerModel, final Customer customerJson) throws ConversionException
	{
		customerJson.setRevenueCloudId(customerModel.getRevenueCloudCustomerId());
		final PersonalInfo personalInfo = new PersonalInfo();
		final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());

		personalInfo.setFirstName(names[0]);
		personalInfo.setLastName(names[1]);

		customerJson.setPersonalInfo(personalInfo);

		customerJson.setCustomerType("INDIVIDUAL");
		final List<Address> addressList = getAddressListForCustomer(customerModel);
		customerJson.setAddresses(addressList);
	}

	protected List<Address> getAddressListForCustomer(final CustomerModel customerModel)
	{
		//return default shipment address as revenue cloud only supports one address
		final List<Address> addressList = new ArrayList<Address>();
		final AddressModel defaultAddress = customerModel.getDefaultShipmentAddress();
		if (defaultAddress != null)
		{
			final Address addressJson = new Address();
			addressJson.setEmail(customerModel.getUid());
			getAddressPopulator().populate(defaultAddress, addressJson);
			addressList.add(addressJson);
		}
		return addressList;
	}

	/**
	 * @return the customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 *           the customerNameStrategy to set
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
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
	 * @return the sapMarketToCatalogMappingModelGenericDao
	 */
	public GenericDao<SAPMarketToCatalogMappingModel> getSapMarketToCatalogMappingModelGenericDao()
	{
		return sapMarketToCatalogMappingModelGenericDao;
	}

	/**
	 * @param sapMarketToCatalogMappingModelGenericDao
	 *           the sapMarketToCatalogMappingModelGenericDao to set
	 */
	public void setSapMarketToCatalogMappingModelGenericDao(
			final GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao)
	{
		this.sapMarketToCatalogMappingModelGenericDao = sapMarketToCatalogMappingModelGenericDao;
	}

}
