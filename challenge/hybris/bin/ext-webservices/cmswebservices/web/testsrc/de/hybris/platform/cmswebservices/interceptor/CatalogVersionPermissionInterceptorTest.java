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
package de.hybris.platform.cmswebservices.interceptor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.catalogversions.service.CatalogVersionPermissionService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import de.hybris.platform.cmswebservices.resolvers.RequestMethodResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;



@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CatalogVersionPermissionInterceptorTest
{
	private static final String VERSION = "staged";
	private static final String CATALOG_ID = "electronicsCatalog";
	private static final String SITE_ID = "electronics";
	private static final String UUID = "electronicsCatalog-staged-componentId";
	private static final String HTTP_REQUEST_GET_METHOD = "GET";
	private static final String HTTP_REQUEST_POST_METHOD = "POST";

	@InjectMocks
	private CatalogVersionPermissionInterceptor interceptor;

	@Mock
	private ObjectFactory<ItemData> itemDataDataFactory;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CatalogVersionPermissionService catalogVersionPermissionService;
	@Spy
	private MockHttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private Object handler;
	@Mock
	private ItemData itemData;
	@Mock
	private CMSItemModel itemModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private RequestMethodResolver requestMethodResolver;

	@Before
	public void setUp()
	{
		when(itemDataDataFactory.getObject()).thenReturn(itemData);
		when(uniqueItemIdentifierService.getItemModel(UUID, CMSItemModel.class)).thenReturn(Optional.of(itemModel));

		when(itemModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogModel.getId()).thenReturn(CATALOG_ID);
		when(catalogVersionModel.getVersion()).thenReturn(VERSION);
	}

	@Test
	public void shouldVerifyPermissionFromUUID() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmswebservicesConstants.URI_UUID, UUID);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);
		when(catalogVersionPermissionService.hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION))
		.thenReturn(true);
		when(requestMethodResolver.resolvePostToGet(request)).thenReturn(HTTP_REQUEST_GET_METHOD);

		interceptor.preHandle(request, response, handler);

		verify(uniqueItemIdentifierService).getItemModel(UUID, CMSItemModel.class);
		verify(catalogVersionPermissionService).hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION);
	}

	@Test
	public void shouldVerifyPermissionFromQueryParams() throws Exception
	{
		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(new HashMap<>());
		when(request.getParameter(CmswebservicesConstants.URI_CATALOG_ID)).thenReturn(CATALOG_ID);
		when(request.getParameter(CmswebservicesConstants.URI_CATALOG_VERSION)).thenReturn(VERSION);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);
		when(catalogVersionPermissionService.hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION))
		.thenReturn(true);
		when(requestMethodResolver.resolvePostToGet(request)).thenReturn(HTTP_REQUEST_GET_METHOD);

		interceptor.preHandle(request, response, handler);

		verifyZeroInteractions(uniqueItemIdentifierService);
		verify(catalogVersionPermissionService).hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION);
	}

	@Test
	public void shouldSkipVerifyPermissionInvalidUUID() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmswebservicesConstants.URI_UUID, UUID);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(uniqueItemIdentifierService.getItemModel(UUID, CMSItemModel.class)).thenReturn(Optional.empty());

		final boolean value = interceptor.preHandle(request, response, handler);

		assertThat(value, is(true));
		verify(uniqueItemIdentifierService).getItemModel(UUID, CMSItemModel.class);
		verifyZeroInteractions(catalogVersionPermissionService);
	}

	@Test
	public void shouldSkipVerifyPermissionNoCatalogAndVersionFound() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmswebservicesConstants.URI_SITE_ID, SITE_ID);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);

		final boolean value = interceptor.preHandle(request, response, handler);

		assertThat(value, is(true));
		verifyZeroInteractions(uniqueItemIdentifierService);
		verifyZeroInteractions(catalogVersionPermissionService);
	}

	@Test
	public void shouldSkipVerifyPermissionNoPathVariablesFound() throws Exception
	{
		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(new HashMap<>());
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);

		final boolean value = interceptor.preHandle(request, response, handler);

		assertThat(value, is(true));
		verifyZeroInteractions(uniqueItemIdentifierService);
		verifyZeroInteractions(catalogVersionPermissionService);
	}

	@Test
	public void shouldVerifyPermissionFromRequestBody() throws Exception
	{
		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(new HashMap<>());
		when(request.getMethod()).thenReturn(HTTP_REQUEST_POST_METHOD);
		request.setContent(
				("{\"" + CmswebservicesConstants.URI_CATALOG_VERSION + "\":\"" + CATALOG_ID + "/" + VERSION + "\"}").getBytes());
		when(uniqueItemIdentifierService.getItemModel(any(), any())).thenReturn(Optional.of(catalogVersionModel));
		when(catalogVersionPermissionService.hasPermission(HTTP_REQUEST_POST_METHOD, response, CATALOG_ID, VERSION))
		.thenReturn(true);
		when(requestMethodResolver.resolvePostToGet(request)).thenReturn(HTTP_REQUEST_POST_METHOD);

		final boolean value = interceptor.preHandle(request, response, handler);

		assertThat(value, is(true));
		verify(uniqueItemIdentifierService).getItemModel(any(), any());
		verify(catalogVersionPermissionService).hasPermission(HTTP_REQUEST_POST_METHOD, response, CATALOG_ID, VERSION);
	}

	@Test
	public void shouldSkipVerifyPermissionNoCatalogVersionFoundInRequestBody() throws Exception
	{
		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(new HashMap<>());
		when(request.getMethod()).thenReturn(HTTP_REQUEST_POST_METHOD);
		request.setContent("{\"key\":\"value\"}".getBytes());
		when(uniqueItemIdentifierService.getItemModel(any(), any())).thenReturn(Optional.empty());

		final boolean value = interceptor.preHandle(request, response, handler);

		assertThat(value, is(true));
		verify(uniqueItemIdentifierService).getItemModel(any(), any());
		verifyZeroInteractions(catalogVersionPermissionService);
	}

}
