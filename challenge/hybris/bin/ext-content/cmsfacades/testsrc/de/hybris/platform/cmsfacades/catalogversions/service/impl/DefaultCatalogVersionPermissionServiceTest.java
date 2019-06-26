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
package de.hybris.platform.cmsfacades.catalogversions.service.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCatalogVersionPermissionServiceTest
{
	private static final String VERSION = "staged";
	private static final String CATALOG_ID = "electronics";
	private static final String HTTP_REQUEST_GET_METHOD = "GET";
	private static final String HTTP_REQUEST_POST_METHOD = "POST";
	private static final int HTTP_RESPONSE_FORBIDDEN = 403;

	@InjectMocks
	private DefaultCatalogVersionPermissionService permissionService;

	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private UserService userService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private UserModel userModel;

	@Before
	public void setUp()
	{
		when(userService.getCurrentUser()).thenReturn(userModel);
		when(userService.isAnonymousUser(userModel)).thenReturn(false);
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION)).thenReturn(catalogVersionModel);
	}

	@Test
	public void shouldPass_HasReadPermissionToCatalogVersion() throws Exception
	{
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);
		when(catalogVersionService.canRead(catalogVersionModel, userModel)).thenReturn(true);

		final boolean hasPermission = permissionService.hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION);

		assertThat(hasPermission, is(true));
		verifyZeroInteractions(response);
	}

	@Test
	public void shouldFail_NoReadPermissionToCatalogVersion() throws Exception
	{
		when(request.getMethod()).thenReturn(HTTP_REQUEST_GET_METHOD);
		when(catalogVersionService.canRead(catalogVersionModel, userModel)).thenReturn(false);

		final boolean hasPermission = permissionService.hasPermission(HTTP_REQUEST_GET_METHOD, response, CATALOG_ID, VERSION);

		assertThat(hasPermission, is(false));
		verify(response).sendError(eq(HTTP_RESPONSE_FORBIDDEN), anyString());
	}

	@Test
	public void shouldFail_NoWritePermissionToCatalogVersion() throws Exception
	{
		when(request.getMethod()).thenReturn(HTTP_REQUEST_POST_METHOD);
		when(catalogVersionService.canWrite(catalogVersionModel, userModel)).thenReturn(false);

		final boolean hasPermission = permissionService.hasPermission(HTTP_REQUEST_POST_METHOD, response, CATALOG_ID, VERSION);

		assertThat(hasPermission, is(false));
		verify(response).sendError(eq(HTTP_RESPONSE_FORBIDDEN), anyString());
	}

	@Test
	public void shouldPass_AnonymousUser() throws Exception
	{
		when(userService.isAnonymousUser(userModel)).thenReturn(true);

		final boolean hasPermission = permissionService.hasPermission(HTTP_REQUEST_POST_METHOD, response, CATALOG_ID, VERSION);

		assertThat(hasPermission, is(true));
		verifyZeroInteractions(catalogVersionService);
		verifyZeroInteractions(response);
	}

}
