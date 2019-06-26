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
 *
 */
package com.hybris.cis.client.mock;


import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.charon.RawResponse;
import com.hybris.cis.client.payment.PaymentClient;
import com.hybris.cis.client.payment.models.CisCreditCard;
import com.hybris.cis.client.payment.models.CisExternalPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentAuthorization;
import com.hybris.cis.client.payment.models.CisPaymentProfileRequest;
import com.hybris.cis.client.payment.models.CisPaymentProfileResult;
import com.hybris.cis.client.payment.models.CisPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentSessionInitRequest;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentAuthorization;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentCapture;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentRefund;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentReverse;
import com.hybris.cis.client.payment.models.CisTokenizedPaymentTransactionResult;
import com.hybris.cis.client.shared.models.AnnotationHashMap;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisCvnDecision;
import com.hybris.cis.client.shared.models.CisDecision;

import rx.Observable;

import static org.mockito.Mockito.when;


public class PaymentClientMock extends SharedClientMock implements PaymentClient
{
	private final static Logger LOGGER = LoggerFactory.getLogger(PaymentClientMock.class);

	private String handleExternalAuthLocation;
	private String captureLocation;
	private String authorizeWithProfileLocation;
	private String reverseLocation;
	private String refundLocation;
	private String addCustomerProfileLocation;
	private String updateCustomerProfileLocation;
	private String deleteCustomerProfileLocation;
	private String initPaymentSessionLocation;
	private String paymentOrderSetupLocation;
	private String tokenizedPaymentAuthorizationLocation;
	private String tokenizedPaymentCaptureLocation;
	private String tokenizedPaymentRefundLocation;
	private String tokenizedPaymentReverseLocation;
	private String pspUrlLocation;

	public PaymentClientMock()
	{
		LOGGER.info("Using MOCK Client to simulate Payment.");
	}

	@Override
	public RawResponse<String> pspUrl(final String xCisClientRef, final String tenantId)
	{
		LOGGER.info("Using MOCK Client - pspUrl()");
		final RawResponse<String> pspUrlResponse = Mockito.mock(RawResponse.class);
		try
		{
			when(pspUrlResponse.header("location")).thenReturn(Optional.of(pspUrlLocation));
			when(pspUrlResponse.location()).thenReturn(Optional.of(new URL(pspUrlLocation)));
		}
		catch (final MalformedURLException e)
		{
			LOGGER.error("Error to mock pspUrlLocation!");
		}
		return pspUrlResponse;
	}

	@Override
	public CisPaymentTransactionResult handleExternalAuthorization(final String xCisClientRef, final String tenantId,
			final CisExternalPaymentRequest cisExternalPayment)
	{
		LOGGER.info("Using MOCK Client - handleExternalAuthorization()");

		final CisPaymentRequest cisPaymentRequest = new CisPaymentRequest();
		cisPaymentRequest.setAmount(new BigDecimal("30.00"));
		cisPaymentRequest.setCurrency("usd");

		final CisPaymentTransactionResult cisPaymentTransactionResult = new CisPaymentTransactionResult();
		cisPaymentTransactionResult.setHref(handleExternalAuthLocation);
		cisPaymentTransactionResult.setClientRefId("TEST-ORDER-1");
		cisPaymentTransactionResult.setVendorId("cybersource");
		cisPaymentTransactionResult.setVendorReasonCode("100");
		cisPaymentTransactionResult.setVendorStatusCode("ACCEPT");
		cisPaymentTransactionResult.setId("3418696812220176056470");
		cisPaymentTransactionResult.setDecision(CisDecision.ACCEPT);
		cisPaymentTransactionResult.setRequest(cisPaymentRequest);
		cisPaymentTransactionResult.setAmount(new BigDecimal("30.00"));

		return cisPaymentTransactionResult;
	}

