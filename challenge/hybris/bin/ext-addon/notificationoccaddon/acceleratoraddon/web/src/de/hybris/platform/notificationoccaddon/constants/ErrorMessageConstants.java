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
package de.hybris.platform.notificationoccaddon.constants;

public class ErrorMessageConstants
{
	public static final String MISSING_BOTH_PARAMS_MESSAGE = "At least one channel is required.";

	public static final String NO_MOBILE_BOUND_MESSAGE = "There is no bound mobile number.";

	public static final String INVALID_CHANNEL_MESSAGE = "The notification channel '%s' is unknown.";

	public static final String INVALID_VALUE_MESSAGE = "The value for channel '%s' is invalid.";


	public static final String SMS_PARAM_NAME = "SMS";

	private ErrorMessageConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
