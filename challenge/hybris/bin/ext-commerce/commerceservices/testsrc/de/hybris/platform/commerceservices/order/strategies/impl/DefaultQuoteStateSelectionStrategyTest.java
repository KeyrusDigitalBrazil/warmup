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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for DefaultQuoteStateSelectionStrategy
 */
@UnitTest
public class DefaultQuoteStateSelectionStrategyTest
{
	@Mock
	private DefaultQuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	private DefaultQuoteStateSelectionStrategy defaultQuoteStateSelectionStrategy;
	private Set<QuoteState> buyerQuoteStateList;
	private Set<QuoteState> sellerQuoteStateList;
	private Set<QuoteState> sellerApproverQuoteStateList;
	private UserModel userModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		buyerQuoteStateList = Collections.singleton(QuoteState.BUYER_DRAFT);
		sellerQuoteStateList = Collections.singleton(QuoteState.SELLER_DRAFT);
		sellerApproverQuoteStateList = Collections.singleton(QuoteState.SELLERAPPROVER_PENDING);

		defaultQuoteStateSelectionStrategy = new DefaultQuoteStateSelectionStrategy();
		defaultQuoteStateSelectionStrategy.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);

		// user type, action and state map
		final Map<QuoteUserType, Map<QuoteAction, Set<QuoteState>>> userTypeActionStateMap = new HashMap<>();
		userTypeActionStateMap.put(QuoteUserType.BUYER, Collections.singletonMap(QuoteAction.VIEW, buyerQuoteStateList));
		userTypeActionStateMap.put(QuoteUserType.SELLER, Collections.singletonMap(QuoteAction.VIEW, sellerQuoteStateList));
		userTypeActionStateMap.put(QuoteUserType.SELLERAPPROVER,
				Collections.singletonMap(QuoteAction.VIEW, sellerApproverQuoteStateList));
		defaultQuoteStateSelectionStrategy.setUserTypeActionStateMap(userTypeActionStateMap);

		// user type, action and state transition map
		final Map<QuoteUserType, Map<QuoteAction, QuoteState>> userTypeActionStateTransitionMap = new HashMap<>();
		userTypeActionStateTransitionMap.put(QuoteUserType.BUYER,
				Collections.singletonMap(QuoteAction.EDIT, QuoteState.BUYER_DRAFT));
		userTypeActionStateTransitionMap.put(QuoteUserType.SELLER,
				Collections.singletonMap(QuoteAction.EDIT, QuoteState.SELLER_DRAFT));
		userTypeActionStateTransitionMap.put(QuoteUserType.SELLERAPPROVER,
				Collections.singletonMap(QuoteAction.SUBMIT, QuoteState.SELLERAPPROVER_APPROVED));
		defaultQuoteStateSelectionStrategy.setUserTypeActionStateTransitionMap(userTypeActionStateTransitionMap);

		userModel = mock(UserModel.class);
	}

	// Buyer
	@Test
	public void shouldGetBuyerAllowedStatesForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));
		assertEquals("Quote states are wrong", buyerQuoteStateList,
				defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel));
	}

	@Test
	public void shouldNotGetBuyerAllowedStatesForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));
		assertTrue("Quote states set should be empty",
				CollectionUtils.isEmpty(defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.SUBMIT, userModel)));
	}

	@Test
	public void shouldGetBuyerAllowedActionsForState()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));
		assertEquals("Allowed actions are wrong", Collections.singleton(QuoteAction.VIEW),
				defaultQuoteStateSelectionStrategy.getAllowedActionsForState(QuoteState.BUYER_DRAFT, userModel));
	}

	@Test
	public void shouldNotGetBuyerAllowedActionsForState()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));
		assertTrue("Allowed actions should be empty", CollectionUtils
				.isEmpty(defaultQuoteStateSelectionStrategy.getAllowedActionsForState(QuoteState.BUYER_ACCEPTED, userModel)));
	}

	@Test
	public void shouldGetBuyerTransitionStateForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));
		assertEquals("State to be updated to should be BUYER_DRAFT", QuoteState.BUYER_DRAFT,
				defaultQuoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.EDIT, userModel).get());
	}

	@Test
	public void shouldNotGetBuyerTransitionStateForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));
		assertFalse("Should not get state to be updated to",
				defaultQuoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.SUBMIT, userModel).isPresent());
	}

	// Seller
	@Test
	public void shouldGetSellerAllowedStatesForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		assertEquals("Quote states are wrong", sellerQuoteStateList,
				defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel));
	}

	@Test
	public void shouldNotGetSellerAllowedStatesForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		assertTrue("Quote states set should be empty",
				CollectionUtils.isEmpty(defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.SUBMIT, userModel)));
	}

	@Test
	public void shouldGetSellerAllowedActionsForState()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		assertEquals("Allowed actions are wrong", Collections.singleton(QuoteAction.VIEW),
				defaultQuoteStateSelectionStrategy.getAllowedActionsForState(QuoteState.SELLER_DRAFT, userModel));
	}

	@Test
	public void shouldNotGetSellerAllowedActionsForState()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		assertTrue("Allowed actions should be empty", CollectionUtils
				.isEmpty(defaultQuoteStateSelectionStrategy.getAllowedActionsForState(QuoteState.SELLER_SUBMITTED, userModel)));
	}

	@Test
	public void shouldGetSellerTransitionStateForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		assertEquals("State to be updated to should be SELLER_DRAFT", QuoteState.SELLER_DRAFT,
				defaultQuoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.EDIT, userModel).get());
	}

	@Test
	public void shouldNotGetSellerTransitionStateForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		assertFalse("Should not get state to be updated to",
				defaultQuoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.CANCEL, userModel).isPresent());
	}

	// Seller approver
	@Test
	public void shouldGetSellerApproverAllowedStatesForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		assertEquals("Quote states are wrong", sellerApproverQuoteStateList,
				defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, userModel));
	}

	@Test
	public void shouldNotGetSellerApproverAllowedStatesForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		assertTrue("Quote states set should be empty",
				CollectionUtils.isEmpty(defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.SUBMIT, userModel)));
	}

	@Test
	public void shouldGetSellerApproverAllowedActionsForState()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		assertEquals("Allowed actions are wrong", Collections.singleton(QuoteAction.VIEW),
				defaultQuoteStateSelectionStrategy.getAllowedActionsForState(QuoteState.SELLERAPPROVER_PENDING, userModel));
	}

	@Test
	public void shouldNotGetSellerApproverAllowedActionsForState()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		assertTrue("Allowed actions should be empty", CollectionUtils
				.isEmpty(defaultQuoteStateSelectionStrategy.getAllowedActionsForState(QuoteState.BUYER_DRAFT, userModel)));
	}

	@Test
	public void shouldGetSellerApproverTransitionStateForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		assertEquals("State to be updated to should be SELLER_DRAFT", QuoteState.SELLERAPPROVER_APPROVED,
				defaultQuoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.SUBMIT, userModel).get());
	}

	@Test
	public void shouldNotGetSellerApproverTransitionStateForAction()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		assertFalse("Should not get state to be updated to",
				defaultQuoteStateSelectionStrategy.getTransitionStateForAction(QuoteAction.EDIT, userModel).isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotgetAllowedStatesIfActionIsNull()
	{
		defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotgetAllowedStatesIfUserIsNull()
	{
		defaultQuoteStateSelectionStrategy.getAllowedStatesForAction(mock(QuoteAction.class), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotgetAllowedActionsIfStateIsNull()
	{
		defaultQuoteStateSelectionStrategy.getAllowedActionsForState(null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotgetAllowedActionsIfUserIsNull()
	{
		defaultQuoteStateSelectionStrategy.getAllowedActionsForState(mock(QuoteState.class), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotgetTransitionStateIfActionIsNull()
	{
		defaultQuoteStateSelectionStrategy.getTransitionStateForAction(null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotgetTransitionStateIfUserIsNull()
	{
		defaultQuoteStateSelectionStrategy.getTransitionStateForAction(mock(QuoteAction.class), null);
	}
}
