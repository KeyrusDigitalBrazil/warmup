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

import com.hybris.cis.client.shared.models.CisResult;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The result of fraud report request containing the transactions and status.
 */
@XmlRootElement(name = "fraudReportResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisFraudReportResult extends CisResult
{
	/** List of conversions of transactions. */
	@XmlElementWrapper(name = "transactions")
	@XmlElement(name = "transaction")
	private List<CisFraudTransactionResult> transactions;

	public List<CisFraudTransactionResult> getTransactions()
	{
		return this.transactions;
	}

	public void setTransactions(final List<CisFraudTransactionResult> conversions)
	{
		this.transactions = conversions;
	}
}
