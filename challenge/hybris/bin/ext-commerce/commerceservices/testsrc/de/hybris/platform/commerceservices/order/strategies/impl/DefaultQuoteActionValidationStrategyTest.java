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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for DefaultQuoteActionValidationStrategy
 */
@UnitTest
public class DefaultQuoteActionValidationStrategyTest
{
	private DefaultQuoteActionValidationStrategy defaultQuoteActionValidationStrategy;
	private Set<QuoteState> buyerQuoteStateList;
	private QuoteModel quoteModel;
	private UserModel userModel;
	@Mock
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_DRAFT);
		defaultQuoteActionValidationStrategy = new DefaultQuoteActionValidationStrategy();
		defaultQuoteActionValidationStrategy.setQuoteStateSelectionStrategy(quoteStateSelectionStrategy);
		quoteModel = new QuoteModel();
		quoteModel.setState(QuoteState.BUYER_DRAFT);
		userModel = new UserModel();
	}

	@Test
	public void shouldPassValidation()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotPassValidationForStates()
	{
		// a list of states which doesn't contain the QuoteModel.state.
		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_SUBMITTED);
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.EDIT, userModel)).willReturn(buyerQuoteStateList);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.EDIT, quoteModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotPassValidationForEmptyAllowedStates()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(Collections.EMPTY_SET);
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPassValidationForNullQuote()
	{
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPassValidationForNullQuoteAction()
	{
		defaultQuoteActionValidationStrategy.validate(null, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPassValidationForNullUser()
	{
		defaultQuoteActionValidationStrategy.validate(QuoteAction.VIEW, quoteModel, null);
	}

	@Test
	public void shouldReturnTrueForValidation()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(buyerQuoteStateList);
		Assert.assertTrue("Should return true for the action validation",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, quoteModel, userModel));
	}

	@Test
	public void shouldReturnFalseForValidationForStates()
	{
		// a list of states which doesn't contain the QuoteModel.state.
		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_SUBMITTED);
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.EDIT, userModel)).willReturn(buyerQuoteStateList);
		Assert.assertFalse("Should return false for the action validation for states",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.EDIT, quoteModel, userModel));
	}

	@Test
	public void shouldReturnFalseForValidationForEmptyAllowedStates()
	{
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel)).willReturn(Collections.EMPTY_SET);
		Assert.assertFalse("Should return false for the action validation for empty allowed states",
				defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, quoteModel, userModel));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReturnFalseForValidationForNullQuote()
	{
		defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReturnFalseForValidationForNullQuoteAction()
	{
		defaultQuoteActionValidationStrategy.isValidAction(null, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldReturnFalseValidationForNullUser()
	{
		defaultQuoteActionValidationStrategy.isValidAction(QuoteAction.VIEW, quoteModel, null);
	}
}
