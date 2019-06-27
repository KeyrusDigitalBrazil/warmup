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
package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.platform.marketplaceservices.strategies.VendorOriginalEntryGroupDisplayStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;


/**
 * A default implementation of {@link VendorOriginalEntryGroupDisplayStrategy}
 */
public class DefaultVendorOriginalEntryGroupDisplayStrategy implements VendorOriginalEntryGroupDisplayStrategy
{

	private static final String DISPLAY_ORIGINAL_ENTRYGROUP = "should.display.original.entrygroup";
	private ConfigurationService configurationService;

	@Override
	public boolean shouldDisplayOriginalEntryGroup()
	{
		return getConfigurationService().getConfiguration().getBoolean(DISPLAY_ORIGINAL_ENTRYGROUP, false);
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
