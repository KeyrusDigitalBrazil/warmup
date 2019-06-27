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


import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ODATA_REQUEST;
import static org.apache.olingo.odata2.api.commons.HttpHeaders.CONTENT_LANGUAGE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.InvalidServiceNameException;
import de.hybris.platform.odata2services.odata.ODataRequestEntityExtractor;
import de.hybris.platform.odata2services.odata.ODataWebException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.core.PathInfoImpl;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultODataContextGeneratorUnitTest
{
	private static final String PRODUCT = "Product";
	private static final String SERVICE_ROOT_PREFIX = "/odata2webservices/";
	private static final String SERVICE_NAME = "InboundProduct";
	private static final String SERVICE = "service";
	private static final String ENTITY_TYPE = "entityType";


	@Mock
	private ODataRequestEntityExtractor entityExtractors;
	@Mock
	private ODataServiceFactory serviceFactory;
	@InjectMocks
	private DefaultODataContextGenerator defaultODataContextGenerator;

	@Before
	public void setUp()
	{
		defaultODataContextGenerator.setEntityExtractors(Collections.singletonList(entityExtractors));
	}

	@Test
	public void testGenerateNullPathInfo()
	{
		final ODataRequest odataRequest =  createOdataRequest(null);

		assertThatThrownBy(() -> defaultODataContextGenerator.generate(
				odataRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGenerateEmptyServiceRoot()
	{
		final ODataRequest odataRequest = createOdataRequest(createPathInfoImp("", ""));

		assertThatThrownBy(() -> defaultODataContextGenerator.generate(
				odataRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGenerateNoServiceNameIsProvided()
	{
		final ODataRequest odataRequest = createOdataRequest(createPathInfoImp(""));

		assertThatThrownBy(() -> defaultODataContextGenerator.generate(
				odataRequest)).isInstanceOf(InvalidServiceNameException.class);
	}

	@Test
	public void testGenerateSetsServiceParameter()
	{
		final ODataRequest oDataRequest = createOdataRequest(createPathInfoImp());

		final ODataContext context = defaultODataContextGenerator.generate(oDataRequest);
		assertEquals(SERVICE_NAME, context.getParameter(SERVICE));
	}

	@Test
	public void testGenerateEntityExtractorIsApplicable()
	{
		final ODataRequest oDataRequest = createOdataRequest(createPathInfoImp());
		when(entityExtractors.isApplicable(any(ODataRequest.class))).thenReturn(true);
		when(entityExtractors.extract(any(ODataRequest.class))).thenReturn(PRODUCT);

		final ODataContext context = defaultODataContextGenerator.generate(oDataRequest);

		assertEquals(PRODUCT, context.getParameter(ENTITY_TYPE));
	}

	@Test
	public void testGenerateEntityExtractorNotApplicable()
	{
		final ODataRequest oDataRequest = createOdataRequest(createPathInfoImp());
		when(entityExtractors.isApplicable(any(ODataRequest.class))).thenReturn(false);

		final ODataContext context = defaultODataContextGenerator.generate(oDataRequest);

		assertEquals("", context.getParameter(ENTITY_TYPE));
	}

	@Test
	public void testGenerateSetsODataService() throws ODataException
	{
		final ODataRequest oDataRequest = createOdataRequest(createPathInfoImp());
		when(serviceFactory.createService(any(ODataContext.class))).thenReturn(mock(ODataService.class));

		final ODataContext context = defaultODataContextGenerator.generate(oDataRequest);

		assertNotNull(context.getService());
	}

	@Test
	public void testGenerateCreateServiceThrowsException() throws ODataException
	{
		final ODataRequest oDataRequest = createOdataRequest(createPathInfoImp());
		when(serviceFactory.createService(any(ODataContext.class))).thenThrow(new ODataException());

		assertThatThrownBy(() -> defaultODataContextGenerator.generate(
				oDataRequest)).isInstanceOf(ODataWebException.class);
	}

	@Test
	public void testGenerateSetsOneContentLanguage()
	{
		final ODataRequest odataRequest = createOdataRequest(createPathInfoImp());
		odataRequest.getRequestHeaders().put(CONTENT_LANGUAGE, Collections.singletonList(Locale.ENGLISH.getLanguage()));

		final ODataContext context = defaultODataContextGenerator.generate(odataRequest);

		assertEquals(Collections.singletonList(Locale.ENGLISH.getLanguage()), context.getParameter(CONTENT_LANGUAGE));
	}

	@Test
	public void testGenerateSetsMultipleContentLanguages()
	{
		final ODataRequest odataRequest = createOdataRequest(createPathInfoImp());
		odataRequest.getRequestHeaders().put(CONTENT_LANGUAGE, Arrays.asList(Locale.ENGLISH.getLanguage(), Locale.JAPANESE.getLanguage()));

		final ODataContext context = defaultODataContextGenerator.generate(odataRequest);

		assertEquals(Arrays.asList(Locale.ENGLISH.getLanguage(), Locale.JAPANESE.getLanguage()), context.getParameter(CONTENT_LANGUAGE));
	}

	@Test
	public void testGenerateNoContentLanguage()
	{
		final ODataRequest odataRequest = createOdataRequest(createPathInfoImp());

		final ODataContext context = defaultODataContextGenerator.generate(odataRequest);

		assertNull(context.getParameter(CONTENT_LANGUAGE));
	}

	@Test
	public void testGenerateSetsRequestParameter()
	{
		final ODataRequest oDataRequest = createOdataRequest(createPathInfoImp());

		final ODataContext context = defaultODataContextGenerator.generate(oDataRequest);
		assertEquals(oDataRequest, context.getParameter(ODATA_REQUEST));
	}

	private ODataRequest createOdataRequest(final PathInfo pathInfoImp)
	{
		return ODataRequest.newBuilder()
				.httpMethod("GET")
				.queryParameters(Maps.newHashMap(PRODUCT, null))
				.pathInfo(pathInfoImp)
				.acceptableLanguages(new ArrayList<>()).build();
	}

	private PathInfo createPathInfoImp(final String serviceRoot, final String serviceName)
	{
		final PathInfoImpl pathInfo = new PathInfoImpl();
		pathInfo.setServiceRoot(URI.create(serviceRoot + serviceName));
		return pathInfo;
	}

	private PathInfo createPathInfoImp(final String serviceName)
	{
		return createPathInfoImp(SERVICE_ROOT_PREFIX, serviceName);
	}

	private PathInfo createPathInfoImp()
	{
		return createPathInfoImp(SERVICE_NAME);
	}
}