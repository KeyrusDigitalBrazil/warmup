/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.cis.client.avs.models;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.client.shared.models.CisResult;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Address verification result.
 * 
 * @see CisResult
 */
@XmlRootElement(name = "avsResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class AvsResult extends CisResult
{
	/**
	 * Contains details about which fields were corrected or are incorrect/missing in case of a review or reject case
	 * (respectively). Depending on the third party service there may or may not be detailed field errors.
	 * If the original address generates an ACCEPT response, the fieldErrors element will be empty.
	 */
	@XmlElementWrapper(name = "fieldErrors")
	@XmlElement(name = "field")
	private List<CisFieldError> fieldErrors = new ArrayList<CisFieldError>();

	/** The standardized and (potentially) corrected address or address suggestions. */
	@XmlElementWrapper(name = "suggestedAddresses")
	@XmlElement(name = "address")
	private List<CisAddress> suggestedAddresses = new ArrayList<CisAddress>();

	/**
	 * Constructor.
	 */
	public AvsResult()
	{
		// required by javax.xml
		super();
	}

	/**
	 * @param decision address validation decision
	 */
	public AvsResult(final CisDecision decision)
	{
		super(decision);
	}

	/**
	 * @return returns a list of suggested addresses which was returned by the third party.
	 */
	public List<CisAddress> getSuggestedAddresses()
	{
		return this.suggestedAddresses;
	}

	/**
	 * @param standardizedAddr the suggested addresses which should be set
	 */
	public void setSuggestedAddresses(final List<CisAddress> standardizedAddr)
	{
		this.suggestedAddresses = standardizedAddr;
	}

	/**
	 * @return the field errors found on an provided address
	 */
	public List<CisFieldError> getFieldErrors()
	{
		return this.fieldErrors;
	}

	/**
	 * @param fieldErrors the fieldErrors to set
	 */
	public void setFieldErrors(final List<CisFieldError> fieldErrors)
	{
		this.fieldErrors = fieldErrors;
	}

	@Override
	public String toString()
	{
		final StringBuilder value = new StringBuilder();
		value.append("AvsResult [id=").append(this.getId()).append(", decision=").append(this.getDecision()).append("]");
		return value.toString();
	}

}
