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
package com.hybris.ymkt.recommendation.dao;

public class SAPOfferContentPosition
{
	protected String communicationMediumName;
	protected final String contentPositionId;

	public SAPOfferContentPosition(final String contentPositionId)
	{
		this.contentPositionId = contentPositionId;
	}

	public String getCommunicationMediumName()
	{
		return communicationMediumName;
	}

	public String getContentPositionId()
	{
		return contentPositionId;
	}

	public void setCommunicationMediumName(final String communicationMediumName)
	{
		this.communicationMediumName = communicationMediumName;
	}
}