	@Override
	public CisPaymentTransactionResult authorizeWithProfile(final String xCisClientRef, final String tenantId,
			final String profileId, final CisPaymentAuthorization cisPaymentAuthorization)
	{
		LOGGER.info("Using MOCK Client - authorizeWithProfile()");

		final CisPaymentRequest cisPaymentRequest = new CisPaymentRequest();
		cisPaymentRequest.setAmount(cisPaymentAuthorization.getAmount());
		cisPaymentRequest.setCurrency(cisPaymentAuthorization.getCurrency());

		final CisPaymentTransactionResult validationResult = new CisPaymentTransactionResult();
		validationResult.setHref("http://localhost:9001/some/profile/profileid/authorization/authorizationid");
		validationResult.setId("VA123");
		validationResult.setVendorReasonCode("100");
		validationResult.setVendorStatusCode("888888");
		validationResult.setDecision(CisDecision.ACCEPT);
		validationResult.setTransactionVerificationKey("transactionVerificationKey");
		validationResult.setRequest(cisPaymentRequest);


		final Map<String, String> parameters = cisPaymentAuthorization.getVendorParameters().convertToMap();

		final CisPaymentProfileResult profile = new CisPaymentProfileResult();

		final String decision = parameters.get("decision");

		if ("REJECT".equals(decision))
		{
			profile.setDecision(CisDecision.REJECT);
		}
		else
		{
			profile.setDecision(CisDecision.ACCEPT);
		}

		final String subscriptionID = parameters.get("paySubscriptionCreateReply_subscriptionID");
		profile.setId(subscriptionID == null ? "P123" : subscriptionID);
		profile.setVendorReasonCode("100");
		profile.setValidationResult(validationResult);
		profile.setAmount(BigDecimal.ZERO);
		profile.setCvnDecision(CisCvnDecision.ACCEPT);
		profile.setCurrency("USD");
		profile.setRequest(cisPaymentRequest);
		final String orderAmount = parameters.get("orderAmount");
		if (orderAmount != null)
		{
			final BigDecimal amount = new BigDecimal(orderAmount);
			validationResult.setAmount(amount);
			profile.setAmount(amount);
		}
		profile.setTransactionVerificationKey("transactionVerificationKey");
		profile.setVendorResponses(new AnnotationHashMap());

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("ccAuthReply_cvCode", "M");
		map.put("decision_publicSignature", "decision_publicSignature");
		map.put("signedFields",
				"billTo_lastName,ccAuthReply_cardBIN,recurringSubscriptionInfo_numberOfPayments,orderAmount,billTo_street1,card_accountNumber,orderAmount_publicSignature,orderPage_serialNumber,recurringSubscriptionInfo_automaticRenew,orderCurrency,paySubscriptionCreateReply_subscriptionIDPublicSignature,recurringSubscriptionInfo_frequencyPublicSignature,reconciliationID,ccAuthReply_cvCode,recurringSubscriptionInfo_numberOfPaymentsPublicSignature,decision,_savePaymentInfo,ccAuthReply_processorResponse,ccAuthReply_cvCodeRaw,billTo_state,billTo_firstName,recurringSubscriptionInfo_automaticRenewPublicSignature,card_expirationYear,_useDeliveryAddress,recurringSubscriptionInfo_startDatePublicSignature,billTo_city,billTo_postalCode,orderPage_requestToken,ccAuthReply_amount,orderCurrency_publicSignature,recurringSubscriptionInfo_amount,paySubscriptionCreateReply_subscriptionID,orderPage_transactionType,ccAuthReply_authorizationCode,decision_publicSignature,ccAuthReply_avsCodeRaw,paymentOption,billTo_country,billTo_email,useDeliveryAddress,reasonCode,recurringSubscriptionInfo_frequency,ccAuthReply_reasonCode,orderPage_environment,recurringSubscriptionInfo_amountPublicSignature,card_nameOnCard,card_expirationMonth,merchantID,orderNumber_publicSignature,requestID,orderNumber,ccAuthReply_authorizedDateTime,card_cardType,ccAuthReply_avsCode,recurringSubscriptionInfo_startDate");
		map.put("orderNumber_publicSignature", "orderNumber_publicSignature");
		map.put("ccAuthReply_authorizedDateTime", new Date().toString());
		map.put("paySubscriptionCreateReply_subscriptionIDPublicSignature",
				parameters.get("paySubscriptionCreateReply_subscriptionIDPublicSignature"));
		map.put("ccAuthReply_avsCode", "X");
		profile.setVendorResponses(new AnnotationHashMap(map));

		profile.setCreditCard(new CisCreditCard());
		profile.getCreditCard().setCardType("001");
		profile.getCreditCard().setCcNumber("############1111");
		profile.getCreditCard().setExpirationMonth(6);
		profile.getCreditCard().setExpirationYear(20);

		profile.setCustomerAddress(new CisAddress());
		profile.getCustomerAddress().setFirstName("mock");
		profile.getCustomerAddress().setLastName("mock");
		profile.getCustomerAddress().setEmail("mock@hyhbris.com");
		profile.getCustomerAddress().setAddressLine1("mockLine1");
		profile.getCustomerAddress().setAddressLine2("mockLine2");
		profile.getCustomerAddress().setZipCode("mockZip");
		profile.getCustomerAddress().setCity("mockCity");
		profile.getCustomerAddress().setState("NY");
		profile.getCustomerAddress().setCountry("us");


		return profile;
	}

