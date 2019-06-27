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
package de.hybris.platform.sap.sapcarintegration.services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapcarintegration.exceptions.BaseStoreUnassignedRuntimeException;
import de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Default concrete implementation to provide business logic for
 * {@link de.hybris.platform.sap.sapcarintegration.services.CarConfigurationService}
 */
public class DefaultCarConfigurationService implements CarConfigurationService
{

	private BaseStoreService baseStoreService;
	private static final Logger LOG = Logger.getLogger(CarConfigurationService.class);


	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;

	}

	@Override
	public String getSapClient()
	{
		return getConfiguration().getSapcarintegration_client();
	}

	@Override
	public String getRootUrl()
	{

		return getConfiguration().getSapcarintegration_HTTPDestination().getTargetURL();
	}

	@Override
	public String getServiceName()
	{

		return getConfiguration().getSapcarintegration_serviceName();
	}

	@Override
	public String getUsername()
	{

		return getConfiguration().getSapcarintegration_HTTPDestination().getUserid();
	}

	@Override
	public String getPassword()
	{

		return getConfiguration().getSapcarintegration_HTTPDestination().getPassword();
	}

	@Override
	public String getTransactionType()
	{
		return getConfiguration().getSapcommon_transactionType();
	}

	@Override
	public String getSalesOrganization()
	{
		return getConfiguration().getSapcommon_salesOrganization();
	}

	@Override
	public String getDistributionChannel()
	{
		return getConfiguration().getSapcommon_distributionChannel();
	}

	@Override
	public String getDivision()
	{
		return getConfiguration().getSapcommon_division();
	}

	protected SAPConfigurationModel getConfiguration()
	{
		SAPConfigurationModel configurationModel = getBaseStoreService().getCurrentBaseStore().getSAPConfiguration();
		if (configurationModel == null)
		{
			String message = "The current BaseStore is not assigned to a SAP Configuration";
			LOG.fatal(message);
			throw new BaseStoreUnassignedRuntimeException(message);
		}
		return configurationModel;
	}


}
