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

package de.hybris.platform.odata2services.odata.monitoring.impl;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ODATA_REQUEST;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.SERVICE;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.apache.olingo.odata2.api.commons.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntity;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.PathSegment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRequestBatchEntityExtractorUnitTest
{
	private static final String PRODUCT_TYPE = "Product";
	private static final String MESSAGE_ID = "123";
	private static final PathInfo PATH_INFO = pathInfo();
	@InjectMocks
	private final DefaultRequestBatchEntityExtractor requestEntityExtractor = new DefaultRequestBatchEntityExtractor();
	@Mock
	private ODataRequest request;
	@Mock
	private ODataContext context;
	@Mock
	private ODataServicesConfiguration configuration;

	private static final String PRODUCT_1_EN =
			"{\n" +
					"  \"@odata.context\": \"$metadata#Products/$entity\",\n" +
					"  \"code\": \"1\",\n" +
					"  \"name\": \"Product 1\",\n" +
					"  \"catalogVersion\": {\n" +
					"    \"catalog\": {\n" +
					"    \"id\": \"Default\"\n" +
					"   }\n" +
					"  },\n" +
					"  \"unit\": {\n" +
					"    \"code\": \"pieces\"\n" +
					"  }\n" +
					"}";
	private static final String PRODUCT_1_DE =
			"{\n" +
					"  \"@odata.context\": \"$metadata#Products/$entity\",\n" +
					"  \"code\": \"1\",\n" +
					"  \"name\": \"the name [DE]\",\n" +
					"  \"catalogVersion\": {\n" +
					"    \"catalog\": {\n" +
					"    \"id\": \"Default\"\n" +
					"   },\n" +
					"  \"version\": \"Staged\",\n" +
					"  \"active\": true\n" +
					"  },\n" +
					"  \"unit\": {\n" +
					"    \"code\": \"pieces\"\n" +
					"  }\n" +
					"}\n";

	@Before
	public void setUp() throws ODataException
	{
		when(context.getParameter(ODATA_REQUEST)).thenReturn(request);
		when(context.getPathInfo()).thenReturn(PATH_INFO);
		when(configuration.getBatchLimit()).thenReturn(5);
	}

	@Test
	public void testExtractSingleIntegrationObject() throws IOException
	{
		when(context.getRequestHeader(CONTENT_TYPE)).thenReturn("application/json");
		mockRequestBody(PRODUCT_1_EN);

		final List<RequestBatchEntity> requestEntities = requestEntityExtractor.extractFrom(context);

		assertThat(requestEntities).hasSize(1);
		final RequestBatchEntity requestEntity = requestEntities.get(0);
		assertThat(requestEntity.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertEqualsIgnoringLinebreak(IOUtils.toString(requestEntity.getContent()), PRODUCT_1_EN);
		assertThat(requestEntity.getNumberOfChangeSets()).isEqualTo(1);
	}

	@Test
	public void testBatchRequestWithSingleChangeSet() throws IOException
	{
		when(context.getRequestHeader(CONTENT_TYPE)).thenReturn("multipart/mixed; boundary=batch");

		final String singleChangeSet =
				"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("en") +
						"\r\n" +
						PRODUCT_1_EN +
						"\r\n" +
						"--changeset--\n" +
						"--batch--\n";

		mockRequestBody(singleChangeSet);

		final List<RequestBatchEntity> requestEntities = requestEntityExtractor.extractFrom(context);

		assertThat(requestEntities).hasSize(1);
		final RequestBatchEntity requestEntity = requestEntities.get(0);
		assertThat(requestEntity.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertEqualsIgnoringLinebreak(IOUtils.toString(requestEntity.getContent()), PRODUCT_1_EN);
		assertThat(requestEntity.getNumberOfChangeSets()).isEqualTo(1);
	}

	@Test
	public void testBatchRequestWith2ChangeSets() throws IOException
	{
		when(context.getRequestHeader(CONTENT_TYPE)).thenReturn("multipart/mixed; boundary=batch");
		final String twoChangeSets =
				"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("en") +
						"\r\n" +
						PRODUCT_1_EN +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("de") +
						"\r\n" +
						PRODUCT_1_DE +
						"\r\n" +
						"--changeset--\n" +
						"--batch--\n";

		mockRequestBody(twoChangeSets);

		final List<RequestBatchEntity> requestEntities = requestEntityExtractor.extractFrom(context);

		assertThat(requestEntities).hasSize(1);
		final RequestBatchEntity requestEntity = requestEntities.get(0);
		assertThat(requestEntity.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertEqualsIgnoringLinebreak(IOUtils.toString(requestEntity.getContent()), PRODUCT_1_EN + PRODUCT_1_DE);
		assertThat(requestEntity.getNumberOfChangeSets()).isEqualTo(2);
	}

	@Test
	public void test2BatchesWith1ChangeSetEach() throws IOException
	{
		when(context.getRequestHeader(CONTENT_TYPE)).thenReturn("multipart/mixed; boundary=batch");

		final String twoBatches =
				"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("en") +
						"\r\n" +
						PRODUCT_1_EN +
						"\r\n" +
						"--changeset--\n" +
						"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("de") +
						"\r\n" +
						PRODUCT_1_DE +
						"\r\n" +
						"--changeset--\n" +
						"--batch--";

		mockRequestBody(twoBatches);

		final List<RequestBatchEntity> requestEntities = requestEntityExtractor.extractFrom(context);

		assertThat(requestEntities).hasSize(2);
		final RequestBatchEntity requestEntity = requestEntities.get(0);
		assertThat(requestEntity.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertEqualsIgnoringLinebreak(IOUtils.toString(requestEntity.getContent()), PRODUCT_1_EN);
		assertThat(requestEntity.getNumberOfChangeSets()).isEqualTo(1);

		final RequestBatchEntity requestEntity2 = requestEntities.get(1);
		assertThat(requestEntity2.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity2.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertEqualsIgnoringLinebreak(IOUtils.toString(requestEntity2.getContent()), PRODUCT_1_DE);
		assertThat(requestEntity2.getNumberOfChangeSets()).isEqualTo(1);
	}

	@Test
	public void testExceptionThrownWhileTryingToParseBatches() throws ODataException, IOException
	{
		when(context.getRequestHeader(CONTENT_TYPE)).thenReturn("multipart/mixed; boundary=batch");
		when(context.getPathInfo()).thenThrow(RuntimeException.class);

		final String twoBatches =
				"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("en") +
						"\r\n" +
						PRODUCT_1_EN +
						"\r\n" +
						"--changeset--\n" +
						"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("de") +
						"\r\n" +
						PRODUCT_1_DE +
						"\r\n" +
						"--changeset--\n" +
						"--batch--";

		mockRequestBody(twoBatches);

		final List<RequestBatchEntity> requestEntities = requestEntityExtractor.extractFrom(context);

		assertThat(requestEntities).hasSize(1);
		final RequestBatchEntity requestEntity = requestEntities.get(0);
		assertThat(requestEntity.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertEqualsIgnoringLinebreak(IOUtils.toString(requestEntity.getContent()), twoBatches);
		assertThat(requestEntity.getNumberOfChangeSets()).isEqualTo(1);
	}

	@Test
	public void testBatchLimitIsExceeded()
	{
		when(configuration.getBatchLimit()).thenReturn(1);
		when(context.getRequestHeader(CONTENT_TYPE)).thenReturn("multipart/mixed; boundary=batch");

		final String twoBatches =
				"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("en") +
						"\r\n" +
						PRODUCT_1_EN +
						"\r\n" +
						"--changeset--\n" +
						"--batch\n" +
						"Content-Type: multipart/mixed; boundary=changeset\n" +
						"\r\n" +
						"--changeset\n" +
						headerForContentLanguage("de") +
						"\r\n" +
						PRODUCT_1_DE +
						"\r\n" +
						"--changeset--\n" +
						"--batch--";

		mockRequestBody(twoBatches);

		final List<RequestBatchEntity> requestEntities = requestEntityExtractor.extractFrom(context);

		assertThat(requestEntities).hasSize(1);
		final RequestBatchEntity requestEntity = requestEntities.get(0);
		assertThat(requestEntity.getMessageId()).isEqualTo(MESSAGE_ID);
		assertThat(requestEntity.getIntegrationObjectType()).isEqualTo(PRODUCT_TYPE);
		assertThat(requestEntity.getContent()).isNull();
	}

	private static String headerForContentLanguage(final String language)
	{
		return "Content-Type: application/http\n" +
				"Content-Transfer-Encoding: binary\n" +
				"\r\n" +
				"POST Products HTTP/1.1\n" +
				"Content-Language: " + language + "\n" +
				"Accept: application/json\n" +
				"Content-Type: application/json\n";
	}

	private static PathInfo pathInfo()
	{
		final PathInfo pathInfo = mock(PathInfo.class);
		when(pathInfo.getServiceRoot()).thenReturn(URI.create("https://localhost:8080/odata2webservices/InboundProduct"));
		when(pathInfo.getRequestUri()).thenReturn(URI.create("https://localhost:8080/odata2webservices/InboundProduct/Products('Staged/Default/1')"));
		final PathSegment pathSegment = mock(PathSegment.class);
		when(pathSegment.getPath()).thenReturn("InboundProduct");
		when(pathInfo.getODataSegments()).thenReturn(Collections.singletonList(pathSegment));
		final PathSegment pathSegment2 = mock(PathSegment.class);
		when(pathSegment2.getPath()).thenReturn("Products('Staged/Default/1')");
		when(pathInfo.getPrecedingSegments()).thenReturn(Collections.singletonList(pathSegment2));
		return pathInfo;
	}

	private void mockRequestBody(final String body)
	{
		when(context.getRequestHeader("integrationMessageId")).thenReturn(MESSAGE_ID);
		when(context.getParameter(SERVICE)).thenReturn(PRODUCT_TYPE);
		when(request.getBody()).thenReturn(toInputStream(body));
	}

	private void assertEqualsIgnoringLinebreak(final String str1, final String str2)
	{
		assertThat(removeCR(str1)).isEqualTo(removeCR(str2));
	}

	private static String removeCR(final String str)
	{
		return str.replace("\r\n", "\n").replace('\r', '\n');
	}
}
