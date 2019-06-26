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

import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.client.shared.models.CisResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;


/**
 * Tax document.
 */
@XmlRootElement(name = "taxDoc")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisTaxDoc extends CisResult
{
	/** The date the document was created. */
	@XmlElement(name = "date")
	private Date date;

	/** List of line item taxes. */
	@XmlElementWrapper(name = "taxLines")
	@XmlElement(name = "taxLine")
	private List<CisTaxLine> taxLines;

	/** Sub-total including discounts but excluding taxes. */
	@XmlElement(name = "subTotal")
	private BigDecimal subTotal;

	/** Total tax amount. */
	@XmlElement(name = "totalTax")
	private BigDecimal totalTax;

	/** Grand total including discounts and taxes (subtoTal + totalTax). */
	@XmlElement(name = "total")
	private BigDecimal total;

	/** Rounding mode when scaling amounts. */
	private transient RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	public CisTaxDoc()
	{
		this(null, null, null); // default constructor required by jaxb
	}

	public CisTaxDoc(final List<CisTaxLine> lineItems)
	{
		this(null, null, lineItems);
	}

	public CisTaxDoc(final CisDecision decision)
	{
		this(decision, null, null);
	}

	public CisTaxDoc(final CisDecision decision, final String id)
	{
		this(decision, id, null);
	}

	public CisTaxDoc(final CisDecision decision, final String id, final List<CisTaxLine> lineItems)
	{
		super(decision, id);
		this.taxLines = lineItems;
	}


	/**
	 * Gets the tax lines.
	 * 
	 * @return the taxLines
	 */
	public List<CisTaxLine> getTaxLines()
	{
		return this.taxLines;
	}

	/**
	 * Sets the tax lines.
	 * 
	 * @param taxLines the taxLines to set
	 */
	public void setTaxLines(final List<CisTaxLine> taxLines)
	{
		this.taxLines = taxLines;
	}

	/**
	 * Gets the total.
	 * 
	 * @return the total
	 */
	public BigDecimal getTotal()
	{
		return this.total;
	}

	/**
	 * Sets the total.
	 * 
	 * @param total the total to set
	 */
	public void setTotal(final BigDecimal total)
	{
		this.total = total == null ? null : total.setScale(2, this.roundingMode);
	}

	/**
	 * Gets the total tax.
	 * 
	 * @return the totalTax
	 */
	public BigDecimal getTotalTax()
	{
		return this.totalTax;
	}

	/**
	 * Sets the total tax.
	 * 
	 * @param totalTax the totalTax to set
	 */
	public void setTotalTax(final BigDecimal totalTax)
	{
		this.totalTax = totalTax == null ? null : totalTax.setScale(2, this.roundingMode);
	}

	/**
	 * Gets the sub total.
	 * 
	 * @return the subTotal
	 */
	public BigDecimal getSubTotal()
	{
		return this.subTotal;
	}

	/**
	 * Sets the sub total.
	 * 
	 * @param subTotal the subTotal to set
	 */
	public void setSubTotal(final BigDecimal subTotal)
	{
		this.subTotal = subTotal == null ? null : subTotal.setScale(2, this.roundingMode);
	}

	/**
	 * Gets the doc date.
	 * 
	 * @return the doc date
	 */
	public Date getDate()
	{
		return this.date == null ? null : new Date(this.date.getTime());
	}

	/**
	 * Sets the doc date.
	 * 
	 * @param docDate the new doc date
	 */
	public void setDate(final Date docDate)
	{
		this.date = docDate == null ? null : new Date(docDate.getTime());
	}

	public RoundingMode getRoundingMode()
	{
		return this.roundingMode;
	}

	public void setRoundingMode(final RoundingMode roundingMode)
	{
		this.roundingMode = roundingMode;
	}

	@Override
	public String toString()
	{
		final StringBuilder value = new StringBuilder();
		value.append("CisTaxDoc [decision=").append(this.getDecision()).append(", id=").append(this.getId()).append(", totalTax=")
				.append(this.getTotalTax()).append("]");
		return value.toString();
	}

}
