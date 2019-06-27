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
package de.hybris.platform.integration.cis.payment.populators;

import java.math.BigDecimal;
import java.util.Map;
import com.hybris.charon.RawResponse;
import com.hybris.cis.client.payment.models.CisCreditCard;
import com.hybris.cis.client.payment.models.CisPaymentProfileResult;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.shared.models.AnnotationHashMap;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;


/**
 * @author florent
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisCreateSubscriptionResultPopulatorTest
{
	private static final String AUTH_LOCATION = "http://location/of/the/profile/in/cis";

	private CisCreateSubscriptionResultPopulator populator;

	@Mock
	private Observable<RawResponse<CisPaymentProfileResult>> rawResponseProfileResult;
	@Mock
	private CisPaymentProfileResult cisPaymentProfileResult;
	@Mock
	private Observable<RawResponse<CisPaymentTransactionResult>> rawResponseTransactionResult;
	@Mock
	private CisPaymentTransactionResult cisPaymentTransactionResult;
	@Mock
	private CisAddress cisAddress;
	@Mock
	private CisCreditCard cisCreditCard;
	@Mock
	private AnnotationHashMap vendorResponses;
	@Mock
	private Map vendorResponsesMap;

	@Before
	public void setup()
	{
		populator = new CisCreateSubscriptionResultPopulator();
		MockitoAnnotations.initMocks(this.getClass());
	}

	@Test
	public void shouldPopulate() throws Exception
	{
		final CreateSubscriptionResult result = new CreateSubscriptionResult();
		result.setAuthReplyData(new AuthReplyData());
		result.setSubscriptionInfoData(new SubscriptionInfoData());
		result.setCustomerInfoData(new CustomerInfoData());
		result.setOrderInfoData(new OrderInfoData());
		result.setPaymentInfoData(new PaymentInfoData());
		result.setSignatureData(new SignatureData());

		when(vendorResponses.convertToMap()).thenReturn(vendorResponsesMap);
		when(cisPaymentProfileResult.getVendorResponses()).thenReturn(vendorResponses);
		when(cisPaymentProfileResult.getDecision()).thenReturn(CisDecision.ACCEPT);
		when(cisPaymentProfileResult.getVendorReasonCode()).thenReturn("10");
		when(cisPaymentProfileResult.getId()).thenReturn("subscriptionIdValue");
		when(cisPaymentProfileResult.getHref()).thenReturn(AUTH_LOCATION);
		when(cisPaymentProfileResult.getAmount()).thenReturn(BigDecimal.TEN);
		when(cisPaymentProfileResult.getValidationResult()).thenReturn(cisPaymentTransactionResult);
		when(cisPaymentProfileResult.getCustomerAddress()).thenReturn(cisAddress);
		when(cisPaymentProfileResult.getComments()).thenReturn("comments");
		when(cisPaymentProfileResult.getCreditCard()).thenReturn(cisCreditCard);
		when(cisPaymentProfileResult.getCurrency()).thenReturn("USD");
		when(cisPaymentProfileResult.getClientAuthorizationId()).thenReturn("clientAuthorizationId");
		when(cisPaymentProfileResult.getTransactionVerificationKey()).thenReturn("transactionVerificationKey");
		when(cisPaymentTransactionResult.getVendorStatusCode()).thenReturn("vendorStatusCode");
		when(cisPaymentTransactionResult.getVendorReasonCode()).thenReturn("123");
		when(cisAddress.getCity()).thenReturn("city");
		when(cisAddress.getCompany()).thenReturn("company");
		when(cisAddress.getFirstName()).thenReturn("firstName");
		when(cisAddress.getLastName()).thenReturn("lastName");
		when(cisAddress.getPhone()).thenReturn("phone");
		when(cisAddress.getZipCode()).thenReturn("zipcode");
		when(cisAddress.getEmail()).thenReturn("email");
		when(cisAddress.getState()).thenReturn("state");
		when(cisAddress.getAddressLine1()).thenReturn("line1");
		when(cisAddress.getAddressLine2()).thenReturn("line2");
		when(cisAddress.getCountry()).thenReturn("country");
		when(cisCreditCard.getCcNumber()).thenReturn("creditCardNumber");
		when(Integer.valueOf(cisCreditCard.getExpirationMonth())).thenReturn(Integer.valueOf(1));
		when(Integer.valueOf(cisCreditCard.getExpirationYear())).thenReturn(Integer.valueOf(2015));

		populator.populate(cisPaymentProfileResult, result);

		assertEquals(CisDecision.ACCEPT.name(), result.getDecision());
		assertEquals(Integer.valueOf(10), result.getReasonCode());
		assertNull(result.getDecisionPublicSignature());

		assertNotNull(result.getSubscriptionInfoData());
		assertEquals(AUTH_LOCATION, result.getSubscriptionInfoData().getSubscriptionID());
		assertEquals("subscriptionIdValue", result.getSubscriptionInfoData().getSubscriptionSignedValue());

		assertNotNull(result.getAuthReplyData());
		assertEquals(BigDecimal.TEN, result.getAuthReplyData().getCcAuthReplyAmount());
		assertEquals("vendorStatusCode", result.getAuthReplyData().getCcAuthReplyAuthorizationCode());
		assertNull(result.getAuthReplyData().getCcAuthReplyAuthorizedDateTime());
		assertNull(result.getAuthReplyData().getCcAuthReplyAvsCode());
		assertNull(result.getAuthReplyData().getCcAuthReplyAvsCodeRaw());
		assertNull(result.getAuthReplyData().getCcAuthReplyCvCode());
		assertNull(result.getAuthReplyData().getCcAuthReplyProcessorResponse());
		assertEquals(Integer.valueOf(123), result.getAuthReplyData().getCcAuthReplyReasonCode());

		assertNotNull(result.getCustomerInfoData());
		assertEquals("city", result.getCustomerInfoData().getBillToCity());
		assertEquals("company", result.getCustomerInfoData().getBillToCompany());
		assertNull(result.getCustomerInfoData().getBillToCompanyTaxId());
		assertNull(result.getCustomerInfoData().getBillToCustomerIdRef());
		assertNull(result.getCustomerInfoData().getBillToDateOfBirth());
		assertEquals("email", result.getCustomerInfoData().getBillToEmail());
		assertEquals("firstName", result.getCustomerInfoData().getBillToFirstName());
		assertEquals("lastName", result.getCustomerInfoData().getBillToLastName());
		assertEquals("phone", result.getCustomerInfoData().getBillToPhoneNumber());
		assertEquals("zipcode", result.getCustomerInfoData().getBillToPostalCode());
		assertEquals("state", result.getCustomerInfoData().getBillToState());
		assertEquals("line1", result.getCustomerInfoData().getBillToStreet1());
		assertEquals("line2", result.getCustomerInfoData().getBillToStreet2());
		assertEquals("COUNTRY", result.getCustomerInfoData().getBillToCountry());

		assertNotNull(result.getOrderInfoData());
		assertEquals("comments", result.getOrderInfoData().getComments());
		assertNull(result.getOrderInfoData().getOrderNumber());
		assertNull(result.getOrderInfoData().getOrderPageRequestToken());
		assertNull(result.getOrderInfoData().getOrderPageTransactionType());
		assertNull(result.getOrderInfoData().getSubscriptionTitle());
		assertNull(result.getOrderInfoData().getTaxAmount());

		assertNotNull(result.getPaymentInfoData());
		assertEquals("creditCardNumber", result.getPaymentInfoData().getCardAccountNumber());
		assertEquals(Integer.valueOf(1), result.getPaymentInfoData().getCardExpirationMonth());
		assertEquals(Integer.valueOf(2015), result.getPaymentInfoData().getCardExpirationYear());
		assertNull(result.getPaymentInfoData().getCardStartMonth());
		assertNull(result.getPaymentInfoData().getCardStartYear());
		assertNull(result.getPaymentInfoData().getPaymentOption());

		assertNotNull(result.getSignatureData());
		assertEquals(BigDecimal.TEN, result.getSignatureData().getAmount());
		assertNull(result.getSignatureData().getAmountPublicSignature());
		assertEquals("USD", result.getSignatureData().getCurrency());
		assertNull(result.getSignatureData().getCurrencyPublicSignature());
		assertEquals("clientAuthorizationId", result.getSignatureData().getMerchantID());
		assertNull(result.getSignatureData().getOrderPageSerialNumber());
		assertNull(result.getSignatureData().getOrderPageVersion());
		assertNull(result.getSignatureData().getSignedFields());
		assertEquals("transactionVerificationKey", result.getSignatureData().getTransactionSignature());

	}

}
