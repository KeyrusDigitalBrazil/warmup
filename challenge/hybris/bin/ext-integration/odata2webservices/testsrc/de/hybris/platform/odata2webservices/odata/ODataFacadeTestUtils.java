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

import static de.hybris.platform.odata2services.odata.content.ODataBatchBuilder.BATCH_BOUNDARY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.ODataContextGenerator;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder;
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;

public class ODataFacadeTestUtils
{
	static final String SERVICE_NAME = "MyProduct";
	static final String PRODUCTS_ENTITYSET = "Products";
	static final String ERROR_CODE = "error.code";
	private static final String METADATA = "$metadata";

	private ODataFacadeTestUtils()
	{
		// non-instantiable
	}


	static ProductModel productModel(final String productCode)
	{
		final ProductModel productModel = new ProductModel();
		productModel.setCode(productCode);
		return productModel;
	}

	static String catalogVersionContent(final String catalogId, final String catalogVersion)
	{
		return JsonBuilder.json()
				.withField("version", catalogVersion)
				.withField("catalog", JsonBuilder.json().withId(catalogId))
				.build();
	}

	public static ODataRequest oDataPostRequest(final String serviceName, final String entityType, final String content, final String contentType)
	{
		return oDataPostRequest(serviceName, entityType, content, Locale.ENGLISH, contentType);
	}

	static ODataRequest oDataPostRequest(final String serviceName, final String entityType, final String content, final Locale locale, final String contentType)
	{
		return oDataPostRequest(serviceName, entityType, content, locale, locale, contentType);
	}

	static ODataRequest oDataPostRequest(final String serviceName, final String entityType, final String content, final Locale contentLanguage, final Locale acceptLanguage, final String contentType)
	{
		return postRequestBuilder(serviceName, entityType, contentType)
				.withContentLanguage(contentLanguage)
				.withAcceptLanguage(acceptLanguage)
				.withBody(content)
				.withContentType(contentType)
				.build();
	}

	static ODataRequestBuilder postRequestBuilder(final String serviceName, final String entityType, final String contentType)
	{
		return ODataRequestBuilder.oDataPostRequest()
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(serviceName)
						.withEntitySet(entityType))
				.withContentType(contentType)
				.withAccepts(APPLICATION_JSON_VALUE);
	}

	static ODataRequest batchODataPostRequest(final String serviceName, final String content)
	{
		return batchPostRequestBuilder(serviceName)
				.withBody(content, StandardCharsets.UTF_8)
				.build();
	}

	static ODataRequestBuilder batchPostRequestBuilder(final String serviceName)
	{
		return ODataRequestBuilder.oDataPostRequest()
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(serviceName)
						.withRequestPath("$batch"))
				.withContentType("multipart/mixed; boundary=" + BATCH_BOUNDARY)
				.withAccepts(APPLICATION_JSON_VALUE);
	}

	static ODataRequest oDataGetRequest(final String serviceName, final String entityType, final String integrationKey, final Locale locale)
	{
		return oDataGetRequest(serviceName, entityType, locale, StringUtils.EMPTY, integrationKey);
	}

	static ODataRequest oDataGetRequest(final String serviceName, final String entityType, final Locale locale, final String navigationSegment, final String... integrationKeys)
	{
		return ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withAcceptLanguage(locale)
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(serviceName)
						.withEntitySet(entityType)
						.withEntityKeys(integrationKeys)
						.withNavigationSegment(navigationSegment))
				.build();
	}

	static ODataRequest oDataGetMetadataRequest(final String serviceName, final String... entityTypes)
	{
		return ODataRequestBuilder.oDataGetRequest()
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(serviceName)
						.withRequestPath(METADATA))
				.withAccepts(APPLICATION_XML_VALUE)
				.withParameters(entityTypes)
				.build();
	}

	static ODataRequest createGetEntitySetPathInfo(final String serviceName, final String entitySetName, final Map<String, String> queryParams, final Locale locale)
	{
		return createGetEntitySetPathInfo(serviceName, entitySetName, queryParams, locale, StringUtils.EMPTY);
	}

	static ODataRequest createGetEntitySetPathInfo(final String serviceName, final String entitySetName, final Map<String, String> queryParams, final Locale locale, final String navigationElement)
	{
		return ODataRequestBuilder.oDataGetRequest()
				.withAccepts(APPLICATION_JSON_VALUE)
				.withAcceptLanguage(locale)
				.withParameters(queryParams)
				.withPathInfo(PathInfoBuilder.pathInfo()
						.withServiceName(serviceName)
						.withEntitySet(entitySetName)
						.withNavigationSegment(navigationElement))
				.build();
	}

	static ODataResponse handleRequest(final ODataFacade facade, final Locale locale, final String content)
	{
		return handleRequest(facade, oDataPostRequest(SERVICE_NAME, PRODUCTS_ENTITYSET, content, locale, APPLICATION_JSON_VALUE));
	}

	static ODataResponse handleRequest(final ODataFacade facade, final ODataRequest oDataRequest)
	{
		final ODataContext context = createContext(oDataRequest);
		return facade.handlePost(context);
	}

	public static ODataContext createContext(final ODataRequestBuilder builder)
	{
		return createContext(builder.build());
	}

	public static ODataContext createContext(final ODataRequest request)
	{
		final ODataContextGenerator generator = Registry.getApplicationContext().getBean("oDataContextGenerator", ODataContextGenerator.class);
		return generator.generate(request);
	}

	static void assertBadRequestWithErrorCode(final String expectedErrorCode, final ODataResponse response)
	{
		ODataAssertions.assertThat(response)
				.hasStatus(HttpStatusCodes.BAD_REQUEST)
				.jsonBody()
				.hasPathWithValue(ERROR_CODE, expectedErrorCode);
	}
}
