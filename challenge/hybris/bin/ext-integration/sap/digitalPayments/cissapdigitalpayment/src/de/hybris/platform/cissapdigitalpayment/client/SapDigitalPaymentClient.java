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
package de.hybris.platform.cissapdigitalpayment.client;

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

import java.util.concurrent.TimeoutException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.OAuth;
import com.hybris.cis.client.CisClient;

import rx.Observable;


/**
 *
 *
 * Client to connect to SAP Digital payment using Charon API.
 */
@OAuth
public interface SapDigitalPaymentClient extends CisClient
{
	/**
	 * Fetch the Registration URL and the session ID from SAP Digital payment
	 *
	 * @param CompanyCode
	 * @param CustomerCountry
	 * @param PaymentMethod
	 * @param PaymentType
	 * @return CisSapDigitalPaymentRegistrationUrlResult
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/getregistrationurl?CompanyCode=${CompanyCode}&CustomerCountry=${CustomerCountry}&PaymentMethod=${PaymentMethod}&RoutingCustomParameterValue=${RoutingCustomParameterValue}")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentRegistrationUrlResult> getRegistrationUrl() throws TimeoutException;


	/**
	 * Poll the registered card using the session ID
	 *
	 * @param sessionId
	 * @return CisSapDigitalPaymentPollRegisteredCardResult
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cards/poll/{sessionId}")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentPollRegisteredCardResult> pollRegisteredCard(@PathParam("sessionId") final String sessionId);


	/**
	 * Authorize the payment
	 *
	 * @param authorizationRequests
	 * @return CisSapDigitalPaymentAuthorizationResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/authorizations")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentAuthorizationResultList> authorizatePayment(
			final CisSapDigitalPaymentAuthorizationRequestList authorizationRequests);

	/**
	 * Delete the card
	 *
	 * @param deletCardRequests
	 * @return CisSapDigitalPaymentCardDeletionResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/cards/delete")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentCardDeletionResultList> deleteCard(
			final CisSapDigitalPaymentCardDeletionRequestList deletCardRequests);


	/**
	 * Charge the payment
	 *
	 * @param chargeRequests
	 * @return CisSapDigitalPaymentChargeResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/charges")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentChargeResultList> chargePayment(final CisSapDigitalPaymentChargeRequestList chargeRequests);


	/**
	 * Refund the payment
	 *
	 * @param refundRequests
	 * @return CisSapDigitalPaymentRefundResultList
	 *
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/refunds")
	@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
	Observable<CisSapDigitalPaymentRefundResultList> refundPayment(final CisSapDigitalPaymentRefundRequestList refundRequests);



}
