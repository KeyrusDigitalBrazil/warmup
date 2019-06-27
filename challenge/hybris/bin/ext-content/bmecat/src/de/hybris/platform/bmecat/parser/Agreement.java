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
package de.hybris.platform.bmecat.parser;

import de.hybris.bootstrap.xml.AbstractValueObject;

import java.util.Date;


/**
 * Object which holds the value of a parsed &lt;AGREEMENT&gt; tag
 * 
 * 
 */
public class Agreement extends AbstractValueObject
{
	private Date startDate;
	private Date endDate;
	private String id;

	/**
	 * BMECat: AGREEMENT.DATETIME type="agreement_end_date"
	 * 
	 * @return Returns the endDate.
	 */
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate
	 *           The endDate to set.
	 */
	public void setEndDate(final Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * BMECat: AGREEMENT.AGREEMENT_ID
	 * 
	 * 
	 * @return Returns the id.
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * @param id
	 *           The id to set.
	 */
	public void setID(final String id)
	{
		this.id = id;
	}

	/**
	 * BMECat: AGREEMENT.DATETIME type="agreement_start_date"
	 * 
	 * @return Returns the startDate.
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate
	 *           The startDate to set.
	 */
	public void setStartDate(final Date startDate)
	{
		this.startDate = startDate;
	}
}
