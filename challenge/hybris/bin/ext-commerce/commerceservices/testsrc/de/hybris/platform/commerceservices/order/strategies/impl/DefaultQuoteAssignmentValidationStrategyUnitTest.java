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

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceQuoteAssignmentException;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteAssignmentValidationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteAssignmentValidationStrategyUnitTest
{
	private DefaultQuoteAssignmentValidationStrategy defaultQuoteAssignmentValidationStrategy;
	@Mock
	private QuoteModel quote;
	@Mock
	private UserModel user1;
	@Mock
	private UserModel user2;

	@Before
	public void setUp()
	{
		defaultQuoteAssignmentValidationStrategy = new DefaultQuoteAssignmentValidationStrategy();

		given(user1.getUid()).willReturn("user1");
		given(user2.getUid()).willReturn("user2");
	}

	@Test
	public void shouldNotThrowExceptionForQuoteAssignmentValidation()
	{
		given(quote.getAssignee()).willReturn(null);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteAssignment(quote, user1, user1);
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			fail("Should not throw CommerceQuoteAssignmentException");
		}

		// then ok
	}

	@Test
	public void shouldThrowExceptionWhenAssigneeIsDifferentFromQuoteAssignee()
	{
		given(quote.getAssignee()).willReturn(user2);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteAssignment(quote, user1, user2);
			fail("Should throw CommerceQuoteAssignmentException");
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			// then ok
		}
	}

	@Test
	public void shouldNotThrowExceptionWhenAssigneeIsAlreadySetForQuoteAssignment()
	{
		given(quote.getAssignee()).willReturn(user1);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteAssignment(quote, user1, user1);
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			fail("Should not throw CommerceQuoteAssignmentException");
		}

		// then ok
	}

	@Test
	public void shouldThrowExceptionWhenAssignerIsNotAssigneeForQuoteAssignment()
	{
		given(quote.getAssignee()).willReturn(null);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteAssignment(quote, user1, user2);
			fail("Should throw CommerceQuoteAssignmentException");
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			// then ok
		}
	}

	@Test
	public void shouldNotThrowExceptionForQuoteUnassignmentValidation()
	{
		given(quote.getAssignee()).willReturn(user1);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteUnassignment(quote, user1);
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			fail("Should not throw CommerceQuoteAssignmentException");
		}

		// then ok
	}

	@Test
	public void shouldNotThrowExceptionForQuoteUnassignmentValidationWhenThereIsNoAssignee()
	{
		given(quote.getAssignee()).willReturn(null);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteUnassignment(quote, user1);
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			fail("Should not throw CommerceQuoteAssignmentException");
		}

		// then ok
	}

	@Test
	public void shouldThrowExceptionWhenAssignerIsNotAssigneeForQuoteUnassign()
	{
		given(quote.getAssignee()).willReturn(user2);

		try
		{
			// when
			defaultQuoteAssignmentValidationStrategy.validateQuoteUnassignment(quote, user1);
			fail("Should throw CommerceQuoteAssignmentException");
		}
		catch (final CommerceQuoteAssignmentException cqae)
		{
			// then ok
		}
	}
}
