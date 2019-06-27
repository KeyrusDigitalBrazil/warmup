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
package de.hybris.platform.commerceservices.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * Abstract Anonymous Consent event. Provides data specific to anonymous user consents (different than for registered
 * users).
 */
public class AnonymousConsentChangeEvent extends AbstractEvent
{
	private final Map<String, String> additionalData;
	private final String consentTemplateCode;
	private final String currentConsentState;
	private final String oldConsentState;
	private final Map<String, String> otherConsents;

	public AnonymousConsentChangeEvent(final String consentTemplateCode, final String oldConsentState,
			final String currentConsentState, final Map<String, String> otherConsents)
	{
		this.additionalData = new HashMap<>();

		this.consentTemplateCode = consentTemplateCode;
		this.oldConsentState = oldConsentState;
		this.currentConsentState = currentConsentState;

		this.otherConsents = new HashMap<>(otherConsents);
	}

	public void addData(final Map<String, String> data)
	{
		additionalData.putAll(data);
	}

	public void addData(final String key, final String value)
	{
		additionalData.put(key, value);
	}

	public Map<String, String> getAdditionalData()
	{
		return new HashMap<>(additionalData);
	}

	public Map<String, String> getOtherConsents()
	{
		return new HashMap<>(otherConsents);
	}

	public String getCurrentConsentState()
	{
		return currentConsentState;
	}

	public String getOldConsentState()
	{
		return oldConsentState;
	}

	public String getConsentTemplateCode()
	{
		return consentTemplateCode;
	}

}
