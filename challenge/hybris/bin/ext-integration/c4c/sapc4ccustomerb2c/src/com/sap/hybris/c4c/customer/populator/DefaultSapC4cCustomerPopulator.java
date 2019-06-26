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

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.dto.C4CHeaderData;


/**
 * Populates C4CCustomerData DTO
 */
public class DefaultSapC4cCustomerPopulator implements Populator<CustomerModel, C4CCustomerData>
{

	private static final String EMPTY_STRING = "";
	private static final String DASH = "-";

	private CustomerNameStrategy customerNameStrategy;
	private ConfigurationService configurationService;

	@Override
	public void populate(final CustomerModel customerModel, final C4CCustomerData customerData) throws ConversionException
	{
		customerData.setCustomerId(customerModel.getCustomerID());
		customerData.setCategoryCode(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_CATEGORY_CODE));

		final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());
		customerData.setFirstName(names[0]);
		customerData.setLastName(names[1]);

		customerData.setGender(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_GENDER_CODE));

		final String roleCode = getConfigurationService().getConfiguration()
				.getString(Sapc4ccustomerb2cConstants.CUSTOMER_ROLE_CODE);
		customerData.setRoleCode(roleCode);

		customerData.setBlockedIndicator(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_BLOCKED_INDICATOR));
		customerData.setDeletedIndicator(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_DELETED_INDICATOR));
		customerData.setReleasedIndicator(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_RELEASED_INDICATOR));

		customerData.setHeader(populateHeaderData());
	}

	/**
	 * Creates and populate header data
	 */
	protected C4CHeaderData populateHeaderData()
	{
		final C4CHeaderData header = new C4CHeaderData();

		final String uuid = UUID.randomUUID().toString().toUpperCase();

		header.setId(uuid.replaceAll(DASH, EMPTY_STRING));
		header.setUuid(uuid);
		header.setSenderParty(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_SENDER_PARTY));
		header.setRecipientParty(getConfigurationProperty(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_RECIPIENT_PARTY));
		header.setTimestamp(new DateTime(DateTimeZone.UTC).toString());

		return header;
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
