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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ODataResponseToResponseEntityConverterUnitTest
{
	private static final String TEST_STR = "A response";
	private static final Integer INT = 5;
	private static final String CONTENT_LANGUAGE = "fr";

	@InjectMocks
	private ODataResponseToResponseEntityConverter oDataResponseToResponseEntityConverter;

	@Test
	public void testConvert_withInputStreamAsEntity() throws ODataException
	{
		final ODataResponse oDataResponse = givenODataResponse();
		final ResponseEntity<String> responseEntity = oDataResponseToResponseEntityConverter.convert(oDataResponse);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(TEST_STR);
		assertThat(responseEntity.getHeaders())
				.doesNotContainKey(HttpHeaders.CONTENT_LENGTH)
				.containsEntry(HttpHeaders.CONTENT_LANGUAGE, Collections.singletonList(CONTENT_LANGUAGE));
	}

	@Test
	public void testConvert_withStringAsEntity() throws ODataException
	{
		final ODataResponse oDataResponse = givenODataResponse();
		when(oDataResponse.getEntity()).thenReturn(TEST_STR);

		final ResponseEntity<String> responseEntity = oDataResponseToResponseEntityConverter.convert(oDataResponse);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(TEST_STR);
		assertThat(responseEntity.getHeaders())
				.doesNotContainKey(HttpHeaders.CONTENT_LENGTH)
				.containsEntry(HttpHeaders.CONTENT_LANGUAGE, Collections.singletonList(CONTENT_LANGUAGE));
	}

	@Test
	public void testConvert_withIntegerAsEntity() throws ODataException
	{
		final ODataResponse oDataResponse = givenODataResponse();
		when(oDataResponse.getEntity()).thenReturn(INT);

		final ResponseEntity<String> responseEntity = oDataResponseToResponseEntityConverter.convert(oDataResponse);
		assertThat(responseEntity.getBody()).isEqualTo(INT.toString());
	}

	@Test
	public void testConvertThrowsException() throws ODataException
	{
		final ODataResponse oDataResponse = givenODataResponse();
		doThrow(ODataException.class).when(oDataResponse).getEntityAsStream();

		assertThatThrownBy(() -> oDataResponseToResponseEntityConverter.convert(oDataResponse))
				.isInstanceOf(InternalProcessingException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasCauseInstanceOf(ODataException.class);
	}

	@Test
	public void testConvertThrowsIOExceptionWhenClosingStream() throws ODataException, IOException
	{
		final InputStream inputStream = mock(InputStream.class);
		doThrow(new IOException()).when(inputStream).close();
		final ODataResponse oDataResponse = givenODataResponse();
		when(oDataResponse.getEntityAsStream()).thenReturn(inputStream);

		assertThatThrownBy(() -> oDataResponseToResponseEntityConverter.convert(oDataResponse))
				.isInstanceOf(InternalProcessingException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasCauseInstanceOf(IOException.class);
	}

	private static ODataResponse givenODataResponse() throws ODataException
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		final InputStream inputStream = new ByteArrayInputStream(TEST_STR.getBytes());
		when(oDataResponse.getEntity()).thenReturn(inputStream);
		when(oDataResponse.getEntityAsStream()).thenReturn(inputStream);
		when(oDataResponse.getHeaderNames()).thenReturn(Sets.newHashSet(HttpHeaders.CONTENT_LANGUAGE, HttpHeaders.CONTENT_LENGTH));
		when(oDataResponse.getHeader(HttpHeaders.CONTENT_LANGUAGE)).thenReturn(CONTENT_LANGUAGE);
		when(oDataResponse.getHeader(HttpHeaders.CONTENT_LENGTH)).thenReturn(String.valueOf(TEST_STR.length()));
		when(oDataResponse.getStatus()).thenReturn(HttpStatusCodes.OK);
		return oDataResponse;
	}
}