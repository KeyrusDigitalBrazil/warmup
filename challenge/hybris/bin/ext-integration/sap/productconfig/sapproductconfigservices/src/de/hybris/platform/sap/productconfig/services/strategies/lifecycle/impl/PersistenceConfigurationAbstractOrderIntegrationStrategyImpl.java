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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


public class PersistenceConfigurationAbstractOrderIntegrationStrategyImpl extends SessionServiceAware
		implements ConfigurationAbstractOrderIntegrationStrategy
{

	private static final Logger LOG = Logger.getLogger(PersistenceConfigurationAbstractOrderIntegrationStrategyImpl.class);

	private ModelService modelService;
	private ProductConfigurationPersistenceService persistenceService;
	private ProductConfigurationService configurationService;
	private ConfigurationVariantUtil configurationVariantUtil;
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;

	/**
	 * @param configurationService
	 */
	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @param modelService
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param persistenceService
	 */
	@Required
	public void setPersistenceService(final ProductConfigurationPersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	/**
	 * @param configurationVariantUtil
	 */
	@Required
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}

	/**
	 * @param configurationLifecycleStrategy
	 */
	@Required
	public void setConfigurationLifecycleStrategy(final ConfigurationLifecycleStrategy configurationLifecycleStrategy)
	{
		this.configurationLifecycleStrategy = configurationLifecycleStrategy;
	}


	protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
	{
		return configurationLifecycleStrategy;
	}


	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Override
	public void updateAbstractOrderEntryOnLink(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry)
	{
		//nothing happens as at this point the cart entry doesn't need to carry the external configuration
	}



	protected void releaseCartEntryProductRelation(final AbstractOrderEntryModel cartEntry)
	{
		final ProductConfigurationModel productConfiguration = cartEntry.getProductConfiguration();
		Preconditions.checkNotNull(productConfiguration, "We expect a configuration attached to the abstract order entry");
		final Collection<ProductModel> product = productConfiguration.getProduct();
		if (!CollectionUtils.isEmpty(product))
		{
			final String productCode = product.iterator().next().getCode();
			getSessionAccessService().removeUiStatusForProduct(productCode);
			productConfiguration.setProduct(Collections.emptyList());
			getModelService().save(productConfiguration);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Unlinking product '%s' from config '%s'", productCode,
						productConfiguration.getConfigurationId()));
			}
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}



	protected ProductConfigurationPersistenceService getPersistenceService()
	{
		return persistenceService;
	}


	@Override
	public void updateAbstractOrderEntryOnUpdate(final String configId, final AbstractOrderEntryModel entry)
	{
		//nothing happens as at this point the cart entry doesn't need to carry the external configuration
	}

	@Override
	public ConfigModel getConfigurationForAbstractOrderEntry(final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationModel productConfiguration = entry.getProductConfiguration();

		if (null == productConfiguration)
		{
			return createDefaultConfiguration(entry);
		}
		else
		{
			return getConfigurationService().retrieveConfigurationModel(productConfiguration.getConfigurationId());
		}
	}

	protected ConfigModel createDefaultConfiguration(final AbstractOrderEntryModel entry)
	{
		ConfigModel configModel = null;
		final ProductModel productModel = entry.getProduct();
		final String productCode = productModel.getCode();
		if (getConfigurationVariantUtil().isCPQVariantProduct(productModel))
		{
			final String baseProductCode = getConfigurationVariantUtil().getBaseProductCode(productModel);
			configModel = getConfigurationService().createConfigurationForVariant(baseProductCode, productCode);
			releaseDraft(entry);
			entry.setProductConfigurationDraft(getPersistenceService().getByConfigId(configModel.getId()));
		}
		else
		{
			final KBKey kbKey = new KBKeyImpl(productCode);
			configModel = getConfigurationService().createDefaultConfiguration(kbKey);
			entry.setProductConfiguration(getPersistenceService().getByConfigId(configModel.getId()));
		}
		return configModel;
	}

	@Override
	public boolean isKbVersionForEntryExisting(final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationModel productConfiguration = getProductConfiguration(entry);

		String productCode = entry.getProduct().getCode();
		if (getConfigurationVariantUtil().isCPQChangeableVariantProduct(entry.getProduct()))
		{
			// for changebale varaints, use the base product (KMAT) code for this check
			productCode = getConfigurationVariantUtil().getBaseProductCode(entry.getProduct());
		}
		// Date is set by constructor to current date.
		final KBKey kbKey = new KBKeyImpl(productCode, productConfiguration.getKbName(), productConfiguration.getKbLogsys(),
				productConfiguration.getKbVersion());
		return getConfigurationService().isKbVersionValid(kbKey);
	}

	protected ProductConfigurationModel getProductConfiguration(final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationModel productConfiguration = entry.getProductConfiguration() != null
				? entry.getProductConfiguration()
				: entry.getProductConfigurationDraft();
		Preconditions.checkNotNull(productConfiguration, "A configuration must be attached to an order entry");
		return productConfiguration;
	}

	@Override
	public void finalizeCartEntry(final AbstractOrderEntryModel entry)
	{
		//the models are not saved, this API can be called on non-saved entries!
		releaseCartEntryProductRelation(entry);
		releaseDraft(entry);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Product configuration attached to abstract order entry: " + entry.getProductConfiguration());
		}
		getSessionAccessService().removeUiStatusForCartEntry(entry.getPk().toString());
	}

	@Override
	public String getExternalConfigurationForAbstractOrderEntry(final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationModel productConfiguration = getProductConfiguration(entry);
		return configurationService.retrieveExternalConfiguration(productConfiguration.getConfigurationId());
	}

	@Override
	public ConfigModel getConfigurationForAbstractOrderEntryForOneTimeAccess(final AbstractOrderEntryModel entry)
	{
		//Note that for persistence scenario, there is no difference to the standard call getConfigurationForAbstractOrderEntry.
		//(For the session based scenario, there is. That's why we have 2 APIs)
		return getConfigurationForAbstractOrderEntry(entry);
	}

	@Override
	public void invalidateCartEntryConfiguration(final AbstractOrderEntryModel entry)
	{
		entry.setProductConfiguration(null);
		//Missing save
		getModelService().save(entry);
	}

	@Override
	public void prepareForOrderReplication(final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationModel productConfiguration = getProductConfiguration(entry);
		final String externalConfiguration = getConfigurationService()
				.retrieveExternalConfiguration(productConfiguration.getConfigurationId());
		entry.setExternalConfiguration(externalConfiguration);
		getModelService().save(entry);
	}

	protected void releaseDraft(final AbstractOrderEntryModel orderEntry)
	{
		final ProductConfigurationModel productConfigurationDraft = orderEntry.getProductConfigurationDraft();
		if (productConfigurationDraft != null)
		{
			getConfigurationLifecycleStrategy().releaseSession(productConfigurationDraft.getConfigurationId());
			orderEntry.setProductConfigurationDraft(null);
		}
	}

	@Override
	public boolean isRuntimeConfigForEntryExisting(final AbstractOrderEntryModel entry)
	{
		return null != entry.getProductConfiguration();
	}

}
