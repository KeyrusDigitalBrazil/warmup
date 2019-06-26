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
 * SAP Digital payment Charge result list
 */
public class CisSapDigitalPaymentChargeResultList
{

	@JsonProperty("Charges")
	private List<CisSapDigitalPaymentChargeResult> cisSapDigitalPaymentChargeResults;

	/**
	 * @return the cisSapDigitalPaymentChargeResults
	 */
	public List<CisSapDigitalPaymentChargeResult> getCisSapDigitalPaymentChargeResults()
	{
		return cisSapDigitalPaymentChargeResults;
	}

	/**
	 * @param cisSapDigitalPaymentChargeResults
	 *           the cisSapDigitalPaymentChargeResults to set
	 */
	public void setCisSapDigitalPaymentChargeResults(
			final List<CisSapDigitalPaymentChargeResult> cisSapDigitalPaymentChargeResults)
	{
		this.cisSapDigitalPaymentChargeResults = cisSapDigitalPaymentChargeResults;
	}

}
