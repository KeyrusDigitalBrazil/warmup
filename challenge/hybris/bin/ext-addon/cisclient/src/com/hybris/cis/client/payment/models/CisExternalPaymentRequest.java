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
package com.hybris.cis.client.payment.models;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import com.hybris.cis.client.shared.models.AnnotationHashMap;



/**
 * A payment request to e.g. authorize or capture that was done externally (e.g. by the CyberSource Silent Order Post).
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "externalPaymentRequest")
public class CisExternalPaymentRequest
{
	/** vendor specific parameters (e.g. response parameters of the Silent Order Post) */
	@XmlElement(name = "parameters")
	private AnnotationHashMap parameters;

	public CisExternalPaymentRequest() // NOPMD
	{
		// default constructor required by jaxb
	}

	public CisExternalPaymentRequest(final Map<String, String> map) // NOPMD
	{
		this.parameters = new AnnotationHashMap(map);
	}

	public AnnotationHashMap getParameters()
	{
		return this.parameters;
	}

	public void setParameters(final AnnotationHashMap parameters)
	{
		this.parameters = parameters;
	}

}
