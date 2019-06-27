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
package com.hybris.ymkt.common.consent.impl;

import com.hybris.ymkt.common.consent.YmktConsentService;


/**
 * Empty implementation of {@link YmktConsentService} when the <code>sapymktconsent</code> extension is not used.<br>
 */
public class DefaultYmktConsentService implements YmktConsentService
{

	@Override
	public boolean getUserConsent(final String consentID)
	{
		return true;
	}

	@Override
	public boolean getUserConsent(final String customerId, final String consentID)
	{
		return true;
	}

}
