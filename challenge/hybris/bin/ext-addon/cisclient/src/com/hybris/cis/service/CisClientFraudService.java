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
package com.hybris.cis.service;


import com.hybris.cis.client.fraud.models.CisFraudReportRequest;
import com.hybris.cis.client.fraud.models.CisFraudReportResult;
import com.hybris.cis.client.fraud.models.CisFraudTransaction;
import com.hybris.cis.client.fraud.models.CisFraudTransactionResult;

import java.net.URI;


/**
 * Interface proving Fraud services.
 */
public interface CisClientFraudService extends CisClientService
{
		/**
		 * generate the fraud report.
		 *
		 * @param reportRequest - the request specifying the start and end dates and times for the report to run
		 * @return a CisFraudReportResult
		 */
		CisFraudReportResult generateFraudReport(final String xCisClientRef, final String tenantId, final CisFraudReportRequest reportRequest);

		/**
		 * handle the order status update.
		 *
		 * @param xml plain text representing the response sent by the vendor
		 * @return CisFraudTransactionResult with the decision
		 */
		CisFraudTransactionResult handleOrderStatusUpdate(final String xCisClientRef, final String tenantId, final String xml);


		/**
		 * Calculate fraud based on the order.
		 *
		 * @param cisTransaction transaction used to calculate the fraud score.
		 * @return a fraud transaction result
		 */
		CisFraudTransactionResult calculateFraudScore(final String xCisClientRef, final String tenantId, final CisFraudTransaction cisTransaction);
	}


