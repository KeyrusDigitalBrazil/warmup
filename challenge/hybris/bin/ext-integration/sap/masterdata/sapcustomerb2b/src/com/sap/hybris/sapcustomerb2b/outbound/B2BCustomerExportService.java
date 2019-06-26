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
package com.sap.hybris.sapcustomerb2b.outbound;

import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2BADMINGROUP;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2BCUSTOMERGROUP;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2BGROUP;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2BUNIT;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.B2B_UNIT_ID;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.BUYER;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.COUNTRY;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.CUSTOMERID;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.DEFAULT_FEED;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.EMAIL;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.EXECUTIVEBOARD;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.FIRSTNAME;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.HEADOFPURCHASING;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.LASTNAME;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.OBJ_TYPE;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.PHONE;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.RAW_HYBRIS_B2B_CUSTOMER;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.RAW_HYBRIS_B2B_UNIT;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.TITLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Class to prepare the customer data and send the data to the Data Hub
 */
public class B2BCustomerExportService
{
	private static final Logger LOGGER = Logger
			.getLogger(com.sap.hybris.sapcustomerb2b.outbound.B2BCustomerExportService.class.getName());
	private CustomerNameStrategy customerNameStrategy;
	private DataHubOutboundService dataHubOutboundService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private BaseStoreService baseStoreService;
	private String feed = DEFAULT_FEED;
	private Map<String, String> batchIdAttributes;

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

	protected void prepareAddressData(final AddressModel addressModel, final Map<String, Object> target)
	{
		final String countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
		target.put(COUNTRY, countryIsoCode);
		target.put(PHONE, addressModel.getPhone1());
	}

	/**
	 * map B2B customer Model to the target map and send data to the Data Hub
	 *
	 * @param changedB2bCustomerModel
	 * 			B2BCustomerModel that was changed
	 * @param language
	 * 			Language
	 */
	public void prepareAndSend(final B2BCustomerModel changedB2bCustomerModel, final String language)
	{
		if (changedB2bCustomerModel.getDefaultB2BUnit() == null)
		{
			return;
		}

		final B2BUnitModel rootB2BUnit = b2bUnitService.getRootUnit(changedB2bCustomerModel.getDefaultB2BUnit());
		final Map<String, Object> rawHybrisB2BUnit = new HashMap<>();
		rawHybrisB2BUnit.put(B2B_UNIT_ID, rootB2BUnit.getUid());
		sendRawItemsToDataHub(RAW_HYBRIS_B2B_UNIT, Collections.singletonList(rawHybrisB2BUnit));

		final Set<B2BCustomerModel> b2bCustomers = new HashSet<>();
		b2bCustomers.addAll(b2bUnitService.getB2BCustomers(rootB2BUnit));
		for (final B2BUnitModel subB2BUnit : b2bUnitService.getB2BUnits(rootB2BUnit))
		{
			b2bCustomers.addAll(b2bUnitService.getB2BCustomers(subB2BUnit));
		}

		final List<Map<String, Object>> rawHybrisB2BCustomers = new ArrayList<>();
		for (final B2BCustomerModel b2bCustomer : b2bCustomers)
		{
			rawHybrisB2BCustomers.add(prepareB2BCustomerData(b2bCustomer, language));
		}
		sendRawItemsToDataHub(RAW_HYBRIS_B2B_CUSTOMER, rawHybrisB2BCustomers);
	}

	protected void sendRawItemsToDataHub(final String rawItemType, final List<Map<String, Object>> rawData)
	{
		if (rawData != null && !rawData.isEmpty())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("The following values were sent to Data Hub " + rawData + " (to the feed " + getFeed()
						+ " into raw item type " + rawItemType + ")");
			}
			try
			{
				dataHubOutboundService.sendToDataHub(getFeed(), rawItemType, rawData);
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
		else
		{
			LOGGER.debug("No send to datahub occured because target is empty");
		}
	}

	protected HashMap<String, Object> prepareB2BCustomerData(final B2BCustomerModel b2bCustomerModel, final String sessionLanguage)
	{
		final HashMap<String, Object> target = new HashMap<String, Object>();
		final String[] names = customerNameStrategy.splitName(b2bCustomerModel.getName());
		final String titleCode = b2bCustomerModel.getTitle() != null ? b2bCustomerModel.getTitle().getCode() : null;
		final B2BUnitModel parentB2BUnit = b2bCustomerModel.getDefaultB2BUnit();
		final String parentB2BUnitUid = (parentB2BUnit != null) ? parentB2BUnit.getUid() : null;

		if (parentB2BUnitUid != null)
		{
			target.put(EMAIL, b2bCustomerModel.getEmail());
			target.put(CUSTOMERID, b2bCustomerModel.getCustomerID());
			target.put(FIRSTNAME, names[0]);
			target.put(LASTNAME, names[1]);
			target.put(SESSION_LANGUAGE, sessionLanguage);
			target.put(TITLE, titleCode);
			target.put(OBJ_TYPE, "KNVK");
			target.put(B2BUNIT, parentB2BUnitUid.split("_")[0]);
			target.put(B2BGROUP, getB2BCustomerFunction(b2bCustomerModel.getGroups()));

			final AddressModel defaultShipmentAddress = b2bCustomerModel.getDefaultShipmentAddress();
			if (defaultShipmentAddress == null)
			{
				target.put(COUNTRY, "");
				target.put(PHONE, "");
			}
			else
			{
				prepareAddressData(defaultShipmentAddress, target);
			}

			prepareBatchIdAttributes(b2bCustomerModel, target);

			return target;
		}
		return null;
	}

	protected void prepareBatchIdAttributes(final B2BCustomerModel customerModel, final Map<String, Object> target)
	{
		getBatchIdAttributes().forEach(target::putIfAbsent);

		target.put("dh_batchId", customerModel.getCustomerID());
	}

	/*
	 * Get the Contact Function value for the corresponding group
	 */
	private String getB2BCustomerFunction(final Set<PrincipalGroupModel> groups)
	{
		final List<String> groupsList = new ArrayList<String>();
		for (final PrincipalGroupModel group : groups)
		{
			groupsList.add(group.getUid());
		}
		if (groupsList.contains(B2BADMINGROUP) && groupsList.contains(B2BCUSTOMERGROUP))
		{
			return HEADOFPURCHASING;
		}
		if (groupsList.contains(B2BADMINGROUP))
		{
			return EXECUTIVEBOARD;
		}
		if (groupsList.contains(B2BCUSTOMERGROUP))
		{
			return BUYER;
		}
		return "";
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

	/**
	 * @return B2BUnitService
	 */
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * set the B2B unit service
	 *
	 * @param b2bUnitService
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	public boolean isB2BCustomerReplicationEnabled()
	{
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		final SAPConfigurationModel sapConfigurationModel = (baseStore != null) ? baseStore.getSAPConfiguration() : null;
		return (sapConfigurationModel != null) ? sapConfigurationModel.getReplicateregisteredb2buser() : false;
	}
}
