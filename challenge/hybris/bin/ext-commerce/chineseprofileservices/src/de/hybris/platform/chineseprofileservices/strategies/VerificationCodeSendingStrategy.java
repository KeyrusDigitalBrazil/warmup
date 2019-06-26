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
package de.hybris.platform.chineseprofileservices.strategies;

/**
 * A strategy that sending a verification code to customer's mobile.
 */
public interface VerificationCodeSendingStrategy
{

	/**
	 * Send code to a mobile.
	 *
	 * @param mobileNumber
	 *           The target mobile number.
	 * @param code
	 *           Verification code.
	 */
	void send(final String mobileNumber, final String code);
}
