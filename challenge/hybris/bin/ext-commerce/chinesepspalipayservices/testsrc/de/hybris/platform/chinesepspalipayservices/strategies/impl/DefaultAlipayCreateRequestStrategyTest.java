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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayService;
import de.hybris.platform.chinesepspalipayservices.data.AlipayCancelPaymentRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayDirectPayRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayPaymentStatusRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAlipayCreateRequestStrategyTest
{
	@Mock
	private AlipayConfiguration alipayConfiguration;

	private DefaultAlipayCreateRequestStrategy defaultAlipayCreateRequestStrategy;

	@Mock
	private AlipayService alipayService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		final DefaultAlipayCreateRequestStrategy testCreateRequestStrategy = new DefaultAlipayCreateRequestStrategy();
		defaultAlipayCreateRequestStrategy = spy(testCreateRequestStrategy);
		defaultAlipayCreateRequestStrategy.setAlipayConfiguration(alipayConfiguration);
		defaultAlipayCreateRequestStrategy.setAlipayService(alipayService);
	}

	@Test
	public void testCreateDirectPayUrl() throws Exception
	{
		doReturn(new HashMap<>()).when(defaultAlipayCreateRequestStrategy).describeRequest(any());
		Mockito.doReturn("requestString").when(alipayService).generateUrl(any(), any());

		final String requestUrl = defaultAlipayCreateRequestStrategy.createDirectPayUrl(new AlipayDirectPayRequestData());

		verify(defaultAlipayCreateRequestStrategy, times(1)).describeRequest(any());
		assertEquals("requestString", requestUrl);
	}

	@Test
	public void testSubmitPaymentStatusRequestWithEmptyResponse() throws ReflectiveOperationException
	{
		doReturn(new HashMap<>()).when(defaultAlipayCreateRequestStrategy).describeRequest(any());
		Mockito.doReturn("").when(alipayService).postRequest(any(), any());

		final AlipayRawPaymentStatus result = defaultAlipayCreateRequestStrategy
				.submitPaymentStatusRequest(new AlipayPaymentStatusRequestData());

		assertNull(result);
	}

	@Test
	public void testSubmitPaymentStatusRequestWithXmlResponse() throws ReflectiveOperationException
	{
		doReturn(new HashMap<>()).when(defaultAlipayCreateRequestStrategy).describeRequest(any());
		Mockito.doReturn("<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success></alipay>")
				.when(alipayService).postRequest(any(), any());
		final AlipayRawPaymentStatus resultData = new AlipayRawPaymentStatus();
		resultData.setIsSuccess("T");
		doReturn(resultData).when(defaultAlipayCreateRequestStrategy).parserXML(anyString(), anyString());

		final AlipayRawPaymentStatus result = defaultAlipayCreateRequestStrategy
				.submitPaymentStatusRequest(new AlipayPaymentStatusRequestData());

		assertEquals("T", result.getIsSuccess());
		verify(defaultAlipayCreateRequestStrategy, times(1)).describeRequest(any());
		verify(defaultAlipayCreateRequestStrategy, times(1)).parserXML(anyString(),
				eq("de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus"));
	}

	@Test
	public void testSubmitCancelPaymentRequest() throws ReflectiveOperationException
	{
		doReturn(new HashMap<>()).when(defaultAlipayCreateRequestStrategy).describeRequest(any());
		Mockito.doReturn("<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success></alipay>")
				.when(alipayService).postRequest(any(), any());
		final AlipayRawCancelPaymentResult alipayRawCancelPaymentResult = new AlipayRawCancelPaymentResult();
		alipayRawCancelPaymentResult.setIsSuccess("F");
		alipayRawCancelPaymentResult.setError("ErrorMsg");
		doReturn(alipayRawCancelPaymentResult).when(defaultAlipayCreateRequestStrategy).parserXML(anyString(), anyString());

		final AlipayRawCancelPaymentResult result = defaultAlipayCreateRequestStrategy
				.submitCancelPaymentRequest(new AlipayCancelPaymentRequestData());

		assertEquals("F", result.getIsSuccess());
		verify(defaultAlipayCreateRequestStrategy, times(1)).describeRequest(any());
		verify(defaultAlipayCreateRequestStrategy, times(1)).parserXML(anyString(),
				eq("de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult"));
	}

	@Test
	public void testCreateRefundUrl() throws Exception
	{
		doReturn(new HashMap<>()).when(defaultAlipayCreateRequestStrategy).describeRequest(any());
		Mockito.doReturn("requestString").when(alipayService).generateUrl(any(), any());

		final String refundRequest = defaultAlipayCreateRequestStrategy.createRefundUrl(new AlipayRefundRequestData());

		verify(defaultAlipayCreateRequestStrategy, times(1)).describeRequest(any());
		assertEquals("requestString", refundRequest);
	}

	@Test
	public void testDescribeRequest() throws ReflectiveOperationException
	{
		final AlipayPaymentStatusRequestData requestData = new AlipayPaymentStatusRequestData();
		requestData.setInputCharset("UTF-8");
		requestData.setOutTradeNo("00010002");
		requestData.setPartner("20880217298747849");
		requestData.setService("single_trade_query");
		requestData.setSignType("MD5");
		requestData.setTradeNo("82018484625794138");

		final Map<String, String> map = defaultAlipayCreateRequestStrategy.describeRequest(requestData);

		assertEquals("UTF-8", map.get("_input_charset"));
		assertEquals("00010002", map.get("out_trade_no"));
		assertEquals("20880217298747849", map.get("partner"));
		assertEquals("single_trade_query", map.get("service"));
		assertEquals("MD5", map.get("sign_type"));
		assertEquals("82018484625794138", map.get("trade_no"));
	}

	@Test
	public void testParserXML()
	{
		final String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><alipay><is_success>T</is_success>"
				+ "<request><param name=\"trade_no\">2010073000030344</param>" + "<param name=\"service\">single_trade_query</param>"
				+ "<param name=\"partner\">2088002007018916</param></request>" + "<response><trade><body>合同催款通知</body>"
				+ "<buyer_email>ltrade008@alitest.com</buyer_email>" + "<buyer_id>2088102002723445</buyer_id>"
				+ "<discount>0.00</discount>" + "<gmt_create>2010-07-30 12:26:33</gmt_create>"
				+ "<gmt_last_modified_time>2010-07-30 12:30:29" + "</gmt_last_modified_time>"
				+ "<gmt_payment>2010-07-30 12:30:29</gmt_payment>" + "<is_total_fee_adjust>F</is_total_fee_adjust>"
				+ "<out_trade_no>1280463992953</out_trade_no>" + "<payment_type>1</payment_type>" + "<price>1.00</price>"
				+ "<quantity>1</quantity>" + "<seller_email>chao.chenc1@alipay.com</seller_email>"
				+ "<seller_id>2088002007018916</seller_id>" + "<subject>合同催款通知</subject>" + "<total_fee>1.00</total_fee>"
				+ "<trade_no>2010073000030344</trade_no>" + "<trade_status>TRADE_FINISHED</trade_status>"
				+ "<use_coupon>F</use_coupon></trade></response>" + "<sign>56ae9c3286886f76e57e0993625c71fe</sign>"
				+ "<sign_type>MD5</sign_type>" + "</alipay>";
		final String className = "de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus";

		final AlipayRawPaymentStatus alipayRawPaymentStatus = (AlipayRawPaymentStatus) defaultAlipayCreateRequestStrategy
				.parserXML(xmlString, className);

		assertEquals("ltrade008@alitest.com", alipayRawPaymentStatus.getBuyerEmail());
		assertEquals("2088102002723445", alipayRawPaymentStatus.getBuyerId());
		assertEquals(0.00, alipayRawPaymentStatus.getDiscount(), 0.001);
		assertEquals("2010-07-30 12:26:33", alipayRawPaymentStatus.getGmtCreate());
		assertEquals("2010-07-30 12:30:29", alipayRawPaymentStatus.getGmtLastModifiedTime());
		assertEquals("2010-07-30 12:30:29", alipayRawPaymentStatus.getGmtPayment());
		assertEquals("F", alipayRawPaymentStatus.getIsTotalFeeAdjust());
		assertEquals("1280463992953", alipayRawPaymentStatus.getOutTradeNo());
		assertEquals("1", alipayRawPaymentStatus.getPaymentType());
		assertEquals(1.00, alipayRawPaymentStatus.getPrice(), 0.001);
		assertEquals(1, alipayRawPaymentStatus.getQuantity());
		assertEquals("chao.chenc1@alipay.com", alipayRawPaymentStatus.getSellerEmail());
		assertEquals("2088002007018916", alipayRawPaymentStatus.getSellerId());
		assertEquals("合同催款通知", alipayRawPaymentStatus.getSubject());
		assertEquals(1.00, alipayRawPaymentStatus.getTotalFee(), 0.01);
		assertEquals("2010073000030344", alipayRawPaymentStatus.getTradeNo());
		assertEquals("TRADE_FINISHED", alipayRawPaymentStatus.getTradeStatus());
		assertEquals("F", alipayRawPaymentStatus.getUseCoupon());
	}

	@Test
	public void testCovert2SnakeCase()
	{
		final String camelCase = "outTradeNo";

		final String snakeCase = defaultAlipayCreateRequestStrategy.covert2SnakeCase(camelCase);

		assertEquals("out_trade_no", snakeCase);
	}
}