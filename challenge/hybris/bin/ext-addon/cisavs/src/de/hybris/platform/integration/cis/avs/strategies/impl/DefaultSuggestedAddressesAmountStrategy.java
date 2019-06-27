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
import de.hybris.platform.integration.cis.avs.strategies.SuggestedAddressesAmountStrategy;
import de.hybris.platform.util.Config;


/**
 * Implementation of SuggestedAddressesAmountStrategy using a property.
 */
public class DefaultSuggestedAddressesAmountStrategy implements SuggestedAddressesAmountStrategy
{

	@Override
	public int getSuggestedAddressesAmountToDisplay()
	{
		return Config.getInt(CisavsConstants.AVS_SUGGESTED_ADDRESS_AMOUNT, 10);
	}
}
