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
package com.hybris.ymkt.common.consent;



/**
 * Consent service for yMKT integration.
 */
public interface YmktConsentService
{

	/**
	 * Consent verification for current session user.
	 *
	 * @param consentId
	 *           Property prefix of yMKT consent Id.
	 * @return <code>true</code> if the user has consent, <code>false</code> otherwise.
	 */
	boolean getUserConsent(String consentId);

	/**
	 * Consent verification for provided customer.
	 *
	 * @param customerId
	 *           Value of <code>Customer.customerID</code>.
	 * @param consentId
	 *           Value of yMKT <code>ConsentTemplate.id</code>.
	 * @return <code>true</code> if the customer has consent, <code>false</code> otherwise.
	 */
	boolean getUserConsent(String customerId, String consentId);

}
