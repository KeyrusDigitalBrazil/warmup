/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.order.hook.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandlerFactory;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ConfigurableProductAddToCartMethodHook implements CommerceAddToCartMethodHook
{
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurableProductAddToCartMethodHook.class);

	private ProductConfigurationHandlerFactory configurationFactory;
	private ModelService modelService;
	private ConfiguratorSettingsService configuratorSettingsService;

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		// Implementation not needed
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result) throws
			CommerceCartModificationException
	{
		if (result.getQuantityAdded() > 0)
		{
			ServicesUtil.validateParameterNotNullStandardMessage("parameters", parameters);
			ServicesUtil.validateParameterNotNullStandardMessage("result", result);

			final AbstractOrderEntryModel entry = result.getEntry();
			if (entry == null)
			{
				LOG.warn("No entry created");
			}
			else if (CollectionUtils.isEmpty(entry.getProductInfos()))
			{
				getConfiguratorSettingsService().getConfiguratorSettingsForProduct(parameters.getProduct())
						.forEach(config -> createProductInfo(config, entry));
			}
		}
	}

	protected void createProductInfo(final AbstractConfiguratorSettingModel configuration, final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationHandler productConfigurationHandler = getConfigurationFactory()
				.handlerOf(configuration.getConfiguratorType());
		if (productConfigurationHandler == null)
		{
			throw new IllegalStateException("No ProductConfigurationHandler registered for configurator type "
					+ configuration.getConfiguratorType());
		}
		List<AbstractOrderEntryProductInfoModel> infos = productConfigurationHandler.createProductInfo(configuration);
		entry.setProductInfos(Stream.concat(
				entry.getProductInfos() == null ? Stream.empty() : entry.getProductInfos().stream(),
				infos.stream().peek(item -> item.setOrderEntry(entry)).peek(getModelService()::save))
				.collect(Collectors.toList()));
		getModelService().save(entry);
	}

	protected ProductConfigurationHandlerFactory getConfigurationFactory()
	{
		return configurationFactory;
	}

	@Required
	public void setConfigurationFactory(final ProductConfigurationHandlerFactory configurationFactory)
	{
		this.configurationFactory = configurationFactory;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ConfiguratorSettingsService getConfiguratorSettingsService()
	{
		return configuratorSettingsService;
	}

	@Required
	public void setConfiguratorSettingsService(
			final ConfiguratorSettingsService configuratorSettingsService)
	{
		this.configuratorSettingsService = configuratorSettingsService;
	}
}
