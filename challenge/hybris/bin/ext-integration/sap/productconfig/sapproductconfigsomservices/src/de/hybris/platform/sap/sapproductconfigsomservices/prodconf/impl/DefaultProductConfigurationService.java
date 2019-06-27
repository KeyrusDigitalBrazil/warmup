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
package de.hybris.platform.sap.sapproductconfigsomservices.prodconf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link ProductConfigurationSomService}
 */
public class DefaultProductConfigurationService
		implements de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService
{
	private static final Logger LOG = Logger.getLogger(DefaultProductConfigurationService.class);
	private SessionAccessService sessionAccessService;
	private de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService productConfigurationService;

	/**
	 * @return the productConfigurationService
	 */
	protected de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}


	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(
			final de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}




	@Override
	public void setIntoSession(final String itemKey, final String configId)
	{
		getSessionAccessService().setConfigIdForCartEntry(itemKey, configId);

	}


	/**
	 * @return the sessionAccessService
	 */
	public SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}





	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}





	@Override
	public boolean isInSession(final String itemKey)
	{
		return getSessionAccessService().getConfigIdForCartEntry(itemKey) != null;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtservices.prodconf.ConfigurationContainer#getExternalConfiguration()
	 */
	@Override
	public String getExternalConfiguration(final String itemKey)
	{
		final String configId = getConfigIdFromSession(itemKey);
		return productConfigurationService.retrieveExternalConfiguration(configId);
	}




	@Override
	public ConfigModel getConfigModel(final String productCode, final String externalConfiguration)
	{
		if (externalConfiguration != null && !externalConfiguration.isEmpty())
		{
			final KBKey kbKey = new KBKeyImpl(productCode);
			return productConfigurationService.createConfigurationFromExternal(kbKey, externalConfiguration);
		}
		else
		{
			return null;
		}
	}


	@Override
	public Double getTotalPrice(final String itemKey)
	{
		final String configId = getConfigIdFromSession(itemKey);
		final ConfigModel configModel = productConfigurationService.retrieveConfigurationModel(configId);
		final PriceModel currentTotalPrice = configModel.getCurrentTotalPrice();
		if (currentTotalPrice != null)
		{
			return Double.valueOf(currentTotalPrice.getPriceValue().doubleValue());
		}
		else
		{
			return Double.valueOf(0);
		}

	}


	@Override
	public String getGetConfigId(final String itemKey)
	{
		return getConfigIdFromSession(itemKey);

	}


	/**
	 * Fetches config ID from hybris session
	 *
	 * @param itemKey
	 * @return Config ID
	 */
	protected String getConfigIdFromSession(final String itemKey)
	{
		final String configId = getSessionAccessService().getConfigIdForCartEntry(itemKey);

		if (configId == null)
		{
			LOG.info("No configuration found for item key: " + itemKey);
		}
		return configId;
	}
}
