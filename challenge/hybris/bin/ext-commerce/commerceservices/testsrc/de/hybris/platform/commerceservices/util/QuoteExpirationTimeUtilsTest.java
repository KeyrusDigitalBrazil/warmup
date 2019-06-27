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
package de.hybris.platform.commerceservices.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuoteExpirationTimeUtilsTest
{
	@Test
	public void shouldUpdateExpirationTimeForNullExpirationDate()
	{
		final Date oldExpiryDate = null;
		final Date today = DateUtils.truncate(new Date(), Calendar.DATE);

		final Date updatedDate = QuoteExpirationTimeUtils.determineExpirationTime(oldExpiryDate, today);

		Assert.assertNotNull("Updated date should not be null", updatedDate);
		Assert.assertEquals("Updated date should be the same",
				calculateExpiryTimeUsingOfferValidityPeriod(today, QuoteExpirationTimeUtils.getDefaultOfferValidityPeriodDays()),
				updatedDate);
	}

	protected Date calculateExpiryTimeUsingOfferValidityPeriod(final Date currentDate, final int offerValidityPeriod)
	{
		Date result = DateUtils.addDays(currentDate, offerValidityPeriod);
		result = DateUtils.addMilliseconds(DateUtils.ceiling(result, Calendar.DATE), -1);
		return result;
	}

	@Test
	public void shouldUpdateExpirationTimeForPassedExpirationDate()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date oldExpiryDate = calculateExpiryTimeUsingOfferValidityPeriod(currentDate, 0);
		final Date today = currentDate;

		final Date updatedDate = QuoteExpirationTimeUtils.determineExpirationTime(oldExpiryDate, today);

		Assert.assertNotNull("Updated date should not be null", updatedDate);
		Assert.assertEquals("Updated date should be the same",
				calculateExpiryTimeUsingOfferValidityPeriod(today, QuoteExpirationTimeUtils.getDefaultOfferValidityPeriodDays()),
				updatedDate);
	}

	@Test
	public void shouldNotUpdateExpirationTimeForValidExpirationDate()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date oldExpiryDate = calculateExpiryTimeUsingOfferValidityPeriod(currentDate,
				QuoteExpirationTimeUtils.getDefaultOfferValidityPeriodDays() + 1);
		final Date today = currentDate;

		final Date updatedDate = QuoteExpirationTimeUtils.determineExpirationTime(oldExpiryDate, today);

		Assert.assertNotNull("Updated date should not be null", updatedDate);
		Assert.assertEquals("Updated date should be the same", oldExpiryDate, updatedDate);
	}

	@Test
	public void shouldValidateExpirationTimeForNullExpirationTime()
	{
		final Date expiryDate = null;
		final Date today = DateUtils.truncate(new Date(), Calendar.DATE);

		boolean isValid = QuoteExpirationTimeUtils.isExpirationTimeValid(expiryDate, today);

		Assert.assertFalse("Expiry date should not be valid", isValid);
	}

	@Test
	public void shouldValidateExpirationTimeForPassedExpirationTime()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date expiryDate = calculateExpiryTimeUsingOfferValidityPeriod(currentDate, 0);
		final Date today = currentDate;

		boolean isValid = QuoteExpirationTimeUtils.isExpirationTimeValid(expiryDate, today);

		Assert.assertFalse("Expiry date should not be valid", isValid);
	}

	@Test
	public void shouldValidateExpirationTimeForValidExpirationTime()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date expiryDate = calculateExpiryTimeUsingOfferValidityPeriod(currentDate, QuoteExpirationTimeUtils.getDefaultOfferValidityPeriodDays() + 1);
		final Date today = currentDate;

		boolean isValid = QuoteExpirationTimeUtils.isExpirationTimeValid(expiryDate, today);

		Assert.assertTrue("Expiry date should be valid", isValid);
	}

	@Test
	public void shouldValidateEndOfDay()
	{
		final Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date endOfDayDate = calculateExpiryTimeUsingOfferValidityPeriod(currentDate, 0);

		final Date updatedDate = QuoteExpirationTimeUtils.getEndOfDay(currentDate);

		Assert.assertEquals("Expiry date should be set to end of day", endOfDayDate, updatedDate);
	}
}
