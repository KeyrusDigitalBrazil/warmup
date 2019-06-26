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
 *
 */
package com.hybris.cis.client.mock;

import com.google.common.collect.Multimap;
import com.hybris.charon.RawResponse;
import com.hybris.cis.client.CisClient;
import rx.Observable;

import javax.ws.rs.core.Response.Status;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * Mock implementation of {@link CisClient}
 */
public class SharedClientMock implements CisClient
{

	public static final String PING_FAIL = "PING_FAIL";

	/**
	 * Test the connection to the service
	 *
	 * @param xCisClientRef
	 * 		client ref to pass in the header
	 * @param tenantId
	 * 		tenantId to pass in the header
	 * @return true unless xCisClientRef is set to PING_FAIL
	 */
	@Override
	public RawResponse doPing(String xCisClientRef, String tenantId)
	{
		return xCisClientRef.equals(PING_FAIL) ? createRawResponseWithStatus(Status.FORBIDDEN) : createRawResponseWithStatus(Status.CREATED);
	}

	/**
	 * Create a dummy {@link RawResponse} with the status given as a parameter
	 *
	 * @param status
	 * 		the status of the {@link RawResponse} to create
	 * @return the created {@link RawResponse}
	 */
	protected RawResponse createRawResponseWithStatus(final Status status)
	{
		return new RawResponse()
		{
			@Override
			public Optional<Date> headerDate(String header)
			{
				return null;
			}

			@Override
			public Status status()
			{
				return status;
			}

			@Override
			public Multimap<String, String> headers()
			{
				return null;
			}

			@Override
			public Optional<String> header(String header)
			{
				return null;
			}

			@Override
			public List<String> headerList(String header)
			{
				return null;
			}

			@Override
			public Observable content()
			{
				return null;
			}
		};
	}
}
