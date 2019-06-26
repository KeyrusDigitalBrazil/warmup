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
 * SAP Digital payment refund result list
 */
public class CisSapDigitalPaymentRefundResultList
{

	@JsonProperty("Refunds")
	private List<CisSapDigitalPaymentRefundResult> cisSapDigitalPaymentRefundResuts;

	/**
	 * @return the cisSapDigitalPaymentRefundResuts
	 */
	public List<CisSapDigitalPaymentRefundResult> getCisSapDigitalPaymentRefundResuts()
	{
		return cisSapDigitalPaymentRefundResuts;
	}

	/**
	 * @param cisSapDigitalPaymentRefundResuts
	 *           the cisSapDigitalPaymentRefundResuts to set
	 */
	public void setCisSapDigitalPaymentRefundResuts(final List<CisSapDigitalPaymentRefundResult> cisSapDigitalPaymentRefundResuts)
	{
		this.cisSapDigitalPaymentRefundResuts = cisSapDigitalPaymentRefundResuts;
	}

}

