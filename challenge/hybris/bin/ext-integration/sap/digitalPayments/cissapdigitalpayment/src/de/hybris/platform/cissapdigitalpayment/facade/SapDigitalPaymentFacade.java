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
package de.hybris.platform.cissapdigitalpayment.facade;

/**
 *
 * Generic facade interface for SAP DIgital payment. Facade responsible for interacting with the Service to get the
 * registration URL from SAP Digital payment and start the registered card poll process
 *
 */
public interface SapDigitalPaymentFacade
{

	/**
	 * Fetch the registration URL from the SAP Digital payment. Application is redirected to this URL to register the
	 * card with SAP Digital payment
	 *
	 * @return String
	 */
	String getCardRegistrationUrl();

	/**
	 * Creates the registration card polling process
	 *
	 * @param sessionId
	 *           - session-id is passed to digital-payments to get the card details.
	 */

	void createPollRegisteredCardProcess(final String sessionId);

}
