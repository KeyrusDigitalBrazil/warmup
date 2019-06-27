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
package com.sap.hybris.saprevenuecloudcustomer.service.impl;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerConversionService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRevenueCloudConfigurationModel;


public class SapRevenueCloudCustomerConversionService implements SapCpiCustomerConversionService
{
	private ModelService modelService;
	private CustomerNameStrategy customerNameStrategy;
	private GenericDao sapRevenueCloudConfigurationModelGenericDao;
	private static final Logger LOGGER = LogManager.getLogger(SapRevenueCloudCustomerConversionService.class);


	@Override
	public SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(final CustomerModel customerModel,
			final AddressModel addressModel, final String baseStoreUid, final String sessionLanguage)
	{
		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getModelService().create(SAPCpiOutboundCustomerModel.class);

		// Configuration
		final SAPCpiOutboundConfigModel config = getModelService().create(SAPCpiOutboundConfigModel.class);
		sapCpiOutboundCustomer.setSapCpiConfig(config);

		// Customer
		final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());
		sapCpiOutboundCustomer.setUid(customerModel.getUid());
		sapCpiOutboundCustomer.setCustomerId(customerModel.getCustomerID());
		sapCpiOutboundCustomer.setFirstName(names[0]);
		sapCpiOutboundCustomer.setLastName(names[1]);
		sapCpiOutboundCustomer.setBaseStore(baseStoreUid);
		sapCpiOutboundCustomer.setRevenueCloudCustomerId(customerModel.getRevenueCloudCustomerId());

		final SAPRevenueCloudConfigurationModel RevenueCloudConfig = getRevenueCloudConfiguration();
		String countryIsoCode = RevenueCloudConfig.getDefaultCountryCode();

		if (addressModel == null)
		{
			sapCpiOutboundCustomer.setCountry(countryIsoCode);
			return sapCpiOutboundCustomer;
		}

		// Address
		countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
		sapCpiOutboundCustomer.setCountry(countryIsoCode);
		sapCpiOutboundCustomer.setStreet(addressModel.getLine2());
		sapCpiOutboundCustomer.setPhone(addressModel.getPhone1());
		sapCpiOutboundCustomer.setFax(addressModel.getFax());
		sapCpiOutboundCustomer.setTown(addressModel.getTown());
		sapCpiOutboundCustomer.setPostalCode(addressModel.getPostalcode());
		sapCpiOutboundCustomer.setStreetNumber(addressModel.getLine1());
		sapCpiOutboundCustomer.setTown(addressModel.getTown());

		final String regionIsoCode = addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort() : null;
		sapCpiOutboundCustomer.setRegion(regionIsoCode);

		return sapCpiOutboundCustomer;
	}

	protected SAPRevenueCloudConfigurationModel getRevenueCloudConfiguration()
	{
		final Optional<SAPRevenueCloudConfigurationModel> revenueCloudConfigOpt = getSapRevenueCloudConfigurationModelGenericDao()
				.find().stream().findFirst();
		if (revenueCloudConfigOpt.isPresent())
		{
			return revenueCloudConfigOpt.get();
		}
		else
		{
			LOGGER.error("No Revenue Cloud Configuration found.");
			return null;
		}
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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
	 * @return the sapRevenueCloudConfigurationModelGenericDao
	 */
	public GenericDao getSapRevenueCloudConfigurationModelGenericDao()
	{
		return sapRevenueCloudConfigurationModelGenericDao;
	}

	/**
	 * @param sapRevenueCloudConfigurationModelGenericDao
	 *           the sapRevenueCloudConfigurationModelGenericDao to set
	 */
	public void setSapRevenueCloudConfigurationModelGenericDao(final GenericDao sapRevenueCloudConfigurationModelGenericDao)
	{
		this.sapRevenueCloudConfigurationModelGenericDao = sapRevenueCloudConfigurationModelGenericDao;
	}

}
