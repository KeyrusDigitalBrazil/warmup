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
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;


/**
 *
 */
public class SapDigitalPaymentConfigurationBaseStoreStrategy implements SapDigitalPaymentConfigurationStrategy
{

	private static final Logger LOG = Logger.getLogger(SapDigitalPaymentConfigurationBaseStoreStrategy.class);

	private BaseStoreService baseStoreService;

	/**
	 * Returns the {@link SAPDigitalPaymentConfigurationModel} from the current base store
	 */
	@Override
	public SAPDigitalPaymentConfigurationModel getSapDigitalPaymentConfiguration()
	{
		try
		{
			return getBaseStoreService().getCurrentBaseStore().getSapDigitalPaymentConfiguration();
		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Current basestore is null" + e);
			}
			LOG.error("Current basestore is null" + e.getMessage());
		}
		return null;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}



}
