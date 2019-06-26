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
package de.hybris.platform.chinesepspalipayservices.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.chinesepaymentservices.enums.ServiceType;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepaymentservices.order.service.ChineseOrderService;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayService;
import de.hybris.platform.chinesepspalipayservices.alipay.impl.DefaultAlipayService;
import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.data.AlipayCancelPaymentRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayDirectPayRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayPaymentStatusRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;
import de.hybris.platform.chinesepspalipayservices.order.AlipayOrderService;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayCreateRequestStrategy;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentInfoStrategy;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayPaymentTransactionStrategy;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayResponseValidationStrategy;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.i18n.impl.DefaultCommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAlipayPaymentServiceTest
{

	private static final Logger LOG = Logger.getLogger(DefaultAlipayPaymentServiceTest.class);

	private DefaultAlipayPaymentService defaultAlipayPaymentService;

	private AlipayPaymentTransactionModel alipayPaymentTransactionModel;

	private OrderModel orderModel;

	private AlipayService alipayService;

	@Mock
	private CMSSiteModel siteModel;

	@Mock
	private LanguageModel currentLanguage;

	@Mock
	private AlipayPaymentTransactionStrategy alipayPaymentTransactionStrategy;

	@Mock
	private AlipayResponseValidationStrategy alipayResponseValidationStrategy;

	@Mock
	private AlipayPaymentInfoStrategy alipayPaymentInfoStrategy;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private CMSSiteService cmsSiteService;

	@Mock
	private DefaultCommerceCommonI18NService commerceCommonI18NService;

	@Mock
	private MediaService mediaService;

	@Mock
	private MediaModel mediaModel;

	@Mock
	private DefaultCommerceCheckoutService commerceCheckoutService;

	@Mock
	private AlipayOrderService alipayOrderService;

	@Mock
	private AlipayCreateRequestStrategy alipayCreateRequestStrategy;

	@Mock
	private ModelService modelService;

	@Mock
	private ChineseOrderService chineseOrderService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		alipayPaymentTransactionModel = new AlipayPaymentTransactionModel();
		alipayPaymentTransactionModel.setAlipayCode("2011011201037066");

		orderModel = new OrderModel();
		orderModel.setCode("00000001");
		orderModel.setTotalPrice(1.5);

		final AlipayConfiguration alipayConfiguration = new AlipayConfiguration();
		alipayConfiguration.setRefundServiceApiName("refund_fastpay_by_platform_pwd");
		alipayConfiguration.setWebPartner("2088101008267254");
		alipayConfiguration.setWebSellerEmail("Jier1105@alitest.com");
		alipayConfiguration.setWebSellerId("2088101008267254");
		alipayConfiguration.setRefundReason("协商退款");

		defaultAlipayPaymentService = new DefaultAlipayPaymentService();
		defaultAlipayPaymentService.setAlipayPaymentTransactionStrategy(alipayPaymentTransactionStrategy);
		defaultAlipayPaymentService.setAlipayConfiguration(alipayConfiguration);
		defaultAlipayPaymentService.setModelService(modelService);
		defaultAlipayPaymentService.setChineseOrderService(chineseOrderService);
		defaultAlipayPaymentService.setBaseSiteService(baseSiteService);
		defaultAlipayPaymentService.setSiteBaseUrlResolutionService(siteBaseUrlResolutionService);
		
		alipayService = new DefaultAlipayService();

		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("electronics");
		baseSite.setChannel(SiteChannel.B2C);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);

		Mockito.when(
				siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, "/"
						+ PaymentConstants.Controller.DIRECT_AND_EXPRESS_RETURN_URL)).thenReturn(
				"https://electronics.local:9002/yacceleratorstorefront/" + PaymentConstants.Controller.DIRECT_AND_EXPRESS_RETURN_URL);
		Mockito.when(
				siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, "/"
						+ PaymentConstants.Controller.DIRECT_AND_EXPRESS_NOTIFY_URL)).thenReturn(
				"https://electronics.local:9002/yacceleratorstorefront/" + PaymentConstants.Controller.DIRECT_AND_EXPRESS_NOTIFY_URL);
		Mockito
				.when(siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, "/"
						+ PaymentConstants.Controller.ERROR_NOTIFY_URL)).thenReturn(
						"https://electronics.local:9002/yacceleratorstorefront/" + PaymentConstants.Controller.ERROR_NOTIFY_URL);
		Mockito.when(
				siteBaseUrlResolutionService
						.getWebsiteUrlForSite(baseSite, true, "/" + PaymentConstants.Controller.REFUND_NOTIFY_URL)).thenReturn(
				"https://electronics.local:9002/yacceleratorstorefront/" + PaymentConstants.Controller.REFUND_NOTIFY_URL);
	}

	@Test
	public void testCreateAlipayRefundRequestDataByOrderSuccessfully()
	{
		Mockito.when(alipayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(Mockito.any(), Mockito.any()))
				.thenReturn(Optional.of(alipayPaymentTransactionModel));

		final Optional<AlipayRefundRequestData> result = defaultAlipayPaymentService
				.createAlipayRefundRequestDataByOrder(orderModel);
		assertTrue(result.isPresent());
		final AlipayRefundRequestData alipayRefundRequestData = result.get();
		assertEquals("refund_fastpay_by_platform_pwd", alipayRefundRequestData.getService());
		assertEquals("2088101008267254", alipayRefundRequestData.getPartner());
		assertEquals("utf-8", alipayRefundRequestData.getInputCharset());
		assertEquals(
				"https://electronics.local:9002/yacceleratorstorefront/checkout/multi/summary/alipay/pspasynresponse/refundnotifyController",
				alipayRefundRequestData.getNotifyUrl());
		assertEquals("Jier1105@alitest.com", alipayRefundRequestData.getSellerEmail());
		assertEquals("2088101008267254", alipayRefundRequestData.getSellerUserId());
		assertEquals("1", alipayRefundRequestData.getBatchNum());
		assertEquals("2011011201037066^1.50^协商退款", alipayRefundRequestData.getDetailData());
	}

	@Test
	public void testCreateAlipayRefundRequestDataByOrderWithUnpaidOrder()
	{
		Mockito.when(alipayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(Mockito.any(), Mockito.any()))
				.thenReturn(Optional.empty());

		final Optional<AlipayRefundRequestData> result = defaultAlipayPaymentService
				.createAlipayRefundRequestDataByOrder(orderModel);
		assertFalse(result.isPresent());
	}

	@Test
	public void testHandleNotificationwithinValidResponse()
	{
		Mockito
				.when(request.getRequestURL())
				.thenReturn(
						new StringBuffer(
								"https://electronics.local:9002/yacceleratorstorefront/checkout/multi/summary/alipay/pspasynresponse/refundnotifyController"));

		final Map<String, String> responseMap = new HashMap<>();
		responseMap.put("notify_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		responseMap.put("notify_type", "batch_refund_notify");
		responseMap.put("notify_id", alipayService.encrypt("MD5", String.valueOf(System.currentTimeMillis())));
		responseMap.put("sign_type", "MD5");
		responseMap.put("batch_no", "2016033100008001");
		responseMap.put("success_num", "1");
		responseMap.put("result_details", "2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^SUCCESS");
		responseMap.put("sign", "12345678910524860135");

		final Map<String, String[]> parameterMap = new HashMap<>();
		parameterMap.put("notify_time", new String[]
		{ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) });
		parameterMap.put("notify_type", new String[]
		{ "batch_refund_notify" });
		parameterMap.put("notify_id", new String[]
		{ alipayService.encrypt("MD5", String.valueOf(System.currentTimeMillis())) });
		parameterMap.put("sign_type", new String[]
		{ "MD5" });
		parameterMap.put("batch_no", new String[]
		{ "2016033100008001" });
		parameterMap.put("success_num", new String[]
		{ "1" });
		parameterMap.put("result_details", new String[]
		{ "2010031906272929^80^SUCCESS$jax_chuanhang@alipay.com^2088101003147483^0.01^SUCCESS" });
		parameterMap.put("sign", new String[]
		{ "12345678910524860135" });

		final DefaultAlipayPaymentService alipayPaymentService = new DefaultAlipayPaymentService();
		final DefaultAlipayPaymentService spAlipayPaymentService = Mockito.spy(alipayPaymentService);

		spAlipayPaymentService.setAlipayResponseValidationStrategy(alipayResponseValidationStrategy);
		Mockito.when(request.getParameterMap()).thenReturn(parameterMap);
		Mockito.when(alipayResponseValidationStrategy.validateResponse(Mockito.any())).thenReturn(false);
		Mockito.when(spAlipayPaymentService.unifyRequestParameterValue(Mockito.anyMap())).thenReturn(responseMap);

		try
		{
			spAlipayPaymentService.handleAsyncResponse(request, response);
			Mockito.verify(spAlipayPaymentService, Mockito.times(0)).handleNotification(responseMap, response);
		}
		catch (final IOException e1)
		{
			LOG.error("IOException!", e1);
		}
	}

	@Test
	public void testGetUrlEncodePattern()
	{
		final CMSSiteModel siteModel = new CMSSiteModel();
		siteModel.setUid("electronics");
		final LanguageModel currentLanguage = new LanguageModel();
		currentLanguage.setIsocode("CN");

		cmsSiteService.setCurrentSite(siteModel);
		defaultAlipayPaymentService.setCmsSiteService(cmsSiteService);

		commerceCommonI18NService.setCurrentLanguage(currentLanguage);
		defaultAlipayPaymentService.setCommerceCommonI18NService(commerceCommonI18NService);

		Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(siteModel);
		Mockito.when(commerceCommonI18NService.getCurrentLanguage()).thenReturn(currentLanguage);

		final String url = defaultAlipayPaymentService.getUrlEncodePattern();

		assertEquals("electronics/CN/", url);
	}

	@Test
	public void testGetPspLogoUrl()
	{
		Mockito.when(mediaModel.getURL()).thenReturn("/images/theme/alipay.jpg");
		Mockito.when(mediaService.getMedia(Mockito.anyString())).thenReturn(mediaModel);

		defaultAlipayPaymentService.setMediaService(mediaService);

		final String url = defaultAlipayPaymentService.getPspLogoUrl();
		assertEquals("/images/theme/alipay.jpg", url);

	}

	@Test
	public void testUnifyRequestParameterValue()
	{
		final Map<String, String[]> mapPars = new HashMap<>();
		final String[] key1 =
		{ "1", "2", "3" };
		mapPars.put("key1", key1);
		final String[] key2 =
		{ "4", "5", "6" };
		mapPars.put("key2", key2);
		final String[] key3 =
		{ "7", "8", "9" };
		mapPars.put("key3", key3);
		final Map<String, String> rtnMap = defaultAlipayPaymentService.unifyRequestParameterValue(mapPars);

		assertEquals("123", rtnMap.get("key1"));
		assertEquals("456", rtnMap.get("key2"));
		assertEquals("789", rtnMap.get("key3"));
	}

	@Test
	public void testConvertKey2CamelCase()
	{
		final Map<String, String> mapPars = new HashMap<>();
		mapPars.put("k1", "k1");
		mapPars.put("_k2", "k2");
		mapPars.put("a_k3", "k3");

		final Map<String, String> rtnMap = defaultAlipayPaymentService.convertKey2CamelCase(mapPars);

		assertEquals("k1", rtnMap.get("k1"));
		assertEquals("k2", rtnMap.get("k2"));
		assertEquals("k3", rtnMap.get("aK3"));
	}

	@Test
	public void testGetOrderModelByCode()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setCode("o000001");
		Mockito.doReturn(orderModel).when(alipayOrderService).getOrderByCode(Mockito.any());
		defaultAlipayPaymentService.setAlipayOrderService(alipayOrderService);
		defaultAlipayPaymentService.getOrderModelByCode("o000001");

		assertEquals("o000001", orderModel.getCode());
	}

	@Test
	public void testSetPaymentInfo()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setCode("c000001");
		final ChinesePaymentInfoModel chinesePaymentInfoModel = new ChinesePaymentInfoModel();
		chinesePaymentInfoModel.setCode("p000001");
		Mockito.when(commerceCheckoutService.setPaymentInfo(Mockito.any())).thenReturn(true);

		defaultAlipayPaymentService.setCommerceCheckoutService(commerceCheckoutService);
		defaultAlipayPaymentService.setAlipayPaymentInfoStrategy(alipayPaymentInfoStrategy);

		assertTrue(defaultAlipayPaymentService.setPaymentInfo(cartModel, chinesePaymentInfoModel));
	}

	@Test
	public void testHandleReturnInfo() throws IOException
	{
		final Map<String, String> responseMap = new HashMap<>();
		responseMap.put("out_trade_no", "t000001");
		responseMap.put("subject", "xxx");
		final String orderCode = defaultAlipayPaymentService.handleReturnInfo(responseMap);

		assertEquals("t000001", orderCode);
	}

	@Test
	public void testCreateAlipayCancelPaymentRequestDataByOrder()
	{
		final OrderModel order = new OrderModel();
		order.setCode("o000001");
		final AlipayConfiguration alipayConfiguration = new AlipayConfiguration();
		alipayConfiguration.setCloseTradeServiceApiName("closetrade");
		alipayConfiguration.setWebPartner("parner");

		final AlipayCancelPaymentRequestData requestData = defaultAlipayPaymentService
				.createAlipayCancelPaymentRequestDataByOrder(order);

		assertEquals("o000001", requestData.getOutOrderNo());
		assertEquals("utf-8", requestData.getInputCharset());
	}

	@Test
	public void testCreateAlipayPaymentStatusRequestDataByOrder()
	{
		final OrderModel order = new OrderModel();
		order.setCode("o000001");
		final AlipayConfiguration alipayConfiguration = new AlipayConfiguration();
		alipayConfiguration.setCloseTradeServiceApiName("closetrade");
		alipayConfiguration.setWebPartner("parner");

		final AlipayPaymentStatusRequestData requestData = defaultAlipayPaymentService
				.createAlipayPaymentStatusRequestDataByOrder(order);

		assertEquals("o000001", requestData.getOutTradeNo());
		assertEquals("utf-8", requestData.getInputCharset());
	}

	@Test
	public void testCancelPaymentOnPaidStatus()
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentStatus(PaymentStatus.PAID);

		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		Mockito.doReturn(orderModel).when(defaultAlipayPaymentService).getOrderModelByCode(Mockito.anyString());

		final boolean ifCancel = defaultAlipayPaymentService.cancelPayment("o000001");

		assertFalse(ifCancel);
	}

	@Test
	public void testCancelPaymentOnNotPaidStatus() throws Exception
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentStatus(PaymentStatus.NOTPAID);

		final AlipayCancelPaymentRequestData alipayCancelPaymentRequestData = new AlipayCancelPaymentRequestData();

		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		Mockito.doReturn(orderModel).when(defaultAlipayPaymentService).getOrderModelByCode(Mockito.anyString());
		Mockito.doReturn(alipayCancelPaymentRequestData).when(defaultAlipayPaymentService)
				.createAlipayCancelPaymentRequestDataByOrder(Mockito.any());

		final AlipayRawCancelPaymentResult alipayRawCancelPaymentResult = new AlipayRawCancelPaymentResult();
		Mockito.doReturn(true).when(alipayPaymentTransactionStrategy).checkCaptureTransactionEntry(Mockito.any(), Mockito.any());
		Mockito.doReturn(alipayRawCancelPaymentResult).when(alipayCreateRequestStrategy).submitCancelPaymentRequest(Mockito.any());

		defaultAlipayPaymentService.setAlipayPaymentTransactionStrategy(alipayPaymentTransactionStrategy);
		defaultAlipayPaymentService.setAlipayCreateRequestStrategy(alipayCreateRequestStrategy);

		final boolean ifCancel = defaultAlipayPaymentService.cancelPayment("o000001");

		assertTrue(ifCancel);

	}

	@Test
	public void testSyncPaymentStatus() throws Exception
	{
		final OrderModel orderModel = new OrderModel();
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		final AlipayPaymentStatusRequestData alipayPaymentStatusRequestData = new AlipayPaymentStatusRequestData();

		Mockito.doReturn(orderModel).when(alipayOrderService).getOrderByCode(Mockito.any());
		Mockito.doReturn(alipayPaymentStatusRequestData).when(defaultAlipayPaymentService)
				.createAlipayPaymentStatusRequestDataByOrder(Mockito.any());
		final AlipayRawPaymentStatus alipayRawPaymentStatus = new AlipayRawPaymentStatus();
		alipayRawPaymentStatus.setSubject("alipay");
		alipayRawPaymentStatus.setTradeNo("t000001");
		Mockito.doReturn(alipayRawPaymentStatus).when(alipayCreateRequestStrategy).submitPaymentStatusRequest(Mockito.any());
		final AlipayPaymentTransactionEntryModel entry = new AlipayPaymentTransactionEntryModel();
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		Mockito.doReturn(entry).when(alipayPaymentTransactionStrategy).saveForStatusCheck(Mockito.any(), Mockito.any());

		defaultAlipayPaymentService.setAlipayCreateRequestStrategy(alipayCreateRequestStrategy);
		defaultAlipayPaymentService.setAlipayPaymentTransactionStrategy(alipayPaymentTransactionStrategy);
		defaultAlipayPaymentService.setAlipayOrderService(alipayOrderService);
		defaultAlipayPaymentService.setModelService(modelService);
		defaultAlipayPaymentService.setChineseOrderService(chineseOrderService);

		defaultAlipayPaymentService.syncPaymentStatus("t000001");

		assertEquals(PaymentStatus.PAID, orderModel.getPaymentStatus());
	}

	@Test
	public void testHandleSyncResponse() throws IOException
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		final Map<String, String> paramsMap = new HashMap<>();

		Mockito.doReturn(paramsMap).when(request).getParameterMap();
		Mockito.doReturn(new StringBuffer("checkout/multi/summary/alipay/pspsyncresponse/returnController")).when(request)
				.getRequestURL();
		Mockito.doReturn(true).when(alipayResponseValidationStrategy).validateResponse(Mockito.any());
		Mockito.doReturn("info").when(defaultAlipayPaymentService).handleReturnInfo(Mockito.any());

		defaultAlipayPaymentService.setAlipayResponseValidationStrategy(alipayResponseValidationStrategy);
		defaultAlipayPaymentService.handleSyncResponse(request, response);

		Mockito.verify(defaultAlipayPaymentService).handleReturnInfo(Mockito.any());
	}

	@Test
	public void testHandleErrorResponse()
	{
		final OrderModel orderModel = new OrderModel();
		final Map<String, String> responseMap = new HashMap<>();
		responseMap.put(PaymentConstants.ErrorHandler.OUT_TRADE_NO, "o000001");

		Mockito.doReturn(orderModel).when(alipayOrderService).getOrderByCode(Mockito.any());
		defaultAlipayPaymentService.setAlipayOrderService(alipayOrderService);

		defaultAlipayPaymentService.handleErrorResponse(responseMap);

		Mockito.verify(alipayPaymentTransactionStrategy).updateForError(Mockito.any(), Mockito.any());
	}

	@Test
	public void testHandleAsyncResponseDirectNotify() throws IOException
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		final Map<String, String> paramsMap = new HashMap<>();

		Mockito.doReturn(paramsMap).when(request).getParameterMap();
		Mockito.doReturn(new StringBuffer("checkout/multi/summary/alipay/pspasynresponse/notifyController")).when(request)
				.getRequestURL();
		Mockito.doReturn(true).when(alipayResponseValidationStrategy).validateResponse(Mockito.any());
		Mockito.doReturn("o000001").when(defaultAlipayPaymentService).handleNotification(Mockito.any(), Mockito.any());

		defaultAlipayPaymentService.setAlipayResponseValidationStrategy(alipayResponseValidationStrategy);
		defaultAlipayPaymentService.handleAsyncResponse(request, response);

		Mockito.verify(defaultAlipayPaymentService).handleNotification(Mockito.any(), Mockito.any());
	}

	@Test
	public void testHandleAsyncResponseErrorNotify() throws IOException
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		final Map<String, String> paramsMap = new HashMap<>();

		Mockito.doReturn(paramsMap).when(request).getParameterMap();
		Mockito.doReturn(new StringBuffer("checkout/multi/summary/alipay/pspasynresponse/errorController")).when(request)
				.getRequestURL();
		Mockito.doNothing().when(defaultAlipayPaymentService).handleErrorResponse(Mockito.any());

		defaultAlipayPaymentService.handleAsyncResponse(request, response);

		Mockito.verify(defaultAlipayPaymentService).handleErrorResponse(Mockito.any());
	}

	@Test
	public void testHandleAsyncResponseRefundNotify() throws IOException
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		final Map<String, String> paramsMap = new HashMap<>();

		Mockito.doReturn(paramsMap).when(request).getParameterMap();
		Mockito.doReturn(new StringBuffer("checkout/multi/summary/alipay/pspasynresponse/refundnotifyController")).when(request)
				.getRequestURL();
		Mockito.doReturn(true).when(alipayResponseValidationStrategy).validateResponse(Mockito.any());
		Mockito.doNothing().when(defaultAlipayPaymentService).handleRefundNotification(Mockito.any(), Mockito.any());

		defaultAlipayPaymentService.setAlipayResponseValidationStrategy(alipayResponseValidationStrategy);
		defaultAlipayPaymentService.handleAsyncResponse(request, response);

		Mockito.verify(defaultAlipayPaymentService).handleRefundNotification(Mockito.any(), Mockito.any());
	}

	@Test
	public void testGetPaymentRequestUrl() throws Exception
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		defaultAlipayPaymentService.setAlipayOrderService(alipayOrderService);

		final AlipayDirectPayRequestData alipayDirectPayRequestData = new AlipayDirectPayRequestData();
		Mockito.doReturn(alipayDirectPayRequestData).when(defaultAlipayPaymentService)
				.createAlipayDirectPayRequestDataByOrder(Mockito.any());

		Mockito.doReturn("/RequestUrl").when(alipayCreateRequestStrategy).createDirectPayUrl(Mockito.any());

		defaultAlipayPaymentService.setAlipayCreateRequestStrategy(alipayCreateRequestStrategy);
		defaultAlipayPaymentService.setAlipayPaymentTransactionStrategy(alipayPaymentTransactionStrategy);

		final String url = defaultAlipayPaymentService.getPaymentRequestUrl("o000001");

		assertEquals("/RequestUrl", url);
	}

	@Test
	public void testGetRefundRequestUrl() throws Exception
	{
		final OrderModel orderModel = new OrderModel();
		orderModel.setPaymentStatus(PaymentStatus.PAID);

		Mockito.doReturn(orderModel).when(alipayOrderService).getOrderByCode(Mockito.any());

		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());
		defaultAlipayPaymentService.setAlipayOrderService(alipayOrderService);

		final Optional<AlipayRefundRequestData> alipayRefundRequestData = Optional.of(new AlipayRefundRequestData());
		Mockito.doReturn(alipayRefundRequestData).when(defaultAlipayPaymentService)
				.createAlipayRefundRequestDataByOrder(Mockito.any());

		Mockito.doReturn("/RefundUrl").when(alipayCreateRequestStrategy).createRefundUrl(Mockito.any());

		Mockito
				.when(alipayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(orderModel, TransactionStatus.FINISHED))
				.thenReturn(Optional.empty());

		defaultAlipayPaymentService.setAlipayCreateRequestStrategy(alipayCreateRequestStrategy);
		defaultAlipayPaymentService.setAlipayPaymentTransactionStrategy(alipayPaymentTransactionStrategy);

		final Optional<String> url = defaultAlipayPaymentService.getRefundRequestUrl("o000001");

		assertEquals("/RefundUrl", url.get());
	}

	@Test
	public void testCreateAlipayDirectPayRequestDataByOrderDirectpay()
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());

		Mockito.doReturn("/test/").when(defaultAlipayPaymentService).getUrlEncodePattern();

		final AlipayConfiguration alipayConfiguration = new AlipayConfiguration();
		alipayConfiguration.setDirectayPaymethodName("directayPaymethodName");
		alipayConfiguration.setExpressPaymethodName("expressPaymethodName");
		alipayConfiguration.setRequestSubject("requestSubject");
		alipayConfiguration.setRequestTimeout("requestTimeout");
		alipayConfiguration.setDirectPayServiceApiName("directPayServiceApiName");
		alipayConfiguration.setWebPartner("webPartner");
		alipayConfiguration.setWebSellerEmail("webSellerEmail");
		defaultAlipayPaymentService.setAlipayConfiguration(alipayConfiguration);

		final ChinesePaymentInfoModel chinesePaymentInfo = new ChinesePaymentInfoModel();
		chinesePaymentInfo.setServiceType(ServiceType.DIRECTPAY);

		final OrderModel orderModel = Mockito.spy(new OrderModel());
		orderModel.setCode("00000001");
		orderModel.setTotalPrice(1.5);
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setQuantity((long) 10);
		final List<AbstractOrderEntryModel> orderEntryModelList = new ArrayList<>();
		orderEntryModelList.add(orderEntryModel);
		Mockito.doReturn(orderEntryModelList).when(orderModel).getEntries();

		orderModel.setPaymentInfo(chinesePaymentInfo);

		defaultAlipayPaymentService.setBaseSiteService(baseSiteService);
		defaultAlipayPaymentService.setSiteBaseUrlResolutionService(siteBaseUrlResolutionService);

		final AlipayDirectPayRequestData requestData = defaultAlipayPaymentService
				.createAlipayDirectPayRequestDataByOrder(orderModel);

		assertEquals("directayPaymethodName", requestData.getPaymethod());
		assertEquals("00000001", requestData.getOutTradeNo());
		assertEquals(10, requestData.getQuantity());
	}

	@Test
	public void testCreateAlipayDirectPayRequestDataByOrderNonDirectpay()
	{
		final DefaultAlipayPaymentService defaultAlipayPaymentService = Mockito.spy(new DefaultAlipayPaymentService());

		Mockito.doReturn("/test/").when(defaultAlipayPaymentService).getUrlEncodePattern();

		final AlipayConfiguration alipayConfiguration = new AlipayConfiguration();
		alipayConfiguration.setDirectayPaymethodName("directayPaymethodName");
		alipayConfiguration.setExpressPaymethodName("expressPaymethodName");
		alipayConfiguration.setRequestSubject("requestSubject");
		alipayConfiguration.setRequestTimeout("requestTimeout");
		alipayConfiguration.setDirectPayServiceApiName("directPayServiceApiName");
		alipayConfiguration.setWebPartner("webPartner");
		alipayConfiguration.setWebSellerEmail("webSellerEmail");
		defaultAlipayPaymentService.setAlipayConfiguration(alipayConfiguration);

		final ChinesePaymentInfoModel chinesePaymentInfo = new ChinesePaymentInfoModel();
		chinesePaymentInfo.setServiceType(ServiceType.EXPRESSPAY);

		final OrderModel orderModel = Mockito.spy(new OrderModel());
		orderModel.setCode("00000001");
		orderModel.setTotalPrice(1.5);
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setQuantity((long) 10);
		final List<AbstractOrderEntryModel> orderEntryModelList = new ArrayList<>();
		orderEntryModelList.add(orderEntryModel);
		Mockito.doReturn(orderEntryModelList).when(orderModel).getEntries();

		orderModel.setPaymentInfo(chinesePaymentInfo);

		defaultAlipayPaymentService.setBaseSiteService(baseSiteService);
		defaultAlipayPaymentService.setSiteBaseUrlResolutionService(siteBaseUrlResolutionService);

		final AlipayDirectPayRequestData requestData = defaultAlipayPaymentService
				.createAlipayDirectPayRequestDataByOrder(orderModel);

		assertEquals("expressPaymethodName", requestData.getPaymethod());
		assertEquals("00000001", requestData.getOutTradeNo());
		assertEquals(10, requestData.getQuantity());
	}

}
