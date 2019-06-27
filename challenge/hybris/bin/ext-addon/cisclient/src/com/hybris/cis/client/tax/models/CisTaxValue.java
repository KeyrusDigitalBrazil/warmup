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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * A tax value containing details about a tax.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisTaxValue
{
	/** Type or level of the jurisdiction. */
	@XmlAttribute(name = "level")
	private String level;

	/** Tax amount. */
	@XmlAttribute(name = "value")
	private BigDecimal value;

	/** Tax rate. (Caution: May be inaccurate (rounded) depending on vendor capabilities.) */
	@XmlAttribute(name = "rate")
	private BigDecimal rate;

	/** Taxable amount. (Caution: May be null depending on vendor capabilities.) */
	@XmlAttribute(name = "taxable")
	private BigDecimal taxable;

	/** Name of the tax. (Caution: May be null depending on vendor capabilities.) */
	@XmlElement(name = "name")
	private String name;

	/** Name of the jurisdiction. (Caution: May be null depending on vendor capabilities) */
	@XmlElement(name = "jurisdiction")
	private String jurisdiction;

	/** Rounding mode when scaling amounts. */
	private transient RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	/**
	 * Instantiates a new cis tax value with all mandatory fields.
	 * 
	 * @param level the id
	 * @param value the value
	 * @param rate the rate
	 */
	public CisTaxValue(final String level, final BigDecimal value, final BigDecimal rate, final BigDecimal taxable)
	{
		super();
		this.level = level;
		this.value = value == null ? null : value.setScale(2, this.roundingMode);
		this.rate = rate == null ? null : rate.setScale(6, this.roundingMode);
		this.taxable = taxable == null ? null : taxable.setScale(2, this.roundingMode);
	}

	/**
	 * Instantiates a new cis tax value.
	 */
	public CisTaxValue()
	{
		// required for java xml bind
	}

	/**
	 * Gets the id.
	 * 
	 * @return the code
	 */
	public String getLevel()
	{
		return this.level;
	}

	/**
	 * Sets the id.
	 * 
	 * @param code the code to set
	 */
	public void setLevel(final String code)
	{
		this.level = code;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public BigDecimal getValue()
	{
		return this.value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the value to set
	 */
	public void setValue(final BigDecimal value)
	{
		this.value = value == null ? null : value.setScale(2, this.roundingMode);
	}

	/**
	 * Gets the rate.
	 * 
	 * @return the percentage
	 */
	public BigDecimal getRate()
	{
		return this.rate;
	}

	/**
	 * Sets the rate.
	 * 
	 * @param rate the percentage to set
	 */
	public void setRate(final BigDecimal rate)
	{
		this.rate = rate == null ? null : rate.setScale(6, this.roundingMode);
	}

	/**
	 * Gets the region.
	 * 
	 * @return the region
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Sets the region.
	 * 
	 * @param region the region to set
	 */
	public void setName(final String region)
	{
		this.name = region;
	}

	public String getJurisdiction()
	{
		return this.jurisdiction;
	}

	public void setJurisdiction(final String jurisdiction)
	{
		this.jurisdiction = jurisdiction;
	}

	public BigDecimal getTaxable()
	{
		return this.taxable;
	}

	public void setTaxable(final BigDecimal taxable)
	{
		this.taxable = taxable == null ? null : taxable.setScale(2, this.roundingMode);
	}

	public RoundingMode getRoundingMode()
	{
		return this.roundingMode;
	}

	public void setRoundingMode(final RoundingMode roundingMode)
	{
		this.roundingMode = roundingMode;
	}

}
