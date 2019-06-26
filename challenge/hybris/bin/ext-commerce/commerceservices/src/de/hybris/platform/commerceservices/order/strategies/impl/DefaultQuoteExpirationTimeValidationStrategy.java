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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.strategies.QuoteExpirationTimeValidationStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default Strategy Implementation of {@link QuoteExpirationTimeValidationStrategy}
 */
public class DefaultQuoteExpirationTimeValidationStrategy implements QuoteExpirationTimeValidationStrategy
{
	private TimeService timeService;

	@Override
	public boolean hasQuoteExpired(final QuoteModel quoteModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);

		final Date expirationTime = quoteModel.getExpirationTime();

		if (expirationTime == null || timeService.getCurrentTime().compareTo(expirationTime) > 0)
		{
			return true;
		}

		return false;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}
