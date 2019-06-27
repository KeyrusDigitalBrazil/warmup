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
package de.hybris.platform.sap.productconfig.b2bfrontend.constants;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;


/**
 * Global class for all Sapproductconfigb2baddon web constants.
 */
public final class Sapproductconfigb2baddonWebConstants
{
	/**
	 * view name to trigger a re-direct to the chekcout page
	 */
	public static final String REDIRECT_TO_CHECKOUT = AbstractController.REDIRECT_PREFIX + "/checkout/multi/summary/view";
	/**
	 * view name to trigger a re-direct to the cart page
	 */
	public static final String REDIRECT_TO_CART = AbstractController.REDIRECT_PREFIX + "/cart";

	private Sapproductconfigb2baddonWebConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
