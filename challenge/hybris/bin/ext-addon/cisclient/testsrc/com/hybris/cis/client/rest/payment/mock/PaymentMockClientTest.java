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
package com.hybris.cis.client.rest.payment.mock;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import com.hybris.charon.RawResponse;
import com.hybris.cis.client.mock.AvsClientMock;
import com.hybris.cis.client.mock.PaymentClientMock;
import com.hybris.cis.client.payment.models.CisExternalPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentAuthorization;
import com.hybris.cis.client.payment.models.CisPaymentProfileResult;
import com.hybris.cis.client.payment.models.CisPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.shared.models.AnnotationHashMap;
import com.hybris.cis.client.shared.models.CisDecision;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@IntegrationTest
public class PaymentMockClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";
	private static final String PSP_URL_LOCATION = "https://electronics.local:9002/acceleratorservices/sop-mock/process";

	private PaymentClientMock paymentClientMock;

	@Before
	public void setUp()
	{
		paymentClientMock = new PaymentClientMock();
		paymentClientMock.setPspUrlLocation(PSP_URL_LOCATION);
	}

	@Test
	public void shouldPingSuccess()
	{
		assertEquals(Response.Status.CREATED, paymentClientMock.doPing(CLIENT_ID, TENANT_ID).status());
	}

	@Test
	public void shouldPingFail()
	{
		assertEquals(Response.Status.FORBIDDEN, paymentClientMock.doPing(AvsClientMock.PING_FAIL, TENANT_ID).status());
	}

	@Test
	public void shouldReturnUrl()
	{
		final String location = paymentClientMock.pspUrl("test", "single").location().get().toString();
		Assert.assertEquals(PSP_URL_LOCATION, location);
	}

	@Test
	public void shouldAcceptExternalAuthorization()
	{
		final CisPaymentTransactionResult result = paymentClientMock
				.handleExternalAuthorization("test", "single", new CisExternalPaymentRequest(this.getAnnotationHashMap()));
		Assert.assertEquals("30.00", result.getAmount().toString());
		Assert.assertEquals("USD", result.getRequest().getCurrency().toUpperCase());
		Assert.assertEquals("3418696812220176056470", result.getId());
		Assert.assertEquals(CisDecision.ACCEPT, result.getDecision());
	}

	@Test
	public void shouldAcceptProfileAuthorization() throws URISyntaxException
	{
		final CisPaymentAuthorization cisReferencePaymentRequest = new CisPaymentAuthorization();
		cisReferencePaymentRequest.setAmount(BigDecimal.TEN);
		cisReferencePaymentRequest.setCurrency("USD");
		cisReferencePaymentRequest.setVendorParameters(new AnnotationHashMap(getAnnotationHashMap()));

		final CisPaymentTransactionResult result = paymentClientMock
				.authorizeWithProfile("test", "single", "profileId", cisReferencePaymentRequest);
		Assert.assertEquals("30.00", result.getAmount().toString());
		Assert.assertEquals("USD", result.getRequest().getCurrency().toUpperCase());
		Assert.assertEquals(CisDecision.ACCEPT, result.getDecision());
	}

	@Test
	public void shouldRejectProfileAuthorization() throws URISyntaxException
	{
		final CisPaymentAuthorization cisReferencePaymentRequest = new CisPaymentAuthorization();
		cisReferencePaymentRequest.setAmount(BigDecimal.TEN);
		cisReferencePaymentRequest.setCurrency("USD");

		final CisExternalPaymentRequest errReq = new CisExternalPaymentRequest(this.getAnnotationHashMap());
		final Map<String, String> map = errReq.getParameters().convertToMap();
		map.put("decision", "REJECT");
		map.put("reasonCode", "102");
		errReq.setParameters(new AnnotationHashMap(map));

		final RawResponse<CisPaymentProfileResult> rawResponse = paymentClientMock.addCustomerProfile("test", "single", "asdf", errReq);
		final CisPaymentProfileResult result = rawResponse.content().toBlocking().single();
		Assert.assertEquals(CisDecision.REJECT, result.getDecision());
	}

	@Test
	public void shouldCaptureTest() throws URISyntaxException
	{
		final CisPaymentRequest paymentRequest = new CisPaymentRequest();
		paymentRequest.setAmount(BigDecimal.TEN);
		paymentRequest.setCurrency("USD");

		final CisPaymentTransactionResult result = paymentClientMock.capture("test", "single", "authGroupId", "transactionId", paymentRequest);

		assertEquals(paymentRequest, result.getRequest());
		assertEquals(BigDecimal.TEN, result.getAmount());
		assertEquals(CisDecision.ACCEPT, result.getDecision());
	}

	private Map<String, String> getAnnotationHashMap()
	{
		final Map<String, String> params = new HashMap<String, String>();

		params.put("billTo_lastName", "Different");
		params.put("billTo_country", "us");
		params.put("billTo_email", "someone.different@arvatosystems.com");
		params.put("signedDataPublicSignature", "ljvfI/C1Ao1qVdnHSUdd8FTjxKI=");
		params.put("ccAuthReply_cardBIN", "411111");
		params.put("reasonCode", "100");
		params.put("decision", "ACCEPT");
		params.put("card_expirationYear", "2016");
		params.put("ccAuthReply_reasonCode", "100");
		params.put("ccAuthReply_processorResponse", "100");
		params.put("ccAuthReply_cvCode", "M");
		params.put("orderAmount", "30.00");
		params.put("transactionSignature", "KRA5wz5k/fd3lGmcyRH62R+TjoY=");
		params.put("billTo_postalCode", "10019");
		params.put("billTo_city", "New York");
		params.put("billTo_street1", "1700 Broadway");
		params.put("card_accountNumber", "############1111");
		params.put("orderPage_requestToken",
				"Ahj//wSRbPHKIb5mRVJAakGDBo0ZMWLCSxs1ZcGY3TfeX/3oQCm+8v/vQtIGXcEKUMmkmVdHpNj0hgTkWzxyiG+ZkVSQAAAA7waz");
		params.put("orderAmount_publicSignature", "yyS3834RChi1AWHJJtstYvbjYhw=");
		params.put("orderPage_serialNumber", "3210258678720176056165");
		params.put("ccAuthReply_amount", "30.00");
		params.put("orderCurrency_publicSignature", "1XCDmAGtawSaC3VB7kC+abqMBV8=");
		params.put("card_expirationMonth", "01");
		params.put("merchantID", "asnainc2");
		params.put("orderCurrency", "USD");
		params.put("billTo_state", "NY");
		params.put("orderNumber_publicSignature", "QUI0/Iw6cyhBGHUQ+BWQxNkBYeA=");
		params.put("orderPage_transactionType", "sale");
		params.put("requestID", "3366018809020178147616");
		params.put("ccAuthReply_authorizationCode", "888888");
		params.put("decision_publicSignature", "b3RWwDrCi3une15nmjaBNvVN0zY=");
		params.put("orderNumber", "1336601880718");
		params.put("billTo_firstName", "Someone");
		params.put("signedFields",
				"billTo_lastName,billTo_email,orderPage_serialNumber,ccAuthReply_avsCodeRaw,orderAmount_publicSignature,"
						+ "orderCurrency,card_expirationYear,card_accountNumber,reasonCode,billTo_firstName,requestID,"
						+ "orderPage_transactionType,ccAuthReply_reasonCode,ccAuthReply_authorizationCode,card_expirationMonth,"
						+ "orderNumber,orderCurrency_publicSignature,reconciliationID,ccAuthReply_avsCode,orderPage_requestToken,"
						+ "ccAuthReply_processorResponse,ccAuthReply_amount,decision_publicSignature,ccAuthReply_authorizedDateTime,"
						+ "orderAmount,comments,orderNumber_publicSignature,card_cardType,ccAuthReply_cardBIN,billTo_street1,decision,"
						+ "paymentOption,billTo_city,billTo_state,merchantID,billTo_postalCode,billTo_country");
		params.put("card_cardType", "001");
		params.put("ccAuthReply_authorizedDateTime", "2012-05-09T221801Z");
		params.put("ccAuthReply_avsCodeRaw", "I1");
		params.put("paymentOption", "card");
		params.put("comments", "A random comment");
		params.put("ccAuthReply_avsCode", "X");
		params.put("ccAuthReply_cvCode", "M");
		params.put("reconciliationID", "00442110I1YUKAL7");
		// Check whether the signature was okay
		params.put("VerifyTransactionSignature()", "true");

		return params;

	}
}
