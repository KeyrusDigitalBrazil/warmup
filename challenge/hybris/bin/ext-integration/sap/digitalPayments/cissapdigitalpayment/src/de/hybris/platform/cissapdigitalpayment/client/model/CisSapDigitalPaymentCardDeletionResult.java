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

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * hold response of SAP Digital Payment after requesting for deletion of card(s).
 */
public class CisSapDigitalPaymentCardDeletionResult
{

	@JsonProperty("DigitalPaymentTransaction")
	private CisSapDigitalPaymentTransactionResult cisDigitalPaymentCardDeletionTxResult;

	@JsonProperty("PaytCardByDigitalPaymentSrvc")
	private String paytCardByDigitalPaymentSrvc;



	/**
	 * @return the cisDigitalPaymentCardDeletionTxResult
	 */
	public CisSapDigitalPaymentTransactionResult getCisDigitalPaymentCardDeletionTxResult()
	{
		return cisDigitalPaymentCardDeletionTxResult;
	}

	/**
	 * @param cisDigitalPaymentCardDeletionTxResult
	 *           the cisDigitalPaymentCardDeletionTxResult to set
	 */
	public void setCisDigitalPaymentCardDeletionTxResult(
			final CisSapDigitalPaymentTransactionResult cisDigitalPaymentCardDeletionTxResult)
	{
		this.cisDigitalPaymentCardDeletionTxResult = cisDigitalPaymentCardDeletionTxResult;
	}

	/**
	 * @return the paytCardByDigitalPaymentSrvc
	 */
	public String getPaytCardByDigitalPaymentSrvc()
	{
		return paytCardByDigitalPaymentSrvc;
	}

	/**
	 * @param paytCardByDigitalPaymentSrvc
	 *           the paytCardByDigitalPaymentSrvc to set
	 */
	public void setPaytCardByDigitalPaymentSrvc(final String paytCardByDigitalPaymentSrvc)
	{
		this.paytCardByDigitalPaymentSrvc = paytCardByDigitalPaymentSrvc;
	}


}
