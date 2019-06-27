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
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.util.QuoteExpirationTimeUtils;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteUpdateExpirationTimeStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteUpdateExpirationTimeStrategyTest
{
	@InjectMocks
	private final DefaultQuoteUpdateExpirationTimeStrategy defaultQuoteUpdateExpirationTimeStrategy = new DefaultQuoteUpdateExpirationTimeStrategy();

	@Mock
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;

	@Mock
	private TimeService timeService;

	@Mock
	private QuoteModel quoteModel;

	@Mock
	private UserModel userModel;

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateQuoteUserType()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.empty());

		defaultQuoteUpdateExpirationTimeStrategy.updateExpirationTime(QuoteAction.SUBMIT, quoteModel, userModel);
	}

	@Test
	public void shouldSetNullExpirationTimeForBuyer()
	{
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));

		Assert.assertEquals("Quote model should be the same", quoteModel,
				defaultQuoteUpdateExpirationTimeStrategy.updateExpirationTime(QuoteAction.SUBMIT, quoteModel, userModel));

		verify(quoteModel).setExpirationTime(null);
	}

	@Test
	public void shouldSetExpirationTimeForSellerWithNullQuoteExpirationTime()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(quoteModel.getExpirationTime()).willReturn(null);
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(currentDate);

		Assert.assertEquals("Quote model should be the same", quoteModel,
				defaultQuoteUpdateExpirationTimeStrategy.updateExpirationTime(QuoteAction.SUBMIT, quoteModel, userModel));

		verify(quoteModel).setExpirationTime(calculateExpiryTimeUsingOfferValidityPeriod(currentDate,
				QuoteExpirationTimeUtils.getDefaultOfferValidityPeriodDays()));
	}

	@Test
	public void shouldSetExpirationTimeForSellerWithPassedQuoteExpirationTime()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(quoteModel.getExpirationTime()).willReturn(calculateExpiryTimeUsingOfferValidityPeriod(currentDate, 0));
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(currentDate);

		Assert.assertEquals("Quote model should be the same", quoteModel,
				defaultQuoteUpdateExpirationTimeStrategy.updateExpirationTime(QuoteAction.SUBMIT, quoteModel, userModel));

		verify(quoteModel).setExpirationTime(calculateExpiryTimeUsingOfferValidityPeriod(currentDate,
				QuoteExpirationTimeUtils.getDefaultOfferValidityPeriodDays()));
	}

	protected Date calculateExpiryTimeUsingOfferValidityPeriod(final Date currentDate, final int offerValidityPeriod)
	{
		Date result = DateUtils.addDays(currentDate, offerValidityPeriod);
		result = DateUtils.addMilliseconds(DateUtils.ceiling(result, Calendar.DATE), -1);
		return result;
	}

	@Test
	public void shouldSetExpirationTimeForSellerWithValidQuoteExpirationTime()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date expirationTime = calculateExpiryTimeUsingOfferValidityPeriod(currentDate,
				QuoteExpirationTimeUtils.getMinOfferValidityPeriodInDays());
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));
		given(quoteModel.getExpirationTime()).willReturn(expirationTime);
		given(timeService.getCurrentDateWithTimeNormalized()).willReturn(currentDate);

		Assert.assertEquals("Quote model should be the same", quoteModel,
				defaultQuoteUpdateExpirationTimeStrategy.updateExpirationTime(QuoteAction.SUBMIT, quoteModel, userModel));

		verify(quoteModel).setExpirationTime(expirationTime);
	}
}
