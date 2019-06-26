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

import de.hybris.platform.commercefacades.user.data.AddressData;


/**
 * HOP facade interface. Facade is responsible for getting all necessary information required to build request and ..
 * response from the implemented hosted order page.
 */
public interface CisSapDigitalPaymentFacade
{


	/**
	 * Gets the sap-digital-payment-card-registration-session-id from the session.
	 *
	 * @return sap-digital-payment-card-registration-session-id set in session
	 */
	String getSapDigitalPaymentRegisterCardSession();


	/**
	 * Removes the sap-digital-payment-card-registration-session-id from the session.
	 */
	void removeSapDigitalPaymentRegisterCardSession();


	/**
	 * Add the payment address to the cart
	 *
	 * @param paymentAddress
	 *           - payment address to be added to the cart
	 */
	void addPaymentAddressToCart(AddressData paymentAddress);

}
