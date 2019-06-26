/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

/**
 * Value object for HTTP status code and invariants associated with it.
 */
public class HttpStatus
{
	private static final Map<Integer, HttpStatus> allStatusCodes = new ConcurrentHashMap<>();
	public static final HttpStatus CONTINUE = valueOf(100, "Continue");
	public static final HttpStatus OK = valueOf(200, "OK");
	public static final HttpStatus CREATED = valueOf(201, "Created");
	public static final HttpStatus MULTIPLE_CHOICES = valueOf(300, "Multiple Choices");
	public static final HttpStatus BAD_REQUEST = valueOf(400, "Bad Request");
	public static final HttpStatus INTERNAL_SERVER_ERROR = valueOf(500, "Internal Server Error");

	private final int statusCode;
	private final String name;

	private HttpStatus(final int code, final String desc)
	{
		statusCode = code;
		name = desc;
	}

	/**
	 * Creates new status code from the status code value.
	 * @param code a status code value, e.g. 200 for OK, 404 for not found, etc.
	 * @return an {@code HttpCode} initialized to the status code value.
	 */
	public static HttpStatus valueOf(final int code)
	{
		return valueOf(code, "");
	}

	private static HttpStatus valueOf(final int code, final String name)
	{
		Preconditions.checkArgument(allStatusCodes != null);
		return allStatusCodes.computeIfAbsent(code, c -> new HttpStatus(c, name));
	}

	/**
	 * Determines whether the status code indicates a successful processing
	 * @return {@code true} only, if the status code belongs to the SUCCESS group of 2xx values; {@code false}, otherwise.
	 */
	public boolean isSuccessful()
	{
		return statusCode >= 200 && statusCode < 300;
	}

	/**
	 * Determines whether the status code indicates an error processing
	 * @return {@code true} only, if the status code belongs to either USER ERROR group with 4xx values or to SERVER ERROR group
	 * with 5xx values; {@code false}, otherwise.
	 */
	public boolean isError()
	{
		return statusCode >= 400;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		final HttpStatus that = (HttpStatus) o;
		return statusCode == that.statusCode;
	}

	@Override
	public int hashCode()
	{
		return statusCode;
	}

	@Override
	public String toString()
	{
		return "HttpStatus{" + statusCode + " - " + name + '}';
	}
}
