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
package de.hybris.platform.sap.productconfig.services.event.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPagingUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Event Handler to release CPS runtime configurations just before the System is initialiazed.<br>
 * The Lifecycle of any CPS runtime configuration created is managed by Hybris. When we initialize, any data on
 * persistet configurations are lost, hence we have to also infor the CPS service, that the corresponding CPS runtime
 * configuratiuon can be deleted.
 */
public class ProductConfigAfterInitializationStartEventListener extends AbstractEventListener<AfterInitializationStartEvent>
{


	static final String ID_SEPERATOR = "; ";
	static final String INIT_MODE = "init";
	static final String INIT_METHOD_PARAM_NAME = "initmethod";
	private static final Logger LOG = Logger.getLogger(ProductConfigAfterInitializationStartEventListener.class);

	@Override
	protected void onEvent(final AfterInitializationStartEvent event)
	{
		if (ServicelayerUtils.isSystemInitialized())
		{
			onEventInternal(event);
		}
	}


	protected void onEventInternal(final AfterInitializationStartEvent event)
	{
		if (INIT_MODE.equals(event.getParams().get(INIT_METHOD_PARAM_NAME)))
		{
			LOG.info("initialization started... releasing all created runtime configurations");
			final StringBuilder builder = new StringBuilder();
			getProductConfigurationPagingUtil().processPageWise(currentPage -> getConfigs(currentPage),
					list -> releaseConfigs(list, builder));
			if (builder.length() > 0)
			{
				builder.delete(builder.length() - 2, builder.length());
				LOG.error("The following CPS Runtime configuration could not be released: [" + builder.toString() + "]");
			}
			else
			{
				LOG.info("Successfully released all CPS runtime configurations found");
			}
		}

	}

	protected SearchPageData<ProductConfigurationModel> getConfigs(final Integer currentPage)
	{
		SearchPageData<ProductConfigurationModel> searchResult = getProductConfigurationPersistenceService()
				.getAll(ProductConfigurationPagingUtil.PAGE_SIZE, currentPage);
		if (0 == currentPage)
		{
			final long totalnum = searchResult.getPagination().getTotalNumberOfResults();
			LOG.info("Found " + totalnum + " runtime configurations.");
			if (totalnum > 0 && !prepeareCleanUp())
			{
				searchResult = createEmptyResult();
			}
		}
		return searchResult;
	}

	protected SearchPageData<ProductConfigurationModel> createEmptyResult()
	{
		SearchPageData<ProductConfigurationModel> searchResult;
		searchResult = new SearchPageData<>();
		searchResult.setResults(Collections.emptyList());
		searchResult.setPagination(new PaginationData());
		return searchResult;
	}

	protected boolean prepeareCleanUp()
	{
		final ConfigurationProvider configurationProviderCPS = getConfigurationProviderCPS();
		final String providerName = configurationProviderCPS.getClass().getName();
		if (!providerName.contains("CPS"))
		{
			final String ids = collectAllConfigIds();
			LOG.error("Provider class " + providerName
					+ " was unexpected, The following CPS runtime configurations will not be released: [" + ids + "]");
			return false;
		}
		LOG.info("Using " + providerName + " to release CPS Configs");

		final BaseSiteModel baseSite = getProductConfigEventListenerUtil().getBaseSiteFromCronJob();
		if (null == baseSite)
		{
			final String ids = collectAllConfigIds();
			LOG.error(
					"Could not find any BaseSite within any ProductConfigurationPersistenceCleanUpCronJobModel configuration. CPS runtime configurations will not be released: ["
							+ ids + "]");
			return false;
		}
		/**
		 * Ensures that the current base site is set to enable yaasconfiguration to find the correct connection to the
		 * configuration engine. <br>
		 * Note that it's fine to have _any_ base site for which the CPS connection is maintained, it does not matter
		 * which of them. Reason: One environment only supports _one_ CPS connection including the SCP tenant.
		 * Yaasconfiguration and SCP tenant concepts deviate.
		 */
		getBaseSiteService().setCurrentBaseSite(baseSite, false);


		return true;
	}


	protected String collectAllConfigIds()
	{
		final StringBuilder builder = new StringBuilder();
		getProductConfigurationPagingUtil().processPageWise(currentPage -> getProductConfigurationPersistenceService()
				.getAll(ProductConfigurationPagingUtil.PAGE_SIZE, currentPage), list -> collectIds(list, builder));
		builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}


	protected void collectIds(final List<ProductConfigurationModel> list, final StringBuilder builder)
	{
		list.stream().forEach(model -> builder.append(model.getConfigurationId()).append(ID_SEPERATOR));
	}

	protected void releaseConfigs(final List<ProductConfigurationModel> list, final StringBuilder builder)
	{
		final ConfigurationProvider configurationProviderCPS = getConfigurationProviderCPS();
		list.stream().forEach(model -> releaseConfig(configurationProviderCPS, model, builder));
	}

	protected void releaseConfig(final ConfigurationProvider configurationProviderCPS, final ProductConfigurationModel model,
			final StringBuilder builder)
	{
		if (!"MOCK".equals(model.getKbLogsys()))
		{
			try
			{
				configurationProviderCPS.releaseSession(model.getConfigurationId(), model.getVersion());
			}
			catch (final RuntimeException ex)
			{
				LOG.debug("Releasing CPS runtime configuration failed, due to " + ex.getMessage(), ex);
				LOG.error("Releasing CPS runtime configuration failed, due to '" + ex.getMessage() + "'. See debug log for details.");
				builder.append(model.getConfigurationId()).append(ID_SEPERATOR);
			}
		}
		else
		{
			LOG.debug(String.format("Persistet runtime configuration with id %s was created by Mock, hence we can ignore it.",
					model.getConfigurationId()));
		}
	}

	protected ProductConfigEventListenerUtil getProductConfigEventListenerUtil()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getProductConfigEventListenerUtil().");
	}

	protected BaseSiteService getBaseSiteService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getBaseSiteSerrvice().");
	}

	protected ProductConfigurationPersistenceService getProductConfigurationPersistenceService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getProductConfigurationPersistenceService().");
	}

	protected ConfigurationProvider getConfigurationProviderCPS()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getConfigurationProviderCPS().");
	}

	protected ProductConfigurationPagingUtil getProductConfigurationPagingUtil()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for ProductConfigurationPagingUtil().");
	}
}
