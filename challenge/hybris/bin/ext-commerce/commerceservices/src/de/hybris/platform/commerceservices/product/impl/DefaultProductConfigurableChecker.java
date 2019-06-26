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
package de.hybris.platform.commerceservices.product.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.product.ProductConfigurableChecker;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ProductConfigurableChecker}
 */
public class DefaultProductConfigurableChecker implements ProductConfigurableChecker
{
	private ConfiguratorSettingsService configuratorSettingsService;

	@Override
	public boolean isProductConfigurable(@Nonnull final ProductModel product)
	{
		validateParameterNotNullStandardMessage("product", product);
		return CollectionUtils.isNotEmpty(getConfiguratorSettingsService().getConfiguratorSettingsForProduct(product));
	}

	@Override
	public String getFirstConfiguratorType(final ProductModel product)
	{
		validateParameterNotNullStandardMessage("product", product);
		String configuratorType = null;
		final List<AbstractConfiguratorSettingModel> configuratorSettingsForProduct = getConfiguratorSettingsService()
				.getConfiguratorSettingsForProduct(product);
		if (configuratorSettingsForProduct != null && configuratorSettingsForProduct.size() > 0)
		{
			final ConfiguratorType configuratorTypeModel = configuratorSettingsForProduct.get(0).getConfiguratorType();
			configuratorType = configuratorTypeModel.getCode();
		}
		return configuratorType;
	}

	protected ConfiguratorSettingsService getConfiguratorSettingsService()
	{
		return configuratorSettingsService;
	}

	@Required
	public void setConfiguratorSettingsService(final ConfiguratorSettingsService configuratorSettingsService)
	{
		this.configuratorSettingsService = configuratorSettingsService;
	}
}
