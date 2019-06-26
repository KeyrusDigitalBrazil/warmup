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
package de.hybris.platform.integration.cis.avs.strategies.impl;

import de.hybris.platform.integration.cis.avs.constants.CisavsConstants;
import de.hybris.platform.integration.cis.avs.strategies.ShowSuggestedAddressesStrategy;
import de.hybris.platform.util.Config;


/**
 * Implementation of ShowSuggestedAddressesStrategy using a property.
 */
public class DefaultShowSuggestedAddressesStrategy implements ShowSuggestedAddressesStrategy
{
	@Override
	public boolean shouldAddressSuggestionsBeShown()
	{
		return "true".equals(Config.getParameter(CisavsConstants.AVS_SHOW_SUGGESTED_ADDRESSES_PROP));
	}
}
