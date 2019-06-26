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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Class to record all the pertinent information that is returned regarding a fraudulant transaction and the
 * notes history that corresponds.
 */
@XmlRootElement(name = "transactionNote")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisTransactionNote
{
	/** The date on which the given note was attached. */
	@XmlElement(name = "date")
	private Date date;

	/** The name of the commenter. */
	@XmlElement(name = "commenter")
	private String commenter;

	/** The note of the commenter. */
	@XmlElement(name = "note")
	private String note;

	public Date getDate()
	{
		return this.date == null ? null : new Date(this.date.getTime());
	}

	public void setDate(final Date date)
	{
		this.date = date == null ? null : new Date(date.getTime());
	}

	public String getCommenter()
	{
		return this.commenter;
	}

	public void setCommenter(final String commenter)
	{
		this.commenter = commenter;
	}

	public String getNote()
	{
		return this.note;
	}

	public void setNote(final String note)
	{
		this.note = note;
	}
}
