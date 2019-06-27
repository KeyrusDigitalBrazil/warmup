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
 * SAP Digital payment refund request list class
 */
public class CisSapDigitalPaymentRefundRequestList
{


	@JsonProperty("Refunds")
	private List<CisSapDigitalPaymentRefundRequest> cisSapDigitalPaymentRefundRequests;

	/**
	 * @return the cisSapDigitalPaymentRefundRequests
	 */
	public List<CisSapDigitalPaymentRefundRequest> getCisSapDigitalPaymentRefundRequests()
	{
		return cisSapDigitalPaymentRefundRequests;
	}

	/**
	 * @param cisSapDigitalPaymentRefundRequests
	 *           the cisSapDigitalPaymentRefundRequests to set
	 */
	public void setCisSapDigitalPaymentRefundRequests(
			final List<CisSapDigitalPaymentRefundRequest> cisSapDigitalPaymentRefundRequests)
	{
		this.cisSapDigitalPaymentRefundRequests = cisSapDigitalPaymentRefundRequests;
	}

}
