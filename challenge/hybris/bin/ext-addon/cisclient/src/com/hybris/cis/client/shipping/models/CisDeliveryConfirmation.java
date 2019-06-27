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
package com.hybris.cis.client.shipping.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


/**
 * Defines the delivery confirmation options of an shipment.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisDeliveryConfirmation
{
	/** Defines which confirmation type for the delivery is required. */
	@XmlElement(name = "type")
	private CisDeliveryConfirmationType type;

	/** Sets if proof of age is required for this shipment. */
	@XmlElement(name = "proofOfAge")
	private Boolean proofOfAge;

	public CisDeliveryConfirmationType getType()
	{
		return this.type;
	}

	public void setType(final CisDeliveryConfirmationType deliveryConfirmationType)
	{
		this.type = deliveryConfirmationType;
	}

	public Boolean getProofOfAge()
	{
		return this.proofOfAge;
	}

	public void setProofOfAge(final Boolean proofOfAge)
	{
		this.proofOfAge = proofOfAge;
	}
}
