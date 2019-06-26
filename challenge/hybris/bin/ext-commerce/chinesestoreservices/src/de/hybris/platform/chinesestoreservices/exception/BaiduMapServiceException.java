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
package de.hybris.platform.chinesestoreservices.exception;

import java.util.HashMap;
import java.util.Map;


/**
 * Exception thrown by {@link GeoWebserviceWrapper}
 *
 */
public class BaiduMapServiceException extends RuntimeException
{
	public static final String GEO_INTERNAL_SERVER_ERROR = "1";
	public static final String GEO_PARAMETER_INVALID = "2";
	public static final String GEO_VERIFY_FAILURE = "3";
	public static final String GEO_QUOTA_FAILURE = "4";
	public static final String GEO_AK_FAILURE = "5";
	public static final String GEO_SERVICE_AKMISSING = "101";
	public static final String GEO_UN_SECURE = "102";
	public static final String GEO_SERVICE_INVALIDAK = "200";
	public static final String GEO_SERVER_UNREACHABLE = "500";
	public static final String GEO_SERVICE_ERROR = "SERVICE_ERROR";

	private static final Map<String, String> errorMessages = new HashMap<>();
	static
	{
		errorMessages.put(GEO_INTERNAL_SERVER_ERROR, "Internal server error.");
		errorMessages.put(GEO_PARAMETER_INVALID, "The parameters are invalid.");
		errorMessages.put(GEO_VERIFY_FAILURE, "The permission verification failed.");
		errorMessages.put(GEO_QUOTA_FAILURE, "The requests of services have exceeded the limit allowed per day.");
		errorMessages.put(GEO_AK_FAILURE, "The address key doesn't exist or is invalid.");
		errorMessages.put(GEO_SERVICE_AKMISSING, "The address key is missing.");
		errorMessages.put(GEO_SERVICE_INVALIDAK, "The address key is invalid.");
		errorMessages.put(GEO_UN_SECURE, "Failed to pass the white-list check or incorrect security code.");
		errorMessages.put(GEO_SERVER_UNREACHABLE, "Baidu service is unreachable.");
		errorMessages.put(GEO_SERVICE_ERROR, "Can't get map service.");

	}

	public BaiduMapServiceException(final String message, final Throwable nested)
	{
		super(message, nested);
	}

	public BaiduMapServiceException(final String message)
	{
		super(message);
	}

	public BaiduMapServiceException(final Throwable nested)
	{
		super(nested);
	}

	public static String getErrorMessage(final String code)
	{
		return errorMessages.get(code);
	}


}
