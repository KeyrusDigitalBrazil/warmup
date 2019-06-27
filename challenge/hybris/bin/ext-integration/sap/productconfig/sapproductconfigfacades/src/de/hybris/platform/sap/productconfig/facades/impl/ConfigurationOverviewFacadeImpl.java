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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOverviewPopulator;
import de.hybris.platform.sap.productconfig.facades.populator.VariantOverviewPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationOverviewFacade}.
 */
public class ConfigurationOverviewFacadeImpl implements ConfigurationOverviewFacade
{
	private static final Logger LOG = Logger.getLogger(ConfigurationOverviewFacadeImpl.class);

	private ProductConfigurationService configurationService;
	private ProductService productService;
	private ConfigurationOverviewPopulator configurationOverviewPopulator;
	private VariantOverviewPopulator variantOverviewPopulator;
	private PricingService pricingService;


	protected VariantOverviewPopulator getVariantOverviewPopulator()
	{
		return variantOverviewPopulator;
	}

	/**
	 * @param variantOverviewPopulator
	 *           the variantOverviewPopulator to set
	 */
	@Required
	public void setVariantOverviewPopulator(final VariantOverviewPopulator variantOverviewPopulator)
	{
		this.variantOverviewPopulator = variantOverviewPopulator;
	}


	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}


	protected ConfigurationOverviewPopulator getConfigurationOverviewPopulator()
	{
		return configurationOverviewPopulator;
	}

	/**
	 * @param configurationOverviewPopulator
	 *           the configurationOverviewPopulator to set
	 */
	@Required
	public void setConfigurationOverviewPopulator(final ConfigurationOverviewPopulator configurationOverviewPopulator)
	{
		this.configurationOverviewPopulator = configurationOverviewPopulator;
	}


	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected PricingService getPricingService()
	{
		return pricingService;
	}

	/**
	 * Setter for pricing Service
	 *
	 * @param pricingService
	 *           the pricingService to set
	 */
	@Required
	public void setPricingService(final PricingService pricingService)
	{
		this.pricingService = pricingService;
	}

	@Override
	public ConfigurationOverviewData getOverviewForConfiguration(final String configId,
			final ConfigurationOverviewData oldConfigOverview)
	{
		ConfigurationOverviewData configOverview = oldConfigOverview;
		if (configOverview == null)
		{
			LOG.debug("configOverview is null and a new instance has to be created");
			configOverview = new ConfigurationOverviewData();
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("cstic filters: " + configOverview.getAppliedCsticFilters());
			LOG.debug("group filters: " + configOverview.getAppliedGroupFilters());
		}

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);
		if (getPricingService().isActive())
		{
			getPricingService().fillOverviewPrices(configModel);
		}
		getConfigurationOverviewPopulator().populate(configModel, configOverview);

		return configOverview;
	}


	@Override
	public ConfigurationOverviewData getOverviewForProductVariant(final String productCode,
			final ConfigurationOverviewData oldConfigOverview)
	{
		ConfigurationOverviewData configOverview = oldConfigOverview;
		if (configOverview == null)
		{
			LOG.debug("configOverview is null and a new instance has to be created");
			configOverview = new ConfigurationOverviewData();
		}

		final ProductModel productModel = getProductService().getProductForCode(productCode);
		getVariantOverviewPopulator().populate(productModel, configOverview);

		return configOverview;
	}


}
