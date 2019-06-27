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
package de.hybris.platform.assistedservicewebservices.controllers;

import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;

import org.junit.Before;


public abstract class AbstractControllerIntegrationIntegrationTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "trusted_client";
	public static final String BASE_SITE_PARAM = "baseSite";
	public static final String BASE_SITE_ID = "testSite";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String OAUTH_USERNAME = "asagent";
	public static final String OAUTH_PASSWORD = "123456";
	public static final String PAGE_SIZE = "pageSize";
	public static final String SORT = "sort";
	public static final String CURRENT_PAGE = "currentPage";

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/impex/asmTestData.impex", "utf-8");
	}

	protected WsSecuredRequestBuilder getWsSecuredRequestBuilder(final String oauthUserName, final String oauthPassword)
	{
		return new WsSecuredRequestBuilder().extensionName(AssistedservicewebservicesConstants.EXTENSIONNAME)
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS).resourceOwner(oauthUserName, oauthPassword)
				.grantResourceOwnerPasswordCredentials();
	}

}
