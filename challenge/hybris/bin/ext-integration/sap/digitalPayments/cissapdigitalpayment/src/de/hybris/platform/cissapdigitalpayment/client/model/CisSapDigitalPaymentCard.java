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
 * SAP Digital payment payment service provider field class
 */
public class CisSapDigitalPaymentCard
{
	@JsonProperty("PaytCardByDigitalPaymentSrvc")
	private String paytCardByDigitalPaymentSrvc;

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
