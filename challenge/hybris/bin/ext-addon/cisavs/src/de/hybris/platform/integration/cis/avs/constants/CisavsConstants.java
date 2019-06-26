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
package de.hybris.platform.integration.cis.avs.constants;

/**
 * Global class for all Cisavs constants. You can add global constants for your extension into this class.
 */
public final class CisavsConstants extends GeneratedCisavsConstants
{
	public static final String EXTENSIONNAME = "cisavs";
	/**
	 * property that contains the countries which addresses need to go through avs check
	 */
	public static final String AVS_COUNTRIES_PROP = "cisavs.check.countries";
	/**
	 * property that defines if suggested addresses should be shown to the end user or not
	 */
	public static final String AVS_SHOW_SUGGESTED_ADDRESSES_PROP = "cisavs.showSuggestedAddress";

	/**
	 * Property that defines the number of suggested addresses to display.
	 */
	public static final String AVS_SUGGESTED_ADDRESS_AMOUNT = "cisavs.suggestedAddressAmount";

	private CisavsConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
