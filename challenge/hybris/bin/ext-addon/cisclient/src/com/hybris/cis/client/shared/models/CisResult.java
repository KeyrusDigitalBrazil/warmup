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
package com.hybris.cis.client.shared.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/**
 * Contains details about the result of a CIS operation.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisResult implements Identifiable
{
	/** The decision (see {@link CisDecision}). */
	@XmlElement(name = "decision")
	private CisDecision decision;

	/** The client reference ID that was passed in the request header (or 'undefined' if not). */
	@XmlAttribute(name = "clientRefId")
	private String clientRefId;

	/**
	 * Name of the vendor that was used to fulfill this request. This field contains a proprietary information that
	 * should not be used to make any decisions (but can be used to troubleshoot).
	 */
	@XmlAttribute(name = "vendorId")
	private String vendorId;

	/**
	 * Pass-thru of the 3rd party service reason code. This field contains a proprietary code that should not be used to
	 * make any decisions (but can be used to troubleshoot).
	 */
	@XmlAttribute(name = "vendorReasonCode")
	private String vendorReasonCode;

	/**
	 * Pass-thru of the 3rd party service status code. This field contains a proprietary code that should not be used to
	 * make any decisions (but can be used to troubleshoot).
	 */
	@XmlAttribute(name = "vendorStatusCode")
	private String vendorStatusCode;

	/**
	 * Identifies this result.
	 */
	@XmlAttribute(name = "id")
	private String id;

	/**
	 * Links to this result.
	 */
	@XmlAttribute(name = "href")
	private String href;

	/** All other elements returned. */
	@XmlElement(name = "vendorResponses")
	private AnnotationHashMap vendorResponses;

	/**
	 * Instantiates a new cis result.
	 */
	public CisResult()
	{
		this(null);
	}

	/**
	 * Instantiates a new cis result.
	 * 
	 * @param decision the decision
	 */
	public CisResult(final CisDecision decision)
	{
		this.decision = decision;
	}

	/**
	 * Instantiates a new cis result.
	 * 
	 * @param decision the decision
	 * @param id the id
	 */
	public CisResult(final CisDecision decision, final String id)
	{
		this.decision = decision;
		this.id = id;
	}

	/**
	 * Gets the decision.
	 * 
	 * @return the decision
	 */
	public CisDecision getDecision()
	{
		return this.decision;
	}

	/**
	 * Sets the decision.
	 * 
	 * @param decision the decision to set
	 */
	public void setDecision(final CisDecision decision)
	{
		this.decision = decision;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Override
	public String getId()
	{
		return this.id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id to set
	 */
	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * Gets the vendor reason code.
	 * 
	 * @return the vendorReasonCode
	 */
	public String getVendorReasonCode()
	{
		return this.vendorReasonCode;
	}

	/**
	 * Sets the vendor reason code.
	 * 
	 * @param vendorReasonCode the vendorReasonCode to set
	 */
	public void setVendorReasonCode(final String vendorReasonCode)
	{
		this.vendorReasonCode = vendorReasonCode;
	}

	/**
	 * Gets the vendor status code.
	 * 
	 * @return the vendorStatusCode
	 */
	public String getVendorStatusCode()
	{
		return this.vendorStatusCode;
	}

	/**
	 * Sets the vendor status code.
	 * 
	 * @param vendorStatusCode the vendorStatusCode to set
	 */
	public void setVendorStatusCode(final String vendorStatusCode)
	{
		this.vendorStatusCode = vendorStatusCode;
	}

	/**
	 * Gets the client ref id.
	 * 
	 * @return the clientRefId
	 */
	public String getClientRefId()
	{
		return this.clientRefId;
	}

	/**
	 * Sets the client ref id.
	 * 
	 * @param clientRefId the clientRefId to set
	 */
	public void setClientRefId(final String clientRefId)
	{
		this.clientRefId = clientRefId;
	}

	/**
	 * Gets the vendor id.
	 * 
	 * @return the vendorId
	 */
	public String getVendorId()
	{
		return this.vendorId;
	}

	/**
	 * Sets the vendor id.
	 * 
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(final String vendorId)
	{
		this.vendorId = vendorId;
	}

	/**
	 * Gets the href.
	 * 
	 * @return the href
	 */
	public String getHref()
	{
		return this.href;
	}

	/**
	 * Sets the href.
	 * 
	 * @param href the href to set
	 */
	public void setHref(final String href)
	{
		this.href = href;
	}

	public AnnotationHashMap getVendorResponses()
	{
		return this.vendorResponses;
	}

	public void setVendorResponses(final AnnotationHashMap vendorResponses)
	{
		this.vendorResponses = vendorResponses;
	}
}
