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
package de.hybris.platform.cmswebservices.navigationentrytypes.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.NavigationEntryTypeData;
import de.hybris.platform.cmswebservices.data.NavigationEntryTypeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

@NeedsEmbeddedServer(webExtensions =
		{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class NavigationEntryTypeControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/navigationentrytypes";
	
	@Test
	public void testGetAllNavigationEntryTypes() throws CMSItemNotFoundException
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
			.path(ENDPOINT).build() //
			.accept(MediaType.APPLICATION_JSON) //
			.get();
		assertResponse(Response.Status.OK, response);
		final List<NavigationEntryTypeData> navigationEntryTypes = response.readEntity(NavigationEntryTypeListData.class)
				.getNavigationEntryTypes();
		assertThat(navigationEntryTypes.size(), is(3));
	}
}
