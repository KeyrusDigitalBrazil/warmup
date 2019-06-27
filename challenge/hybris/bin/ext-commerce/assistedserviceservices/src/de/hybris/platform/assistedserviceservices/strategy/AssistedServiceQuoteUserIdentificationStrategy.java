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
package de.hybris.platform.assistedserviceservices.strategy;

import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUserIdentificationStrategy;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * The Class AssistedServiceQuoteUserTypeIdentificationStrategy.
 *
 * Return the ASM agent if there is a ASM session.
 */
public class AssistedServiceQuoteUserIdentificationStrategy extends DefaultQuoteUserIdentificationStrategy
{
	private AssistedServiceService assistedServiceService;
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;

	@Override
	public UserModel getCurrentQuoteUser()
	{
		final AssistedServiceSession asmSession = getAssistedServiceService().getAsmSession();
		if (asmSession != null && asmSession.getAgent() != null)
		{
			final UserModel agent = asmSession.getAgent();
			final Optional<QuoteUserType> currentQuoteUserType = getQuoteUserTypeIdentificationStrategy()
					.getCurrentQuoteUserType(agent);
			if (currentQuoteUserType.isPresent())
			{
				return agent;
			}
		}

		return super.getCurrentQuoteUser();
	}

	protected AssistedServiceService getAssistedServiceService()
	{
		return assistedServiceService;
	}

	@Required
	public void setAssistedServiceService(final AssistedServiceService assistedServiceService)
	{
		this.assistedServiceService = assistedServiceService;
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