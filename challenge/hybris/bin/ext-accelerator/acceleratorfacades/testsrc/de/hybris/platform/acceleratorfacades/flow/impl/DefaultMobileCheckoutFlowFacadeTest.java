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
package de.hybris.platform.acceleratorfacades.flow.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.device.impl.DefaultDeviceDetectionFacade;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;


/**
 * Test case when user agent is 'a mobile'
 */
@IntegrationTest
public class DefaultMobileCheckoutFlowFacadeTest extends DefaultCheckoutFlowFacadeTest
{
	@Resource
	private CheckoutFlowFacade checkoutFlowFacade;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private DefaultDeviceDetectionFacade deviceDetectionFacade;

	@Mock
	private HttpServletRequest request;

	@Mock
	private SiteConfigService mockSiteConfigService;

	@Resource
	private SiteConfigService siteConfigService;

	@Resource
	private UserService userService;

	@Resource
	@Override
	@Before
	public void prepareRequest() throws ImpExException
	{
		importCsv("/acceleratorfacades/test/testDefaultFlow.impex", "utf-8");
		BDDMockito
				.given(request.getHeader("User-Agent"))
				.willReturn(
						"Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16");
		BDDMockito.given(mockSiteConfigService.getString("uiexperience.level.supported", "")).willReturn("");
		deviceDetectionFacade.setSiteConfigService(mockSiteConfigService);
		deviceDetectionFacade.initializeRequest(request);
	}

	@After
	public void tearDown() throws Exception
	{
		deviceDetectionFacade.setSiteConfigService(siteConfigService);
	}
}
