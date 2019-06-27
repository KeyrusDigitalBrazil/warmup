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
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.assistedservicewebservices.dto.CustomerSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.PrincipalWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;


@NeedsEmbeddedServer(webExtensions =
{ AssistedservicewebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CustomersControllerIntegrationTest extends AbstractControllerIntegrationIntegrationTest
{
	public static final String CUSTOMER_SEARCH_URI = "/customers/search";
	public static final String CUSTOMER_LIST_PARAM = "customerListId";
	public static final String DUMMY_USER_UID = "dummyuser";
	public static final String TEMP_CUSTOMER_LIST_ID = "bopisCustomers";
	public static final String[] DUMMY_USERS =
	{ DUMMY_USER_UID + 1, DUMMY_USER_UID + 2, DUMMY_USER_UID + 3 };
	public static final String NOT_AGENT = "notasagent";

	@Test
	public void getCustomersByUid()
	{
		//given
		//when
		final Response response = performGetCustomersCall(DUMMY_USER_UID, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final List<String> results = getCustomerNamesFromResponse(response);
		Assert.assertEquals(DUMMY_USERS.length, results.size());
	}

	@Test
	public void getFirstTwoCustomersInDescendingOrderSortedByName()
	{
		//given
		final int pageSize = 2;
		//when
		final Response response = performGetCustomersCall(DUMMY_USER_UID, AssistedserviceservicesConstants.SORT_BY_NAME_DESC,
				String.valueOf(pageSize), StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final List<String> results = getCustomerNamesFromResponse(response);
		Assert.assertEquals(pageSize, results.size());
		Assert.assertThat(results, contains(DUMMY_USERS[2], DUMMY_USERS[1]));
	}

	@Test
	public void getCustomerFromThirdPageSortedByUidInAscendingOrder()
	{
		//given
		final int pageSize = 1;
		final int page = 2;
		//when
		final Response response = performGetCustomersCall(DUMMY_USER_UID, AssistedserviceservicesConstants.SORT_BY_UID_ASC,
				String.valueOf(pageSize), String.valueOf(page));
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final List<String> results = getCustomerNamesFromResponse(response);
		Assert.assertEquals(pageSize, results.size());
		Assert.assertThat(results, contains(DUMMY_USERS[page]));
	}

	@Test
	public void getCustomersByNotExistingUid()
	{
		//given
		final String notExistingCustomerUid = "notExistingCustomer@example.com";
		//when
		final Response response = performGetCustomersCall(notExistingCustomerUid, StringUtils.EMPTY, StringUtils.EMPTY,
				StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CustomerSearchPageWsDTO results = response.readEntity(CustomerSearchPageWsDTO.class);
		Assert.assertTrue(CollectionUtils.isEmpty(results.getEntries()));
	}

	@Test
	public void shouldGetAllCustomerByEmptyCriteria()
	{
		//given
		//when
		final Response response = performGetCustomersCall(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
				StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CustomerSearchPageWsDTO results = response.readEntity(CustomerSearchPageWsDTO.class);
		Assert.assertTrue(CollectionUtils.isNotEmpty(results.getEntries()));
	}

	@Test
	public void shouldGetCustomerWithOrder()
	{
		final Response get = performGetCustomersWithDifferentQueries(
				ImmutableMap.of(BASE_SITE_PARAM, BASE_SITE_ID, "orderId", "order1"));

		assertResponse(Response.Status.OK, get);
		final CustomerSearchPageWsDTO entity = get.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains("user1")).isTrue();
	}

	@Test
	public void shouldNotGetCustomerWithWrongOrder()
	{
		// call with wrong order and baseSite
		final Response get = performGetCustomersWithDifferentQueries(
				ImmutableMap.of(BASE_SITE_PARAM, BASE_SITE_ID, "orderId", "wrong_order"));
		assertResponse(Status.BAD_REQUEST, get);
	}

	@Test
	public void getCustomersByEmptyQuery()
	{
		//when
		final Response response = performGetCustomersCall(null, null, null, null);
		//then
		final List<String> results = getCustomerNamesFromResponse(response);
		assertThat(results.size() >= DUMMY_USERS.length).isTrue();
	}

	@Test
	public void shouldNotGetCustomersByUnknownCustomerListId()
	{
		// default call
		final Response simpleGetResponce = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(CUSTOMER_LIST_PARAM, "someID").queryParam(BASE_SITE_PARAM, BASE_SITE_ID).build()
				.accept(MediaType.APPLICATION_JSON).get();
		simpleGetResponce.bufferEntity();

		assertResponse(Status.BAD_REQUEST, simpleGetResponce);
	}

	@Test
	public void shouldGetCustomersByCustomerListId()
	{
		// default call
		final Response simpleGetResponce = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(CUSTOMER_LIST_PARAM, TEMP_CUSTOMER_LIST_ID).queryParam(BASE_SITE_PARAM, BASE_SITE_ID).build()
				.accept(MediaType.APPLICATION_JSON).get();
		simpleGetResponce.bufferEntity();

		assertResponse(Status.OK, simpleGetResponce);
		final CustomerSearchPageWsDTO entity = simpleGetResponce.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains("user1")).isTrue();
		assertThat(uids.contains("user2")).isTrue();
	}

	@Test
	public void shouldGetCustomersByCustomerListIdWithPaging()
	{
		// call with pagination and default sorting
		final Response getWithPaging = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(CUSTOMER_LIST_PARAM, TEMP_CUSTOMER_LIST_ID).queryParam(BASE_SITE_PARAM, BASE_SITE_ID)
				.queryParam(PAGE_SIZE, "1").queryParam(CURRENT_PAGE, "0").build().accept(MediaType.APPLICATION_JSON).get();
		getWithPaging.bufferEntity();
		assertResponse(Status.OK, getWithPaging);
		final CustomerSearchPageWsDTO entity = getWithPaging.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains("user2")).isTrue();
		assertThat(entity.getPagination().getPageSize() == NumberUtils.INTEGER_ONE).isTrue();
		assertThat(entity.getPagination().getSort().equalsIgnoreCase("byOrderDateDesc")).isTrue();
	}

	@Test
	public void shouldGetCustomersByCustomerListIdWithPagingAndSort()
	{
		// call with pagination and another sorting
		final Response getWithPagingAndSorting = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD)
				.path(CUSTOMER_SEARCH_URI).queryParam(CUSTOMER_LIST_PARAM, TEMP_CUSTOMER_LIST_ID)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).queryParam(PAGE_SIZE, "1").queryParam(CURRENT_PAGE, "0")
				.queryParam(SORT, "byOrderDateAsc").build().accept(MediaType.APPLICATION_JSON).get();
		getWithPagingAndSorting.bufferEntity();
		assertResponse(Status.OK, getWithPagingAndSorting);
		final CustomerSearchPageWsDTO entity = getWithPagingAndSorting.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains("user1")).isTrue();
		assertThat(entity.getPagination().getPageSize() == NumberUtils.INTEGER_ONE).isTrue();
		assertThat(entity.getPagination().getSort().equalsIgnoreCase("byOrderDateAsc")).isTrue();
	}

	@Test
	public void getCustomersByNotAsAgent()
	{
		final Response result = getWsSecuredRequestBuilder(NOT_AGENT, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).queryParam("query", "test").build().accept(MediaType.APPLICATION_JSON)
				.get();
		assertResponse(Status.FORBIDDEN, result);
	}

	protected Response performGetCustomersWithDifferentQueries(final Map<String, String> params)
	{
		WsSecuredRequestBuilder builder = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI);
		final Set<Map.Entry<String, String>> entries = params.entrySet();
		for (final Map.Entry<String, String> e : entries)
		{
			builder = builder.queryParam(e.getKey(), e.getValue());
		}
		final Response response = builder.build().accept(MediaType.APPLICATION_JSON).get();
		response.bufferEntity();
		return response;
	}

	protected Response performGetCustomersCall(final String searchCritera, final String sort, final String pageSize,
			final String currentPage)
	{
		final String queryParamKey = "query";
		final Response result = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).queryParam(queryParamKey, searchCritera).queryParam(SORT, sort)
				.queryParam(PAGE_SIZE, pageSize).queryParam(CURRENT_PAGE, currentPage).build().accept(MediaType.APPLICATION_JSON)
				.get();
		result.bufferEntity();
		return result;
	}

	protected List<String> getCustomerNamesFromResponse(final Response response)
	{
		final CustomerSearchPageWsDTO results = response.readEntity(CustomerSearchPageWsDTO.class);
		return results.getEntries().stream().map(PrincipalWsDTO::getName).collect(Collectors.toList());
	}
}