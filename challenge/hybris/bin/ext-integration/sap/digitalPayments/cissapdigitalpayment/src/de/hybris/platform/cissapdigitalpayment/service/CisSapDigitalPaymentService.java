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
package de.hybris.platform.cissapdigitalpayment.service;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCardDeletionResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentChargeResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRefundResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentRegistrationUrlResult;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;

import java.util.concurrent.TimeoutException;

import com.hybris.cis.service.CisClientService;

import rx.Observable;


/**
 * CIS service which exposes the payment functionalities of SAP Digital Payments Addon
 */
public interface CisSapDigitalPaymentService extends CisClientService
{

	/**
	 * Retrieves the payment service url from SAP Digital Payments Addon
	 *
	 * @return a cisSapDigitalPaymentRegistrationUrlResult response with the card registration url
	 */
	Observable<CisSapDigitalPaymentRegistrationUrlResult> getRegistrationUrl() throws TimeoutException;


	/**
	 * Requests card details from SAP Digital Payments Addon
	 *
	 * @param sessionId
	 *           - used to fetch the card details upon registration
	 * @param sapDigiPayConfig
	 *           - sap digital payment configuration
	 * @return registered card details
	 */
	Observable<CisSapDigitalPaymentPollRegisteredCardResult> pollRegisteredCard(String sessionId,
			SAPDigitalPaymentConfigurationModel sapDigiPayConfig);


	/**
	 * Requests an authorization of a payment
	 *
	 * @param authorizationRequests
	 *           - external payment request
	 * @return authorization result
	 */
	Observable<CisSapDigitalPaymentAuthorizationResultList> authorizePayment(
			CisSapDigitalPaymentAuthorizationRequestList authorizationRequests);


	/**
	 * Requests deletion of registered card
	 *
	 * @param deletCardRequests
	 *           - external payment request
	 * @param sapDigitalPaymentConfigurationStrategy
	 *           - sap digital payment configuration strategy
	 * @return Delete card result list
	 */
	Observable<CisSapDigitalPaymentCardDeletionResultList> deleteCard(
			CisSapDigitalPaymentCardDeletionRequestList deletCardRequests,
			SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy);

	/**
	 * Requests an charge of a payment
	 *
	 * @param chargeRequests
	 *           - payment settlement request list
	 * @param sapDigitalPaymentConfig
	 *           - sap digital payment configuration
	 * @return CisSapDigitalPaymentChargeResultList
	 */
	Observable<CisSapDigitalPaymentChargeResultList> chargePayment(CisSapDigitalPaymentChargeRequestList chargeRequests,
			SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig);


	/**
	 * Requests an refund of a payment
	 *
	 * @param refundRequests
	 *           - refund request list
	 * @param sapDigitalPaymentConfig
	 *           - sap digital payment configuration
	 * @return CisSapDigitalPaymentRefundResultList
	 */
	Observable<CisSapDigitalPaymentRefundResultList> refundPayment(CisSapDigitalPaymentRefundRequestList refundRequests,
			SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig);



}
