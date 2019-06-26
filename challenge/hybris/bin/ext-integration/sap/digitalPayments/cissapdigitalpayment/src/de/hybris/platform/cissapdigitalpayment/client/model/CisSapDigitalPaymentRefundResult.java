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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment refund result class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentRefundResult
{

	@JsonProperty("DigitalPaymentTransaction")
	private CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult;

	@JsonProperty("Refund")
	private CisSapDigitalPaymentRefund cisSapDigitalPaymentRefund;

	@JsonProperty("Source")
	private CisSapDigitalPaymentSource cisSapDigitalPaymentSource;

	/**
	 * @return the cisSapDigitalPaymentTransactionResult
	 */
	public CisSapDigitalPaymentTransactionResult getCisSapDigitalPaymentTransactionResult()
	{
		return cisSapDigitalPaymentTransactionResult;
	}

	/**
	 * @param cisSapDigitalPaymentTransactionResult
	 *           the cisSapDigitalPaymentTransactionResult to set
	 */
	public void setCisSapDigitalPaymentTransactionResult(
			final CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult)
	{
		this.cisSapDigitalPaymentTransactionResult = cisSapDigitalPaymentTransactionResult;
	}

	/**
	 * @return the cisSapDigitalPaymentRefund
	 */
	public CisSapDigitalPaymentRefund getCisSapDigitalPaymentRefund()
	{
		return cisSapDigitalPaymentRefund;
	}

	/**
	 * @param cisSapDigitalPaymentRefund
	 *           the cisSapDigitalPaymentRefund to set
	 */
	public void setCisSapDigitalPaymentRefund(final CisSapDigitalPaymentRefund cisSapDigitalPaymentRefund)
	{
		this.cisSapDigitalPaymentRefund = cisSapDigitalPaymentRefund;
	}

	/**
	 * @return the cisSapDigitalPaymentSource
	 */
	public CisSapDigitalPaymentSource getCisSapDigitalPaymentSource()
	{
		return cisSapDigitalPaymentSource;
	}

	/**
	 * @param cisSapDigitalPaymentSource
	 *           the cisSapDigitalPaymentSource to set
	 */
	public void setCisSapDigitalPaymentSource(final CisSapDigitalPaymentSource cisSapDigitalPaymentSource)
	{
		this.cisSapDigitalPaymentSource = cisSapDigitalPaymentSource;
	}

}