	@Override
	public CisPaymentTransactionResult capture(final String xCisClientRef, final String tenantId, final String authGroupId,
			final String transactionId, final CisPaymentRequest paymentRequest)
	{
		LOGGER.info("Using MOCK Client - capture()");

		final CisPaymentTransactionResult cisPaymentTransactionResult = new CisPaymentTransactionResult();
		cisPaymentTransactionResult.setHref(captureLocation);
		cisPaymentTransactionResult.setRequest(paymentRequest);
		cisPaymentTransactionResult.setAmount(paymentRequest.getAmount());
		cisPaymentTransactionResult.setDecision(CisDecision.ACCEPT);
		return cisPaymentTransactionResult;
	}

	@Override
	public CisPaymentTransactionResult reverse(final String xCisClientRef, final String tenantId, final String authGroupId,
			final String transactionId, final CisPaymentRequest paymentRequest)
	{
		LOGGER.info("Using MOCK Client - reverse()");

		final CisPaymentTransactionResult cisPaymentTransactionResult = new CisPaymentTransactionResult();
		cisPaymentTransactionResult.setHref(reverseLocation);
		return cisPaymentTransactionResult;
	}

	@Override
	public CisPaymentTransactionResult refund(final String xCisClientRef, final String tenantId, final String authGroupId,
			final String transactionId, final CisPaymentRequest paymentRequest)
	{
		LOGGER.info("Using MOCK Client - refund()");

		final CisPaymentTransactionResult cisPaymentTransactionResult = new CisPaymentTransactionResult();
		cisPaymentTransactionResult.setHref(refundLocation);
		return cisPaymentTransactionResult;
	}

