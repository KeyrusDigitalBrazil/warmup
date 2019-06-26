/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.security;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.catalogversioneventhandling.AvailableCatalogVersionsTag;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeDynamicCatalogVersionActivationFilterTest
{
	private static final String CATALOG_VERSIONS_UUID = "catalog_versions_tag";

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CatalogService catalogService;

	@Mock
	private UserService userService;

	@Mock
	private SessionService sessionService;

	@Mock
	private AvailableCatalogVersionsTag availableCatalogVersionsTag;


	private ServletRequest request;


	private ServletResponse response;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	@Spy
	private BackofficeDynamicCatalogVersionActivationFilter testSubject;

	@Before
	public void setUp()
	{
		mockRequest();
		mockResponse();
	}

	@Test
	public void shouldNotReassignCatalogVersionsWhenNoChangesDetected()
	{
		//given
		setUpNoChangesInCatalogVersions();

		//when
		try
		{
			testSubject.doFilter(request, response, filterChain);

			verify(testSubject, never()).setSessionCatalogVersions(any(), any(), any());
		}
		catch (final IOException | ServletException e) 
		{
			fail();
		}
	}

	@Test
	public void shouldReassignCatalogVersionsWhenChangesDetected()
	{
		//given
		setUpChangesInCatalogVersions();

		//when
		try
		{
			testSubject.doFilter(request, response, filterChain);

			verify(testSubject).setSessionCatalogVersions(any(), any(), any());
		}
		catch (final IOException | ServletException e)
		{
			fail();
		}
	}

	@Test
	public void shouldDoNextFilterAlways()
	{
		try
		{
			setUpChangesInCatalogVersions();
			testSubject.doFilter(request, response, filterChain);
			setUpNoChangesInCatalogVersions();
			testSubject.doFilter(request, response, filterChain);

			verify(filterChain, times(2)).doFilter(eq(request), eq(response));
		}
		catch (final IOException | ServletException e)
		{
			fail();
		}
	}

	@Test
	public void shouldAssignAllCatalogVersionsWhenCurrentUserIsAdmin()
	{
		setUpChangesInCatalogVersions();
		final UserModel adminUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(adminUser);
		when(userService.isAdmin(adminUser)).thenReturn(true);

		final Collection<CatalogVersionModel> allVersions = new HashSet<>();
		when(catalogVersionService.getAllCatalogVersions()).thenReturn(allVersions);

		try
		{
			testSubject.doFilter(request, response, filterChain);

			final ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);

			verify(testSubject).setSessionCatalogVersions(any(),any() , captor.capture());
			assertThat(captor.getValue()).isEqualTo(allVersions);
		}
		catch (final IOException | ServletException e)
		{
			fail();
		}
	}

	@Test
	public void shouldSetCurrentCatalogVersionTagInSessionWhenCatalogVersionsReassigned()
	{
		setUpChangesInCatalogVersions();
		try
		{
			testSubject.doFilter(request, response, filterChain);

			verify(sessionService).setAttribute(eq(CATALOG_VERSIONS_UUID),any());
		}
		catch (final IOException | ServletException e)
		{
			fail();
		}
	}

	@Test
	public void shouldAssignReadableAndWritableCatalogVersionsWhenCurrentUserIsNotAnAdmin()
	{
		setUpChangesInCatalogVersions();
		final UserModel notAdminUser = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(notAdminUser);
		when(userService.isAdmin(notAdminUser)).thenReturn(false);

		final HashSet<CatalogVersionModel> readableVersions = new HashSet<>();
		final CatalogVersionModel readableVersion = mock(CatalogVersionModel.class);
		readableVersions.add(readableVersion);
		when(catalogVersionService.getAllReadableCatalogVersions(eq(notAdminUser))).thenReturn(readableVersions);

		final HashSet<CatalogVersionModel> writableVersions = new HashSet<>();
		final CatalogVersionModel writableVersion = mock(CatalogVersionModel.class);
		readableVersions.add(writableVersion);
		when(catalogVersionService.getAllWritableCatalogVersions(eq(notAdminUser))).thenReturn(writableVersions);

		try
		{
			testSubject.doFilter(request, response, filterChain);

			final ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);

			verify(testSubject).setSessionCatalogVersions(any(),any() , captor.capture());
			assertThat(captor.getValue()).contains(readableVersion, writableVersion);
		}
		catch (final IOException | ServletException e)
		{
			fail();
		}
	}

	private void setUpNoChangesInCatalogVersions()
	{
		final UUID tag = UUID.randomUUID();
		when(availableCatalogVersionsTag.getTag()).thenReturn(tag);
		when(sessionService.getAttribute(CATALOG_VERSIONS_UUID)).thenReturn(tag);
	}

	private void setUpChangesInCatalogVersions()
	{
		final UUID globalTag = UUID.randomUUID();
		when(availableCatalogVersionsTag.getTag()).thenReturn(globalTag);

		final UUID sessionTag = UUID.randomUUID();
		when(sessionService.getAttribute(CATALOG_VERSIONS_UUID)).thenReturn(sessionTag);
	}

	private void mockRequest()
	{
		request = mock(ServletRequest.class,
				withSettings().extraInterfaces(HttpServletRequest.class));
	}

	private void mockResponse()
	{
		response = mock(ServletResponse.class,
				withSettings().extraInterfaces(HttpServletResponse.class));
	}
}
