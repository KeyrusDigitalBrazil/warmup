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

import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.model.ODataError;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.security.SecureRandom;

import org.apache.http.util.EncodingUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Custom HTTP message converter for Multipart mixed mode
 */
public class MultipartMixedHttpMessageConverter implements HttpMessageConverter<MultiValueMap<String, ?>>
{
	private static final Logger LOGGER = Logger.getLogger(MultipartMixedHttpMessageConverter.class);
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final String ERROR_KEY = "error";

	private final List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();

	public MultipartMixedHttpMessageConverter()
	{
		//add needed default converters to use their write functionality
		this.partConverters.add(new ByteArrayHttpMessageConverter());
		final StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(DEFAULT_CHARSET);
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		this.partConverters.add(stringHttpMessageConverter);
		this.partConverters.add(new ResourceHttpMessageConverter());
		this.partConverters.add(new MappingJackson2HttpMessageConverter());
	}


	@Override
	public void write(final MultiValueMap<String, ?> values, final MediaType mediaType, final HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException
	{
		//invoke the custom write multipart
		writeMultipart((MultiValueMap<String, Object>) values, outputMessage);
	}


	protected void writeMultipart(final MultiValueMap<String, Object> parts, final HttpOutputMessage outputMessage)
			throws IOException
	{

		final byte[] boundary = generateMultipartBoundary();
		final byte[] changeset = generateMultipartBoundary();

		//add the batch boundary to the multipart mixed mode media type
		final Map<String, String> parameters = Collections.singletonMap("boundary", new String(boundary, "US-ASCII"));

		final MediaType contentType = new MediaType(Customerticketingc4cintegrationConstants.MULTIPART,
				Customerticketingc4cintegrationConstants.MIXED, parameters);
		final HttpHeaders headers = outputMessage.getHeaders();
		headers.setContentType(contentType);

		//now loop over the parts to be added and write part by part separating each part by a change-set
		if (outputMessage instanceof StreamingHttpOutputMessage)
		{
			final StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body()
			{
				@Override
				public void writeTo(final OutputStream outputStream) throws IOException
				{
					writeBoundary(outputMessage.getBody(), boundary);
					writeParts(outputStream, parts, changeset);
					writeEnd(outputStream, changeset, boundary);
				}
			});
		}
		else
		{
			writeBoundary(outputMessage.getBody(), boundary);
			addContentTypeHeader(outputMessage.getBody(), changeset);
			writeParts(outputMessage.getBody(), parts, changeset);
			writeEnd(outputMessage.getBody(), changeset, boundary);
		}
	}

	protected void writeParts(final OutputStream os, final MultiValueMap<String, Object> parts, final byte[] changeset)
			throws IOException
	{

		//loop over the parts to be added and write part by part separating each part by a change-set
		for (final Map.Entry<String, List<Object>> entry : parts.entrySet())
		{
			for (final Object part : entry.getValue())
			{
				if (part != null)
				{
					writeBoundary(os, changeset);
					addEncodingHeader(os);
					writePart(getHttpEntity(part), os);
					writeNewLine(os);
				}
			}
		}
	}

	protected void addEncodingHeader(final OutputStream os) throws IOException
	{
		//add content type and transfer encoding to changeset
		os.write("Content-Type: application/http".getBytes());
		writeNewLine(os);
		os.write("Content-Transfer-Encoding: binary".getBytes());
		writeNewLine(os);
		writeNewLine(os);
	}

	protected void addContentTypeHeader(final OutputStream os, final byte[] changeset) throws IOException
	{
		final byte[] fixed = "Content-Type: multipart/mixed; boundary=".getBytes();
		final byte[] text = new byte[fixed.length + changeset.length];
		System.arraycopy(fixed, 0, text, 0, fixed.length);
		System.arraycopy(changeset, 0, text, fixed.length, changeset.length);

		os.write(text);
		writeNewLine(os);
		writeNewLine(os);
	}

