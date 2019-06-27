/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayService;
import de.hybris.platform.chinesepspalipayservices.alipay.HttpProtocolHandler;
import de.hybris.platform.chinesepspalipayservices.data.HttpResponse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class DefaultAlipayResponseValidationStrategyUnitTest
{
	@Spy
	private DefaultAlipayResponseValidationStrategy defaultAlipayResponseValidationStrategy;

	@Mock
	private AlipayConfiguration alipayConfiguration;
	@Mock
	private HttpResponse response;
	@Mock
	private HttpProtocolHandler httpProtocolHandler;
	@Mock
	private AlipayService alipayService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultAlipayResponseValidationStrategy.setAlipayConfiguration(alipayConfiguration);
		defaultAlipayResponseValidationStrategy.setAlipayService(alipayService);
	}

	@Test
	public void testValidateResponse()
	{
		final String partner = "20880217298746149";
		final String key = "sveitc3mpw8e4hkbs4k8irqhx4bxxxxx";
		final String trustGateway = "true";
		final String result = "true";
		final String verifyUrl = "https://electronics.local:9002/yacceleratorstorefront/checkout/multi/alipay/mock/gateway.do/notify.verify?";
		final String actualSign = "test_sign";
		final Map<String, String> params = new HashMap<>();
		params.put("sign", "test_sign");
		params.put("notify_id", "test_notify_id");

		params.put("filtered", "test");
		final Map<String, String> filteredParams = new HashMap<>();
		filteredParams.put("sign", "sign_sent");
		filteredParams.put("notify_id", "test_notify_id");

		Mockito.doReturn(httpProtocolHandler).when(defaultAlipayResponseValidationStrategy).getHttpProtocolHandler();
		Mockito.doReturn(response).when(httpProtocolHandler).execute(Mockito.any());
		given(alipayConfiguration.getWebPartner()).willReturn(partner);
		given(alipayConfiguration.getWebKey()).willReturn(key);
		given(alipayConfiguration.getHttpsVerifyUrl()).willReturn(verifyUrl);
		given(response.getStringResult()).willReturn(result);
		Mockito.doReturn(filteredParams).when(alipayService).paraFilter(params);
		Mockito.doReturn(actualSign).when(alipayService).buildMysign(filteredParams, key, "MD5");

		assertTrue(defaultAlipayResponseValidationStrategy.validateResponse(params));
	}
}
