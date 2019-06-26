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
package de.hybris.platform.sap.productconfig.pricing.bol.backend.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.sappricingbol.backend.impl.SapPricingItemMapper;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;


/**
 *
 */
public class ProductConfigurationSapPricingItemMapper extends SapPricingItemMapper
{

	protected static final String FACTOR = "FACTOR";
	protected static final String VARCOND = "VARCOND";
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;
	private ProductConfigurationService configurationService;

	private static final Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(ProductConfigurationSapPricingItemMapper.class.getName());


	@Override
	public void fillImportParameters(final JCoParameterList importParameters, final List<ProductModel> productModels,
			final ConversionService conversionService)
	{
		super.fillImportParameters(importParameters, productModels, conversionService);
		final JCoTable itItem = importParameters.getTable("IT_ITEM");
		fillVariantConditions(itItem, productModels);
	}

	protected void fillVariantConditions(final JCoTable itItem, final List<ProductModel> productModels)
	{
		for (final ProductModel product : productModels)
		{
			if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(product))
			{
				ConfigModel configModel = null;
				try
				{
					configModel = createConfiguration(product);
					addVariantConditions(itItem, product, configModel);
				}
				catch (final IllegalStateException ex)
				{
					sapLogger.getLogger().error("Could not create configuration for product \'" + product.getCode() + "\'", ex);
					continue;
				}
				finally
				{
					if (null != configModel)
					{
						getConfigurationService().releaseSession(configModel.getId());
					}
				}
			}
		}
	}

	protected ConfigModel createConfiguration(final ProductModel product)
	{
		ConfigModel configModel = null;
		if (getCpqConfigurableChecker().isCPQConfigurableProduct(product))
		{
			final KBKey kbKey = new KBKeyImpl(product.getCode());
			configModel = getConfigurationService().createDefaultConfiguration(kbKey);
		}
		else
		{
			final String baseProductCode = ((VariantProductModel) product).getBaseProduct().getCode();
			configModel = getConfigurationService().createConfigurationForVariant(baseProductCode, product.getCode());
		}
		return configModel;
	}

	@Override
	protected void fillImportParameters(final JCoTable itItem, final AbstractOrderEntryModel orderEntryModel,
			final ConversionService conversionService)
	{
		super.fillImportParameters(itItem, orderEntryModel, conversionService);
		fillVariantConditions(itItem, orderEntryModel);
	}

	protected void fillVariantConditions(final JCoTable itItem, final AbstractOrderEntryModel orderEntryModel)
	{
		final ProductModel product = orderEntryModel.getProduct();
		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(product))
		{
			final ConfigModel configModel = getConfigurationAbstractOrderIntegrationStrategy()
					.getConfigurationForAbstractOrderEntry(orderEntryModel);

			addVariantConditions(itItem, product, configModel);
		}
	}

	protected void addVariantConditions(final JCoTable itItem, final ProductModel product, final ConfigModel configModel)
	{
		if (sapLogger.isDebugEnabled())
		{
			sapLogger.debug(new StringBuilder().append("Product: ").append(product.getCode()).toString());
		}

		final JCoTable variantConditions = itItem.getTable(VARCOND);
		for (final VariantConditionModel source : configModel.getRootInstance().getVariantConditions())
		{
			variantConditions.appendRow();
			final String condKey = source.getKey();
			variantConditions.setValue(VARCOND, condKey);
			final BigDecimal condFactor = source.getFactor();
			variantConditions.setValue(FACTOR, Double.valueOf(condFactor.doubleValue()));

			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug(new StringBuilder().append(VARCOND).append(" ").append(condKey).append(": ").append(FACTOR)
						.append(" ").append(condFactor.toString()).toString());
			}
		}
	}

	/**
	 * @return the cpqConfigurableChecker
	 */
	public CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return cpqConfigurableChecker;
	}

	/**
	 * @param cpqConfigurableChecker
	 *           the cpqConfigurableChecker to set
	 */
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	/**
	 * @param configurationAbstractOrderEntryLinkStrategy
	 *           the configurationAbstractOrderEntryLinkStrategy to set
	 */
	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setConfigurationAbstractOrderIntegrationStrategy(
			final ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy)
	{
		this.configurationAbstractOrderIntegrationStrategy = configurationAbstractOrderIntegrationStrategy;
	}

	protected ConfigurationAbstractOrderIntegrationStrategy getConfigurationAbstractOrderIntegrationStrategy()
	{
		return configurationAbstractOrderIntegrationStrategy;
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
