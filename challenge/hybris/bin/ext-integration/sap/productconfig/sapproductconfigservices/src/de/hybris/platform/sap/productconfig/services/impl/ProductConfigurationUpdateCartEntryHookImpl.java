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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Deletes configuration session after removing cart entry.
 */
public class ProductConfigurationUpdateCartEntryHookImpl implements CommerceUpdateCartEntryHook
{

	private ProductConfigurationService productConfigurationService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private TrackingRecorder recorder;

	@Override
	public void afterUpdateCartEntry(final CommerceCartParameter parameter, final CommerceCartModification result)
	{
		// Check if update was a deletion (qty = 0).
		if (parameter.getQuantity() == 0)
		{
			// Check if a configuration has to be deleted
			releaseIfNotEmpty(parameter.getConfigToBeDeleted());
			releaseIfNotEmpty(parameter.getDraftConfigToBeDeleted());
		}
	}

	protected void releaseIfNotEmpty(final String configDraftToBeDeleted)
	{
		if (StringUtils.isNotEmpty(configDraftToBeDeleted))
		{
			getProductConfigurationService().releaseSession(configDraftToBeDeleted);
		}
	}

	@Override
	public void beforeUpdateCartEntry(final CommerceCartParameter parameter)
	{
		// Check if entry should be deleted (qty = 0)
		final long qty = parameter.getQuantity();
		if (qty > 0)
		{
			return;
		}
		// Get the entry-object for the entry-number
		final List<AbstractOrderEntryModel> entries = parameter.getCart().getEntries();
		if (CollectionUtils.isNotEmpty(entries))
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				if (null != entry && entry.getEntryNumber().intValue() == parameter.getEntryNumber())
				{
					handleCartEntry(parameter, entry);
				}
			}
		}
	}

	protected void handleCartEntry(final CommerceCartParameter parameter, final AbstractOrderEntryModel entry)
	{
		// Entry found: Check if it is configurable
		final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(entry.getPk().toString());
		if (StringUtils.isNotEmpty(configId))
		{
			// Store configId in parameter object to be used in afterUpdateCartEntry method
			parameter.setConfigToBeDeleted(configId);
			final String configDraftId = getAbstractOrderEntryLinkStrategy().getDraftConfigIdForCartEntry(entry.getPk().toString());
			parameter.setDraftConfigToBeDeleted(configDraftId);

			getAbstractOrderEntryLinkStrategy().removeSessionArtifactsForCartEntry(entry.getPk().toString());
			getRecorder().recordDeleteCartEntry(entry, parameter);
		}

	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}
}
