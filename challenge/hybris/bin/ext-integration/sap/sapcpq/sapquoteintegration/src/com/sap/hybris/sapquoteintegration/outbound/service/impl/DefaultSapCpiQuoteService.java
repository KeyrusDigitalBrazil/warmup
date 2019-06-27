/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.Optional;
import java.util.Set;

import com.sap.hybris.sapquoteintegration.events.SapCpiQuoteBuyerSubmitEvent;
import com.sap.hybris.sapquoteintegration.events.SapCpiQuoteCancelEvent;

import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 *
 */
public class DefaultSapCpiQuoteService extends DefaultCommerceQuoteService {

	@Override
	public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel) {

		QuoteModel quoteToCancel = quoteModel;
		validateParameterNotNullStandardMessage("quoteModel", quoteToCancel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		final String externalQuoteId = quoteToCancel.getExternalQuoteId();
		getQuoteActionValidationStrategy().validate(QuoteAction.CANCEL, quoteToCancel, userModel);

		if (isSessionQuoteSameAsRequestedQuote(quoteToCancel)) {
			final Optional<CartModel> optionalCart = Optional.ofNullable(getCartService().getSessionCart());
			if (optionalCart.isPresent()) {
				quoteToCancel = updateQuoteFromCartInternal(optionalCart.get());
				removeQuoteCart(quoteToCancel);
			}
		}

		quoteToCancel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.CANCEL, quoteToCancel, userModel);
		if (quoteToCancel.getExternalQuoteId() == null && externalQuoteId != null && !externalQuoteId.isEmpty()) {
			quoteToCancel.setExternalQuoteId(externalQuoteId);
            for(int i= 0 ; i < quoteModel.getEntries().size() ; i++) {
			    String externalEntryId =  ((QuoteEntryModel) quoteModel.getEntries().get(i)).getExternalQuoteEntryId();
			    ((QuoteEntryModel) quoteToCancel.getEntries().get(i)).setExternalQuoteEntryId(externalEntryId);
			}
		}
		getModelService().save(quoteToCancel);
		getModelService().refresh(quoteToCancel);

   	   if (quoteToCancel.getExternalQuoteId() != null &&  !quoteToCancel.getExternalQuoteId().isEmpty()) {
		    getEventService().publishEvent(new SapCpiQuoteCancelEvent(quoteToCancel, userModel,
		                getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get()));
		}
		
	}

	@Override
	public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel) {
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.SUBMIT, quoteModel, userModel);

		QuoteModel updatedQuoteModel = isSessionQuoteSameAsRequestedQuote(quoteModel)
				? updateQuoteFromCart(getCartService().getSessionCart(), userModel) : quoteModel;

		validateQuoteTotal(updatedQuoteModel);

		getQuoteMetadataValidationStrategy().validate(QuoteAction.SUBMIT, updatedQuoteModel, userModel);

		updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(QuoteAction.SUBMIT,
				updatedQuoteModel, userModel);
		updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel,
				userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel)
				.get();
		if (QuoteUserType.BUYER.equals(quoteUserType)) {
			final SapCpiQuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new SapCpiQuoteBuyerSubmitEvent(updatedQuoteModel,
					userModel, quoteUserType);
			getEventService().publishEvent(quoteBuyerSubmitEvent);
		} else if (QuoteUserType.SELLER.equals(quoteUserType)) {
			final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(updatedQuoteModel,
					userModel, quoteUserType);
			getEventService().publishEvent(quoteSalesRepSubmitEvent);
		}

		return updatedQuoteModel;
	}
	
	@Override
	public Set<QuoteAction> getAllowedActions(final QuoteModel quoteModel, final UserModel userModel)
	{
		Set<QuoteAction> allowedActions = super.getAllowedActions(quoteModel, userModel);
		allowedActions.remove(QuoteAction.DOWNLOAD_PROPOSAL_DOCUMENT);
		if(quoteModel.getProposalDocument() != null) {
			allowedActions.add(QuoteAction.DOWNLOAD_PROPOSAL_DOCUMENT);
		}
		return allowedActions;
	}

}