	//write data part
	@SuppressWarnings("unchecked")
	protected void writePart(final HttpEntity<?> partEntity, final OutputStream os) throws IOException
	{
		final Object partBody = partEntity.getBody();
		final Class<?> partType = partBody.getClass();
		final HttpHeaders partHeaders = partEntity.getHeaders();
		final MediaType partContentType = partHeaders.getContentType();
		for (final HttpMessageConverter<?> messageConverter : this.partConverters)
		{
			if (messageConverter.canWrite(partType, partContentType))
			{
				final HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os);
				if (!partHeaders.isEmpty())
				{
					multipartMessage.getHeaders().putAll(partHeaders);
				}
				((HttpMessageConverter<Object>) messageConverter).write(partBody, partContentType, multipartMessage);
				return;
			}
		}
		throw new HttpMessageNotWritableException(
				"Could not write request: no suitable HttpMessageConverter " + "found for request type [" + partType.getName() + "]");
	}

	/**
	 * The pool of ASCII chars to be used for generating a multipart boundary.
	 */

	protected static byte[] MULTIPART_CHARS = EncodingUtils
			.getAsciiBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

	protected static byte[] generateMultipartBoundary()
	{
		final SecureRandom rand = new SecureRandom();
		final byte[] bytes = new byte[rand.nextInt(11) + 30]; // a random size from 30 to 40
		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
		}
		return bytes;
	}

	protected HttpEntity<?> getHttpEntity(final Object part) //NOSONAR
	{
		if (part instanceof HttpEntity)
		{
			return (HttpEntity<?>) part;
		}
		else
		{
			return new HttpEntity<Object>(part);
		}
	}

	//write the boundary part
	protected void writeBoundary(final OutputStream os, final byte[] boundary) throws IOException
	{
		os.write('-');
		os.write('-');
		os.write(boundary);
		writeNewLine(os);
	}

	//write ending part
	protected static void writeEnd(final OutputStream os, final byte[] changeset, final byte[] boundary) throws IOException
	{
		os.write('-');
		os.write('-');
		os.write(changeset);
		os.write('-');
		os.write('-');
		writeNewLine(os);
		os.write('-');
		os.write('-');
		os.write(boundary);
		os.write('-');
		os.write('-');
		writeNewLine(os);
	}

	protected static void writeNewLine(final OutputStream os) throws IOException
	{
		os.write('\r');
		os.write('\n');
	}

	/**
	 * Implementation of {@link org.springframework.http.HttpOutputMessage} used to write a MIME multipart.
	 */
	protected static class MultipartHttpOutputMessage implements HttpOutputMessage
	{

		private final OutputStream outputStream;

		private final HttpHeaders headers = new HttpHeaders();

		private boolean headersWritten = false;

		public MultipartHttpOutputMessage(final OutputStream outputStream)
		{
			this.outputStream = outputStream;
		}

		@Override
		public HttpHeaders getHeaders()
		{
			return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
		}

		@Override
		public OutputStream getBody() throws IOException
		{
			writeHeaders();
			return this.outputStream;
		}

		protected void writeHeaders() throws IOException
		{
			if (!this.headersWritten)
			{
				for (final Map.Entry<String, List<String>> entry : this.headers.entrySet())
				{
					writeHeaderEntry(entry);
				}
				writeNewLine(this.outputStream);
				this.headersWritten = true;
			}
		}

		protected void writeHeaderEntry(final Map.Entry<String, List<String>> entry) throws IOException
		{
			final byte[] headerName = getAsciiBytes(entry.getKey());
			for (final String headerValueString : entry.getValue())
			{
				final byte[] headerValue = getAsciiBytes(headerValueString);
				if (null != entry.getKey() && !entry.getKey().isEmpty())
				{
					this.outputStream.write(headerName);
					this.outputStream.write(':');
					this.outputStream.write(' ');
				}
				this.outputStream.write(headerValue);
				writeNewLine(this.outputStream);
			}
		}

		protected byte[] getAsciiBytes(final String name)
		{
			try
			{
				return name.getBytes("US-ASCII");
			}
			catch (final UnsupportedEncodingException ex)
			{
				// Should not happen - US-ASCII is always supported.
				throw new IllegalStateException(ex);
			}
		}
	}

	@Override
	public boolean canWrite(final Class clazz, final MediaType mediaType)
	{
		if (clazz == LinkedMultiValueMap.class
				&& mediaType.getType().equalsIgnoreCase(Customerticketingc4cintegrationConstants.MULTIPART)
				&& mediaType.getSubtype().equalsIgnoreCase(Customerticketingc4cintegrationConstants.MIXED))
		{
			return true;
		}
		return false;
	}

	//return supported media types
	@Override
	public List getSupportedMediaTypes()
	{
		final List<MediaType> supportedTypes = new ArrayList<MediaType>();

		final MediaType multipartMixedType = new MediaType(Customerticketingc4cintegrationConstants.MULTIPART,
				Customerticketingc4cintegrationConstants.MIXED);
		supportedTypes.add(multipartMixedType);
		return supportedTypes;
	}


	//can we read ?
	@Override
	public boolean canRead(final Class clazz, final MediaType mediaType)
	{
		if (clazz == MultiValueMap.class && null != mediaType
				&& mediaType.getType().equalsIgnoreCase(Customerticketingc4cintegrationConstants.MULTIPART)
				&& mediaType.getSubtype().equalsIgnoreCase(Customerticketingc4cintegrationConstants.MIXED))
		{
			return true;
		}
		return false;
	}


	@Override
	public MultiValueMap<String, ?> read(final Class val, final HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException
	{
		final MediaType contentType = inputMessage.getHeaders().getContentType();
		final Charset charset = contentType.getCharSet() != null ? contentType.getCharSet() : DEFAULT_CHARSET;
		final String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

		final String[] pairs = StringUtils.tokenizeToStringArray(body, "\r\n");
		final MultiValueMap<String, Object> result = new LinkedMultiValueMap<String, Object>(pairs.length);

		//when reading response we need to make sure we don't have any error,
		//if we do we will create ODataError and return the error object
		for (final String pair : pairs)
		{
			if (org.apache.commons.lang.StringUtils.containsIgnoreCase(pair, ERROR_KEY))
			{
				result.add(Customerticketingc4cintegrationConstants.MULTIPART_HAS_ERROR, Boolean.TRUE);
				final ODataError odataError = retrieveJsonError(pair);
				if (null != odataError)
				{
					result.add(Customerticketingc4cintegrationConstants.MULTIPART_ERROR_MESSAGE, odataError.getMessage().getValue());
					result.add(Customerticketingc4cintegrationConstants.MULTIPART_ERROR_CODE, odataError.getCode());
					break;
				}
			}
		}

		result.add(Customerticketingc4cintegrationConstants.MULTIPART_BODY, body);

		return result;
	}

	protected ODataError retrieveJsonError(final String jsonToCheck)
	{
		try
		{
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode node = mapper.readTree(jsonToCheck);
			if (null != node.get(ERROR_KEY))
			{
				final ODataError odataError = mapper.readValue(node.get(ERROR_KEY).toString(), ODataError.class);

				return odataError;
			}
		}
		catch (final IOException exp)
		{
			LOGGER.info(exp);
		}
		return null;
	}
}
