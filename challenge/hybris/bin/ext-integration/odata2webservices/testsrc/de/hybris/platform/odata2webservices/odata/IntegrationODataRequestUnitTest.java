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
package de.hybris.platform.odata2webservices.odata;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.RuntimeIOException;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

@UnitTest
public class IntegrationODataRequestUnitTest
{
	private static final String REQUEST_BODY = "{\n" +
			"\t\"@odata.context\": \"$metadata#Products/$entity\",\n" +
			"\t\"code\": \"test_article\",\n" +
			"\t\"name\": \"du bist mein liebchen updated\",\n" +
			"\t\"catalogVersion\": {\n" +
			"\t\t\"catalog\": {\n" +
			"\t\t\t\"id\": \"Default\"\n" +
			"\t\t},\n" +
			"\t\t\"version\": \"Staged\"\n" +
			"\t},\n" +
			"\t\"unit\" : {\n" +
			"\t\t\"code\": \"pieces\"\n" +
			"\t}\n" +
			"}";
	private IntegrationODataRequest integrationODataRequest = new IntegrationODataRequest();

	@Test
	public void testBufferedBodyReadBodyMultipleTimes() throws IOException
	{
		integrationODataRequest.customBuilder().body(stubInputStream(REQUEST_BODY));

		final InputStream body = integrationODataRequest.getBody();
		assertThatBodyHasExpectedValue(body, REQUEST_BODY);

		final InputStream bodyReadAgain = integrationODataRequest.getBody();
		assertThatBodyHasExpectedValue(bodyReadAgain, REQUEST_BODY);
	}

	@Test
	public void testBufferedBodyWhenReadingStreamThrowsException() throws IOException
	{
		final InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any())).thenThrow(new IOException());
		
		Assertions.assertThatThrownBy(() -> integrationODataRequest.customBuilder().body(inputStream))
				.isInstanceOf(RuntimeIOException.class);
	}

	@Test
	public void testBufferedBodyWhenClosingStreamThrowsException() throws IOException
	{
		final InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any())).thenReturn(-1);
		doThrow(new IOException()).when(inputStream).close();

		Assertions.assertThatThrownBy(() -> integrationODataRequest.customBuilder().body(inputStream))
				.isInstanceOf(RuntimeIOException.class);
	}

	private void assertThatBodyHasExpectedValue(final InputStream body, final String expectedValue) throws IOException
	{
		Assertions.assertThat(IOUtils.toString(body))
				.isNotNull()
				.isNotEmpty()
				.isEqualTo(expectedValue);
	}

	private InputStream stubInputStream(final String requestBody)
	{
		return IOUtils.toInputStream(requestBody);
	}
}