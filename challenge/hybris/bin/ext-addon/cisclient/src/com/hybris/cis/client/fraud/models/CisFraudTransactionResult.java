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

import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.client.shared.models.CisResult;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "fraudTransactionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisFraudTransactionResult extends CisResult
{
	/** Original decision of transaction. */
	@XmlElement(name = "originalDecision")
	private CisDecision originalDecision;

	/** Decision of transaction after review. */
	@XmlElement(name = "newDecision")
	private CisDecision newDecision;

	/** Reviewer's remark on the transaction. */
	@XmlElement(name = "remark")
	private String remark;

	/** Merchant Reference ID of the transaction. */
	@XmlElement(name = "clientAuthorizationId")
	private String clientAuthorizationId;

	/** Date of review/converstion. */
	@XmlElement(name = "date")
	private Date date;

	/** Notes related to the transaction. */
	@XmlElement(name = "notes")
	private List<CisTransactionNote> notes;

	public CisDecision getOriginalDecision()
	{
		return this.originalDecision;
	}

	public void setOriginalDecision(final CisDecision originalDecision)
	{
		this.originalDecision = originalDecision;
	}

	public CisDecision getNewDecision()
	{
		return this.newDecision;
	}

	public void setNewDecision(final CisDecision newDecision)
	{
		this.newDecision = newDecision;
	}

	public String getClientAuthorizationId()
	{
		return this.clientAuthorizationId;
	}

	public void setClientAuthorizationId(final String merchantNumber)
	{
		this.clientAuthorizationId = merchantNumber;
	}

	public String getRemark()
	{
		return this.remark;
	}

	public void setRemark(final String remark)
	{
		this.remark = remark;
	}

	public List<CisTransactionNote> getNotes()
	{
		return this.notes;
	}

	public void setNotes(final List<CisTransactionNote> notes)
	{
		this.notes = notes;
	}

	public Date getDate()
	{
		return this.date == null ? null : new Date(this.date.getTime());
	}

	public void setDate(final Date date)
	{
		this.date = date == null ? null : new Date(date.getTime());
	}
}
