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
 * Generate a verification code which will be sent to customer's mobile and verify customer's mobile number.
 */
public interface VerificationCodeGenerationStrategy
{

	/**
	 * Generate a verification code.
	 *
	 * @return verification code
	 */
	String generate();
}
