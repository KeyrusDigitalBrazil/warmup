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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.SessionServiceAware;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationAbstractOrderEntryLinkStrategy}. It uses the hybris session to
 * store any data and hence delegates to the {@link SessionAccessService}.
 */
public class DefaultConfigurationAbstractOrderIntegrationStrategyImpl extends SessionServiceAware
		implements ConfigurationAbstractOrderIntegrationStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultConfigurationAbstractOrderIntegrationStrategyImpl.class);

	private TrackingRecorder recorder;
	private ProductConfigurationService configurationService;
	private ModelService modelService;
	private ConfigurationVariantUtil configurationVariantUtil;

	@Override
	public void updateAbstractOrderEntryOnLink(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry)
	{
		final String xml = getConfigurationService().retrieveExternalConfiguration(parameters.getConfigId());
		entry.setExternalConfiguration(xml);
		getModelService().save(entry);
		getRecorder().recordUpdateCartEntry(entry, parameters);
		LOG.debug("Configuration with config ID " + parameters.getConfigId() + " set at cart entry " + entry.getPk().toString()
				+ ": " + xml);
	}

	public void updateAbstractOrderEntryOnUpdate(final String configId, final AbstractOrderEntryModel entry)
	{
		final String newExternalConfiguration = getConfigurationService().retrieveExternalConfiguration(configId);
		entry.setExternalConfiguration(newExternalConfiguration);
		getModelService().save(entry);
		LOG.debug(
				"Configuration with config ID " + configId + " set at cart entry " + entry.getPk() + ": " + newExternalConfiguration);
	}

	public ConfigModel getConfigurationForAbstractOrderEntry(final AbstractOrderEntryModel entry)
	{
		final String cartEntryKey = entry.getPk().toString();
		final String configId = getConfigIdForCartEntry(cartEntryKey);
		if (configId != null)
		{
			ensureExternalConfigurationIsPresent(configId, entry);
			return getConfigurationService().retrieveConfigurationModel(configId);
		}
		else
		{
			final ConfigModel configurationFromExternal = createConfiguration(entry, cartEntryKey);
			return configurationFromExternal;
		}
	}

	protected ConfigModel createConfiguration(final AbstractOrderEntryModel entry, final String cartEntryKey)
	{
		ConfigModel configModel = null;
		final ProductModel productModel = entry.getProduct();
		if (getConfigurationVariantUtil().isCPQNotChangeableVariantProduct(productModel))
		{
			final String baseProductCode = ((VariantProductModel) productModel).getBaseProduct().getCode();
			configModel = getConfigurationService().createConfigurationForVariant(baseProductCode, productModel.getCode());
		}
		else
		{
			final KBKey kbKey = new KBKeyImpl(entry.getProduct().getCode());
			final String externalConfiguration = entry.getExternalConfiguration();
			if (externalConfiguration != null)
			{
				configModel = getConfigurationService().createConfigurationFromExternal(kbKey, externalConfiguration);
			}
			else
			{
				configModel = getConfigurationService().createDefaultConfiguration(kbKey);
			}
			setConfigIdForCartEntry(cartEntryKey, configModel.getId());
		}
		return configModel;
	}

	protected void ensureExternalConfigurationIsPresent(final String configId, final AbstractOrderEntryModel entry)
	{
		if (StringUtils.isEmpty(entry.getExternalConfiguration()))
		{
			entry.setExternalConfiguration(getConfigurationService().retrieveExternalConfiguration(configId));
			getModelService().save(entry);
		}
	}

	public boolean isKbVersionForEntryExisting(final AbstractOrderEntryModel entry)
	{
		String productCode = entry.getProduct().getCode();
		if (getConfigurationVariantUtil().isCPQChangeableVariantProduct(entry.getProduct()))
		{
			// for changebale varaints, use the base product (KMAT) code for this check
			productCode = getConfigurationVariantUtil().getBaseProductCode(entry.getProduct());
		}
		final KBKey kbKey = getConfigurationService().extractKbKey(productCode, entry.getExternalConfiguration());
		return getConfigurationService().isKbVersionValid(kbKey);
	}

	public void finalizeCartEntry(final AbstractOrderEntryModel entry)
	{
		final String cartEntryKey = entry.getPk().toString();
		getConfigurationService().releaseSession(getConfigIdForCartEntry(cartEntryKey));
		getSessionAccessService().removeSessionArtifactsForCartEntry(cartEntryKey);

	}

	public String getExternalConfigurationForAbstractOrderEntry(final AbstractOrderEntryModel entry)
	{
		return entry.getExternalConfiguration();
	}

	public ConfigModel getConfigurationForAbstractOrderEntryForOneTimeAccess(final AbstractOrderEntryModel entry)
	{
		final ConfigModel configModel = getConfigurationForAbstractOrderEntry(entry);
		getConfigurationService().releaseSession(configModel.getId(), true);
		removeConfigIdForCartEntry(entry.getPk().toString());
		return configModel;
	}

	public void invalidateCartEntryConfiguration(final AbstractOrderEntryModel entry)
	{
		entry.setExternalConfiguration(null);
	}

	public void prepareForOrderReplication(final AbstractOrderEntryModel entry)
	{
		//nothing to do here as the external configuration is already part of the order entry
	}

	@Override
	public boolean isRuntimeConfigForEntryExisting(final AbstractOrderEntryModel entry)
	{
		final boolean configCached = null != getSessionAccessService().getConfigIdForCartEntry(entry.getPk().toString());
		final boolean extConfigPresent = null != entry.getExternalConfiguration();
		return configCached || extConfigPresent;
	}

	protected void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		getSessionAccessService().setConfigIdForCartEntry(cartEntryKey, configId);
	}

	protected String getConfigIdForCartEntry(final String cartEntryKey)
	{
		return getSessionAccessService().getConfigIdForCartEntry(cartEntryKey);
	}

	protected void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		getSessionAccessService().removeConfigIdForCartEntry(cartEntryKey);
	}

	@Required
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}

	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

}
