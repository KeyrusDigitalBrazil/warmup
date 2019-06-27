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
package com.hybris.cis.client.fraud.models;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import com.hybris.cis.client.shared.models.AnnotationHashMap;


/**
 * A request to receive an update on possible fraudulent transactions.
 */
@XmlRootElement(name = "fraudReportRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisFraudReportRequest
{

	/** The start date and time of the report. */
	@XmlElement(name = "startDateTime")
	private Date startDateTime;

	/** The end date and time of the report. */
	@XmlElement(name = "endDateTime")
	private Date endDateTime;

	/** vendor specific parameters */
	@XmlElement(name = "parameters")
	private AnnotationHashMap parameters;

	public Date getStartDateTime()
	{
		return this.startDateTime == null ? null : new Date(this.startDateTime.getTime());
	}

	public void setStartDateTime(final Date startDate)
	{
		this.startDateTime = startDate == null ? null : new Date(startDate.getTime());
	}

	public Date getEndDateTime()
	{
		return this.endDateTime == null ? null : new Date(this.endDateTime.getTime());
	}

	public void setEndDateTime(final Date endDate)
	{
		this.endDateTime = endDate == null ? null : new Date(endDate.getTime());
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
