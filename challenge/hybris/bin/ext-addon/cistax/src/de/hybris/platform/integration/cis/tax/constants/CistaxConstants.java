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
package de.hybris.platform.integration.cis.tax.constants;

/**
 * Global class for all Cistax constants. You can add global constants for your extension into this class.
 */
public final class CistaxConstants extends GeneratedCistaxConstants
{
	public static final String EXTENSIONNAME = "cistax";

	/**
	 * Product code that we send to CIS tax service tp for the delivery cost.
	 */
	public static final String EXTERNALTAX_DELIVERY_LINEITEM_ID = "delivery line item";

	/**
	 * Product description that we send to CIS tax service tp for the delivery cost.
	 */
	public static final String EXTERNALTAX_DELIVERY_DESCRIPTION = "Delivery Cost";

	private CistaxConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
