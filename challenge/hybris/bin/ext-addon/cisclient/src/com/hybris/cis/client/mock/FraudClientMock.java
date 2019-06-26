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


import com.hybris.cis.client.fraud.FraudClient;
import com.hybris.cis.client.fraud.models.CisFraudReportRequest;
import com.hybris.cis.client.fraud.models.CisFraudReportResult;
import com.hybris.cis.client.fraud.models.CisFraudTransaction;
import com.hybris.cis.client.fraud.models.CisFraudTransactionResult;
import com.hybris.cis.client.shared.models.AnnotationHashMap;
import com.hybris.cis.client.shared.models.CisDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Mock implementation of {@link FraudClient}
 */
public class FraudClientMock extends SharedClientMock implements FraudClient
{
	private final static Logger LOGGER = LoggerFactory.getLogger(FraudClientMock.class);

	private String calculateFraudScoreLocation;
	private String orderStatusUpdateLocation;
	private String generateFraudReportLocation;

	public FraudClientMock()
	{
		LOGGER.info("Using MOCK Client to simulate Fraud.");
	}

	@Override
	public CisFraudTransactionResult handleOrderStatusUpdate(final String xCisClientRef, final String tenantId,
															 final String request)
	{
		LOGGER.info("Using MOCK Client - handleOrderStatusUpdate()");

		final CisFraudTransactionResult result = new CisFraudTransactionResult();
		result.setId("123456");
		result.setDecision(CisDecision.ACCEPT);
		result.setOriginalDecision(CisDecision.ACCEPT);
		return result;
	}

	@Override
	public CisFraudTransactionResult handleOrderStatusUpdateXML(final String xCisClientRef, final String tenantId,
			final String request)
	{
		LOGGER.info("Using MOCK Client - handleOrderStatusUpdateXML()");

		final CisFraudTransactionResult result = new CisFraudTransactionResult();
		result.setId("123456");
		result.setDecision(CisDecision.ACCEPT);
		result.setOriginalDecision(CisDecision.ACCEPT);
		return result;
	}

	@Override
	public CisFraudReportResult generateFraudReport(final String xCisClientRef, final String tenantId,
													final CisFraudReportRequest request)
	{
		LOGGER.info("Using MOCK Client - generateFraudReport()");

		final CisFraudReportResult result = new CisFraudReportResult();
		final List<CisFraudTransactionResult> fraudResults = new ArrayList<CisFraudTransactionResult>();
		final CisFraudTransactionResult fraudResult = new CisFraudTransactionResult();

		result.setId("123456");
		result.setDecision(CisDecision.ACCEPT);

		final Map<String, String> map = new HashMap<String, String>();
		map.put("test1", "test2");
		final AnnotationHashMap parameters = new AnnotationHashMap(map);
		result.setVendorResponses(parameters);

		fraudResult.setNewDecision(CisDecision.ACCEPT);
		fraudResult.setOriginalDecision(CisDecision.REVIEW);
		fraudResult.setClientAuthorizationId("654321");
		fraudResults.add(fraudResult);
		result.setTransactions(fraudResults);

		return result;
	}


	@Override
	public CisFraudTransactionResult calculateFraudScore(final String xCisClientRef, final String tenantId,
			final CisFraudTransaction cisTransaction)
	{
		LOGGER.info("Using MOCK Client - calculateFraudScore()");

		final CisFraudTransactionResult result = new CisFraudTransactionResult();
		result.setId("123456");
		result.setDecision(CisDecision.ACCEPT);
		return result;
	}

	public String getCalculateFraudScoreLocation()
	{
		return calculateFraudScoreLocation;
	}

	public void setCalculateFraudScoreLocation(final String calculateFraudScoreLocation)
	{
		this.calculateFraudScoreLocation = calculateFraudScoreLocation;
	}

	public String getOrderStatusUpdateLocation()
	{
		return orderStatusUpdateLocation;
	}

	public void setOrderStatusUpdateLocation(final String orderStatusUpdateLocation)
	{
		this.orderStatusUpdateLocation = orderStatusUpdateLocation;
	}

	public String getGenerateFraudReportLocation()
	{
		return generateFraudReportLocation;
	}

	public void setGenerateFraudReportLocation(final String generateFraudReportLocation)
	{
		this.generateFraudReportLocation = generateFraudReportLocation;
	}
}
