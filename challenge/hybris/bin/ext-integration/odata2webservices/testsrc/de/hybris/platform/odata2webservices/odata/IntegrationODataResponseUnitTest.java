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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.RuntimeIOException;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.junit.Test;

@UnitTest
public class IntegrationODataResponseUnitTest
{
	private static final String RESPONSE_BODY = "<response><body>asdf</body></response>";
	private IntegrationODataResponse integrationODataResponse = new IntegrationODataResponse();

	@Test
	public void testBufferedEntityReadBodyMultipleTimes() throws IOException, ODataException
	{
		integrationODataResponse.customBuilder().entity(RESPONSE_BODY).build();

		final InputStream response = integrationODataResponse.getEntityAsStream();
		assertThatResponseHasExpectedValue(response, RESPONSE_BODY);

		final InputStream responseReadAgain = integrationODataResponse.getEntityAsStream();
		assertThatResponseHasExpectedValue(responseReadAgain, RESPONSE_BODY);
	}

	@Test(expected = ODataException.class)
	public void testBufferedEntityReadBodyExceptionThrown() throws ODataException
	{
		integrationODataResponse.customBuilder().entity(null).build();

		integrationODataResponse.getEntityAsStream();
	}

	@Test
	public void testBufferedEntityWhenReadingStreamThrowsException() throws IOException
	{
		final InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any())).thenThrow(new IOException());
		integrationODataResponse.customBuilder().entity(inputStream).build();

		assertThatThrownBy(() -> integrationODataResponse.getEntityAsStream())
				.isInstanceOf(RuntimeIOException.class);
	}

	@Test
	public void testBufferedEntityWhenClosingStreamThrowsException() throws IOException
	{
		final InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any())).thenReturn(-1);
		doThrow(new IOException()).when(inputStream).close();
		integrationODataResponse.customBuilder().entity(inputStream).build();

		assertThatThrownBy(() -> integrationODataResponse.getEntityAsStream())
				.isInstanceOf(RuntimeIOException.class);
	}

	private void assertThatResponseHasExpectedValue(final InputStream body, final String expectedValue) throws IOException
	{
		assertThat(IOUtils.toString(body))
				.isNotNull()
				.isNotEmpty()
				.isEqualTo(expectedValue);
	}
}