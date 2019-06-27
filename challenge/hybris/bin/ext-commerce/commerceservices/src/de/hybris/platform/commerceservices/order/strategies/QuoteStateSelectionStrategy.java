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
package de.hybris.platform.commerceservices.order.strategies;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;
import java.util.Set;


/**
 * Strategy to help select quote states
 */
public interface QuoteStateSelectionStrategy
{
	/**
	 * Provides the list of quote states based on provided action.
	 *
	 * @param action
	 *           quote action that is being performed
	 * @param userModel
	 *           user used to determine the allowed states
	 * @return Set of quote states associated with the action. Empty set will be returned if none of the states is
	 *         allowed.
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	Set<QuoteState> getAllowedStatesForAction(QuoteAction action, UserModel userModel);

	/**
	 * Provides the list of actions based on the given state.
	 *
	 * @param state
	 *           quote state that can allow one action
	 * @param userModel
	 *           user used to determine the allowed actions
	 * @return Set of actions allowed by this state. Empty set will be returned if none of the actions is allowed.
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	Set<QuoteAction> getAllowedActionsForState(QuoteState state, UserModel userModel);

	/**
	 * Provides a quote state that the quote should be in for the corresponding action.
	 *
	 * @param action
	 *           quote action that is being performed
	 * @param userModel
	 *           user used to determine the transition state
	 * @return quote state
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	Optional<QuoteState> getTransitionStateForAction(QuoteAction action, UserModel userModel);

}
