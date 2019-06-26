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
package de.hybris.platform.customerticketingc4cintegration.facade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * Test cases for {@link MultipartMixedHttpMessageConverter}
 */
@UnitTest
public class MultipartMixedHttpMessageConverterTest
{
	private MultipartMixedHttpMessageConverter converter;

	/**
	 * Test case should write the the data.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldWrite() throws IOException
	{
		converter = new MultipartMixedHttpMessageConverter();

		MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
		values.add("anything mocked value", Integer.toString(1));

		HttpOutputMessage outputMessage = Mockito.mock(HttpOutputMessage.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);
		OutputStream os = Mockito.mock(OutputStream.class);

		Mockito.when(outputMessage.getHeaders()).thenReturn(headers);

		Mockito.when(outputMessage.getBody()).thenReturn(os);
		converter.write(values, MediaType.ALL, outputMessage);

		Mockito.verify(outputMessage, Mockito.times(4)).getBody();
		Mockito.verify(os, Mockito.times(42)).write(Mockito.anyByte()); // NOSONAR

	}

	/**
	 * Should set body when the message is StreamingHttpOutputMessage.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldSetBodyWhenStreamingHttpOutputMessage() throws IOException
	{
		converter = new MultipartMixedHttpMessageConverter();

		MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
		values.add("anything mocked value", Integer.toString(1));

		StreamingHttpOutputMessage outputMessage = Mockito.mock(StreamingHttpOutputMessage.class);
		HttpHeaders headers = Mockito.mock(HttpHeaders.class);

		Mockito.when(outputMessage.getHeaders()).thenReturn(headers);

		converter.write(values, MediaType.ALL, outputMessage);
		Mockito.verify(outputMessage).setBody(Mockito.any());
	}

	/**
	 * Test {@link MultipartMixedHttpMessageConverter#canRead(Class, MediaType)}
	 * should return true
	 */
	@Test
	public void shouldCanRead()
	{
		converter = new MultipartMixedHttpMessageConverter();

		MediaType type = new MediaType(Customerticketingc4cintegrationConstants.MULTIPART,
				Customerticketingc4cintegrationConstants.MIXED);
		Assert.assertTrue(converter.canRead(MultiValueMap.class, type));
	}

	/**
	 * Test {@link MultipartMixedHttpMessageConverter#canWrite(Class, MediaType)}
	 * should return true
	 */
	@Test
	public void shouldCanWrite()
	{
		converter = new MultipartMixedHttpMessageConverter();

		MediaType type = new MediaType(Customerticketingc4cintegrationConstants.MULTIPART,
				Customerticketingc4cintegrationConstants.MIXED);
		Assert.assertTrue(converter.canWrite(LinkedMultiValueMap.class, type));
	}

	/**
	 * Test {@link MultipartMixedHttpMessageConverter#canRead(Class, MediaType)}
	 * should return false
	 */
	@Test
	public void shouldNotCanRead()
	{
		converter = new MultipartMixedHttpMessageConverter();

		Assert.assertFalse(converter.canRead(MultiValueMap.class, MediaType.IMAGE_GIF));
	}

	/**
	 * Test {@link MultipartMixedHttpMessageConverter#getSupportedMediaTypes()}
	 *
	 * should return all supported media types.
	 */
	@Test
	public void shouldGetSupportedMediaTypes()
	{
		converter = new MultipartMixedHttpMessageConverter();
		List supportedMediaTypes = converter.getSupportedMediaTypes();

		MediaType type = new MediaType(Customerticketingc4cintegrationConstants.MULTIPART,
				Customerticketingc4cintegrationConstants.MIXED);
		Assert.assertFalse(supportedMediaTypes.isEmpty());
		Assert.assertEquals(1, supportedMediaTypes.size());
		Assert.assertEquals(type, supportedMediaTypes.get(0));
	}

}
