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
package com.hybris.cis.client.rest.fraud.mock;

import com.hybris.cis.client.fraud.models.CisFraudLineItem;
import com.hybris.cis.client.fraud.models.CisFraudPaymentInformation;
import com.hybris.cis.client.fraud.models.CisFraudReportRequest;
import com.hybris.cis.client.fraud.models.CisFraudReportResult;
import com.hybris.cis.client.fraud.models.CisFraudTransaction;
import com.hybris.cis.client.fraud.models.CisFraudTransactionResult;
import com.hybris.cis.client.mock.AvsClientMock;
import com.hybris.cis.client.mock.FraudClientMock;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisAddressType;
import com.hybris.cis.client.shared.models.CisDecision;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.Assert.assertEquals;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 * 
 */
public class FraudMockClientTest extends ServicelayerTest
{
	private FraudClientMock fraudClientMock;


	private static final String TENANT_ID = "single";
	private static final String CLIENT_ID = "test";

	@Before
	public void setUp()
	{
		fraudClientMock = new FraudClientMock();
	}

	@Test
	public void shouldPingSuccess()
	{
		assertEquals(Response.Status.CREATED, fraudClientMock.doPing(CLIENT_ID, TENANT_ID).status());
	}

	@Test
	public void shouldPingFail()
	{
		assertEquals(Response.Status.FORBIDDEN, fraudClientMock.doPing(AvsClientMock.PING_FAIL, TENANT_ID).status());
	}

	@Test
	public void shouldAcceptOrderStatusUpdate()
	{
		final CisFraudTransactionResult result = fraudClientMock.handleOrderStatusUpdate(CLIENT_ID,TENANT_ID,
				this.getOrderStatusUpdateAccept());
		Assert.assertNotNull(result);
	}

	@Test
	public void shouldAcceptFraudReportRequest()
	{
		final CisFraudReportRequest test = new CisFraudReportRequest();
		test.setEndDateTime(new Date());
		test.setStartDateTime(new Date());
		final CisFraudReportResult result = fraudClientMock.generateFraudReport(CLIENT_ID,TENANT_ID, test);
		Assert.assertNotNull(result);
	}

	@Test
	public void shouldCalculateAcceptForFraud()
	{
		final CisFraudTransactionResult result = fraudClientMock.calculateFraudScore(CLIENT_ID,TENANT_ID, this.createTransaction());

		Assert.assertEquals(result.getDecision(), CisDecision.ACCEPT);
	}


	private String getOrderStatusUpdateAccept()
	{
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<!DOCTYPE CaseManagementOrderStatus "
				+ "SYSTEM \"https://ebctest.cybersource.com/ebctest/reports/dtd/cmorderstatus_1_1.dtd\">"
				+ "<CaseManagementOrderStatus MerchantID=\"example_merchant\" "
				+ "Name=\"Case Management Order Status\" Date=\"2008-12-18 12:22:09 GMT\" "
				+ "Version=\"1.1\" xmlns=\"http://reports.cybersource.com/reports/cmos/1.0\">"
				+ "<Update MerchantReferenceNumber=\"10679256010963322294714\" RequestID=\"1744185012770167904567\">"
				+ "<OriginalDecision>REVIEW</OriginalDecision><NewDecision>ACCEPT</NewDecision><Reviewer>example_reviewer</Reviewer>"
				+ "<ReviewerComments>example_comment</ReviewerComments><Queue>example_queue</Queue><Profile>test</Profile>"
				+ "<FollowonResult><Status>Success</Status><Application>Credit Card Settlement</Application>"
				+ "<RequestID>1744185012770167904567</RequestID><Decision>Accept</Decision><ReasonCode>100</ReasonCode>"
				+ "<RCode>1</RCode><RFlag>SOK</RFlag><RMsg>Request was processed successfully.</RMsg></FollowonResult></Update>"
				+ "</CaseManagementOrderStatus>";

	}

