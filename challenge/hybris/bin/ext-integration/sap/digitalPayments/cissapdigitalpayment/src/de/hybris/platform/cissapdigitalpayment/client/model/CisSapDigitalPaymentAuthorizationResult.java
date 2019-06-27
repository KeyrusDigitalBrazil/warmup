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
 * SAP Digital payment authorization result class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentAuthorizationResult
{

	@JsonProperty("DigitalPaymentTransaction")
	private CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult;

	@JsonProperty("Authorization")
	private CisSapDigitalPaymentAuthorization cisSapDigitalPaymentAuthorization;

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
	 * @return the cisSapDigitalPaymentAuthorization
	 */
	public CisSapDigitalPaymentAuthorization getCisSapDigitalPaymentAuthorization()
	{
		return cisSapDigitalPaymentAuthorization;
	}

	/**
	 * @param cisSapDigitalPaymentAuthorization
	 *           the cisSapDigitalPaymentAuthorization to set
	 */
	public void setCisSapDigitalPaymentAuthorization(final CisSapDigitalPaymentAuthorization cisSapDigitalPaymentAuthorization)
	{
		this.cisSapDigitalPaymentAuthorization = cisSapDigitalPaymentAuthorization;
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

	@Override
	public String toString()
	{
		return "{ " + "Authorizations : [ " + "{ \'DigitalPaymentTransaction\' : {" + " \'DigitalPaymentTransaction\' :" + "\'"
				+ getCisSapDigitalPaymentTransactionResult().getDigitalPaymentTransaction() + "\'," + " \'DigitalPaymentDateTime\' :"
				+ "\'" + getCisSapDigitalPaymentTransactionResult().getDigitalPaymentDateTime() + "\',"
				+ " \'DigitalPaytTransResult\' :" + "\'" + getCisSapDigitalPaymentTransactionResult().getDigitalPaytTransResult()
				+ "\'," + " \'DigitalPaytTransRsltDesc\' :" + "\'"
				+ getCisSapDigitalPaymentTransactionResult().getDigitalPaytTransRsltDesc() + "\'" + "}," + "\'Authorization\': {"
				+ " \'AuthorizationByPaytSrvcPrvdr\' :" + "\'"
				+ getCisSapDigitalPaymentAuthorization().getAuthorizationByPaytSrvcPrvdr() + "\'," + " \'AuthorizationByAcquirer\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationByAcquirer() + "\',"
				+ " \'AuthorizationByDigitalPaytSrvc\' :" + "\'"
				+ getCisSapDigitalPaymentAuthorization().getAuthorizationByDigitalPaytSrvc() + "\',"
				+ " \'AuthorizedAmountInAuthznCrcy\' :" + "\'"
				+ getCisSapDigitalPaymentAuthorization().getAuthorizedAmountInAuthznCrcy() + "\'," + " \'AuthorizationCurrency\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationCurrency() + "\'," + " \'AuthorizationDateTime\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationDateTime() + "\'," + " \'AuthorizationStatus\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationStatus() + "\'," + " \'AuthorizationStatusName\' :"
				+ "\'" + getCisSapDigitalPaymentAuthorization().getAuthorizationStatusName() + "\'" + "}," + "\'Source\': {"
				+ "\'Card\': {" + " \'PaytCardByDigitalPaymentSrvc\' :" + "\'"
				+ getCisSapDigitalPaymentSource().getCisSapDigitalPaymentCard().getPaytCardByDigitalPaymentSrvc() + "\'" + "}" + "}"
				+ "}" + "]" + "}";
	}



}
