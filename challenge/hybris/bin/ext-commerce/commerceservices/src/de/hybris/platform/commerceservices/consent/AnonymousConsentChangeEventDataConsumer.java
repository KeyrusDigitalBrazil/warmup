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
package de.hybris.platform.commerceservices.consent;

import de.hybris.platform.commerceservices.event.AnonymousConsentChangeEvent;

import java.util.Map;


/**
 * Interface for consuming data created by {@link AnonymousConsentChangeEventDataProvider}
 */
public interface AnonymousConsentChangeEventDataConsumer
{
	/**
	 * Consumes data form {@link AnonymousConsentChangeEvent}
	 * 
	 * @param data
	 *           data to consume
	 */
	void process(Map<String, String> data);

}
