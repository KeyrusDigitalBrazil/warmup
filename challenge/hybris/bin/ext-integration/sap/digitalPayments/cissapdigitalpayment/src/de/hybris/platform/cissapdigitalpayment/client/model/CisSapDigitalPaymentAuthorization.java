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
 * Payment Authorization fields for SAP Digital payment
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentAuthorization
{

	@JsonProperty("AuthorizationByPaytSrvcPrvdr")
	private String authorizationByPaytSrvcPrvdr;

	@JsonProperty("AuthorizationByAcquirer")
	private String authorizationByAcquirer;

	@JsonProperty("AuthorizationByDigitalPaytSrvc")
	private String authorizationByDigitalPaytSrvc;

	@JsonProperty("AuthorizedAmountInAuthznCrcy")
	private String authorizedAmountInAuthznCrcy;

	@JsonProperty("AuthorizationCurrency")
	private String authorizationCurrency;

	@JsonProperty("AuthorizationDateTime")
	private String authorizationDateTime;

	@JsonProperty("AuthorizationExpirationDateTme")
	private String authorizationExpirationDateTime;

	@JsonProperty("AuthorizationStatus")
	private String authorizationStatus;

	@JsonProperty("AuthorizationStatusName")
	private String authorizationStatusName;

	/**
	 * @return the authorizationByPaytSrvcPrvdr
	 */
	public String getAuthorizationByPaytSrvcPrvdr()
	{
		return authorizationByPaytSrvcPrvdr;
	}

	/**
	 * @param authorizationByPaytSrvcPrvdr
	 *           the authorizationByPaytSrvcPrvdr to set
	 */
	public void setAuthorizationByPaytSrvcPrvdr(final String authorizationByPaytSrvcPrvdr)
	{
		this.authorizationByPaytSrvcPrvdr = authorizationByPaytSrvcPrvdr;
	}

	/**
	 * @return the authorizationByAcquirer
	 */
	public String getAuthorizationByAcquirer()
	{
		return authorizationByAcquirer;
	}

	/**
	 * @param authorizationByAcquirer
	 *           the authorizationByAcquirer to set
	 */
	public void setAuthorizationByAcquirer(final String authorizationByAcquirer)
	{
		this.authorizationByAcquirer = authorizationByAcquirer;
	}

	/**
	 * @return the authorizationByDigitalPaytSrvc
	 */
	public String getAuthorizationByDigitalPaytSrvc()
	{
		return authorizationByDigitalPaytSrvc;
	}

	/**
	 * @param authorizationByDigitalPaytSrvc
	 *           the authorizationByDigitalPaytSrvc to set
	 */
	public void setAuthorizationByDigitalPaytSrvc(final String authorizationByDigitalPaytSrvc)
	{
		this.authorizationByDigitalPaytSrvc = authorizationByDigitalPaytSrvc;
	}

	/**
	 * @return the authorizedAmountInAuthznCrcy
	 */
	public String getAuthorizedAmountInAuthznCrcy()
	{
		return authorizedAmountInAuthznCrcy;
	}

	/**
	 * @param authorizedAmountInAuthznCrcy
	 *           the authorizedAmountInAuthznCrcy to set
	 */
	public void setAuthorizedAmountInAuthznCrcy(final String authorizedAmountInAuthznCrcy)
	{
		this.authorizedAmountInAuthznCrcy = authorizedAmountInAuthznCrcy;
	}

	/**
	 * @return the authorizationCurrency
	 */
	public String getAuthorizationCurrency()
	{
		return authorizationCurrency;
	}

	/**
	 * @param authorizationCurrency
	 *           the authorizationCurrency to set
	 */
	public void setAuthorizationCurrency(final String authorizationCurrency)
	{
		this.authorizationCurrency = authorizationCurrency;
	}

	/**
	 * @return the authorizationDateTime
	 */
	public String getAuthorizationDateTime()
	{
		return authorizationDateTime;
	}

	/**
	 * @param authorizationDateTime
	 *           the authorizationDateTime to set
	 */
	public void setAuthorizationDateTime(final String authorizationDateTime)
	{
		this.authorizationDateTime = authorizationDateTime;
	}



	/**
	 * @return the authorizationExpirationDateTime
	 */
	public String getAuthorizationExpirationDateTime()
	{
		return authorizationExpirationDateTime;
	}

	/**
	 * @param authorizationExpirationDateTime
	 *           the authorizationExpirationDateTime to set
	 */
	public void setAuthorizationExpirationDateTime(final String authorizationExpirationDateTime)
	{
		this.authorizationExpirationDateTime = authorizationExpirationDateTime;
	}

	/**
	 * @return the authorizationStatus
	 */
	public String getAuthorizationStatus()
	{
		return authorizationStatus;
	}

	/**
	 * @param authorizationStatus
	 *           the authorizationStatus to set
	 */
	public void setAuthorizationStatus(final String authorizationStatus)
	{
		this.authorizationStatus = authorizationStatus;
	}

	/**
	 * @return the authorizationStatusName
	 */
	public String getAuthorizationStatusName()
	{
		return authorizationStatusName;
	}

	/**
	 * @param authorizationStatusName
	 *           the authorizationStatusName to set
	 */
	public void setAuthorizationStatusName(final String authorizationStatusName)
	{
		this.authorizationStatusName = authorizationStatusName;
	}

}
