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
package de.hybris.platform.odata2webservices.converter;

import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class ODataResponseToResponseEntityConverter implements Converter<ODataResponse, ResponseEntity<String>>
{
	@Override
	public ResponseEntity<String> convert(final ODataResponse response)
	{
		final String payload = extractPayload(response);
		final HttpHeaders headers = extractHeaders(response);

		return ResponseEntity.status(response.getStatus().getStatusCode())
				.headers(headers)
				.body(payload);
	}

	protected String extractPayload(final ODataResponse response)
	{
		if (response.getEntity() instanceof String)
		{
			return (String) response.getEntity();
		}
		else if (response.getEntity() instanceof Integer)
		{
			return response.getEntity().toString();
		}
		else
		{
			try (final InputStream inputStream = response.getEntityAsStream())
			{
				return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
			}
			catch (final ODataException | IOException e)
			{
				throw new InternalProcessingException(e);
			}
		}
	}

	protected HttpHeaders extractHeaders(final ODataResponse response)
	{
		final HttpHeaders headers = new HttpHeaders();
		response.getHeaderNames()
				.stream()
				.filter(h -> !h.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
				.forEach(name -> headers.add(name, response.getHeader(name)));
		return headers;
	}
}
