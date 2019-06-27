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
 * SAP Digital payment registration URL result class. Contains the registration URL and the SAP Digital payment session
 * ID
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentRegistrationUrlResult
{
	@JsonProperty("PaymentCardRegistrationURL")
	private String paymentCardRegistrationURL;

	@JsonProperty("PaymentCardRegistrationSession")
	private String paymentCardRegistrationSession;

	/**
	 *
	 */
	public CisSapDigitalPaymentRegistrationUrlResult()
	{
		// YTODO Auto-generated constructor stub
	}


	/**
	 *
	 */
	public CisSapDigitalPaymentRegistrationUrlResult(final String paymentCardRegistrationURL,
			final String paymentCardRegistrationSession)
	{
		super();
		this.paymentCardRegistrationURL = paymentCardRegistrationURL;
		this.paymentCardRegistrationSession = paymentCardRegistrationSession;
	}



	/**
	 * @return the paymentCardRegistrationURL
	 */
	public String getPaymentCardRegistrationURL()
	{
		return paymentCardRegistrationURL;
	}

	/**
	 * @param paymentCardRegistrationURL
	 *           the paymentCardRegistrationURL to set
	 */
	public void setPaymentCardRegistrationURL(final String paymentCardRegistrationURL)
	{
		this.paymentCardRegistrationURL = paymentCardRegistrationURL;
	}

	/**
	 * @return the paymentCardRegistrationSession
	 */
	public String getPaymentCardRegistrationSession()
	{
		return paymentCardRegistrationSession;
	}

	/**
	 * @param paymentCardRegistrationSession
	 *           the paymentCardRegistrationSession to set
	 */
	public void setPaymentCardRegistrationSession(final String paymentCardRegistrationSession)
	{
		this.paymentCardRegistrationSession = paymentCardRegistrationSession;
	}

	@Override
	public String toString()
	{
		// YTODO Auto-generated method stub
		return "{PaymentCardRegistrationURL='" + paymentCardRegistrationURL + '\'' + "PaymentCardRegistrationSession='"
				+ paymentCardRegistrationSession + '}';
	}




}
