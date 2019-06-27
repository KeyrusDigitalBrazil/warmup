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


import com.hybris.cis.client.shared.models.CisLineItem;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class CisFraudLineItem extends CisLineItem
{
	@XmlElement(name = "itemNumber")
	private int itemNumber;

	@XmlElement(name = "productType")
	private String productType;

	@XmlElement(name = "totalTax")
	private BigDecimal totalTax;

	@XmlElement(name = "totalPrice")
	private BigDecimal totalPrice;

	@XmlElement(name = "itemSku")
	private String itemSku;

	@XmlElement(name = "unit")
	private String unit;

	@XmlElement(name = "discount")
	private BigDecimal discount;

	public int getItemNumber()
	{
		return this.itemNumber;
	}

	public void setItemNumber(final int itemNumber)
	{
		this.itemNumber = itemNumber;
	}

	public String getProductType()
	{
		return this.productType;
	}

	public void setProductType(final String productType)
	{
		this.productType = productType;
	}

	public BigDecimal getTotalTax()
	{
		return this.totalTax;
	}

	public void setTotalTax(final BigDecimal totalTax)
	{
		this.totalTax = totalTax;
	}

	public BigDecimal getTotalPrice()
	{
		return this.totalPrice;
	}

	public void setTotalPrice(final BigDecimal totalPrice)
	{
		this.totalPrice = totalPrice;
	}

	public String getItemSku()
	{
		return this.itemSku;
	}

	public void setItemSku(final String itemSku)
	{
		this.itemSku = itemSku;
	}

	public String getUnit()
	{
		return this.unit;
	}

	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	public BigDecimal getDiscount()
	{
		return this.discount;
	}

	public void setDiscount(final BigDecimal discount)
	{
		this.discount = discount;
	}
}
