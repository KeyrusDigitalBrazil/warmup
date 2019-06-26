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
package de.hybris.platform.odata2services.odata.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@UnitTest
public class CustomODataExceptionAwareErrorCallbackUnitTest
{
	private final CustomODataExceptionAwareErrorCallback callback = new CustomODataExceptionAwareErrorCallback();

	private ODataErrorContext context;

	@Before
	public void setUp()
	{
		context = new ODataErrorContext();
		context.setContentType("application/json");
	}

	@Test
	public void handleInternalProcessingException() throws ODataException
	{
		givenInternalProcessingException();

		final ODataResponse response = callback.handleError(context);

		assertThat(response).hasFieldOrPropertyWithValue("status", HttpStatusCodes.INTERNAL_SERVER_ERROR);

		final DocumentContext ctx = JsonPath.parse(response.getEntityAsStream());
		assertThat(ctx.read("$.error.code", String.class)).isEqualTo("internal_error");
		assertThat(ctx.read("$.error.message.lang", String.class)).isEqualTo("en");
		assertThat(ctx.read("$.error.message.value", String.class)).isEqualTo("Duplicate primary key!");
	}

	@Test
	public void handleInvalidDataException() throws ODataException
	{
		givenInvalidDataException();

		final ODataResponse response = callback.handleError(context);

		assertThat(response).hasFieldOrPropertyWithValue("status", HttpStatusCodes.BAD_REQUEST);

		final DocumentContext ctx = JsonPath.parse(response.getEntityAsStream());
		assertThat(ctx.read("$.error.code", String.class)).isEqualTo("missing_key");
		assertThat(ctx.read("$.error.message.lang", String.class)).isEqualTo("en");
		assertThat(ctx.read("$.error.message.value", String.class)).isEqualTo("Key [name of key] is required.");
	}

	@Test
	public void handleInvalidODataSchemaException() throws ODataException
	{
		givenInvalidODataSchemaException();

		final ODataResponse response = callback.handleError(context);

		assertThat(response).hasFieldOrPropertyWithValue("status", HttpStatusCodes.INTERNAL_SERVER_ERROR);

		final DocumentContext ctx = JsonPath.parse(response.getEntityAsStream());
		assertThat(ctx.read("$.error.code", String.class)).isEqualTo("schema_generation_error");
		assertThat(ctx.read("$.error.message.lang", String.class)).isEqualTo("en");
		assertThat(ctx.read("$.error.message.value", String.class)).isEqualTo("The EDMX schema could not be generated. Please make sure that your Integration Object is defined correctly.");
	}

	@Test
	public void testNonOdataContextException() throws ODataException
	{
		context.setException(new RuntimeException());

		final ODataResponse response = callback.handleError(context);

		assertThat(response).hasFieldOrPropertyWithValue("status", null);

		final DocumentContext ctx = JsonPath.parse(response.getEntityAsStream());
		assertThat(ctx.read("$.error.code", String.class)).isNull();
		assertThat(ctx.read("$.error.message.lang", String.class)).isNull();
		assertThat(ctx.read("$.error.message.value", String.class)).isNull();
	}

	@Test
	public void testNonOdataCauseException() throws ODataException
	{
		context.setException(new ODataException(new RuntimeException()));

		final ODataResponse response = callback.handleError(context);

		assertThat(response).hasFieldOrPropertyWithValue("status", null);

		final DocumentContext ctx = JsonPath.parse(response.getEntityAsStream());
		assertThat(ctx.read("$.error.code", String.class)).isNull();
		assertThat(ctx.read("$.error.message.lang", String.class)).isNull();
		assertThat(ctx.read("$.error.message.value", String.class)).isNull();
	}

	@Test
	public void testNullContextException() throws ODataException
	{
		context.setException(null);

		final ODataResponse response = callback.handleError(context);

		assertThat(response).hasFieldOrPropertyWithValue("status", null);

		final DocumentContext ctx = JsonPath.parse(response.getEntityAsStream());
		assertThat(ctx.read("$.error.code", String.class)).isNull();
		assertThat(ctx.read("$.error.message.lang", String.class)).isNull();
		assertThat(ctx.read("$.error.message.value", String.class)).isNull();
	}

	private void givenInternalProcessingException()
	{
		context.setException(new InternalProcessingException("internal_error", "Duplicate primary key!", new RuntimeException()));
		context.setContentType("application/json");
	}

	private void givenInvalidDataException()
	{
		context.setException(new InvalidDataException("missing_key", "Key [name of key] is required."));
	}

	private void givenInvalidODataSchemaException()
	{
		context.setException(new ODataException(new InvalidODataSchemaException(new RuntimeException())));
	}
}