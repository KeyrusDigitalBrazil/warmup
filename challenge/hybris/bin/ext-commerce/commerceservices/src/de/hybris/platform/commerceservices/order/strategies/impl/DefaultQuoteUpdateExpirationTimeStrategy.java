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
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateExpirationTimeStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.util.QuoteExpirationTimeUtils;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link QuoteUpdateExpirationTimeStrategy}
 */
public class DefaultQuoteUpdateExpirationTimeStrategy implements QuoteUpdateExpirationTimeStrategy
{
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	private TimeService timeService;

	@Override
	public QuoteModel updateExpirationTime(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel)
				.orElseThrow(
						() -> new IllegalArgumentException(String.format("Failed to determine quote user's [%s] type.",
								userModel.getPk())));

		if (QuoteAction.EDIT.equals(quoteAction))
		{
			updateExpirationTimeForEditAction(quoteModel, quoteUserType);
		}
		else if (QuoteAction.SUBMIT.equals(quoteAction))
		{
			updateExpirationTimeForSubmitAction(quoteModel, quoteUserType);
		}

		return quoteModel;
	}

	protected void updateExpirationTimeForEditAction(final QuoteModel quoteModel, final QuoteUserType quoteUserType)
	{
		if (QuoteUserType.BUYER.equals(quoteUserType))
		{
			quoteModel.setExpirationTime(null);
		}
	}

	protected void updateExpirationTimeForSubmitAction(final QuoteModel quoteModel, final QuoteUserType quoteUserType)
	{
		if (QuoteUserType.BUYER.equals(quoteUserType))
		{
			quoteModel.setExpirationTime(null);
		}
		else if (QuoteUserType.SELLER.equals(quoteUserType))
		{
			final Date updatedExpirationTime = QuoteExpirationTimeUtils.determineExpirationTime(quoteModel.getExpirationTime(),
					getTimeService().getCurrentDateWithTimeNormalized());
			quoteModel.setExpirationTime(updatedExpirationTime);
		}
		else
		{
			throw new IllegalArgumentException("Quote user type not supported for submit action.");
		}
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
