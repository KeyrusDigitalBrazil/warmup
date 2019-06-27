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
package de.hybris.platform.cmswebservices.pages.controller;


import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageTypeData;
import de.hybris.platform.cmswebservices.data.PageTypeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageTypeControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String[] CMS2_SUPPORTED_PAGE_TYPES =
		{ //
				CategoryPageModel._TYPECODE, ContentPageModel._TYPECODE, ProductPageModel._TYPECODE
		};

	private static final String ENDPOINT = "/v1/pagetypes";

	@Test
	public void shouldGetAllPageTypes() throws Exception
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(ENDPOINT).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// Assert correct status code
		assertResponse(Status.OK, response);

		// Assert that all cms2 page types are coming out of the API.
		final PageTypeListData entity = response.readEntity(PageTypeListData.class);
		final Collection<String> pageTypeCodes = entity.getPageTypes().stream() //
				.map(PageTypeData::getCode) //
				.collect(Collectors.toList());

		assertThat(pageTypeCodes,
				allOf(hasItem(CategoryPageModel._TYPECODE), //
						hasItem(ContentPageModel._TYPECODE), //
						hasItem(ProductPageModel._TYPECODE)));
	}
}
