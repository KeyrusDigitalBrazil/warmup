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
package de.hybris.platform.chineseprofileservices.sms;

/**
 * SMS service.
 */
public interface ChineseSmsService
{

	/**
	 * Send a message to a mobile.
	 *
	 * @param mobileNumber
	 *           The target mobile number.
	 * @param msg
	 *           The message to be sent.
	 */
	void sendMsg(final String mobileNumber, final String msg);
}
