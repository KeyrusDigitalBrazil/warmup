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
package de.hybris.platform.assistedservicestorefront.redirect;

/**
 * Redirect strategy that used in ASM when user starts to be emulated.
 */
public interface AssistedServiceRedirectStrategy
{
	/**
	 * Returns redirect path after emulation is called.
	 */
	String getRedirectPath();

	/**
	 * Returns redirect path after emulation is called with exceptions.
	 */
	String getErrorRedirectPath();
}