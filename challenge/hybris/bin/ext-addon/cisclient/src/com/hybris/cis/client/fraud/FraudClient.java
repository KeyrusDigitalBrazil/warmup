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
package com.hybris.cis.client.fraud;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.cis.client.CisClient;
import com.hybris.cis.client.fraud.models.CisFraudReportRequest;
import com.hybris.cis.client.fraud.models.CisFraudReportResult;
import com.hybris.cis.client.fraud.models.CisFraudTransaction;
import com.hybris.cis.client.fraud.models.CisFraudTransactionResult;


/**
 * Charon Client to the CIS Fraud API
 */
@Http("fraud")
public interface FraudClient extends CisClient
{
	/**
	 * Gives back the plain text interpretation of an order status update.
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param request
	 *           order status update from 3rd party as a string
	 * @return the interpretation of the order status update
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/statusupdates")
	@Control(retries = "3", retriesInterval = "500")
	CisFraudTransactionResult handleOrderStatusUpdate(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
													  @HeaderParam(value = "X-tenantId") String tenantId, final String request);

	/**
	 * Gives back the XML interpretation of an order status update.
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param request
	 *           order status update from 3rd party as a string
	 * @return the interpretation of the order status update
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/statusupdates")
	@Control(retries = "3", retriesInterval = "500")
	CisFraudTransactionResult handleOrderStatusUpdateXML(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, final String request);

	/**
	 * Gives back all the orders that were updated in a given time frame.
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param request
	 *           fraud report requets
	 * @return the orders that were updated in the requested time frame
	 */

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/fraudreports")
	@Control(retries = "3", retriesInterval = "500")
	CisFraudReportResult generateFraudReport(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
											 @HeaderParam(value = "X-tenantId") String tenantId, final CisFraudReportRequest request);

	/**
	 * Calculate fraud score based on the transaction.
	 *
	 * @param xCisClientRef
	 *           client ref to pass in the header
	 * @param cisTransaction
	 *           transaction to calculate the fraud on
	 * @return the rest response containting the fraud transaction result
	 */
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/fraudresults")
	@Control(retries = "3", retriesInterval = "500")
	CisFraudTransactionResult calculateFraudScore(@HeaderParam(value = "X-CIS-Client-ref") String xCisClientRef,
			@HeaderParam(value = "X-tenantId") String tenantId, final CisFraudTransaction cisTransaction);

}


