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
package com.hybris.cis.client.tax.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


/**
 * A tax line containing tax details.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisTaxLine
{
	/** ID of the tax line by which it can be referenced to an order line from the request. */
	@XmlAttribute(name = "id")
	private String id;

	/** List of tax values. */
	@XmlElementWrapper(name = "taxValues")
	@XmlElement(name = "taxValue")
	private List<CisTaxValue> taxValues = new ArrayList<CisTaxValue>();

	/** Total tax amount of this line. */
	@XmlElement(name = "totalTax")
	private BigDecimal totalTax;

	/** Rounding mode when scaling amounts. */
	private transient RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	/**
	 * Gets the tax values.
	 * 
	 * @return the taxValues
	 */
	public List<CisTaxValue> getTaxValues()
	{
		return taxValues;
	}

	/**
	 * Sets the tax values.
	 * 
	 * @param taxValues the taxValues to set
	 */
	public void setTaxValues(final List<CisTaxValue> taxValues)
	{
		this.taxValues = taxValues;
	}

	/**
	 * Gets the total tax.
	 * 
	 * @return the value
	 */
	public BigDecimal getTotalTax()
	{
		return totalTax;
	}

	/**
	 * Sets the total tax.
	 * 
	 * @param totalTax the value to set
	 */
	public void setTotalTax(final BigDecimal totalTax)
	{
		this.totalTax = totalTax == null ? null : totalTax.setScale(2, roundingMode);
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	public RoundingMode getRoundingMode()
	{
		return roundingMode;
	}

	public void setRoundingMode(final RoundingMode roundingMode)
	{
		this.roundingMode = roundingMode;
	}

}