	@Override
	public RawResponse<CisPaymentProfileResult> addCustomerProfile(final String xCisClientRef, final String tenantId,
			final String documentId, final CisExternalPaymentRequest cisExternalPayment)
	{
		LOGGER.info("Using MOCK Client - addCustomerProfile()");

		final CisPaymentTransactionResult validationResult = new CisPaymentTransactionResult();
		validationResult.setId("VA123");
		validationResult.setVendorReasonCode("100");
		validationResult.setVendorStatusCode("888888");
		validationResult.setDecision(CisDecision.ACCEPT);
		validationResult.setTransactionVerificationKey("transactionVerificationKey");
		validationResult.setHref("http://localhost:9001/some/profile");

		final Map<String, String> parameters = cisExternalPayment.getParameters().convertToMap();

		final CisPaymentProfileResult profile = new CisPaymentProfileResult();

		final String decision = parameters.get("decision");

		if ("REJECT".equals(decision))
		{
			profile.setDecision(CisDecision.REJECT);
		}
		else
		{
			profile.setDecision(CisDecision.ACCEPT);
		}

		final String subscriptionID = parameters.get("paySubscriptionCreateReply_subscriptionID");
		profile.setId(subscriptionID == null ? "P123" : subscriptionID);
		profile.setVendorReasonCode("100");
		profile.setValidationResult(validationResult);
		profile.setAmount(BigDecimal.ZERO);
		profile.setCvnDecision(CisCvnDecision.ACCEPT);

		final String orderAmount = parameters.get("orderAmount");
		if (orderAmount != null)
		{
			final BigDecimal amount = new BigDecimal(orderAmount);
			validationResult.setAmount(amount);
			profile.setAmount(amount);
		}
		profile.setTransactionVerificationKey("transactionVerificationKey");
		profile.setVendorResponses(new AnnotationHashMap());

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("ccAuthReply_cvCode", "M");
		map.put("decision_publicSignature", "decision_publicSignature");
		map.put("signedFields",
				"billTo_lastName,ccAuthReply_cardBIN,recurringSubscriptionInfo_numberOfPayments,orderAmount,billTo_street1,card_accountNumber,orderAmount_publicSignature,orderPage_serialNumber,recurringSubscriptionInfo_automaticRenew,orderCurrency,paySubscriptionCreateReply_subscriptionIDPublicSignature,recurringSubscriptionInfo_frequencyPublicSignature,reconciliationID,ccAuthReply_cvCode,recurringSubscriptionInfo_numberOfPaymentsPublicSignature,decision,_savePaymentInfo,ccAuthReply_processorResponse,ccAuthReply_cvCodeRaw,billTo_state,billTo_firstName,recurringSubscriptionInfo_automaticRenewPublicSignature,card_expirationYear,_useDeliveryAddress,recurringSubscriptionInfo_startDatePublicSignature,billTo_city,billTo_postalCode,orderPage_requestToken,ccAuthReply_amount,orderCurrency_publicSignature,recurringSubscriptionInfo_amount,paySubscriptionCreateReply_subscriptionID,orderPage_transactionType,ccAuthReply_authorizationCode,decision_publicSignature,ccAuthReply_avsCodeRaw,paymentOption,billTo_country,billTo_email,useDeliveryAddress,reasonCode,recurringSubscriptionInfo_frequency,ccAuthReply_reasonCode,orderPage_environment,recurringSubscriptionInfo_amountPublicSignature,card_nameOnCard,card_expirationMonth,merchantID,orderNumber_publicSignature,requestID,orderNumber,ccAuthReply_authorizedDateTime,card_cardType,ccAuthReply_avsCode,recurringSubscriptionInfo_startDate");
		map.put("orderNumber_publicSignature", "orderNumber_publicSignature");
		map.put("ccAuthReply_authorizedDateTime", new Date().toString());
		map.put("paySubscriptionCreateReply_subscriptionIDPublicSignature",
				parameters.get("paySubscriptionCreateReply_subscriptionIDPublicSignature"));
		map.put("ccAuthReply_avsCode", "X");
		map.put("paySubscriptionCreateReply_subscriptionID", "VA123");
		profile.setVendorResponses(new AnnotationHashMap(map));

		profile.setCreditCard(new CisCreditCard());
		profile.getCreditCard().setCardType("001");
		profile.getCreditCard().setCcNumber("############1111");
		profile.getCreditCard().setExpirationMonth(6);
		profile.getCreditCard().setExpirationYear(20);

		profile.setCustomerAddress(new CisAddress());
		profile.getCustomerAddress().setFirstName("mock");
		profile.getCustomerAddress().setLastName("mock");
		profile.getCustomerAddress().setEmail("mock@hyhbris.com");
		profile.getCustomerAddress().setAddressLine1("mockLine1");
		profile.getCustomerAddress().setAddressLine2("mockLine2");
		profile.getCustomerAddress().setZipCode("mockZip");
		profile.getCustomerAddress().setCity("mockCity");
		profile.getCustomerAddress().setState("NY");
		profile.getCustomerAddress().setCountry("us");
		profile.setHref("http://localhost:9001/some/profile");

		final RawResponse<CisPaymentProfileResult> addCustomerResponse = Mockito.mock(RawResponse.class);
		final Observable<CisPaymentProfileResult> addCustomerResponseContent = Observable.just(profile);
		when(addCustomerResponse.content()).thenReturn(addCustomerResponseContent);
		when(addCustomerResponse.header("location")).thenReturn(Optional.of(profile.getHref()));
		return addCustomerResponse;
	}

