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
package de.hybris.platform.commerceservices.order.strategies.impl;


import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.core.enums.QuoteState;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import de.hybris.platform.core.model.user.UserModel;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Default implementation of {@link QuoteStateSelectionStrategy}
 */
public class DefaultQuoteStateSelectionStrategy implements QuoteStateSelectionStrategy
{
	private Map<QuoteUserType, Map<QuoteAction, Set<QuoteState>>> userTypeActionStateMap;
	private Map<QuoteUserType, Map<QuoteState, Set<QuoteAction>>> userTypeStateActionMap;
	private Map<QuoteUserType, Map<QuoteAction, QuoteState>> userTypeActionStateTransitionMap;
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;

	@Override
	public Set<QuoteState> getAllowedStatesForAction(final QuoteAction action, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("action", action);
		validateParameterNotNullStandardMessage("userModel", userModel);

		final Optional<QuoteUserType> currentQuoteUserType = getQuoteUserTypeIdentificationStrategy()
				.getCurrentQuoteUserType(userModel);
		if (currentQuoteUserType.isPresent())
		{
			final Set<QuoteState> allowedStates = getUserTypeActionStateMap().get(currentQuoteUserType.get()).get(action);
			if (allowedStates != null)
			{
				return allowedStates;
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Set<QuoteAction> getAllowedActionsForState(final QuoteState state, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("state", state);
		validateParameterNotNullStandardMessage("userModel", userModel);

		final Optional<QuoteUserType> currentQuoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel);
		if (currentQuoteUserType.isPresent())
		{
			final Set<QuoteAction> allowedActions = getUserTypeStateActionMap().get(currentQuoteUserType.get()).get(state);
			if (allowedActions != null)
			{
				return allowedActions;
			}
		}
		return Collections.emptySet();
	}

	@Override
	public Optional<QuoteState> getTransitionStateForAction(final QuoteAction action, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("action", action);
		validateParameterNotNullStandardMessage("userModel", userModel);

		final Optional<QuoteUserType> currentQuoteUserType = getQuoteUserTypeIdentificationStrategy()
				.getCurrentQuoteUserType(userModel);
		if (currentQuoteUserType.isPresent())
		{
			final QuoteState transitionState = getUserTypeActionStateTransitionMap().get(currentQuoteUserType.get()).get(action);
			if (transitionState != null)
			{
				return Optional.of(transitionState);
			}
		}
		return Optional.empty();
	}

	protected Map<QuoteUserType, Map<QuoteState, Set<QuoteAction>>> getInvertedNestedMap(
			final Map<QuoteUserType, Map<QuoteAction, Set<QuoteState>>> userTypeActionStateMap)
	{
		final Map<QuoteUserType, Map<QuoteState, Set<QuoteAction>>> userTypeStateActionMap = new HashMap();

		for (final Entry<QuoteUserType, Map<QuoteAction, Set<QuoteState>>> entry : userTypeActionStateMap.entrySet())
		{
			userTypeStateActionMap.put(entry.getKey(), getInvertedMap(entry.getValue()));
		}
		return userTypeStateActionMap;
	}

	protected Map<QuoteState, Set<QuoteAction>> getInvertedMap(final Map<QuoteAction, Set<QuoteState>> actionStateMap)
	{
		final Map<QuoteState, Set<QuoteAction>> stateActionMap = new HashMap();
		for (final QuoteAction action : actionStateMap.keySet())
		{
			final Set<QuoteState> states = actionStateMap.get(action);
			for (final QuoteState state : states)
			{
				if (!stateActionMap.containsKey(state))
				{
					stateActionMap.put(state, new HashSet());
				}
				final Set<QuoteAction> allowedActions = stateActionMap.get(state);
				allowedActions.add(action);
			}
		}
		return stateActionMap;
	}

	protected Map<QuoteUserType, Map<QuoteAction, Set<QuoteState>>> getUserTypeActionStateMap()
	{
		return userTypeActionStateMap;
	}

	@Required
	public void setUserTypeActionStateMap(final Map<QuoteUserType, Map<QuoteAction, Set<QuoteState>>> quoteUserTypeActionStateMap)
	{
		this.userTypeActionStateMap = quoteUserTypeActionStateMap;

		// populates the inverted map (state,action) too
		userTypeStateActionMap = getInvertedNestedMap(quoteUserTypeActionStateMap);
	}

	protected Map<QuoteUserType, Map<QuoteState, Set<QuoteAction>>> getUserTypeStateActionMap()
	{
		return userTypeStateActionMap;
	}

	protected Map<QuoteUserType, Map<QuoteAction, QuoteState>> getUserTypeActionStateTransitionMap()
	{
		return userTypeActionStateTransitionMap;
	}

	@Required
	public void setUserTypeActionStateTransitionMap(
			final Map<QuoteUserType, Map<QuoteAction, QuoteState>> userTypeActionStateTransitionMap)
	{
		this.userTypeActionStateTransitionMap = userTypeActionStateTransitionMap;
	}

	protected QuoteUserTypeIdentificationStrategy getQuoteUserTypeIdentificationStrategy()
	{
		return quoteUserTypeIdentificationStrategy;
	}

	@Required
	public void setQuoteUserTypeIdentificationStrategy(
			final QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy)
	{
		this.quoteUserTypeIdentificationStrategy = quoteUserTypeIdentificationStrategy;
	}
}
