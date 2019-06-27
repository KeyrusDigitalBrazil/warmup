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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundNotification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;



@UnitTest
public class DefaultAlipayHandleResponseStrategyTest
{

	private DefaultAlipayHandleResponseStrategy defaultAlipayHandleResponseStrategy;

	@Before
	public void setUp() throws Exception
	{
		defaultAlipayHandleResponseStrategy = new DefaultAlipayHandleResponseStrategy();
	}

	@Test
	public void test_CamelCase_Formatter()
	{
		final Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put("notify_time", "2009-08-12 11:08:32");
		responseMap.put("notify_type", "batch_refund_notify");
		responseMap.put("notify_id", "70fec0c2730b27528665af4517c27b95");
		responseMap.put("sign_type", "MD5");
		responseMap.put("sign", "b7baf9af3c91b37bef4261849aa76281");
		responseMap.put("batch_no", "20060702001");
		responseMap.put("success_num", "2");
		responseMap.put("result_details", "2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^SUCCESS");

		AlipayRefundNotification alipayRefundNotificationActual = new AlipayRefundNotification();
		alipayRefundNotificationActual = (AlipayRefundNotification) defaultAlipayHandleResponseStrategy
				.camelCaseFormatter(responseMap, alipayRefundNotificationActual);

		assertTrue(alipayRefundNotificationActual instanceof AlipayRefundNotification);
		assertEquals(alipayRefundNotificationActual.getBatchNo(), "20060702001");
		assertEquals(alipayRefundNotificationActual.getNotifyTime(), "2009-08-12 11:08:32");
		assertEquals(alipayRefundNotificationActual.getNotifyType(), "batch_refund_notify");
		assertEquals(alipayRefundNotificationActual.getNotifyId(), "70fec0c2730b27528665af4517c27b95");
		assertEquals(alipayRefundNotificationActual.getSignType(), "MD5");
		assertEquals(alipayRefundNotificationActual.getSign(), "b7baf9af3c91b37bef4261849aa76281");
		assertEquals(alipayRefundNotificationActual.getSuccessNum(), "2");
		assertEquals(alipayRefundNotificationActual.getResultDetails(),
				"2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^SUCCESS");
	}


	@Test
	public void test_get_Alipay_Refund_DataList_OneSuccess_HasFee()
	{
		final AlipayRefundNotification alipayRefundNotification = new AlipayRefundNotification();

		alipayRefundNotification.setBatchNo("20060702001");
		alipayRefundNotification.setNotifyTime("2009-08-12 11:08:32");
		alipayRefundNotification.setNotifyType("batch_refund_notify");
		alipayRefundNotification.setNotifyId("70fec0c2730b27528665af4517c27b95");
		alipayRefundNotification.setSignType("MD5");
		alipayRefundNotification.setSign("b7baf9af3c91b37bef4261849aa76281");
		alipayRefundNotification.setSuccessNum("1");
		alipayRefundNotification
				.setResultDetails("2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^FAIL");

		List<AlipayRefundData> alipayRefundDatas = defaultAlipayHandleResponseStrategy
				.getAlipayRefundDataList(alipayRefundNotification);

		assertEquals(1, alipayRefundDatas.size());
		assertEquals("2010031906272929", alipayRefundDatas.get(0).getAlipayCode());
		assertEquals("20060702001", alipayRefundDatas.get(0).getBatchNo());
		assertEquals(80, alipayRefundDatas.get(0).getPayerRefundAmount(), 0.000001);
		assertEquals("SUCCESS", alipayRefundDatas.get(0).getPayerRefundStatus());
		assertEquals("jax_chuanhang@alipay.com", alipayRefundDatas.get(0).getSellerEmail());
		assertEquals("2088101003147483", alipayRefundDatas.get(0).getSellerId());
		assertEquals(0.01, alipayRefundDatas.get(0).getSellerRefundAmount(), 0.000001);
		assertEquals("FAIL", alipayRefundDatas.get(0).getSellerRefundStatus());

	}


