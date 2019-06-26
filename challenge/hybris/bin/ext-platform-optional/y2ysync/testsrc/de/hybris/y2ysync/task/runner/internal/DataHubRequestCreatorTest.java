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
package de.hybris.y2ysync.task.runner.internal;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.y2ysync.model.media.SyncImpExMediaModel;
import de.hybris.y2ysync.task.dao.Y2YSyncDAO;
import de.hybris.y2ysync.task.runner.Y2YSyncContext;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DataHubRequestCreatorTest
{
	private final static String SYNC_EXECUTION_ID = "testExecutionId";
	private final static String HOME_URL = "http://localhost:9001";
	private final static String CONSUME_CHANGES_WEBROOT = "/y2ysync";
	private final static String DATAHUB_URI = "/datahub-webapp/v1/data-feeds/y2ysync";

	private static final String FEED_NAME = "Y2YSYNC_FEED";
	private static final String POOL_NAME = "Y2YSYNC_POOL";
	private static final String TARGET_SYSTEMS = "";

	private DataHubRequestCreator requestCreator;
	private final RestTemplate restTemplate = getRestTemplate();

	@Mock
	private Y2YSyncDAO dao;
	@Mock
	private SyncImpExMediaModel m1, m2, m3, m4, m5;
	@Mock
	private ComposedTypeModel productType, titleType;
	private Y2YSyncContext ctx;

	@Before
	public void setUp() throws Exception
	{
		requestCreator = new DataHubRequestCreator()
		{
			@Override
			String getHomeUrl()
			{
				return HOME_URL;
			}

			@Override
			String getY2YSyncWebRoot()
			{
				return HOME_URL + CONSUME_CHANGES_WEBROOT;
			}

			@Override
			protected String getDataHubUserName()
			{
				return "test_admin";
			}

			@Override
			protected String getDataHubPassword()
			{
				return "test_nimda";
			}


		};
		requestCreator.setY2YSyncDAO(dao);
		requestCreator.setRestTemplate(restTemplate);

		given(productType.getCode()).willReturn("Product");
		given(titleType.getCode()).willReturn("Title");

		given(m1.getImpexHeader()).willReturn("INSERT_UPDATE Product;code[unique=true];description");
		given(m1.getDataHubColumns()).willReturn("code;description");
		given(m1.getSyncType()).willReturn(productType);
		given(m1.getURL()).willReturn(HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m1");

		given(m2.getImpexHeader()).willReturn("INSERT_UPDATE Product;code[unique=true];description");
		given(m2.getDataHubColumns()).willReturn("code;description");
		given(m2.getSyncType()).willReturn(productType);
		given(m2.getURL()).willReturn(HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m2");

		given(m3.getImpexHeader()).willReturn("DELETE Product;code[unique=true]");
		given(m3.getDataHubColumns()).willReturn("code");
		given(m3.getSyncType()).willReturn(productType);
		given(m3.getURL()).willReturn(HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m3");

		given(m4.getImpexHeader()).willReturn("INSERT_UPDATE Title;code[unique=true];");
		given(m4.getDataHubColumns()).willReturn("code");
		given(m4.getSyncType()).willReturn(titleType);
		given(m4.getURL()).willReturn(HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m4");

		given(m5.getImpexHeader()).willReturn("INSERT_UPDATE Title;code[unique=true];");
		given(m5.getDataHubColumns()).willReturn("code");
		given(m5.getSyncType()).willReturn(titleType);
		given(m5.getURL()).willReturn(HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m5");

		given(dao.findSyncMediasBySyncCronJob(SYNC_EXECUTION_ID)).willReturn(Lists.newArrayList(m1, m2, m3, m4, m5));

		ctx = Y2YSyncContext.builder().withSyncExecutionId(SYNC_EXECUTION_ID).withUri(DATAHUB_URI).withFeed(FEED_NAME)
				.withPool(POOL_NAME).withAutoPublishTargetSystems(TARGET_SYSTEMS).build();
	}

	@Test
	public void shouldSuccessfullySendSyncMediaUrlsGrouppedByImpExHeaderAsJSON() throws Exception
	{
		// given
		final String expectedJson = getExpectedJson();
		final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
		server.expect(requestTo("/datahub-webapp/v1/data-feeds/y2ysync")) //
				.andExpect(method(HttpMethod.POST)) //
				.andExpect(content().contentType("application/json;charset=UTF-8")) //
				.andExpect(content().string(new JSONMatcher(expectedJson))) //
				.andRespond(withSuccess()); //

		// when
		requestCreator.sendRequest(ctx);

		// then
		server.verify();
	}


	@Test
	public void shouldThrowAnExceptionWhenRemoteDataHubControllerRespondsWithNot200OK() throws Exception
	{
		try
		{
			// when
			final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
			server.expect(requestTo("/datahub-webapp/v1/data-feeds/y2ysync")) //
					.andRespond(withBadRequest()); //
			requestCreator.sendRequest(ctx);
			fail("Should throw IllegalStateException");
		}
		catch (final HttpStatusCodeException e)
		{
			// then fine
		}

	}

	private RestTemplate getRestTemplate()
	{
		final RestTemplate template = new RestTemplate();
		template.setMessageConverters(Lists.newArrayList(new MappingJackson2HttpMessageConverter()));

		return template;
	}

	private String getExpectedJson()
	{
		return "{\"syncExecutionId\":\"" + SYNC_EXECUTION_ID + "\",\"sourcePlatformUrl\":\"" + HOME_URL + CONSUME_CHANGES_WEBROOT
				+ "\",\"dataStreams\":[{\"itemType\":\"Product\",\"dataHubType\":\"\",\"columns\":\"code;description\",\"delete\":false,\"urls\":[\""
				+ HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m1\",\"" + HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m2\"]},"
				+ "{\"itemType\":\"Product\",\"dataHubType\":\"\",\"columns\":\"code\",\"delete\":false,\"urls\":[\"" + HOME_URL
				+ CONSUME_CHANGES_WEBROOT
				+ "/medias/m3\"]},{\"itemType\":\"Title\",\"dataHubType\":\"\",\"columns\":\"code\",\"delete\":false,\"urls\":[\""
				+ HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m4\",\"" + HOME_URL + CONSUME_CHANGES_WEBROOT + "/medias/m5\"]}]"
				+ ",\"pool\":\"" + POOL_NAME + "\",\"feed\":\"" + FEED_NAME + "\",\"autoPublishTargetSystems\":\"" + TARGET_SYSTEMS
				+ "\"}";
	}

	private class JSONMatcher extends BaseMatcher
	{
		private final String expectedJSON;

		public JSONMatcher(final String expectedJson)
		{
			this.expectedJSON = expectedJson;
		}

		@Override
		public boolean matches(final Object o)
		{
			if (!(o instanceof String))
			{
				return false;
			}
			try
			{
				JSONAssert.assertEquals(expectedJSON, (String) o, false);
			}
			catch (final Exception exception)
			{
				return false;
			}
			return true;
		}

		@Override
		public void describeTo(final Description description)
		{
			//Should be documented
		}
	}
}
