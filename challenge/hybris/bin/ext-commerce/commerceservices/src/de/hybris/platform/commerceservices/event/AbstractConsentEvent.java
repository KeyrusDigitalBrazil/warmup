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

import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * Abstract Consent event, implementation of {@link AbstractCommerceUserEvent}
 */
public class AbstractConsentEvent extends AbstractEvent
{

	private ConsentModel consent;

	/**
	 * @return the consent
	 */
	public ConsentModel getConsent()
	{
		return consent;
	}

	/**
	 * @param consent
	 *           the consent to set
	 */
	public void setConsent(final ConsentModel consent)
	{
		this.consent = consent;
	}
}