	@Override
	public CisPaymentProfileResult updateCustomerProfile(final String xCisClientRef, final String tenantId, final String profileId,
			final CisPaymentProfileRequest cisPaymentProfileRequest)
	{
		LOGGER.info("Using MOCK Client - updateCustomerProfile()");

		final CisPaymentProfileResult cisPaymentProfileResult = new CisPaymentProfileResult();
		cisPaymentProfileResult.setHref(updateCustomerProfileLocation);
		return cisPaymentProfileResult;
	}

	@Override
	public String deleteCustomerProfile(final String xCisClientRef, final String tenantId, final String profileId)
	{
		LOGGER.info("Using MOCK Client - deleteCustomerProfile()");

		return deleteCustomerProfileLocation;
	}

	@Override
	public CisTokenizedPaymentTransactionResult initPaymentSession(final String xCisClientRef, final String tenantId,
			final CisPaymentSessionInitRequest cisPaymentSessionInitRequest)
	{
		LOGGER.info("Using MOCK Client - initPaymentSession()");

		final CisTokenizedPaymentTransactionResult tokenizedPaymentTransactionResult = new CisTokenizedPaymentTransactionResult();
		tokenizedPaymentTransactionResult.setHref(initPaymentSessionLocation);
		return tokenizedPaymentTransactionResult;
	}

