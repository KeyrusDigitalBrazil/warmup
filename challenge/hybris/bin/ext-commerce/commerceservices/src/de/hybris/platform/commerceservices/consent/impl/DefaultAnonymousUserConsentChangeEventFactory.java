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
package de.hybris.platform.commerceservices.consent.impl;

import de.hybris.platform.commerceservices.consent.AnonymousConsentChangeEventDataProvider;
import de.hybris.platform.commerceservices.consent.AnonymousConsentChangeEventFactory;
import de.hybris.platform.commerceservices.event.AnonymousConsentChangeEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DefaultAnonymousUserConsentChangeEventFactory implements AnonymousConsentChangeEventFactory
{
	List<AnonymousConsentChangeEventDataProvider> providers = Collections.emptyList();

	@Override
	public AnonymousConsentChangeEvent buildEvent(final String templateCode, final String previousState, final String currentState,
			final Map<String, String> consents)
	{
		final AnonymousConsentChangeEvent event = new AnonymousConsentChangeEvent(templateCode, previousState, currentState,
				consents);

		providers.forEach(p -> event.addData(p.getData()));

		return event;
	}

	public void setProviders(final List<AnonymousConsentChangeEventDataProvider> providers)
	{
		this.providers = providers;
	}
}
