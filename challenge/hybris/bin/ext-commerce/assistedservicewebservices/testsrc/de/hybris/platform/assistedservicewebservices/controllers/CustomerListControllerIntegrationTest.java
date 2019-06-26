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

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.assistedservicewebservices.dto.CustomerListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.PrincipalWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupListWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ AssistedservicewebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CustomerListControllerIntegrationTest extends AbstractControllerIntegrationIntegrationTest
{
	public static final String OAUTH_NO_CUSTOMER_LISTS_USERNAME = "asagentWithNoCustomerLists";

	protected static final String RECENT_CUSTOMER_LIST_ID = "myRecentCustomerSessions";
	protected static final String RECENT_CUSTOMER_LIST_NAME = "My Recent Customer Sessions";

	@Test
	public void shouldGetCustomerLists()
	{
		final Response response = getCustomerListWSCall(OAUTH_USERNAME, OAUTH_PASSWORD);

		assertResponse(Status.OK, response);
		assertCustomerLists(response);
	}

	protected Response getCustomerListWSCall(final String oauthUserName, final String oauthPassword)
	{
		final Response result = getWsSecuredRequestBuilder(oauthUserName, oauthPassword).path("/customerlists")
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		return result;
	}

	protected void assertCustomerLists(final Response response)
	{
		final List<String> expectedUserIds = Arrays.asList("myRecentCustomerSessions", "instoreCustomers", "bopisCustomers");
		final List<String> resultUids = getResultUidsFromResponse(response);
		assertThat(resultUids.containsAll(expectedUserIds)).isTrue();
		assertThat(resultUids.size() == expectedUserIds.size()).isTrue();
	}

	protected List<String> getResultUidsFromResponse(final Response result)
	{
		final UserGroupListWsDTO entity = result.readEntity(UserGroupListWsDTO.class);
		return entity.getUserGroups()!=null ? entity.getUserGroups().stream().map(PrincipalWsDTO::getUid)
				.collect(Collectors.toList()) : Collections.emptyList();
	}

	@Test
	public void shouldGetEmptyCustomerLists()
	{
		final Response response = getCustomerListWSCall(OAUTH_NO_CUSTOMER_LISTS_USERNAME, OAUTH_PASSWORD);

		assertResponse(Status.OK, response);
		final List<String> resultUids = getResultUidsFromResponse(response);
		assertThat(resultUids.isEmpty()).isTrue();
	}

	@Test
	public void shouldGetValidCustomerListDetails()
	{
		final Response response = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD)
				.path("/customerlists/" + RECENT_CUSTOMER_LIST_ID).queryParam(BASE_SITE_PARAM, BASE_SITE_ID).build()
				.accept(MediaType.APPLICATION_JSON).get();
		response.bufferEntity();

		assertResponse(Status.OK, response);
		final CustomerListWsDTO customerListWsDTO = response.readEntity(CustomerListWsDTO.class);
		Assert.assertNotNull("The customerListWsDTO should not be null", customerListWsDTO);
		Assert.assertEquals("The uid of the customer list is not the expected one", RECENT_CUSTOMER_LIST_ID,
				customerListWsDTO.getUid());
		Assert.assertEquals("The name of the customer list is not the expected one", RECENT_CUSTOMER_LIST_NAME,
				customerListWsDTO.getName());
		Assert.assertTrue("searchBoxEnabled should be true", customerListWsDTO.isSearchBoxEnabled());
	}
}
