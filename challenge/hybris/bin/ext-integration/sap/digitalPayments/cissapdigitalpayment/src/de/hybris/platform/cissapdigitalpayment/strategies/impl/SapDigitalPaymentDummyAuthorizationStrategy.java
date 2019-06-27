/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorization;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequest;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentTransactionResult;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentAuthorizationService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Dummy payment amount authorization strategy for {@link SapDigitalPaymentAuthorizationStrategy}
 */
public class SapDigitalPaymentDummyAuthorizationStrategy implements SapDigitalPaymentAuthorizationStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SapDigitalPaymentDummyAuthorizationStrategy.class);
	private SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService;
	private I18NService i18nService;


	@Override
	public boolean authorizePayment(final CommerceCheckoutParameter parameter)
	{
		LOG.info("Inside authorizePayment from SapDigitalPaymentDummyAuthorizationStrategy");

		final CartModel cartModel = parameter.getCart();
		final BigDecimal amount = BigDecimal.valueOf(0d);
		final Currency currency = getI18nService().getBestMatchingJavaCurrency(cartModel.getCurrency().getIsocode());
		final PaymentInfoModel paymentInfo = cartModel.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardPaymentInfoModel ccPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfo;

			final CisSapDigitalPaymentAuthorizationRequestList authorizationRequests = getSapDigitalPaymentAuthorizationService()
					.createAuthorizePaymentRequest(ccPaymentInfoModel.getSubscriptionId(), amount, currency.getCurrencyCode());

			//Calls  dummy authorization response method
			final CisSapDigitalPaymentAuthorizationResultList authorizationResultList = this
					.getDummyAuthorizationResponse(authorizationRequests);

			final PaymentTransactionEntryModel paymentTransactionEntryModel = getSapDigitalPaymentAuthorizationService()
					.processSapDigitalPaymentAuthorizationResult(parameter, authorizationResultList);

			return paymentTransactionEntryModel != null
					&& (TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
							|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus()));
		}
		return false;

	}


	/**
	 * Dummy payment authorization response
	 *
	 * @param authorizationRequests
	 *           - {@link CisSapDigitalPaymentAuthorizationRequestList}
	 * @return - {@link CisSapDigitalPaymentAuthorizationResultList}
	 */
	private CisSapDigitalPaymentAuthorizationResultList getDummyAuthorizationResponse(
			final CisSapDigitalPaymentAuthorizationRequestList authorizationRequests)
	{
		final CisSapDigitalPaymentAuthorizationRequest authReq = authorizationRequests
				.getCisSapDigitalPaymentAuthorizationRequests().stream().findFirst().get();

		final CisSapDigitalPaymentAuthorizationResultList dummyPaymentAuthResList = new CisSapDigitalPaymentAuthorizationResultList();
		final CisSapDigitalPaymentAuthorizationResult dummyPaymentAuthRes = new CisSapDigitalPaymentAuthorizationResult();
		dummyPaymentAuthRes.setCisSapDigitalPaymentSource(authReq.getCisSapDigitalPaymentSource());

		final CisSapDigitalPaymentTransactionResult paymentTransRes = new CisSapDigitalPaymentTransactionResult();
		paymentTransRes.setDigitalPaymentTransaction(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DUMMY_STRING);
		final DateFormat df = new SimpleDateFormat(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_AUTH_DATE_FORMAT);
		paymentTransRes.setDigitalPaymentDateTime(df.format(new Date()));
		paymentTransRes.setDigitalPaytTransResult(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_TRANS_RES);
		paymentTransRes.setDigitalPaytTransRsltDesc(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_TRANS_RES_DESC);
		dummyPaymentAuthRes.setCisSapDigitalPaymentTransactionResult(paymentTransRes);

		final CisSapDigitalPaymentAuthorization paymentAuthRes = new CisSapDigitalPaymentAuthorization();
		paymentAuthRes.setAuthorizationByAcquirer(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DUMMY_STRING);
		paymentAuthRes.setAuthorizationByPaytSrvcPrvdr(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DUMMY_STRING);
		paymentAuthRes.setAuthorizationByDigitalPaytSrvc(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DUMMY_STRING);
		paymentAuthRes.setAuthorizationCurrency(authReq.getAuthorizationCurrency());
		paymentAuthRes.setAuthorizedAmountInAuthznCrcy(authReq.getAmountInAuthorizationCurrency());
		paymentAuthRes.setAuthorizationDateTime(df.format(new Date()));
		paymentAuthRes.setAuthorizationStatus(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_TRANS_RES);
		paymentAuthRes.setAuthorizationStatusName(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DESC);
		dummyPaymentAuthRes.setCisSapDigitalPaymentAuthorization(paymentAuthRes);

		dummyPaymentAuthResList.setCisSapDigitalPaymentAuthorizationResults(Arrays.asList(dummyPaymentAuthRes));
		return dummyPaymentAuthResList;


	}




	public SapDigitalPaymentAuthorizationService getSapDigitalPaymentAuthorizationService()
	{
		return sapDigitalPaymentAuthorizationService;
	}


	public void setSapDigitalPaymentAuthorizationService(
			final SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService)
	{
		this.sapDigitalPaymentAuthorizationService = sapDigitalPaymentAuthorizationService;
	}


	public I18NService getI18nService()
	{
		return i18nService;
	}


	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}



}
