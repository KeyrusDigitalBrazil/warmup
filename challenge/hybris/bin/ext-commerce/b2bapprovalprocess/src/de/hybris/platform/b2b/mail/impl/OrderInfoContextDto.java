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
package de.hybris.platform.b2b.mail.impl;

import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Context used by order approval and order rejection emails.
 */
public class OrderInfoContextDto
{
	private String rendererTemplate;
	private String currencyIsoCode;
	private String orderNumber;
	private List<OrderEntryInfoContextDto> orderInfoEntries;
	private AddressModel deliveryAddress;
	private AddressModel paymentAddress;
	private String subtotalAmount;
	private String taxAmount;
	private String deliveryCost;
	private String paymentCost;
	private String discountInfo;
	private String totalAmount;
	private boolean hasDiscounts;
	private String baseUrl = "";
	private String userName = "";
	private String storeName = "";
	private String productURI = "";
	private Collection<B2BPermissionResultModel> permissionResults;
	private String emailAddress;

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(final String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(final String userName)
	{
		this.userName = userName;
	}

	public String getStoreName()
	{
		return storeName;
	}

	public void setStoreName(final String storeName)
	{
		this.storeName = storeName;
	}

	public List<OrderEntryInfoContextDto> getOrderInfoEntries()
	{
		return this.orderInfoEntries;
	}

	public void setOrderInfoEntries(final List<OrderEntryInfoContextDto> orderInfoEntries)
	{
		this.orderInfoEntries = orderInfoEntries;
	}

	public String getTotalAmount()
	{
		return this.totalAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setTotalAmount(java.lang.String)
	 */

	public void setTotalAmount(final String totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getCurrencyIsoCode()
	 */

	public String getCurrencyIsoCode()
	{
		return this.currencyIsoCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setCurrencyIsoCode(java.lang.String)
	 */

	public void setCurrencyIsoCode(final String currencyIsoCode)
	{
		this.currencyIsoCode = currencyIsoCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getOrderNumber()
	 */

	public String getOrderNumber()
	{
		return orderNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setOrderNumber(java.lang.String)
	 */

	public void setOrderNumber(final String orderNumber)
	{
		this.orderNumber = orderNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getDeliveryAddress()
	 */

	public AddressModel getDeliveryAddress()
	{
		return deliveryAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setDeliveryAddress(de.hybris.platform.core.model.user.
	 * AddressModel)
	 */

	public void setDeliveryAddress(final AddressModel deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getPaymentAddress()
	 */

	public AddressModel getPaymentAddress()
	{
		return paymentAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setPaymentAddress(de.hybris.platform.core.model.user
	 * .AddressModel )
	 */

	public void setPaymentAddress(final AddressModel paymentAddress)
	{
		this.paymentAddress = paymentAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getSubtotalAmount()
	 */

	public String getSubtotalAmount()
	{
		return subtotalAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setSubtotalAmount(java.lang.String)
	 */

	public void setSubtotalAmount(final String subtotalAmount)
	{
		this.subtotalAmount = subtotalAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getTaxAmount()
	 */

	public String getTaxAmount()
	{
		return taxAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setTaxAmount(java.lang.String)
	 */

	public void setTaxAmount(final String taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getDeliveryCost()
	 */

	public String getDeliveryCost()
	{
		return deliveryCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setDeliveryCost(java.lang.String)
	 */

	public void setDeliveryCost(final String deliveryCost)
	{
		this.deliveryCost = deliveryCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getDiscountInfo()
	 */

	public String getDiscountInfo()
	{
		return discountInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setDiscountInfo(java.lang.String)
	 */

	public void setDiscountInfo(final String discountInfo)
	{
		this.discountInfo = discountInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getPaymentCost()
	 */

	public String getPaymentCost()
	{
		return paymentCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setPaymentCost(java.lang.String)
	 */

	public void setPaymentCost(final String paymentCost)
	{
		this.paymentCost = paymentCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#isHasDiscounts()
	 */

	public boolean isHasDiscounts()
	{
		return hasDiscounts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setHasDiscounts(boolean)
	 */

	public void setHasDiscounts(final boolean hasDiscounts)
	{
		this.hasDiscounts = hasDiscounts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getPermissionResults()
	 */

	public Collection<B2BPermissionResultModel> getPermissionResults()
	{
		return permissionResults;


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setPermissionResults(java.util.Collection)
	 */

	public void setPermissionResults(final Collection<B2BPermissionResultModel> permissionResults)
	{
		this.permissionResults = permissionResults;
	}

	public static class OrderEntryInfoContextDto
	{

		/**
		 * The order entry.
		 */
		private AbstractOrderEntryModel orderEntry;

		/**
		 * The product link.
		 */
		private String productLink;

		/**
		 * The base price.
		 */
		private String basePrice;

		/**
		 * The total price.
		 */
		private String totalPrice;

		/**
		 * The product name.
		 */
		private String productName;

		/**
		 * The discount price.
		 */
		private String discountPrice;

		/**
		 * The product store name.
		 */
		private String productStoreName;

		/**
		 * The order entry number.
		 */
		private String orderEntryNumber;

		/**
		 * The order entry status.
		 */
		private String orderEntryStatus;

		/**
		 * The custom attributes.
		 */
		private String customAttributes;

		/**
		 * The tracking numbers.
		 */
		private List<String> trackingNumbers = new ArrayList<String>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getOrderEntry()
		 */

		public AbstractOrderEntryModel getOrderEntry()
		{
			return orderEntry;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setOrderEntry(de.hybris.platform.core.model.order
		 * .AbstractOrderEntryModel)
		 */

		public void setOrderEntry(final AbstractOrderEntryModel orderEntry)
		{
			this.orderEntry = orderEntry;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getProductLink()
		 */

		public String getProductLink()
		{
			return productLink;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setProductLink(java.lang.String)
		 */

		public void setProductLink(final String productLink)
		{
			this.productLink = productLink;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getBasePrice()
		 */

		public String getBasePrice()
		{
			return basePrice;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setBasePrice(java.lang.String)
		 */

		public void setBasePrice(final String basePrice)
		{
			this.basePrice = basePrice;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getTotalPrice()
		 */

		public String getTotalPrice()
		{
			return totalPrice;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setTotalPrice(java.lang.String)
		 */

		public void setTotalPrice(final String totalPrice)
		{
			this.totalPrice = totalPrice;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getProductName()
		 */

		public String getProductName()
		{
			return productName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setProductName(java.lang.String)
		 */

		public void setProductName(final String productName)
		{
			this.productName = productName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setDiscountPrice(java.lang.String)
		 */

		public void setDiscountPrice(final String discountPrice)
		{
			this.discountPrice = discountPrice;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getProductStoreName()
		 */

		public String getProductStoreName()
		{
			return productStoreName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setProductStoreName(java.lang.String)
		 */

		public void setProductStoreName(final String productStoreName)
		{
			this.productStoreName = productStoreName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getDiscountPrice()
		 */

		public String getDiscountPrice()
		{
			return discountPrice;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getOrderEntryNumber()
		 */

		public String getOrderEntryNumber()
		{
			return orderEntryNumber;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setOrderEntryNumber(java.lang.String)
		 */

		public void setOrderEntryNumber(final String orderEntryNumber)
		{
			this.orderEntryNumber = orderEntryNumber;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getOrderEntryStatus()
		 */

		public String getOrderEntryStatus()
		{
			return orderEntryStatus;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setOrderEntryStatus(java.lang.String)
		 */

		public void setOrderEntryStatus(final String orderEntryStatus)
		{
			this.orderEntryStatus = orderEntryStatus;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getCustomAttributes()
		 */

		public String getCustomAttributes()
		{
			return customAttributes;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setCustomAttributes(java.lang.String)
		 */

		public void setCustomAttributes(final String customAttributes)
		{
			this.customAttributes = customAttributes;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#getTrackingNumbers()
		 */

		public List<String> getTrackingNumbers()
		{
			return trackingNumbers;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.hybris.platform.b2b.mail.context.OrderEntryInfoContext#setTrackingNumbers(java.util.List)
		 */

		public void setTrackingNumbers(final List<String> trackingNumbers)
		{
			this.trackingNumbers = trackingNumbers;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#getProductURI()
	 */

	public String getProductURI()
	{
		return productURI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.b2b.mail.context.OrderInfoContext#setProductURI(java.lang.String)
	 */

	public void setProductURI(final String productURI)
	{
		this.productURI = productURI;
	}


	public String getRendererTemplate()
	{
		return rendererTemplate;
	}

	public void setRendererTemplate(final String rendererTemplate)
	{
		this.rendererTemplate = rendererTemplate;
	}


}
