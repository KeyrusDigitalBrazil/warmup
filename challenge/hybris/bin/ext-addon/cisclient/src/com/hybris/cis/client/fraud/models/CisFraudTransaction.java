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

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.Identifiable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Class that holds all the information related to a transaction so it can be checked for fraud.
 */
@XmlRootElement(name = "transaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisFraudTransaction implements Identifiable
{
	/** Unique id (e.g order/shipment/return number). */
	@XmlAttribute(name = "id")
	private String id;

	/** The date of the transaction. */
	@XmlElement(name = "date")
	private Date date;

	/** The type of transaction. */
	@XmlElement(name = "type")
	private String type;

	/** The total amount of the fraudulent order. */
	@XmlElement(name = "totalAmount")
	private BigDecimal totalAmount;

	/** The specified shipping costs. */
	@XmlElement(name = "shippingCost")
	private BigDecimal shippingCost;

	/** The specified sales tax. */
	@XmlElement(name = "salesTax")
	private BigDecimal salesTax;

	/** The specified shipping taxes. */
	@XmlElement(name = "shippingTax")
	private BigDecimal shippingTax;

	/** The currency of the order. */
	@XmlElement(name = "currency")
	private String currency;

	/** A promotion code used on the order. */
	@XmlElement(name = "promotionCode")
	private String promotionCode;

	/** The specified total discount. */
	@XmlElement(name = "totalDiscount")
	private BigDecimal totalDiscount;

	/** List of additional payment information. */
	@XmlElementWrapper(name = "paymentInformations")
	@XmlElement(name = "paymentInformation")
	private List<CisFraudPaymentInformation> paymentInformations;

	/** the addresses related to this fraud report. */
	@XmlElementWrapper(name = "addresses")
	@XmlElement(name = "address")
	private List<CisAddress> addresses;

	/** The line items covered in this fraud report. */
	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "lineItem")
	private List<CisFraudLineItem> lineItems;

	/** A specified member id on this fraud report. */
	@XmlElement(name = "memberId")
	private String memberId;

	/** The member shipping date. */
	@XmlElement(name = "memberShipDate")
	private Date memberShipDate;

	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	public Date getDate()
	{
		return this.date == null ? null : new Date(this.date.getTime());
	}

	public void setDate(final Date date)
	{
		this.date = date == null ? null : new Date(date.getTime());
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(final String type)
	{
		this.type = type;
	}

	public BigDecimal getTotalAmount()
	{
		return this.totalAmount;
	}

	public void setTotalAmount(final BigDecimal totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public BigDecimal getShippingCost()
	{
		return this.shippingCost;
	}

	public void setShippingCost(final BigDecimal shippingCost)
	{
		this.shippingCost = shippingCost;
	}

	public BigDecimal getSalesTax()
	{
		return this.salesTax;
	}

	public void setSalesTax(final BigDecimal salesTax)
	{
		this.salesTax = salesTax;
	}

	public BigDecimal getShippingTax()
	{
		return this.shippingTax;
	}

	public void setShippingTax(final BigDecimal shippingTax)
	{
		this.shippingTax = shippingTax;
	}

	public String getCurrency()
	{
		return this.currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	public String getPromotionCode()
	{
		return this.promotionCode;
	}

	public void setPromotionCode(final String promotionCode)
	{
		this.promotionCode = promotionCode;
	}

	public BigDecimal getTotalDiscount()
	{
		return this.totalDiscount;
	}

	public void setTotalDiscount(final BigDecimal totalDiscount)
	{
		this.totalDiscount = totalDiscount;
	}

	public List<CisAddress> getAddresses()
	{
		return this.addresses;
	}

	public void setAddresses(final List<CisAddress> addresses)
	{
		this.addresses = addresses;
	}

	public List<CisFraudLineItem> getLineItems()
	{
		return this.lineItems;
	}

	public void setLineItems(final List<CisFraudLineItem> lineItems)
	{
		this.lineItems = lineItems;
	}

	public String getMemberId()
	{
		return this.memberId;
	}

	public void setMemberId(final String memberId)
	{
		this.memberId = memberId;
	}

	public Date getMemberShipDate()
	{
		return this.memberShipDate == null ? null : new Date(this.memberShipDate.getTime());
	}

	public void setMemberShipDate(final Date memberShipDate)
	{
		this.memberShipDate = memberShipDate == null ? null : new Date(memberShipDate.getTime());
	}

	public List<CisFraudPaymentInformation> getPaymentInformations()
	{
		return this.paymentInformations;
	}

	public void setPaymentInformations(final List<CisFraudPaymentInformation> paymentInformations)
	{
		this.paymentInformations = paymentInformations;
	}
}
