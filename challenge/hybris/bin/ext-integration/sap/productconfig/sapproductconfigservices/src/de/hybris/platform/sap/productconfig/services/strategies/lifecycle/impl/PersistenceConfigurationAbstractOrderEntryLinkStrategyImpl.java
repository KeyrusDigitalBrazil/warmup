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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


public class PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl extends SessionServiceAware
		implements ConfigurationAbstractOrderEntryLinkStrategy
{

	private static final Logger LOG = Logger.getLogger(PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl.class);

	private ModelService modelService;
	private ProductConfigurationPersistenceService persistenceService;


	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		final AbstractOrderEntryModel orderEntry = getPersistenceService().getOrderEntryByPK(cartEntryKey);
		final ProductConfigurationModel productConfiguration = getPersistenceService().getByConfigId(configId);
		orderEntry.setProductConfiguration(productConfiguration);
		getModelService().save(orderEntry);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Linking confingId '%s' with cartEntry '%s'", configId, cartEntryKey));
		}
	}

	@Override
	public void setDraftConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		final AbstractOrderEntryModel orderEntry = getPersistenceService().getOrderEntryByPK(cartEntryKey);
		final ProductConfigurationModel productConfiguration = getPersistenceService().getByConfigId(configId);
		orderEntry.setProductConfigurationDraft(productConfiguration);
		getModelService().save(orderEntry);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Linking draft confingId '%s' with cartEntry '%s'", configId, cartEntryKey));
		}
	}


	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		final ProductConfigurationModel configuration = getPersistenceService().getOrderEntryByPK(cartEntryKey)
				.getProductConfiguration();
		return null == configuration ? null : configuration.getConfigurationId();
	}

	@Override
	public String getDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		final ProductConfigurationModel configuration = getPersistenceService().getOrderEntryByPK(cartEntryKey)
				.getProductConfigurationDraft();
		return null == configuration ? null : configuration.getConfigurationId();
	}

	public String getCartEntryForConfigId(final String configId)
	{
		final AbstractOrderEntryModel entry = getPersistenceService().getOrderEntryByConfigId(configId, false);
		return null == entry ? null : entry.getPk().toString();
	}


	@Override
	public String getCartEntryForDraftConfigId(final String configId)
	{
		final AbstractOrderEntryModel entry = getPersistenceService().getOrderEntryByConfigId(configId, true);
		return null == entry ? null : entry.getPk().toString();
	}

	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		final AbstractOrderEntryModel orderEntry = getPersistenceService().getOrderEntryByPK(cartEntryKey);
		if (null != orderEntry.getProductConfiguration())
		{
			if (LOG.isDebugEnabled())
			{
				final String configurationId = orderEntry.getProductConfiguration().getConfigurationId();
				LOG.debug(String.format("Unlinking confingId '%s' from cartEntry '%s'", configurationId, cartEntryKey));
			}
			orderEntry.setProductConfiguration(null);
			getModelService().save(orderEntry);
		}
	}


	@Override
	public void removeDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		final AbstractOrderEntryModel orderEntry = getPersistenceService().getOrderEntryByPK(cartEntryKey);
		if (null != orderEntry.getProductConfigurationDraft())
		{
			if (LOG.isDebugEnabled())
			{
				final String configurationId = orderEntry.getProductConfigurationDraft().getConfigurationId();
				LOG.debug(String.format("Unlinking draft confingId '%s' from cartEntry '%s'", configurationId, cartEntryKey));
			}
			orderEntry.setProductConfigurationDraft(null);
			getModelService().save(orderEntry);
		}
	}

	public void removeSessionArtifactsForCartEntry(final String cartEntryId)
	{

		final AbstractOrderEntryModel cartEntry = releaseCartEntryProductRelation(cartEntryId);
		cartEntry.setProductConfiguration(null);
		cartEntry.setProductConfigurationDraft(null);
		getModelService().save(cartEntry);
		getSessionAccessService().removeUiStatusForCartEntry(cartEntryId);
	}

	protected AbstractOrderEntryModel releaseCartEntryProductRelation(final String cartEntryId)
	{
		final AbstractOrderEntryModel cartEntry = getPersistenceService().getOrderEntryByPK(cartEntryId);
		releaseCartEntryProductRelation(cartEntry);
		return cartEntry;
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

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	protected ProductConfigurationPersistenceService getPersistenceService()
	{
		return persistenceService;
	}


	@Required
	public void setPersistenceService(final ProductConfigurationPersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}


	protected ProductConfigurationModel getProductConfiguration(final AbstractOrderEntryModel entry)
	{
		final ProductConfigurationModel productConfiguration = entry.getProductConfiguration();
		Preconditions.checkNotNull(productConfiguration, "A configuration must be attached to an order entry");
		return productConfiguration;
	}

	@Override
	public boolean isDocumentRelated(final String configId)
	{
		return !getPersistenceService().getAllOrderEntriesByConfigId(configId).isEmpty();
	}
}
