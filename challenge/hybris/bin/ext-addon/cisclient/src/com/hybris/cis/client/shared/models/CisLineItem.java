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
package com.hybris.cis.client.shared.models;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/**
 * A line item.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisLineItem
{
	/** The line item id (usually the line item number). */
	@XmlAttribute(name = "id", required = true)
	private Integer id;

	/** The item code (e.g. SKU of a product) */
	@XmlElement(name = "itemCode", required = true)
	private String itemCode;

	/** The tax code (e.g. tax category code) */
	@XmlElement(name = "taxCode")
	private String taxCode;

	/** The quantity of this line. */
	@XmlElement(name = "quantity", required = true)
	private Integer quantity;

	/** The unit price of this line. */
	@XmlElement(name = "unitPrice", required = true)
	private BigDecimal unitPrice = BigDecimal.ZERO;

	/** The description of this line (e.g. name of a product). */
	@XmlElement(name = "productDescription", required = true)
	private String productDescription;

	/**
	 * Vendor specific values to pass in the request.
	 */
	@XmlElement(name = "vendorParameters")
	private AnnotationHashMap vendorParameters;

	public String getProductDescription()
	{
		return this.productDescription;
	}

	public void setProductDescription(final String productDescription)
	{
		this.productDescription = productDescription;
	}

	/**
	 * Gets the quantity.
	 * 
	 * @return the quantity
	 */
	public Integer getQuantity()
	{
		return this.quantity;
	}

	/**
	 * Sets the quantity.
	 * 
	 * @param quantity the quantity to set
	 */
	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * Gets the item no.
	 * 
	 * @return the itemNo
	 */
	public Integer getId()
	{
		return this.id;
	}

	/**
	 * Sets the item no.
	 * 
	 * @param id the itemNo to set
	 */
	public void setId(final Integer id)
	{
		this.id = id;
	}

	/**
	 * Gets the unit price.
	 * 
	 * @return the total
	 */
	public BigDecimal getUnitPrice()
	{
		return this.unitPrice;
	}

	/**
	 * Sets the unit price.
	 * 
	 * @param total the total to set
	 */
	public void setUnitPrice(final BigDecimal total)
	{
		this.unitPrice = total;
	}

	public String getItemCode()
	{
		return this.itemCode;
	}

	public void setItemCode(final String itemCode)
	{
		this.itemCode = itemCode;
	}

	public String getTaxCode()
	{
		return this.taxCode;
	}

	public void setTaxCode(final String taxCode)
	{
		this.taxCode = taxCode;
	}

	public AnnotationHashMap getVendorParameters()
	{
		return vendorParameters;
	}

	public void setVendorParameters(final AnnotationHashMap vendorParameters)
	{
		this.vendorParameters = vendorParameters;
	}
}
