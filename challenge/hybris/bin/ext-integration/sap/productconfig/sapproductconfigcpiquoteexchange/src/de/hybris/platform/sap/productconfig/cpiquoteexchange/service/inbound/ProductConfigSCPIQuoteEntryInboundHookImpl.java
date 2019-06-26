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
package de.hybris.platform.sap.productconfig.cpiquoteexchange.service.inbound;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapquoteintegration.inbound.helper.InboundQuoteEntryHelper;


/**
 * Inbound hook for processing quote entries received from an external system. If the quote entry is linked to a
 * configuration, the reference will be replaced by a copy of the configuration to ensure independency.
 */
public class ProductConfigSCPIQuoteEntryInboundHookImpl implements InboundQuoteEntryHelper
{

	private static final Logger LOG = Logger.getLogger(ProductConfigSCPIQuoteEntryInboundHookImpl.class);
	private ConfigurationDeepCopyHandler configurationDeepCopyHandler;
	private BaseSiteService baseSiteService;
	private ModelService modelService;
	private ProductConfigurationPersistenceService productConfigurationPersistenceService;


	@Override
	public QuoteEntryModel processInboundQuoteEntry(final QuoteEntryModel inboundQuoteEntry)
	{
		final ProductConfigurationModel productConfigFromExternal = inboundQuoteEntry.getProductConfiguration();
		if (null != productConfigFromExternal)
		{
			ensureBaseSiteIsAvailable(inboundQuoteEntry.getOrder());
			final String configId = productConfigFromExternal.getConfigurationId();
			final String productCode = inboundQuoteEntry.getProduct().getCode();
			final String newConfigId = getConfigurationDeepCopyHandler().deepCopyConfiguration(configId, productCode, null, true,
					ProductConfigurationRelatedObjectType.QUOTE_ENTRY);
			final ProductConfigurationModel internalProductConfigModel = getProductConfigurationPersistenceService()
					.getByConfigId(newConfigId);
			internalProductConfigModel.setUser(inboundQuoteEntry.getOrder().getUser());
			inboundQuoteEntry.setProductConfiguration(internalProductConfigModel);
			// drop the intermediate model with the callidus ID, we do not need it anymore
			modelService.detach(productConfigFromExternal);
			// required to show the view config link on the quotation page
			createBasicConfigurationInfo(inboundQuoteEntry);


		}
		return inboundQuoteEntry;
	}

	protected void createBasicConfigurationInfo(final AbstractOrderEntryModel orderEntry)
	{
		final List<AbstractOrderEntryProductInfoModel> configInfos = new ArrayList<>();
		final CPQOrderEntryProductInfoModel configInfo = getModelService().create(CPQOrderEntryProductInfoModel.class);
		configInfo.setOrderEntry(orderEntry);
		configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configInfos.add(configInfo);
		orderEntry.setProductInfos(Collections.unmodifiableList(configInfos));
	}

	protected void ensureBaseSiteIsAvailable(final QuoteModel quoteModel)
	{
		if (null == getBaseSiteService().getCurrentBaseSite())
		{
			LOG.info(
					"Injecting current BaseSite from QuoteModel to enable cloning of configuration. Consider to fix the process, so that a base site is always available.");
			baseSiteService.setCurrentBaseSite(quoteModel.getSite(), true);
		}
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


	protected ConfigurationDeepCopyHandler getConfigurationDeepCopyHandler()
	{
		return configurationDeepCopyHandler;
	}

	@Required
	public void setConfigurationDeepCopyHandler(final ConfigurationDeepCopyHandler configurationDeepCopyHandler)
	{
		this.configurationDeepCopyHandler = configurationDeepCopyHandler;
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

	public ProductConfigurationPersistenceService getProductConfigurationPersistenceService()
	{
		return productConfigurationPersistenceService;
	}

	@Required
	public void setProductConfigurationPersistenceService(
			final ProductConfigurationPersistenceService productConfigurationPersistenceService)
	{
		this.productConfigurationPersistenceService = productConfigurationPersistenceService;
	}

}
