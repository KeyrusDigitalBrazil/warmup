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
package de.hybris.platform.chineseprofileservices.customer;

import de.hybris.platform.chineseprofileservices.data.VerificationData;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Optional;


/**
 * Extended to support more function.
 */
public interface ChineseCustomerAccountService extends de.hybris.platform.commerceservices.customer.CustomerAccountService
{

	/**
	 * Generate a verification code.
	 *
	 * @return Verification Code
	 */
	String generateVerificationCode();

	/**
	 * Send verification code
	 *
	 * @param data
	 *           the data that contains sending info.
	 */
	void sendVerificationCode(final VerificationData data);

	/**
	 * Update customer's mobile number
	 *
	 * @param customerModel
	 *           customer to be updated
	 */
	void updateMobileNumber(final CustomerModel customerModel);

	/**
	 * Find a customer by using mobile number.
	 *
	 * @param mobileNumber
	 *           the number to query
	 * @return an Optional containing the customer, or an empty Optional if no customer is found.
	 */
	Optional<CustomerModel> getCustomerForMobileNumber(final String mobileNumber);
}
