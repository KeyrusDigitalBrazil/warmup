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
import de.hybris.platform.commerceservices.order.strategies.QuoteMetadataValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link QuoteMetadataValidationStrategy}
 */
public class DefaultQuoteMetadataValidationStrategy implements QuoteMetadataValidationStrategy
{
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;

	@Override
	public void validate(final QuoteAction quoteAction, final QuoteModel quoteModel, final UserModel userModel)
	{
		if (StringUtils.isEmpty(quoteModel.getName()))
		{
			throw new IllegalArgumentException(String.format("Name is required for quote [%s] with version [%s].",
					quoteModel.getCode(), quoteModel.getVersion()));
		}

		if (QuoteAction.APPROVE.equals(quoteAction))
		{
			final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel)
					.orElseThrow(
							() -> new IllegalArgumentException(String.format("Failed to determine quote user's [%s] type.",
									userModel.getPk())));

			checkExpirationTimeForApproveAction(quoteModel, quoteUserType);
		}
	}

	protected void checkExpirationTimeForApproveAction(final QuoteModel quoteModel, final QuoteUserType quoteUserType)
	{
		if (QuoteUserType.SELLERAPPROVER.equals(quoteUserType))
		{
			if (quoteModel.getExpirationTime() == null)
			{
				throw new IllegalStateException(String.format("Expiration time is not set for quote [%s] with version [%s].",
						quoteModel.getCode(), quoteModel.getVersion()));
			}
		}
		else
		{
			throw new IllegalArgumentException("Quote user type not supported for approve action.");
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
}
