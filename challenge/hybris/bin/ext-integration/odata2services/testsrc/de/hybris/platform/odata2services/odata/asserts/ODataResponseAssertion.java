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

package de.hybris.platform.odata2services.odata.asserts;

import de.hybris.platform.integrationservices.asserts.JsonObjectAssertion;
import de.hybris.platform.integrationservices.util.JsonObject;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.StringAssert;

/**
 * Assertions for {@link org.apache.olingo.odata2.api.processor.ODataResponse} class
 */
public class ODataResponseAssertion extends AbstractAssert<ODataResponseAssertion, ODataResponse>
{
	private static final ODataResponseEvaluator EVALUATOR = ODataResponseEvaluator.instance();
	private final ODataResponse actualResponse;

	private ODataResponseAssertion(final ODataResponse response)
	{
		super(response, ODataResponseAssertion.class);
		actualResponse = response;
	}

	public static ODataResponseAssertion assertionOf(final ODataResponse actual)
	{
		return new ODataResponseAssertion(actual);
	}

	public ODataResponseAssertion hasStatus(final HttpStatusCodes status)
	{
		return hasStatus(status.getStatusCode());
	}

	public ODataResponseAssertion hasStatus(final int status)
	{
		EVALUATOR.assertStatusEqual(info, actualResponse.getStatus().getStatusCode(), status);
		return myself;
	}

	public ODataResponseAssertion isSuccessful()
	{
		EVALUATOR.assertStatusSuccessful(info, actualResponse.getStatus().getStatusCode());
		return myself;
	}

	public ODataResponseAssertion hasBody(final String expectedBody)
	{
		EVALUATOR.assertBodyEqual(info, String.valueOf(actualResponse.getEntity()), expectedBody);
		return myself;
	}

	public JsonObjectAssertion jsonBody()
	{
		try
		{
			final JsonObject json = JsonObject.createFrom(actualResponse.getEntityAsStream());
			return JsonObjectAssertion.assertionOf(json);
		}
		catch (final ODataException e)
		{
			throw new AssertionError("Failed to extract body from " + actualResponse, e);
		}
	}

	public StringAssert body()
	{
		try
		{
			final String bodyContent = IOUtils.toString(actualResponse.getEntityAsStream());
			return new StringAssert(bodyContent);
		}
		catch (final IOException | ODataException e)
		{
			throw new AssertionError("Failed to extract body from " + actualResponse, e);
		}
	}
}
