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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.site.BaseSiteService;

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

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SiteLanguageInterceptorTest
{
	private static final String INVALID = "invalid";
	private static final String SITE_ID = "electronics";

	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private CMSAdminSiteService adminSiteService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private Object handler;

	@InjectMocks
	private SiteLanguageInterceptor interceptor;

	@Test
	public void shouldAddSiteToSession() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmswebservicesConstants.URI_SITE_ID, SITE_ID);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

		interceptor.preHandle(request, response, handler);

		verify(baseSiteService).setCurrentBaseSite(SITE_ID, Boolean.FALSE);
		verify(adminSiteService).setActiveSiteForId(SITE_ID);
	}

	// No exception is thrown when siteId is invalid, but the current session site is set to NULL.
	@Test
	public void shouldNotAddSiteToSession_InvalidSiteId() throws Exception
	{
		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmswebservicesConstants.URI_SITE_ID, INVALID);

		when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

		interceptor.preHandle(request, response, handler);

		verify(baseSiteService).setCurrentBaseSite(INVALID, Boolean.FALSE);
		verify(adminSiteService).setActiveSiteForId(INVALID);
	}

}
