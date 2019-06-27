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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.product.impl.DefaultProductConfigurableChecker;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.variants.model.VariantProductModel;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Support class to check if a specific product is configurable with the CPQ configurator.
 */
public class CPQConfigurableChecker extends DefaultProductConfigurableChecker
{
	private static final String PRODUCT = "product";
	private ConfigurationVariantUtil configurationVariantUtil;

	/**
	 * Check if the given product is CPQ configurable base product and can be configured by the CPQ configurator.
	 *
	 * @param product
	 *           The product to check
	 * @return TRUE if it is a CPQ configurable base product and can be configured by the CPQ configurator , otherwise
	 *         FALSE
	 */
	public boolean isCPQConfigurableProduct(@Nonnull final ProductModel product)
	{
		validateParameterNotNullStandardMessage(PRODUCT, product);
		return !(product instanceof VariantProductModel) && isUseableWithCPQConfigurator(product);
	}


	/**
	 * Check if the given product is CPQ changeable variant product and can be configured by the CPQ configurator.
	 *
	 * @param product
	 *           The product to check
	 * @return TRUE if it is a CPQ changeable variant product and can be configured by the CPQ configurator , otherwise
	 *         FALSE
	 */
	public boolean isCPQChangeableVariantProduct(@Nonnull final ProductModel product)
	{
		validateParameterNotNullStandardMessage(PRODUCT, product);
		return getConfigurationVariantUtil().isCPQChangeableVariantProduct(product) && isUseableWithCPQConfigurator(product);
	}

	/**
	 * Check if the given product is CPQ a not-changeable variant product
	 *
	 * @param product
	 *           The product to check
	 * @return TRUE if it is a CPQ not-changeable variant product otherwise FALSE
	 */
	public boolean isCPQNotChangeableVariantProduct(@Nonnull final ProductModel product)
	{
		validateParameterNotNullStandardMessage(PRODUCT, product);
		return getConfigurationVariantUtil().isCPQNotChangeableVariantProduct(product);
	}

	/**
	 * Checks if the given product is applicable to the CPQ configurator.<br>
	 * This is true if the given product is CPQ Configurable product <b>OR</b> a CPQ changeable variant product and can
	 * be configured by the CPQ configurator.
	 *
	 * @param product
	 *           The product to check
	 * @return TRUE if it is a wither a CPQ changeable variant product or a CPQ configurable product otherwise FALSE
	 */
	public boolean isCPQConfiguratorApplicableProduct(@Nonnull final ProductModel product)
	{
		validateParameterNotNullStandardMessage(PRODUCT, product);
		return (!(product instanceof VariantProductModel) || getConfigurationVariantUtil().isCPQChangeableVariantProduct(product))
				&& isUseableWithCPQConfigurator(product);
	}



	protected boolean isUseableWithCPQConfigurator(final ProductModel product)
	{
		return getConfiguratorSettingsService().getConfiguratorSettingsForProduct(product).stream()
				.anyMatch(model -> model.getConfiguratorType() == ConfiguratorType.CPQCONFIGURATOR);
	}


	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	@Required
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}
}
