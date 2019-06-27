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
package de.hybris.platform.webservicescommons.testsupport.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;


public class WebservicesAssert
{
	/**
	 * Standard headers that need to be present across hybris webservices.
	 */
	public static final Map<String, String> SECURED_HEADERS;

	static
	{
		final Map<String, String> localSecuredHeaders = new HashMap<>();
		localSecuredHeaders.put("X-FRAME-Options", "SAMEORIGIN");
		localSecuredHeaders.put("X-XSS-Protection", "1; mode=block");
		localSecuredHeaders.put("X-Content-Type-Options", "nosniff");
		localSecuredHeaders.put("Strict-Transport-Security", "max-age=16070400 ; includeSubDomains");
		SECURED_HEADERS = Collections.unmodifiableMap(localSecuredHeaders);
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertOk(final Response response, final boolean expectEmptyBody)
	{
		assertResponseStatus(Status.OK, response, expectEmptyBody);
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertCreated(final Response response, final boolean expectEmptyBody)
	{
		assertResponseStatus(Status.CREATED, response, expectEmptyBody);
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertForbidden(final Response response, final boolean expectEmptyBody)
	{
		assertResponseStatus(Status.FORBIDDEN, response, expectEmptyBody);
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertBadRequest(final Response response, final boolean expectEmptyBody)
	{
		assertResponseStatus(Status.BAD_REQUEST, response, expectEmptyBody);
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertUnauthorized(final Response response, final boolean expectEmptyBody)
	{
		assertResponseStatus(Status.UNAUTHORIZED, response, expectEmptyBody);
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertResponseStatus(final Status expectedStatus, final Response response, final boolean expectEmptyBody)
	{
		assertEquals("Wrong HTTP status at response: " + response, expectedStatus.getStatusCode(), response.getStatus());
		if (expectEmptyBody)
		{
			Assert.assertTrue("Body should be empty at response: " + response, !response.hasEntity());
		}
	}

	/**
	 * @deprecated Since 6.1 use assertResponse instead
	 */
	@Deprecated
	public static void assertResponseStatus(final Status expectedStatus, final Response response)
	{
		assertResponseStatus(expectedStatus, response, false);
	}



	/**
	 * Assert response status and verify that no body is there if expectEmptyBody is true. This will also check the
	 * response against the expected headers passed in parameters.
	 *
	 * @param expectedStatus
	 *           expected status
	 * @param response
	 *           response to test
	 *
	 * @param expectedHeaders
	 *           headers that should be present in the response header
	 */
	public static void assertResponse(final Status expectedStatus, final Optional<Map<String, String>> expectedHeaders,
			final Response response)
	{
		assertEquals("Wrong HTTP status at response: " + response, expectedStatus.getStatusCode(), response.getStatus());
		if (expectedHeaders.isPresent())
		{
			for (final Entry<String, String> header : expectedHeaders.get().entrySet())
			{
				assertEquals(header.getValue(), response.getHeaderString(header.getKey()));
			}
		}
	}

	/**
	 * Assert response status and verify that no body is there if expectEmptyBody is true. This will also check the
	 * response against basic security headers.
	 *
	 * @param expectedStatus
	 *           expected status
	 * @param response
	 *           response to test
	 *
	 */
	public static void assertResponse(final Status expectedStatus, final Response response)
	{
		assertResponse(expectedStatus, Optional.of(SECURED_HEADERS), response);
	}

	public static void assertForbiddenError(final Response response)
	{
		WebservicesAssert.assertResponse(Status.FORBIDDEN, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error1 = errors.getErrors().get(0);
		assertEquals("ForbiddenError", error1.getType());
	}


	public static void assertJSONEquals(final Object expected, final Object actual)
	{
		assertJSONEquals(expected, actual, "***", true);
	}

	public static void assertJSONEquals(final Object expected, final Object actual, final boolean acceptAdditionalFields)
	{
		assertJSONEquals(expected, actual, "***", acceptAdditionalFields);
	}

	public static void assertJSONEquals(final Object expected, final Object actual, final String wildCard,
			final boolean acceptAdditionalFields)
	{

		if (actual instanceof Map && expected instanceof Map)
		{
			final Map<?, ?> actualMap = (Map) actual;
			final Map<?, ?> expectedMap = (Map) expected;
			expectedMap.entrySet().forEach(entry -> {
				Assert.assertTrue(actualMap.containsKey(entry.getKey()));
				final Object actualValue = actualMap.get(entry.getKey());
				final Object expectedValue = entry.getValue();
				assertJSONEquals(expectedValue, actualValue, wildCard, acceptAdditionalFields);
			});
			if (!acceptAdditionalFields)
			{
				actualMap.keySet().forEach(k -> {
					Assert.assertTrue("Actual json contains unexpected field: [" + k + "] ", expectedMap.keySet().contains(k));
				});
			}
		}
		else if (actual instanceof List && expected instanceof List)
		{
			final List<?> actualList = (List) actual;
			final List<?> expectedList = (List) expected;
			Assert.assertEquals(expectedList.size(), actualList.size());
			for (int i = 0; i < expectedList.size(); i++)
			{
				assertJSONEquals(expectedList.get(i), actualList.get(i), wildCard, acceptAdditionalFields);
			}
		}
		else
		{
			if (!wildCard.equals(expected))
			{
				Assert.assertEquals(expected, actual);
			}
		}
	}

}
