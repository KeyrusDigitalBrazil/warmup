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
 * SAP Digital payment authorization request list class
 */
public class CisSapDigitalPaymentAuthorizationRequestList
{

	@JsonProperty("Authorizations")
	private List<CisSapDigitalPaymentAuthorizationRequest> cisSapDigitalPaymentAuthorizationRequests;

	/**
	 * @return the cisSapDigitalPaymentAuthorizationRequests
	 */
	public List<CisSapDigitalPaymentAuthorizationRequest> getCisSapDigitalPaymentAuthorizationRequests()
	{
		return cisSapDigitalPaymentAuthorizationRequests;
	}

	/**
	 * @param cisSapDigitalPaymentAuthorizationRequests
	 *           the cisSapDigitalPaymentAuthorizationRequests to set
	 */
	public void setCisSapDigitalPaymentAuthorizationRequests(
			final List<CisSapDigitalPaymentAuthorizationRequest> cisSapDigitalPaymentAuthorizationRequests)
	{
		this.cisSapDigitalPaymentAuthorizationRequests = cisSapDigitalPaymentAuthorizationRequests;
	}



}
