/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.c4c.quote.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.Optional;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.c4c.quote.events.C4CQuoteBuyerSubmitEvent;
import de.hybris.platform.sap.c4c.quote.events.C4CQuoteCancelEvent;


/**
 * Default C4C implementation.
 */
public class DefaultC4CQuoteService extends DefaultCommerceQuoteService
{

	@Override
	public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel)
	{

		QuoteModel quoteToCancel = quoteModel;
		validateParameterNotNullStandardMessage("quoteModel", quoteToCancel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		final String c4cQuoteId = quoteToCancel.getC4cQuoteId();
		getQuoteActionValidationStrategy().validate(QuoteAction.CANCEL, quoteToCancel, userModel);

		if (isSessionQuoteSameAsRequestedQuote(quoteToCancel))
		{
			final Optional<CartModel> optionalCart = Optional.ofNullable(getCartService().getSessionCart());
			if (optionalCart.isPresent())
			{
				quoteToCancel = updateQuoteFromCartInternal(optionalCart.get());
				removeQuoteCart(quoteToCancel);
			}
		}

		quoteToCancel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.CANCEL, quoteToCancel, userModel);
		if(quoteToCancel.getC4cQuoteId() == null && c4cQuoteId != null && !c4cQuoteId.isEmpty())
		{
			quoteToCancel.setC4cQuoteId(c4cQuoteId); 	
		}
		getModelService().save(quoteToCancel);
		getModelService().refresh(quoteToCancel);

		getEventService().publishEvent(
				new C4CQuoteCancelEvent(quoteToCancel, userModel, getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(
						userModel).get()));

	}
	
	@Override
	public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.SUBMIT, quoteModel, userModel);

		QuoteModel updatedQuoteModel = isSessionQuoteSameAsRequestedQuote(quoteModel)
				? updateQuoteFromCart(getCartService().getSessionCart(), userModel) : quoteModel;

		validateQuoteTotal(updatedQuoteModel);

		getQuoteMetadataValidationStrategy().validate(QuoteAction.SUBMIT, updatedQuoteModel, userModel);

		updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(QuoteAction.SUBMIT, updatedQuoteModel,
				userModel);
		updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel, userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get();
		if (QuoteUserType.BUYER.equals(quoteUserType))
		{
			final C4CQuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new C4CQuoteBuyerSubmitEvent(updatedQuoteModel, userModel,
					quoteUserType);
			getEventService().publishEvent(quoteBuyerSubmitEvent);
		}
		else if (QuoteUserType.SELLER.equals(quoteUserType))
		{
			final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(updatedQuoteModel, userModel,
					quoteUserType);
			getEventService().publishEvent(quoteSalesRepSubmitEvent);
		}

		return updatedQuoteModel;
	}

}
