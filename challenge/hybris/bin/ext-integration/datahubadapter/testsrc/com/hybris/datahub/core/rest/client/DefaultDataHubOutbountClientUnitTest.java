/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.rest.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import com.hybris.datahub.core.dto.ResultData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.springframework.retry.support.RetryTemplate;

@UnitTest
public class DefaultDataHubOutbountClientUnitTest
{
	private static final String FEED_NAME = "TestFeed";
	private static final String RAW_ITEM_TYPE = "RawProduct";
	private static final String POOL_NAME = "SwimmingPool";
	private Client mockClient = mock(Client.class);
	private WebTarget mockTarget = mock(WebTarget.class);
	private Invocation.Builder mockInvocationBuilder = mock(Invocation.Builder.class);
	private Response mockResponse = mock(Response.class);
	private DataHubOutboundClient outboundClient = new DefaultDataHubOutboundClient(mockClient);


	@Before
	public void setUp()
	{
		((DefaultDataHubOutboundClient) outboundClient).setRetryTemplate(new RetryTemplate());
		when(mockClient.target(anyString())).thenReturn(mockTarget);
		when(mockTarget.request()).thenReturn(mockInvocationBuilder);
		when(mockTarget.request(MediaType.APPLICATION_XML)).thenReturn(mockInvocationBuilder);
		when(mockInvocationBuilder.delete()).thenReturn(mockResponse);
		when(mockInvocationBuilder.post(any(Entity.class))).thenReturn(mockResponse);
		when(mockResponse.getStatus()).thenReturn(200, 404, 200);
		when(mockResponse.readEntity(String.class)).thenReturn("ErrorMessage!");
		when(mockResponse.readEntity(ResultData.class)).thenReturn(new ResultData());
	}

	@Test
	public void testDeleteByFeed() throws Exception
	{
		outboundClient.deleteByFeed(FEED_NAME, RAW_ITEM_TYPE);

		verifyInvocations(1);
	}

	@Test
	public void testDeleteByFeed_WithRetry() throws Exception
	{
		outboundClient.deleteByFeed(FEED_NAME, RAW_ITEM_TYPE);
		outboundClient.deleteByFeed(FEED_NAME, RAW_ITEM_TYPE);

		verifyInvocationsWithRetry(2, 1);
	}

	@Test
	public void testDeleteItem() throws Exception
	{
		outboundClient.deleteItem(POOL_NAME, RAW_ITEM_TYPE, null);

		verifyInvocations(1);
	}

	@Test
	public void testDeleteItem_WithRetry() throws Exception
	{
		outboundClient.deleteItem(POOL_NAME, RAW_ITEM_TYPE, null);
		outboundClient.deleteItem(POOL_NAME, RAW_ITEM_TYPE, null);

		verifyInvocationsWithRetry(2, 1);
	}

	@Test
	public void testExportData() throws Exception
	{
		outboundClient.exportData(new String[]{"id,name", "1,p1", "2,p2"}, FEED_NAME, RAW_ITEM_TYPE);

		verify(mockClient).target(anyString());
		verify(mockTarget).request(anyString());
		verify(mockInvocationBuilder).post(any(Entity.class));
	}

	@Test
	public void testExportData_WithRetry() throws Exception
	{
		outboundClient.exportData(new String[]{"id,name", "1,p1", "2,p2"}, FEED_NAME, RAW_ITEM_TYPE);
		outboundClient.exportData(new String[]{"id,name", "3,p3", "4,p4"}, FEED_NAME, RAW_ITEM_TYPE);

		verify(mockClient, times(2)).target(anyString());
		verify(mockTarget, times(3)).request(anyString());
		verify(mockInvocationBuilder, times(3)).post(any(Entity.class));
	}

	private void verifyInvocations(final int numberOfInvocations)
	{
		verify(mockClient, times(numberOfInvocations)).target(anyString());
		verify(mockTarget, times(numberOfInvocations)).request();
		verify(mockInvocationBuilder, times(numberOfInvocations)).delete();
	}

	private void verifyInvocationsWithRetry(final int numberOfInvocations, final int numberOfRetries)
	{
		verify(mockClient, times(numberOfInvocations)).target(anyString());
		verify(mockTarget, times(numberOfInvocations + numberOfRetries)).request();
		verify(mockInvocationBuilder, times(numberOfInvocations + numberOfRetries)).delete();
	}
}
