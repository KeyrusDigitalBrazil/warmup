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
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteExpirationTimeValidationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteExpirationTimeValidationStrategyTest
{
	@InjectMocks
	private final DefaultQuoteExpirationTimeValidationStrategy defaultQuoteExpiryDateValidationStrategy = new DefaultQuoteExpirationTimeValidationStrategy();

	@Mock
	private TimeService timeService;
	@Mock
	private QuoteModel quoteModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForNullQuote()
	{
		defaultQuoteExpiryDateValidationStrategy.hasQuoteExpired(null);
	}

	@Test
	public void shouldReturnFalseIfExpirationTimeAfterCurrentTime()
	{
		final Date currentTime = Calendar.getInstance().getTime();
		given(quoteModel.getExpirationTime()).willReturn(DateUtils.addDays(currentTime, 5));
		given(timeService.getCurrentTime()).willReturn(currentTime);

		Assert.assertFalse(defaultQuoteExpiryDateValidationStrategy.hasQuoteExpired(quoteModel));
	}

	@Test
	public void shouldReturnFalseIfExpirationTimeEqualToCurrentTime()
	{
		final Date currentTime = Calendar.getInstance().getTime();
		given(quoteModel.getExpirationTime()).willReturn(currentTime);
		given(timeService.getCurrentTime()).willReturn(currentTime);

		Assert.assertFalse(defaultQuoteExpiryDateValidationStrategy.hasQuoteExpired(quoteModel));
	}

	@Test
	public void shouldReturnTrueForNullExpirationTime()
	{
		given(quoteModel.getExpirationTime()).willReturn(null);
		Assert.assertTrue(defaultQuoteExpiryDateValidationStrategy.hasQuoteExpired(quoteModel));
	}

	@Test
	public void shouldReturnTrueIfExpirationTimeBeforeCurrentTime()
	{
		final Date currentTime = Calendar.getInstance().getTime();
		given(quoteModel.getExpirationTime()).willReturn(DateUtils.addDays(currentTime, -5));
		given(timeService.getCurrentTime()).willReturn(currentTime);

		Assert.assertTrue(defaultQuoteExpiryDateValidationStrategy.hasQuoteExpired(quoteModel));
	}

}
