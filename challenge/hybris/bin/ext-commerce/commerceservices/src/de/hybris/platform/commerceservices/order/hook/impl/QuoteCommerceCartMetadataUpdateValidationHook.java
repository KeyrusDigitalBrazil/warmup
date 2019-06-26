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
package de.hybris.platform.commerceservices.order.hook.impl;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.hook.CommerceCartMetadataUpdateMethodHook;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.commerceservices.util.QuoteExpirationTimeUtils;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.text.DateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Validates cart metadata attributes (i.e. name, description) for a cart created from a quote using quote specific
 * rules.
 */
public class QuoteCommerceCartMetadataUpdateValidationHook implements CommerceCartMetadataUpdateMethodHook
{
	private QuoteActionValidationStrategy quoteActionValidationStrategy;
	private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	private TimeService timeService;

	/**
	 * Validates cart metadata attributes (name, description, and expiration time) for a cart created from a quote using
	 * quote specific rules. Updates to a cart associated with a quote can only be done for the quote states linked to
	 * Save action.
	 *
	 * A buyer cannot set or remove the expiration time for a quote. A seller cannot set name and description attributes.
	 * Also, expiration time when present must be valid as per
	 * {@link QuoteExpirationTimeUtils#isExpirationTimeValid(Date, Date)}. A seller approver cannot set cart metadata
	 * attributes.
	 *
	 * @param parameter
	 *           a bean holding any number of additional attributes a client may want to pass to the method
	 * @throws IllegalArgumentException
	 *            if any of the attributes cannot be set by the user as per the quote rules or if the type of the user
	 *            cannot be determined
	 * @throws IllegalQuoteStateException
	 *            if the cart is associated with a quote for which the operation cannot be performed
	 *
	 */
	@Override
	public void beforeMetadataUpdate(final CommerceCartMetadataParameter parameter)
	{
		final CartModel cart = parameter.getCart();
		final QuoteModel cartQuoteModel = cart.getQuoteReference();
		if (cartQuoteModel == null)
		{
			return;
		}
		final UserModel quoteUser = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();

		getQuoteActionValidationStrategy().validate(QuoteAction.SAVE, cartQuoteModel, quoteUser);

		final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(quoteUser).orElseThrow(
				() -> new IllegalArgumentException(String.format("Failed to determine quote user's [%s] type.", quoteUser.getPk())));
		validateParameter(parameter, quoteUserType);
	}

	protected void validateParameter(final CommerceCartMetadataParameter parameter, final QuoteUserType quoteUserType)
	{
		final Optional<Date> optionalExpirationTime = parameter.getExpirationTime();

		if (QuoteUserType.BUYER.equals(quoteUserType))
		{
			if (optionalExpirationTime.isPresent() || parameter.isRemoveExpirationTime())
			{
				throw new IllegalArgumentException("Buyer is not allowed to modify expiration time of a cart created from a quote.");
			}
		}
		else if (QuoteUserType.SELLER.equals(quoteUserType))
		{
			optionalExpirationTime.ifPresent(expirationTime -> validateExpirationTime(expirationTime));

			if (parameter.getName().isPresent() || parameter.getDescription().isPresent())
			{
				throw new IllegalArgumentException(
						"Seller is not allowed to modify name or description of a cart created from a quote.");
			}
		}
		else if (QuoteUserType.SELLERAPPROVER.equals(quoteUserType))
		{
			throw new IllegalArgumentException(
					"Seller approver is not allowed to modify cart's metadata of a cart created from a quote.");
		}
		else
		{
			throw new IllegalArgumentException("Unknown quote user type.");
		}
	}

	protected void validateExpirationTime(final Date expirationTime)
	{
		if (!QuoteExpirationTimeUtils.isExpirationTimeValid(expirationTime, getTimeService().getCurrentDateWithTimeNormalized()))
		{
			final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
			final String expirationTimeString = expirationTime == null ? "null" : df.format(expirationTime);
			throw new IllegalArgumentException(String.format("Invalid quote expiration time [%s].", expirationTimeString));
		}
	}

	@Override
	public void afterMetadataUpdate(final CommerceCartMetadataParameter parameter)
	{
		//empty
	}

	protected QuoteActionValidationStrategy getQuoteActionValidationStrategy()
	{
		return quoteActionValidationStrategy;
	}

	@Required
	public void setQuoteActionValidationStrategy(final QuoteActionValidationStrategy quoteActionValidationStrategy)
	{
		this.quoteActionValidationStrategy = quoteActionValidationStrategy;
	}

	protected QuoteUserIdentificationStrategy getQuoteUserIdentificationStrategy()
	{
		return quoteUserIdentificationStrategy;
	}

	@Required
	public void setQuoteUserIdentificationStrategy(final QuoteUserIdentificationStrategy quoteUserIdentificationStrategy)
	{
		this.quoteUserIdentificationStrategy = quoteUserIdentificationStrategy;
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
