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

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for DefaultQuoteUpdateStateStrategy
 */
@UnitTest
public class DefaultQuoteUpdateStateStrategyTest
{
	private DefaultQuoteUpdateStateStrategy defaultQuoteUpdateStateStrategy;
	private QuoteModel quoteModel;
	@Mock
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;

	private UserModel userModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultQuoteUpdateStateStrategy = new DefaultQuoteUpdateStateStrategy();
		defaultQuoteUpdateStateStrategy.setQuoteStateSelectionStrategy(quoteStateSelectionStrategy);
		quoteModel = new QuoteModel();
		quoteModel.setState(QuoteState.BUYER_OFFER);
		userModel = mock(UserModel.class);
	}

	@Test
	public void shouldUpdateQuoteState()
	{
		given(quoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.EDIT, userModel))
				.willReturn(Optional.of(QuoteState.BUYER_DRAFT));
		defaultQuoteUpdateStateStrategy.updateQuoteState(QuoteAction.EDIT, quoteModel, userModel);
		assertEquals("Quote state should be buyer draft", QuoteState.BUYER_DRAFT, quoteModel.getState());
	}

	@Test
	public void shouldNotUpdateQuoteState()
	{
		given(quoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.EDIT, userModel)).willReturn(Optional.empty());
		defaultQuoteUpdateStateStrategy.updateQuoteState(QuoteAction.EDIT, quoteModel, userModel);
		assertEquals("Quote state should be buyer offer", QuoteState.BUYER_OFFER, quoteModel.getState());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateQuoteStateIfQuoteActionIsNull()
	{
		defaultQuoteUpdateStateStrategy.updateQuoteState(null, quoteModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateQuoteStateIfQuoteIsNull()
	{
		defaultQuoteUpdateStateStrategy.updateQuoteState(QuoteAction.EDIT, null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateQuoteStateIfUserIsNull()
	{
		defaultQuoteUpdateStateStrategy.updateQuoteState(QuoteAction.EDIT, quoteModel, null);
	}
}