	@Test
	public void test_get_Alipay_Refund_DataList_OneSuccess_NoFee()
	{
		final AlipayRefundNotification alipayRefundNotification = new AlipayRefundNotification();

		alipayRefundNotification.setBatchNo("20060702001");
		alipayRefundNotification.setNotifyTime("2009-08-12 11:08:32");
		alipayRefundNotification.setNotifyType("batch_refund_notify");
		alipayRefundNotification.setNotifyId("70fec0c2730b27528665af4517c27b95");
		alipayRefundNotification.setSignType("MD5");
		alipayRefundNotification.setSign("b7baf9af3c91b37bef4261849aa76281");
		alipayRefundNotification.setSuccessNum("1");
		alipayRefundNotification.setResultDetails("2010031906272929^80^SUCCESS");

		List<AlipayRefundData> alipayRefundDatas = defaultAlipayHandleResponseStrategy
				.getAlipayRefundDataList(alipayRefundNotification);

		assertEquals(1, alipayRefundDatas.size());
		assertEquals("2010031906272929", alipayRefundDatas.get(0).getAlipayCode());
		assertEquals("20060702001", alipayRefundDatas.get(0).getBatchNo());
		assertEquals(80, alipayRefundDatas.get(0).getPayerRefundAmount(), 0.000001);
		assertEquals("SUCCESS", alipayRefundDatas.get(0).getPayerRefundStatus());
		assertNull(alipayRefundDatas.get(0).getSellerEmail());
		assertNull(alipayRefundDatas.get(0).getSellerId());
		assertEquals(0, alipayRefundDatas.get(0).getSellerRefundAmount(), 0.00001);
		assertNull(alipayRefundDatas.get(0).getSellerRefundStatus());
	}


	@Test
	public void test_get_Alipay_Refund_DataList_MultiSuccess()
	{
		final AlipayRefundNotification alipayRefundNotification = new AlipayRefundNotification();


		alipayRefundNotification.setBatchNo("20060702001");
		alipayRefundNotification.setNotifyTime("2009-08-12 11:08:32");
		alipayRefundNotification.setNotifyType("batch_refund_notify");
		alipayRefundNotification.setNotifyId("70fec0c2730b27528665af4517c27b95");
		alipayRefundNotification.setSignType("MD5");
		alipayRefundNotification.setSign("b7baf9af3c91b37bef4261849aa76281");
		alipayRefundNotification.setSuccessNum("2");
		alipayRefundNotification.setResultDetails(
				"2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^FAIL#2010031906272910^100^SUCCESS$chuanhang@alipay.com^2088101003147410^0.02^SUCCESS");

		List<AlipayRefundData> alipayRefundDatas = defaultAlipayHandleResponseStrategy
				.getAlipayRefundDataList(alipayRefundNotification);

		assertEquals(2, alipayRefundDatas.size());
		assertEquals("2010031906272929", alipayRefundDatas.get(0).getAlipayCode());
		assertEquals("20060702001", alipayRefundDatas.get(0).getBatchNo());
		assertEquals(80, alipayRefundDatas.get(0).getPayerRefundAmount(), 0.000001);
		assertEquals("SUCCESS", alipayRefundDatas.get(0).getPayerRefundStatus());
		assertEquals("jax_chuanhang@alipay.com", alipayRefundDatas.get(0).getSellerEmail());
		assertEquals("2088101003147483", alipayRefundDatas.get(0).getSellerId());
		assertEquals(0.01, alipayRefundDatas.get(0).getSellerRefundAmount(), 0.000001);
		assertEquals("FAIL", alipayRefundDatas.get(0).getSellerRefundStatus());

		assertEquals("2010031906272910", alipayRefundDatas.get(1).getAlipayCode());
		assertEquals("20060702001", alipayRefundDatas.get(1).getBatchNo());
		assertEquals(100, alipayRefundDatas.get(1).getPayerRefundAmount(), 0.000001);
		assertEquals("SUCCESS", alipayRefundDatas.get(1).getPayerRefundStatus());
		assertEquals("chuanhang@alipay.com", alipayRefundDatas.get(1).getSellerEmail());
		assertEquals("2088101003147410", alipayRefundDatas.get(1).getSellerId());
		assertEquals(0.02, alipayRefundDatas.get(1).getSellerRefundAmount(), 0.000001);
		assertEquals("SUCCESS", alipayRefundDatas.get(1).getSellerRefundStatus());
	}
}
