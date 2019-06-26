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

import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;

import java.util.List;
import java.util.Optional;


/**
 * Facade interface for executing accelerator cart entry actions.
 */
public interface CartEntryActionFacade
{
	/**
	 * This method will trigger {@link CartEntryActionHandler#handleAction(List)} on the {@link CartEntryActionHandler}
	 * configured for the given {@Link CartEntryAction}.
	 *
	 * @param action
	 *           the action you want to execute.
	 * @param entryNumbers
	 *           the cart entry numbers for which the action is executed.
	 * @return An empty optional to signal the controller to apply the default behaviour: redisplay the cart page with a
	 *         success message. Otherwise return a custom redirect URL to navigate elsewhere upon the action completion.
	 *         The expected url format is the format used as SpringMVC controller method return values.
	 * @throws CartEntryActionException
	 *            when an error occurs during the action execution.
	 */
	Optional<String> executeAction(CartEntryAction action, List<Long> entryNumbers) throws CartEntryActionException;

	/**
	 * Provides the key to the message that should be displayed when an action runs with success.
	 *
	 * @param action
	 *           the action you want the message key for
	 * @return the success message key.
	 */
	Optional<String> getSuccessMessageKey(CartEntryAction action);

	/**
	 * Provides the key to the message that should be displayed when a CartEntryActionException is thrown.
	 *
	 * @param action
	 *           the action you want the message key for.
	 * @return the error message key.
	 */
	Optional<String> getErrorMessageKey(CartEntryAction action);
}
