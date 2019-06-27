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
package com.hybris.ymkt.common.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;


public class HttpURLConnectionResponse
{
	private static final byte[] ZERO_BYTES = new byte[0];

	protected Map<String, List<String>> headerFields = Collections.emptyMap();
	protected IOException ioException;
	protected byte[] payload = ZERO_BYTES;
	protected byte[] payloadError = ZERO_BYTES;
	protected int responseCode = -1;
	protected long timeEnd = Long.MAX_VALUE;
	protected final long timeStart = System.currentTimeMillis();

	public long getDuration()
	{
		this.timeEnd = Math.min(this.timeEnd, System.currentTimeMillis());
		return this.timeEnd - this.timeStart;
	}

	/**
	 * @return All header keys and values.
	 * @see HttpURLConnection#getHeaderFields()
	 * @see #getHeaderField(String)
	 */
	public Map<String, List<String>> getHeaderFields()
	{
		return headerFields;
	}

	/**
	 * Return all values set for the header key.
	 *
	 * @param headerKey
	 *           Case insensitive header key.
	 * @return {@link List} of {@link String}
	 */
	@Nonnull
	public List<String> getHeaderField(final String headerKey)
	{
		return headerFields.entrySet().stream() //
				.filter(e -> headerKey.equalsIgnoreCase(e.getKey())) //
				.map(Entry::getValue) //
				.flatMap(List::stream) //
				.collect(Collectors.toList());
	}

	public IOException getIOException()
	{
		return this.ioException;
	}

	public byte[] getPayload()
	{
		return this.payload;
	}

	public byte[] getPayloadError()
	{
		return this.payloadError;
	}

	public int getResponseCode()
	{
		return this.responseCode;
	}

	public void setHeaderFields(final Map<String, List<String>> headerFields)
	{
		this.headerFields = headerFields;
	}

	public void setIOException(final IOException ioException)
	{
		this.ioException = ioException;
	}

	public void setPayload(final byte[] payload)
	{
		this.payload = payload;
	}

	public void setPayloadError(final byte[] payloadError)
	{
		this.payloadError = payloadError;
	}

	public void setResponseCode(final int responseCode)
	{
		this.responseCode = responseCode;
	}

}
