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
 * SAP Digital payment card source field class
 */
public class CisSapDigitalPaymentSource
{
	@JsonProperty("Card")
	private CisSapDigitalPaymentCard cisSapDigitalPaymentCard;

	/**
	 * @return the cisSapDigitalPaymentCard
	 */
	public CisSapDigitalPaymentCard getCisSapDigitalPaymentCard()
	{
		return cisSapDigitalPaymentCard;
	}

	/**
	 * @param cisSapDigitalPaymentCard
	 *           the cisSapDigitalPaymentCard to set
	 */
	public void setCisSapDigitalPaymentCard(final CisSapDigitalPaymentCard cisSapDigitalPaymentCard)
	{
		this.cisSapDigitalPaymentCard = cisSapDigitalPaymentCard;
	}



}
