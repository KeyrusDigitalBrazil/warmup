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
package de.hybris.platform.sap.productconfig.b2b.context;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationParameterB2B}
 */
public class ConfigurationParameterB2BImpl implements ConfigurationParameterB2B
{
	private B2BCustomerService b2bCustomerService;
	private B2BUnitService b2bUnitService;

	private static final Logger LOG = Logger.getLogger(ConfigurationParameterB2BImpl.class);

	@Override
	public boolean isSupported()
	{
		return true;
	}

	@Override
	public String getCustomerNumber()
	{
		String customerNumber = null;
		final B2BUnitModel b2bUnit = retrieveB2BUnitModel();
		if (b2bUnit != null)
		{
			customerNumber = b2bUnitService.getRootUnit(b2bUnit).getUid();
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("CUSTOMER_NUMBER='[" + customerNumber + "']");
		}

		return customerNumber != null ? customerNumber.toUpperCase() : customerNumber;
	}

	@Override
	public String getCustomerPriceGroup()
	{
		String priceGroup = null;
		final B2BUnitModel b2bUnit = retrieveB2BUnitModel();
		if (b2bUnit != null)
		{
			final UserPriceGroup userPriceGroup = b2bUnit.getUserPriceGroup();
			if (userPriceGroup != null)
			{
				priceGroup = userPriceGroup.getCode();
			}
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("CUSTOMER_PRICE_GROUP='[" + priceGroup + "']");
		}

		return priceGroup;
	}

	@Override
	public String getCountrySapCode()
	{
		String country = null;
		final B2BUnitModel b2bUnit = retrieveB2BUnitModel();
		if (b2bUnit != null)
		{
			final CountryModel countryModel = b2bUnit.getCountry();
			if (countryModel != null)
			{
				country = countryModel.getSapCode();
			}
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("COUNTRY_SAP_CODE='[" + country + "']");
		}

		return country;
	}

	protected B2BUnitModel retrieveB2BUnitModel()
	{
		B2BUnitModel b2bUnit = null;
		final B2BCustomerModel b2bCustomer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
		b2bUnit = (B2BUnitModel) b2bUnitService.getParent(b2bCustomer);
		return b2bUnit;
	}

	/**
	 * @param b2bCustomerService
	 */
	@Required
	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}

	/**
	 * @param b2bUnitService
	 */
	@Required
	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