	@Override
	public CisTokenizedPaymentTransactionResult paymentOrderSetup(final String xCisClientRef, final String tenantId,
			final String authGroupId, final CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization)
	{
		LOGGER.info("Using MOCK Client - paymentOrderSetup()");

		final CisTokenizedPaymentTransactionResult tokenizedPaymentTransactionResult = new CisTokenizedPaymentTransactionResult();
		tokenizedPaymentTransactionResult.setHref(paymentOrderSetupLocation);
		return tokenizedPaymentTransactionResult;
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentAuthorization(final String xCisClientRef, final String tenantId,
			final CisTokenizedPaymentAuthorization cisTokenizedPaymentAuthorization)
	{
		LOGGER.info("Using MOCK Client - tokenizedPaymentAuthorization()");

		final CisTokenizedPaymentTransactionResult tokenizedPaymentTransactionResult = new CisTokenizedPaymentTransactionResult();
		tokenizedPaymentTransactionResult.setHref(tokenizedPaymentAuthorizationLocation);
		return tokenizedPaymentTransactionResult;
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentCapture(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentCapture cisTokenizedPaymentCapture)
	{
		LOGGER.info("Using MOCK Client - tokenizedPaymentCapture()");

		final CisTokenizedPaymentTransactionResult tokenizedPaymentTransactionResult = new CisTokenizedPaymentTransactionResult();
		tokenizedPaymentTransactionResult.setHref(tokenizedPaymentCaptureLocation);
		return tokenizedPaymentTransactionResult;
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentRefund(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final String transactionId,
			final CisTokenizedPaymentRefund cisTokenizedPaymentRefund)
	{
		LOGGER.info("Using MOCK Client - tokenizedPaymentRefund()");

		final CisTokenizedPaymentTransactionResult tokenizedPaymentTransactionResult = new CisTokenizedPaymentTransactionResult();
		tokenizedPaymentTransactionResult.setHref(tokenizedPaymentRefundLocation);
		return tokenizedPaymentTransactionResult;
	}

	@Override
	public CisTokenizedPaymentTransactionResult tokenizedPaymentReverse(final String xCisClientRef, final String tenantId,
			final String authGroupId, final String authId, final CisTokenizedPaymentReverse cisTokenizedPaymentReverse)
	{
		LOGGER.info("Using MOCK Client - tokenizedPaymentReverse()");

		final CisTokenizedPaymentTransactionResult tokenizedPaymentTransactionResult = new CisTokenizedPaymentTransactionResult();
		tokenizedPaymentTransactionResult.setHref(tokenizedPaymentReverseLocation);
		return tokenizedPaymentTransactionResult;
	}

	public String getHandleExternalAuthLocation()
	{
		return handleExternalAuthLocation;
	}

	public void setHandleExternalAuthLocation(final String handleExternalAuthLocation)
	{
		this.handleExternalAuthLocation = handleExternalAuthLocation;
	}

	public String getCaptureLocation()
	{
		return captureLocation;
	}

	public void setCaptureLocation(final String captureLocation)
	{
		this.captureLocation = captureLocation;
	}

	public String getAuthorizeWithProfileLocation()
	{
		return authorizeWithProfileLocation;
	}

	public void setAuthorizeWithProfileLocation(final String authorizeWithProfileLocation)
	{
		this.authorizeWithProfileLocation = authorizeWithProfileLocation;
	}

	public String getReverseLocation()
	{
		return reverseLocation;
	}

	public void setReverseLocation(final String reverseLocation)
	{
		this.reverseLocation = reverseLocation;
	}

	public String getRefundLocation()
	{
		return refundLocation;
	}

	public void setRefundLocation(final String refundLocation)
	{
		this.refundLocation = refundLocation;
	}

	public String getAddCustomerProfileLocation()
	{
		return addCustomerProfileLocation;
	}

	public void setAddCustomerProfileLocation(final String addCustomerProfileLocation)
	{
		this.addCustomerProfileLocation = addCustomerProfileLocation;
	}

	public String getUpdateCustomerProfileLocation()
	{
		return updateCustomerProfileLocation;
	}

	public void setUpdateCustomerProfileLocation(final String updateCustomerProfileLocation)
	{
		this.updateCustomerProfileLocation = updateCustomerProfileLocation;
	}

	public String getInitPaymentSessionLocation()
	{
		return initPaymentSessionLocation;
	}

	public void setInitPaymentSessionLocation(final String initPaymentSessionLocation)
	{
		this.initPaymentSessionLocation = initPaymentSessionLocation;
	}

	public String getPaymentOrderSetupLocation()
	{
		return paymentOrderSetupLocation;
	}

	public void setPaymentOrderSetupLocation(final String paymentOrderSetupLocation)
	{
		this.paymentOrderSetupLocation = paymentOrderSetupLocation;
	}

	public String getTokenizedPaymentAuthorizationLocation()
	{
		return tokenizedPaymentAuthorizationLocation;
	}

	public void setTokenizedPaymentAuthorizationLocation(final String tokenizedPaymentAuthorizationLocation)
	{
		this.tokenizedPaymentAuthorizationLocation = tokenizedPaymentAuthorizationLocation;
	}

	public String getTokenizedPaymentCaptureLocation()
	{
		return tokenizedPaymentCaptureLocation;
	}

	public void setTokenizedPaymentCaptureLocation(final String tokenizedPaymentCaptureLocation)
	{
		this.tokenizedPaymentCaptureLocation = tokenizedPaymentCaptureLocation;
	}

	public String getTokenizedPaymentRefundLocation()
	{
		return tokenizedPaymentRefundLocation;
	}

	public void setTokenizedPaymentRefundLocation(final String tokenizedPaymentRefundLocation)
	{
		this.tokenizedPaymentRefundLocation = tokenizedPaymentRefundLocation;
	}

	public String getTokenizedPaymentReverseLocation()
	{
		return tokenizedPaymentReverseLocation;
	}

	public void setTokenizedPaymentReverseLocation(final String tokenizedPaymentReverseLocation)
	{
		this.tokenizedPaymentReverseLocation = tokenizedPaymentReverseLocation;
	}

	public String getPspUrlLocation()
	{
		return pspUrlLocation;
	}

	public void setPspUrlLocation(final String pspUrlLocation)
	{
		this.pspUrlLocation = pspUrlLocation;
	}

	public String getDeleteCustomerProfileLocation()
	{
		return deleteCustomerProfileLocation;
	}

	public void setDeleteCustomerProfileLocation(final String deleteCustomerProfileLocation)
	{
		this.deleteCustomerProfileLocation = deleteCustomerProfileLocation;
	}
}
