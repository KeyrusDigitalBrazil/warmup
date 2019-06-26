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
package de.hybris.platform.cmswebservices.usergroups.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.UserGroupListWsDTO;
import de.hybris.platform.cmswebservices.dto.UserGroupWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class UserGroupControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String ENDPOINT = "/v1/usergroups";

	private static final String CMS_ITEM_NOT_FOUND_ERROR = "CMSItemNotFoundError";
	private static final String INVALID = "invalid";

	@Test
	public void shouldGetOneUserGroupById()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(ENDPOINT) //
				.path("cmsmanagergroup").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final UserGroupWsDTO userGroup = response.readEntity(UserGroupWsDTO.class);
		assertThat(userGroup,
				allOf(hasProperty("uid", equalTo("cmsmanagergroup")), hasProperty("name", hasEntry("en", "CMS Manager Group"))));
	}

	@Test
	public void shouldFailGetUserGroupByIdWithInvalidId()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(ENDPOINT) //
				.path(INVALID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(1));
		assertThat(errors.getErrors().get(0).getType(), equalTo(CMS_ITEM_NOT_FOUND_ERROR));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo("UserGroup with id [" + INVALID + "] is not found"));
	}

	@Test
	public void shouldSearchForUserGroupsContainingUS()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(ENDPOINT) //
				.queryParam("mask", "us") //
				.queryParam("pageSize", "5") //
				.queryParam("currentPage", 0) //
				.queryParam("sort", "name").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final UserGroupListWsDTO userGroups = response.readEntity(UserGroupListWsDTO.class);

		assertThat(userGroups.getPagination().getCount(), is(1));
		assertThat(userGroups.getPagination().getTotalCount(), is(1L));
		assertThat(userGroups.getPagination().getPage(), is(0));

		final UserGroupWsDTO userGroup = userGroups.getUserGroups().get(0);
		assertThat(userGroup, allOf(hasProperty("uid", equalTo("cmsmanagergroup-us")), //
				hasProperty("name", hasEntry("en", "US-CMS Manager Group"))));
	}

	@Test
	public void shouldReturnEmptyListWhenSearchingForUserGroups()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(ENDPOINT) //
				.queryParam("mask", INVALID) //
				.queryParam("pageSize", "5") //
				.queryParam("currentPage", 0) //
				.queryParam("sort", "name").build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Response.Status.OK, response);

		final UserGroupListWsDTO userGroups = response.readEntity(UserGroupListWsDTO.class);

		assertThat(userGroups.getPagination().getCount(), is(0));
		assertThat(userGroups.getPagination().getTotalCount(), is(0L));
		assertThat(userGroups.getPagination().getPage(), is(0));
		assertThat(userGroups.getUserGroups(), hasSize(0));
	}

}
