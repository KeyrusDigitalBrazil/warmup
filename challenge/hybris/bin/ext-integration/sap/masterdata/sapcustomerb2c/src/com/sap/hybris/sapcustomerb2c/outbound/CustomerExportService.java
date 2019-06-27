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
package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESSUSAGE_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESS_USAGE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.BASE_STORE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.CONTACT_ID;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.COUNTRY;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.COUNTRY_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.DEFAULT_FEED;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.FAX;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.FIRSTNAME;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.LASTNAME;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJTYPE_KNA1;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJ_TYPE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.PHONE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.POSTALCODE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.RAW_HYBRIS_CUSTOMER;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REGION;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.REPLICATEREGISTEREDUSER;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.STREET;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.STREETNUMBER;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.TITLE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.TOWN;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.UID;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.impl.SAPGlobalConfigurationServiceImpl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * Class to prepare the customer data and send the data to the Data Hub
 */
public class CustomerExportService
{

	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2c.outbound.CustomerExportService.class.getName());

	private CustomerNameStrategy customerNameStrategy;
	private DataHubOutboundService dataHubOutboundService;
	private SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService;

	private Map<String, String> batchIdAttributes;

	private String feed = DEFAULT_FEED;
	private final String country = COUNTRY_DE;

	/**
	 * return Data Hub Outbound Service
	 *
	 * @return dataHubOutboundService
	 */
	public DataHubOutboundService getDataHubOutboundService()
	{
		return dataHubOutboundService;
	}

	/**
	 * set Data Hub Outbound Service
	 *
	 * @param dataHubOutboundService
	 */
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService)
	{
		this.dataHubOutboundService = dataHubOutboundService;
	}


	public Map<String, String> getBatchIdAttributes()
	{
		return batchIdAttributes;
	}

	@Required
	public void setBatchIdAttributes(Map<String, String> batchIdAttributes)
	{
		this.batchIdAttributes = batchIdAttributes;
	}

	/**
	 * map customer Model to the target map, set session language and base store name, and send data to the Data Hub
	 *
	 * @param customerModel
	 * 			Customer model
	 * @param baseStoreUid
	 * 			Base store UID
	 * @param sessionLanguage
	 * 			Language of session
	 */
	public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid, final String sessionLanguage)
	{
		sendCustomerData(customerModel, baseStoreUid, sessionLanguage, null);
	}

	/**
	 * map customer Model and address Model to the target map, set session language and base store name, and send data to
	 * the Data Hub
	 *
	 * @param customerModel
	 * 			Customer model
	 * @param baseStoreUid
	 * 			Base store UID
	 * @param sessionLanguage
	 * 			Language of session
	 * @param addressModel
	 * 			Address model
	 */
	public void sendCustomerData(final CustomerModel customerModel, final String baseStoreUid, final String sessionLanguage,
			final AddressModel addressModel)
	{
		final Map<String, Object> target = getTarget();

		prepareCustomerData(customerModel, baseStoreUid, sessionLanguage, target);

		if (addressModel == null)
		{
			target.put(COUNTRY, country);
		}
		else
		{
			prepareAddressData(addressModel, target);
		}

		// add batch id attriutes
		prepareBatchIdAttributes(customerModel, target);

		sendCustomerToDataHub(target);
	}

	/**
	 * @return new target instance
	 */
	protected Map<String, Object> getTarget()
	{
		return new HashMap<String, Object>();
	}

	protected void prepareCustomerData(final CustomerModel customerModel, final String baseStoreUid, final String sessionLanguage,
			final Map<String, Object> target)
	{
		final String[] names = customerNameStrategy.splitName(customerModel.getName());

		target.put(UID, customerModel.getUid());
		target.put(CUSTOMER_ID, customerModel.getCustomerID());
		target.put(CONTACT_ID, customerModel.getSapContactID());
		target.put(FIRSTNAME, names[0]);
		target.put(LASTNAME, names[1]);
		target.put(SESSION_LANGUAGE, sessionLanguage);
		target.put(TITLE, customerModel.getTitle().getCode());
		target.put(BASE_STORE, baseStoreUid);
		target.put(OBJ_TYPE, OBJTYPE_KNA1);
		target.put(ADDRESS_USAGE, ADDRESSUSAGE_DE);
	}

	protected void prepareAddressData(final AddressModel addressModel, final Map<String, Object> target)
	{
		final String countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
		target.put(COUNTRY, countryIsoCode);

		target.put(STREET, addressModel.getStreetname());
		target.put(PHONE, addressModel.getPhone1());
		target.put(FAX, addressModel.getFax());
		target.put(TOWN, addressModel.getTown());
		target.put(POSTALCODE, addressModel.getPostalcode());
		target.put(STREETNUMBER, addressModel.getStreetnumber());

		final String regionIsoCode = addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort() : null;
		target.put(REGION, regionIsoCode);
	}

	protected void prepareBatchIdAttributes(final CustomerModel customerModel, final Map<String, Object> target)
	{
		getBatchIdAttributes().forEach(target::putIfAbsent);

		target.put("dh_batchId", customerModel.getCustomerID());
	}


	protected void sendCustomerToDataHub(final Map<String, Object> target)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("The following values was send to Data Hub" + target);
			LOGGER.debug("To the feed" + getFeed() + " into raw model " + RAW_HYBRIS_CUSTOMER);
		}
		try
		{
			getDataHubOutboundService().sendToDataHub(getFeed(), RAW_HYBRIS_CUSTOMER, target);
		}
		catch (final DataHubCommunicationException e)
		{
			LOGGER.warn("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
			LOGGER.warn(e);
		}
		catch (final DataHubOutboundException e)
		{
			LOGGER.warn("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
			LOGGER.warn(e);
		}

	}

	/**
	 * return data hub feed
	 *
	 * @return feed
	 */
	public String getFeed()
	{
		return feed;
	}

	/**
	 * set data hub feed (usually set via the local property file)
	 *
	 * @param feed
	 */
	public void setFeed(final String feed)
	{
		this.feed = feed;
	}

	/**
	 * @return customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	public SAPGlobalConfigurationServiceImpl getSapCoreSAPGlobalConfigurationService()
	{
		return sapCoreSAPGlobalConfigurationService;
	}

	public void setSapCoreSAPGlobalConfigurationService(
			final SAPGlobalConfigurationServiceImpl sapCoreSAPGlobalConfigurationService)
	{
		this.sapCoreSAPGlobalConfigurationService = sapCoreSAPGlobalConfigurationService;
	}

	public boolean isCustomerReplicationEnabled()
	{
		return getSapCoreSAPGlobalConfigurationService() != null
				&& getSapCoreSAPGlobalConfigurationService().sapGlobalConfigurationExists()
				&& (Boolean) getSapCoreSAPGlobalConfigurationService().getProperty(REPLICATEREGISTEREDUSER);
	}

	/**
	 * State checker to test whether passed object is a valid CustomerModel
	 * 
	 * @param o
	 * 		Object passed to check
	 * @return Whether or not the tests object is a customer model
	 */
	public boolean isClassCustomerModel(final Object o)
	{
		return o.getClass() == CustomerModel.class;
	}
}
