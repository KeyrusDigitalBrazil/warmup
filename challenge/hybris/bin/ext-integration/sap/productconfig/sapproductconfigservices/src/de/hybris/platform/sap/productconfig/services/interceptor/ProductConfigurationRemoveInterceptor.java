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
package de.hybris.platform.sap.productconfig.services.interceptor;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.site.BaseSiteService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This Interceptor ensures, that no orphaned product configurations exist after an abstract order entry gets deleted.
 */
public class ProductConfigurationRemoveInterceptor implements RemoveInterceptor<AbstractOrderEntryModel>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigurationRemoveInterceptor.class);
	private ProductConfigurationPersistenceService productConfigurationPersistenceService;
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;
	private BaseSiteService baseSiteService;
	private ProductConfigEventListenerUtil productConfigEventListenerUtil;

	@Override
	public void onRemove(final AbstractOrderEntryModel model, final InterceptorContext ctx) throws InterceptorException
	{
		releaseConfigIfPossible(model, model.getProductConfiguration(), false);
		releaseConfigIfPossible(model, model.getProductConfigurationDraft(), true);
	}

	protected void releaseConfigIfPossible(final AbstractOrderEntryModel model,
			final ProductConfigurationModel productConfiguration, final boolean ignoreProductRelation)
	{
		if (null != productConfiguration)
		{
			final String configId = productConfiguration.getConfigurationId();
			if (checkBaseSiteAvailable(configId)
					&& (ignoreProductRelation || CollectionUtils.isEmpty(productConfiguration.getProduct())))
			{
				releaseConfigIfOrphaned(model.getPk().toString(), configId);
			}
		}
	}

	protected boolean checkBaseSiteAvailable(final String configId)
	{
		if (null == getBaseSiteService().getCurrentBaseSite())
		{
			final BaseSiteModel baseSiteFromCronJob = getProductConfigEventListenerUtil().getBaseSiteFromCronJob();
			if (null == baseSiteFromCronJob)
			{
				LOG.info(String.format(
						"Current Base Site not set. Product Configuration Model with id '%s' is possibly orphaned. Consider to run the clean up cron job 'sapProductConfigPersistenceCleanUpCronJob'",
						configId));
				return false;
			}
			getBaseSiteService().setCurrentBaseSite(baseSiteFromCronJob, false);
		}
		return true;
	}

	protected void releaseConfigIfOrphaned(final String cartEntryKey, final String configId)
	{
		if (getProductConfigurationPersistenceService().isOnlyRelatedToGivenEntry(configId, cartEntryKey))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(
						"Deleting Product Configuration Model with id '%s', as the parent abstract order entry with key '%s' gets deleted as well.",
						configId, cartEntryKey));
			}
			getConfigurationLifecycleStrategy().releaseSession(configId);
		}
	}

	protected ProductConfigurationPersistenceService getProductConfigurationPersistenceService()
	{
		return productConfigurationPersistenceService;
	}

	@Required
	public void setProductConfigurationPersistenceService(
			final ProductConfigurationPersistenceService productConfigurationPersistenceService)
	{
		this.productConfigurationPersistenceService = productConfigurationPersistenceService;
	}

	protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
	{
		return configurationLifecycleStrategy;
	}

	@Required
	public void setConfigurationLifecycleStrategy(final ConfigurationLifecycleStrategy configurationLifecycleStrategy)
	{
		this.configurationLifecycleStrategy = configurationLifecycleStrategy;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected ProductConfigEventListenerUtil getProductConfigEventListenerUtil()
	{
		return productConfigEventListenerUtil;
	}

	@Required
	public void setProductConfigEventListenerUtil(final ProductConfigEventListenerUtil productConfigEventListenerUtil)
	{
		this.productConfigEventListenerUtil = productConfigEventListenerUtil;
	}

}
