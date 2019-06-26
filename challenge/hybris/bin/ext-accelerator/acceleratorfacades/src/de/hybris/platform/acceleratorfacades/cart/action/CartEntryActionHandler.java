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
import de.hybris.platform.core.model.order.CartEntryModel;

import java.util.List;
import java.util.Optional;


/**
 * Handler interface for the execution of {@link CartEntryAction}.
 */
public interface CartEntryActionHandler
{

	/**
	 * This method contains the logic of the action performed by the CartEntryActionHandler implementation.
	 *
	 * Even though the method {@link #supports(CartEntryModel)} prevents unsupported actions to be visible in the
	 * contextual menu, it is the responsibility of the handleAction method and the processes it calls to perform
	 * appropriate validations in case the action is called on a cart entry for which it should not.
	 *
	 * @param entryNumbers
	 *           the cart entry number for which the action is executed.
	 * @return An empty optional to signal the controller to apply the default behaviour: redisplay the cart page with a
	 *         success message. Otherwise return a custom redirect URL to navigate elsewhere upon the action completion.
	 *         The expected url format is the format used as SpringMVC controller method return values.
	 * @throws CartEntryActionException
	 *            when an error occurs.
	 */
	Optional<String> handleAction(List<Long> entryNumbers) throws CartEntryActionException;

	/**
	 * Provides the key to the message that should be displayed when an action runs with success.
	 *
	 * @return the success message key.
	 */
	String getSuccessMessageKey();

	/**
	 * Provides the key to the message that should be displayed when a CartEntryActionException is thrown.
	 *
	 * @return the error message key.
	 */
	String getErrorMessageKey();

	/**
	 * This method determines if the action should show or not in the cart entry tools menu. An action may not be
	 * supported by every cart entry and this is the method to override to make this decision.
	 *
	 * This method is intended to refine the contents of the contextual menu of a cart item. It is the responsibility of
	 * the {@Link #handleAction} method and the processes it calls to perform appropriate validations in case the action
	 * is called on a cart entry for which it should not.
	 *
	 * @param cartEntry
	 *           the cart entry model.
	 * @return true if the cart entry supports the action, false otherwise.
	 */
	boolean supports(CartEntryModel cartEntry);
}
