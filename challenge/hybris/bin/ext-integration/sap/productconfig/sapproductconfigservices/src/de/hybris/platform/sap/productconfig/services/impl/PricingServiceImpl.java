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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Provides price information for configuration
 *
 */
public class PricingServiceImpl implements PricingService
{
	private static final Logger LOG = Logger.getLogger(PricingServiceImpl.class);

	private ProviderFactory providerFactory;
	private ConfigurationModelCacheStrategy configurationModelCacheStrategy;
	private PricingConfigurationParameter pricingConfigurationParameter;
	private ProductConfigurationCacheAccessService productConfigurationCacheAccessService;

	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;

	private ProductConfigurationService productConfigurationService;

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	@Override
	public PriceSummaryModel getPriceSummary(final String configId)
	{
		PriceSummaryModel priceSummaryModel = getProductConfigurationCacheAccessService().getPriceSummaryState(configId);
		if (priceSummaryModel == null)
		{
			final ConfigModel configModel = loadConfigModel(configId);
			try
			{
				final ConfigurationRetrievalOptions options = prepareRetrievalOptionsWithDate(configModel);
				priceSummaryModel = getProviderFactory().getPricingProvider().getPriceSummary(configId, options);
				configModel.setPricingError(false);
				getProductConfigurationCacheAccessService().setPriceSummaryState(configId, priceSummaryModel);
			}
			catch (final PricingEngineException | ConfigurationEngineException e)
			{
				configModel.setPricingError(true);
				LOG.error("error when retrieving price summary from provider", e);
			}
		}
		return priceSummaryModel;
	}

	protected ConfigModel loadConfigModel(final String configId)
	{
		ConfigModel configModel = getConfigurationModelCacheStrategy().getConfigurationModelEngineState(configId);
		if (null == configModel)
		{
			configModel = getProductConfigurationService().retrieveConfigurationModel(configId);
		}
		if (null == configModel)
		{
			throw new IllegalStateException("No config model found for: " + configId);
		}
		return configModel;
	}

	protected ConfigurationRetrievalOptions prepareRetrievalOptionsWithDate(final ConfigModel configModel)
	{
		final ConfigurationRetrievalOptions options = prepareRetrievalOptions(configModel);
		options.setPricingDate(getAssignmentResolverStrategy().retrieveCreationDateForRelatedEntry(configModel.getId()));
		return options;
	}

	protected ConfigurationRetrievalOptions prepareRetrievalOptions(final ConfigModel configModel)
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		// specifying a different pricing product (e.g for changeable product variants) is only supported for singlelevel / root products
		// stetting this also for non-root procuts would break multilevel pricing!
		if (configModel.isSingleLevel())
		{
			options.setPricingProduct(getAssignmentResolverStrategy().retrieveRelatedProductCode(configModel.getId()));
		}
		return options;
	}

	/**
	 * @deprecated since 18.08
	 */
	@Override
	@Deprecated
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId)
	{
		try
		{
			retrieveValuePrices(updateModels, kbId, null);
		}
		catch (final PricingEngineException e)
		{
			LOG.debug("ignore errors when filling value prices", e);
			LOG.error("ignore errors when filling value prices, see debug log for details");
		}
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final ConfigModel configModel)
	{
		try
		{
			retrieveValuePrices(updateModels, configModel.getKbId(), configModel.getId());
		}
		catch (final PricingEngineException e)
		{
			LOG.debug("ignore errors when filling value prices", e);
			LOG.error("ignore errors when filling value prices, see debug log for details");
		}
	}

	protected void retrieveValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId, final String configId)
			throws PricingEngineException
	{
		getProviderFactory().getPricingProvider().fillValuePrices(updateModels, kbId);
	}


	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}


	/**
	 * @param providerFactory
	 *           the providerFactory to set
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}


	@Override
	public boolean isActive()
	{
		return getProviderFactory().getPricingProvider().isActive() && getPricingConfigurationParameter().isPricingSupported();
	}

	@Override
	public void fillOverviewPrices(final ConfigModel configModel)
	{
		final PriceSummaryModel summary = getPriceSummary(configModel.getId());
		if (summary != null)
		{
			fillConfigPrices(summary, configModel);
		}
		try
		{
			retrieveValuePrices(configModel);
		}
		catch (final PricingEngineException e)
		{
			LOG.error("ignore error when filling value prices for overview", e);
		}
	}

	protected void retrieveValuePrices(final ConfigModel configModel) throws PricingEngineException
	{
		getProviderFactory().getPricingProvider().fillValuePrices(configModel);
	}

	protected void fillConfigPrices(final PriceSummaryModel summary, final ConfigModel configModel)
	{
		configModel.setBasePrice(summary.getBasePrice());
		configModel.setCurrentTotalPrice(summary.getCurrentTotalPrice());
		configModel.setCurrentTotalSavings(summary.getCurrentTotalSavings());
		configModel.setSelectedOptionsPrice(summary.getSelectedOptionsPrice());
	}

	protected PricingConfigurationParameter getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 */
	public void setPricingConfigurationParameter(final PricingConfigurationParameter pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;

	}

	protected ConfigurationModelCacheStrategy getConfigurationModelCacheStrategy()
	{
		return configurationModelCacheStrategy;
	}

	@Required
	public void setConfigurationModelCacheStrategy(final ConfigurationModelCacheStrategy configurationModelCacheStrategy)
	{
		this.configurationModelCacheStrategy = configurationModelCacheStrategy;
	}

	protected ProductConfigurationCacheAccessService getProductConfigurationCacheAccessService()
	{
		return productConfigurationCacheAccessService;
	}

	@Required
	public void setProductConfigurationCacheAccessService(
			final ProductConfigurationCacheAccessService productConfigurationCacheAccessService)
	{
		this.productConfigurationCacheAccessService = productConfigurationCacheAccessService;
	}

	protected ConfigurationAssignmentResolverStrategy getAssignmentResolverStrategy()
	{
		return assignmentResolverStrategy;
	}

	@Required
	public void setAssignmentResolverStrategy(final ConfigurationAssignmentResolverStrategy assignmentResolverStrategy)
	{
		this.assignmentResolverStrategy = assignmentResolverStrategy;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{

		return this.productConfigurationService;
	}

}
