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
package com.hybris.ymkt.recommendationbuffer.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.service.impl.DefaultRecommendationBufferService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRecommendationBufferServiceTest
{
	private static final Date EXPIRY_DATE = new Date(1000); //some old date

	static final String P10 = "0123456789";
	static final String P50 = P10 + P10 + P10 + P10 + P10;
	static final String S254 = Arrays.asList(P50, P50, P50, P50, P50).stream().collect(Collectors.joining(",")); //less than 255
	static final String S255 = Arrays.asList(P50, P50, P50, P50, P50+"X").stream().collect(Collectors.joining(",")); //exactly 255
	static final String S255C = Arrays.asList(S254, "X").stream().collect(Collectors.joining(",")); //comma at 255
	static final String S256C = Arrays.asList(S255, "X").stream().collect(Collectors.joining(",")); //comma at 256
	static final String S305 = Arrays.asList(S254, P50).stream().collect(Collectors.joining(",")); //more than 255
	
	@InjectMocks
	private DefaultRecommendationBufferService recommendationBufferService;

	@Mock
	private SAPRecommendationBufferModel recommendationModel;

	@Before
	public void setUp()
	{
		recommendationBufferService.setEnableRecommendationBuffer(true);
	}

	@Test
	public void testExpiredRecommendationByDate()
	{
		Mockito.when(recommendationModel.getExpiresOn()).thenReturn(EXPIRY_DATE);
		Assert.assertTrue(recommendationBufferService.isRecommendationExpired(recommendationModel));
	}

	@Test
	public void testCutString()
	{
		Assert.assertEquals("", recommendationBufferService.cutTo255(""));
		Assert.assertEquals("a", recommendationBufferService.cutTo255("a"));
		Assert.assertEquals("a,b", recommendationBufferService.cutTo255("a,b"));
		Assert.assertEquals(P50, recommendationBufferService.cutTo255(P50)); 
		Assert.assertEquals(S254, recommendationBufferService.cutTo255(S254)); //less than 255
		Assert.assertEquals(S255, recommendationBufferService.cutTo255(S255)); //exactly 255
		Assert.assertEquals(S254, recommendationBufferService.cutTo255(S255C)); //comma at 255
		Assert.assertEquals(S255, recommendationBufferService.cutTo255(S256C)); //comma at 256
		Assert.assertEquals(S254, recommendationBufferService.cutTo255(S305)); //more than 255
	}
}
