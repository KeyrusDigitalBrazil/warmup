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
package de.hybris.platform.cmssmarteditwebservices.interceptor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.catalogversions.service.CatalogVersionPermissionService;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;
import de.hybris.platform.core.model.user.UserModel;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.HandlerMapping;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CatalogVersionInterceptorTest
{
	private static final String VERSION = "staged";
	private static final String CATALOG_ID = "electronics";
	private static final String HTTP_REQUEST_GET_METHOD = "GET";
	private static final String HTTP_REQUEST_POST_METHOD = "POST";

	@InjectMocks
	private CatalogVersionInterceptor catalogVersionInterceptor;

	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CatalogVersionPermissionService catalogVersionPermissionService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private UserModel userModel;
	@Mock
	private Object handler;

	@Test
	public void shouldAddCatalogVersionToSession() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmssmarteditwebservicesConstants.URI_CATALOG_ID, CATALOG_ID);
		pathVariables.put(CmssmarteditwebservicesConstants.URI_VERSION_ID, VERSION);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);

		catalogVersionInterceptor.preHandle(request, response, handler);

		verify(cmsAdminSiteService).setActiveCatalogVersion(CATALOG_ID, VERSION);
		verify(catalogVersionService).setSessionCatalogVersion(CATALOG_ID, VERSION);
	}

	@Test
	public void shouldNotAddCatalogVersionToSession_IncompleteURI() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmssmarteditwebservicesConstants.URI_CATALOG_ID, CATALOG_ID);
		pathVariables.put(CmssmarteditwebservicesConstants.URI_VERSION_ID, null);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);

		catalogVersionInterceptor.preHandle(request, response, handler);

		verifyZeroInteractions(cmsAdminSiteService);
	}

	@Test
	public void shouldNotAddCatalogVersionToSession_EmptyURI() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmssmarteditwebservicesConstants.URI_CATALOG_ID, null);
		pathVariables.put(CmssmarteditwebservicesConstants.URI_VERSION_ID, null);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);

		catalogVersionInterceptor.preHandle(request, response, handler);

		verifyZeroInteractions(cmsAdminSiteService);
	}

	@Test
	public void shouldFail_NoReadPermissionToCatalogVersion() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmssmarteditwebservicesConstants.URI_CATALOG_ID, CATALOG_ID);
		pathVariables.put(CmssmarteditwebservicesConstants.URI_VERSION_ID, VERSION);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);
		when(catalogVersionPermissionService.hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION))
		.thenReturn(false);

		catalogVersionInterceptor.preHandle(request, response, handler);

		verify(catalogVersionService).setSessionCatalogVersion(CATALOG_ID, VERSION);
		verify(cmsAdminSiteService).setActiveCatalogVersion(CATALOG_ID, VERSION);
		verify(catalogVersionPermissionService).hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION);
	}

	@Test
	public void shouldFail_NoWritePermissionToCatalogVersion() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmssmarteditwebservicesConstants.URI_CATALOG_ID, CATALOG_ID);
		pathVariables.put(CmssmarteditwebservicesConstants.URI_VERSION_ID, VERSION);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);
		when(request.getMethod()).thenReturn(HTTP_REQUEST_POST_METHOD);
		when(catalogVersionPermissionService.hasPermission(HTTP_REQUEST_POST_METHOD, response, CATALOG_ID, VERSION))
		.thenReturn(false);

		catalogVersionInterceptor.preHandle(request, response, handler);

		verify(catalogVersionService).setSessionCatalogVersion(CATALOG_ID, VERSION);
		verify(cmsAdminSiteService).setActiveCatalogVersion(CATALOG_ID, VERSION);
		verify(catalogVersionPermissionService).hasPermission(HTTP_REQUEST_POST_METHOD, response, CATALOG_ID, VERSION);
	}
}