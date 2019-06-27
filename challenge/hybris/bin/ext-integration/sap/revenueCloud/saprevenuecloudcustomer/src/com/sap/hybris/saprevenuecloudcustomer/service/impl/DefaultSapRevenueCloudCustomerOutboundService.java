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

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerConversionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.saprevenuecloudcustomer.constants.SaprevenuecloudcustomerConstants;
import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.saprevenuecloudcustomer.service.SapRevenueCloudCustomerOutboundService;
import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;

import rx.Observable;


public class DefaultSapRevenueCloudCustomerOutboundService implements SapRevenueCloudCustomerOutboundService
{

	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;
	private ConfigurationService configurationService;
	private SapCpiCustomerConversionService sapCpiCustomerConversionService;
	private OutboundServiceFacade outboundServiceFacade;

	@Override
	public Observable<ResponseEntity<Map>> sendCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage, final AddressModel addressModel)
	{
		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getSapCpiCustomerConversionService()
				.convertCustomerToSapCpiCustomer(customerModel, addressModel, baseStoreUid, sessionLanguage);
		return getOutboundServiceFacade().send(sapCpiOutboundCustomer, "OutboundB2CCustomer", "scpiCustomerDestination");
	}


	@Override
	public ResponseData publishCustomerUpdate(final Customer customerJson) throws IOException
	{
		return getCloudPlatformIntegrationConnection().sendPost(
				getConfigurationService().getConfiguration().getString(SaprevenuecloudcustomerConstants.CUSTOMER_UPDATE_IFLOW_KEY),
				customerJson.toString());
	}

	/**
	 * @return the cloudPlatformIntegrationConnection
	 */
	public CloudPlatformIntegrationConnection getCloudPlatformIntegrationConnection()
	{
		return cloudPlatformIntegrationConnection;
	}


	/**
	 * @param cloudPlatformIntegrationConnection
	 *           the cloudPlatformIntegrationConnection to set
	 */
	public void setCloudPlatformIntegrationConnection(final CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection)
	{
		this.cloudPlatformIntegrationConnection = cloudPlatformIntegrationConnection;
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
	 * @return the sapCpiCustomerConversionService
	 */
	public SapCpiCustomerConversionService getSapCpiCustomerConversionService()
	{
		return sapCpiCustomerConversionService;
	}


	/**
	 * @param sapCpiCustomerConversionService
	 *           the sapCpiCustomerConversionService to set
	 */
	public void setSapCpiCustomerConversionService(final SapCpiCustomerConversionService sapCpiCustomerConversionService)
	{
		this.sapCpiCustomerConversionService = sapCpiCustomerConversionService;
	}


	/**
	 * @return the outboundServiceFacade
	 */
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}


	/**
	 * @param outboundServiceFacade
	 *           the outboundServiceFacade to set
	 */
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

}