	private CisFraudTransaction createTransaction()
	{
		final CisFraudTransaction cisFraudTransaction = new CisFraudTransaction();
		cisFraudTransaction.setAddresses(new ArrayList<CisAddress>());
		cisFraudTransaction.setCurrency("USD");
		cisFraudTransaction.setId("1234");
		cisFraudTransaction.setLineItems(new ArrayList<CisFraudLineItem>());
		cisFraudTransaction.setMemberId("member");
		cisFraudTransaction.setMemberShipDate(new Date());
		cisFraudTransaction.setPaymentInformations(new ArrayList<CisFraudPaymentInformation>());
		cisFraudTransaction.setPromotionCode("promo");
		cisFraudTransaction.setSalesTax(BigDecimal.ONE);
		cisFraudTransaction.setShippingCost(BigDecimal.ONE);
		cisFraudTransaction.setShippingTax(BigDecimal.ONE);
		cisFraudTransaction.setTotalAmount(BigDecimal.ONE);
		cisFraudTransaction.setTotalDiscount(BigDecimal.ONE);
		cisFraudTransaction.setType("type");

		final CisAddress address1 = new CisAddress();
		address1.setAddressLine1("line1");
		address1.setCity("city");
		address1.setZipCode("zip");
		address1.setState("state");
		address1.setCountry("US");
		address1.setType(CisAddressType.SHIP_TO);

		final CisAddress address2 = new CisAddress();
		address2.setAddressLine1("line1");
		address2.setCity("city");
		address2.setZipCode("zip");
		address2.setState("state");
		address2.setCountry("US");
		address2.setType(CisAddressType.SHIP_TO);

		cisFraudTransaction.getAddresses().add(address1);
		cisFraudTransaction.getAddresses().add(address2);

		final CisFraudLineItem lineItem = new CisFraudLineItem();
		lineItem.setDiscount(BigDecimal.ONE);
		lineItem.setId(Integer.valueOf(0));
		lineItem.setItemCode("itemCode");
		lineItem.setItemNumber(0);
		lineItem.setItemSku("sku");
		lineItem.setProductDescription("descrirption");
		lineItem.setProductType("type");
		lineItem.setQuantity(Integer.valueOf(2));
		lineItem.setTaxCode("123");
		lineItem.setTotalPrice(BigDecimal.ONE);
		lineItem.setTotalTax(BigDecimal.ONE);
		lineItem.setUnit("piece");
		lineItem.setUnitPrice(BigDecimal.ONE);
		cisFraudTransaction.getLineItems().add(lineItem);

		final CisFraudPaymentInformation cisFraudPaymentInformation = new CisFraudPaymentInformation();
		cisFraudPaymentInformation.setAmount(BigDecimal.ONE);
		cisFraudPaymentInformation.setAvsResponse("avs");
		cisFraudPaymentInformation.setCardAuthorizationCode("autho");
		cisFraudPaymentInformation.setCardAuthorizedAmount(BigDecimal.TEN);
		cisFraudPaymentInformation.setCardExpireMonth("01");
		cisFraudPaymentInformation.setCardExpireMonth("2015");
		cisFraudPaymentInformation.setCardHolderName("name");
		cisFraudPaymentInformation.setCardNumber("1234567890");
		cisFraudPaymentInformation.setCardType("type");
		cisFraudPaymentInformation.setCvvResponse("cvv");
		cisFraudPaymentInformation.setPaypalAuthorizedAmount(BigDecimal.ONE);
		cisFraudPaymentInformation.setPaypalEmail("test@test.com");
		cisFraudPaymentInformation.setPaypalRequestId("id");
		cisFraudPaymentInformation.setPaypalStatus("status");
		cisFraudPaymentInformation.setType("type");
		cisFraudTransaction.getPaymentInformations().add(cisFraudPaymentInformation);

		return cisFraudTransaction;
	}

}
