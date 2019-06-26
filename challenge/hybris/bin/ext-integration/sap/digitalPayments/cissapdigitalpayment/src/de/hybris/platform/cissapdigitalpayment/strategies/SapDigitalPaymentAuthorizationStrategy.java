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
package de.hybris.platform.cissapdigitalpayment.strategies;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;


/**
 * Defines the method for payment amount authorization stategy
 */
public interface SapDigitalPaymentAuthorizationStrategy
{
	/**
	 * Payment authorization method for {@link SapDigitalPaymentAuthorizationStrategy}
	 * 
	 * @param parameter
	 *           - {@link CommerceCheckoutParameter} commerce checkout parameter
	 *
	 * @return boolean - determine whether the authorization call is success or not
	 */
	boolean authorizePayment(final CommerceCheckoutParameter parameter);


}
