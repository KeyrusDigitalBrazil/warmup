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
package de.hybris.platform.cissapdigitalpayment.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment authorization result list
 */
public class CisSapDigitalPaymentAuthorizationResultList
{

	@JsonProperty("Authorizations")
	private List<CisSapDigitalPaymentAuthorizationResult> cisSapDigitalPaymentAuthorizationResults;

	/**
	 * @return the cisSapDigitalPaymentAuthorizationResults
	 */
	public List<CisSapDigitalPaymentAuthorizationResult> getCisSapDigitalPaymentAuthorizationResults()
	{
		return cisSapDigitalPaymentAuthorizationResults;
	}

	/**
	 * @param cisSapDigitalPaymentAuthorizationResults
	 *           the cisSapDigitalPaymentAuthorizationResults to set
	 */
	public void setCisSapDigitalPaymentAuthorizationResults(
			final List<CisSapDigitalPaymentAuthorizationResult> cisSapDigitalPaymentAuthorizationResults)
	{
		this.cisSapDigitalPaymentAuthorizationResults = cisSapDigitalPaymentAuthorizationResults;
	}





}
