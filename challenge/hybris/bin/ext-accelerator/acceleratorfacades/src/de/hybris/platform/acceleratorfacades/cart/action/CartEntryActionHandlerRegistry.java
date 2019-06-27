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
package de.hybris.platform.acceleratorfacades.cart.action;

/**
 * Registry for cart entry action handlers.
 */
public interface CartEntryActionHandlerRegistry
{
	/**
	 * Returns the configured handler for the given action type.
	 * 
	 * @param action
	 *           the action to get the handler implementation for
	 * @return the matching handler for the goven action
	 */
	CartEntryActionHandler getHandler(CartEntryAction action);
}
