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
 * SAP Digital payment charge request list class
 */
public class CisSapDigitalPaymentChargeRequestList
{

	@JsonProperty("Charges")
	private List<CisSapDigitalPaymentChargeRequest> cisSapDigitalPaymentChargeRequests;

	/**
	 * @return the cisSapDigitalPaymentChargeRequests
	 */
	public List<CisSapDigitalPaymentChargeRequest> getCisSapDigitalPaymentChargeRequests()
	{
		return cisSapDigitalPaymentChargeRequests;
	}

	/**
	 * @param cisSapDigitalPaymentChargeRequests
	 *           the cisSapDigitalPaymentChargeRequests to set
	 */
	public void setCisSapDigitalPaymentChargeRequests(
			final List<CisSapDigitalPaymentChargeRequest> cisSapDigitalPaymentChargeRequests)
	{
		this.cisSapDigitalPaymentChargeRequests = cisSapDigitalPaymentChargeRequests;
	}


}
