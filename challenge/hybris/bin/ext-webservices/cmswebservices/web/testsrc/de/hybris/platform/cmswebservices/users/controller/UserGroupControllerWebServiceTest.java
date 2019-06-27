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
package de.hybris.platform.cmswebservices.users.controller;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.UserDataWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Locale;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@NeedsEmbeddedServer(webExtensions =
		{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class UserGroupControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String USERS_ENDPOINT = "/v1/users";
	private static final String CMS_ITEM_NOT_FOUND_ERROR = "CMSItemNotFoundError";

	private static final String CMSMANAGER_UID = "cmsmanager";
	private static final String INVALID_USER_UID = "invalid";

	private static final String EN = Locale.ENGLISH.toString();
	private static final String FR = Locale.FRENCH.toString();

	@Test
	public void givenValidUserId_WhenUserEndpointIsCalled_ThenItReturnsTheUserData()
	{
		// GIVEN

		// WHEN
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(USERS_ENDPOINT) //
				.path(CMSMANAGER_UID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.OK, response);

		final UserDataWsDTO user = response.readEntity(UserDataWsDTO.class);
		assertThat(user,
				allOf(
						hasProperty("uid", equalTo(CMSMANAGER_UID)),
						hasProperty("readableLanguages", containsInAnyOrder(EN, FR)),
						hasProperty("writeableLanguages", containsInAnyOrder(EN, FR))
				));
	}

	@Test
	public void givenInvalidId_WhenUserEndpointIsCalled_ThenItReturnsACMSItemNotFoundError()
	{
		// GIVEN

		// WHEN
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(USERS_ENDPOINT) //
				.path(INVALID_USER_UID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		// THEN
		assertResponse(Response.Status.NOT_FOUND, response);

		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertThat(errors.getErrors(), hasSize(1));
		assertThat(errors.getErrors().get(0).getType(), equalTo(CMS_ITEM_NOT_FOUND_ERROR));
		assertThat(errors.getErrors().get(0).getMessage(), equalTo(String.format("Cannot find user with uid [%s]", INVALID_USER_UID)));
	}
}
