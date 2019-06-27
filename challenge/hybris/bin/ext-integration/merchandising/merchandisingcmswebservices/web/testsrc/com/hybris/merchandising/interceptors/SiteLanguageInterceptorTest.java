/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.interceptors;

import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.HandlerMapping;


/**
 * SiteLanguageInterceptorTest is a test class for {@link SiteLanguageInterceptor}.
 */
public class SiteLanguageInterceptorTest
{

	public static final String BASE_SITE_ID = "apparel-uk";

	BaseSiteService mockBaseSiteService;
	CMSAdminSiteService mockAdminSiteService;
	SiteLanguageInterceptor interceptor;

	@Before
	public void setUp()
	{
		mockBaseSiteService = Mockito.mock(BaseSiteService.class);
		mockAdminSiteService = Mockito.mock(CMSAdminSiteService.class);
		interceptor = new SiteLanguageInterceptor();
		interceptor.setBaseSiteService(mockBaseSiteService);
		interceptor.setAdminSiteService(mockAdminSiteService);
	}

	@Test
	public void testFiltering()
	{
		final HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
		final HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
		final Object handler = new Object();

		final Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put(CmswebservicesConstants.URI_SITE_ID, BASE_SITE_ID);
		Mockito.when(mockRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathVariables);

		final boolean preHandleResult = interceptor.preHandle(mockRequest, mockResponse, handler);
		Assert.assertEquals("Expected the prehandle result to be true", Boolean.TRUE.booleanValue(), preHandleResult);

		Mockito.verify(mockBaseSiteService, Mockito.times(1)).setCurrentBaseSite(BASE_SITE_ID, Boolean.FALSE);
		Mockito.verify(mockAdminSiteService, Mockito.times(1)).setActiveSiteForId(BASE_SITE_ID);
	}

	@Test
	public void testGetBaseSiteService() {
		final BaseSiteService configuredBaseSiteService = interceptor.getBaseSiteService();
		Assert.assertNotNull(configuredBaseSiteService);
		Assert.assertEquals("Expected baseSiteService to be injected one", mockBaseSiteService, configuredBaseSiteService);
	}

	@Test
	public void testGetCMSAdminSiteService()
	{
		final CMSAdminSiteService cmsAdminSiteService = interceptor.getAdminSiteService();
		Assert.assertNotNull(cmsAdminSiteService);
		Assert.assertEquals("Expected cmsAdminSiteService to be injected one", mockAdminSiteService, cmsAdminSiteService);
	}
}
