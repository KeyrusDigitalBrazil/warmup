/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:04
 * ----------------------------------------------------------------
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ordermanagementwebservices.dto.order;

import java.io.Serializable;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionResultWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderEntryRequestWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.payment.PaymentTransactionWsDTO;
import java.util.Date;
import java.util.List;
import java.util.Set;

public  class OrderRequestWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.externalOrderCode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String externalOrderCode;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.name</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String name;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.description</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String description;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.guid</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String guid;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.user</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private UserWsDTO user;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.siteUid</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String siteUid;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.storeUid</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String storeUid;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.currencyIsocode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String currencyIsocode;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.languageIsocode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String languageIsocode;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.deliveryAddress</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private AddressWsDTO deliveryAddress;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.paymentAddress</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private AddressWsDTO paymentAddress;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.deliveryModeCode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String deliveryModeCode;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.deliveryStatus</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String deliveryStatus;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.net</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private boolean net;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.calculated</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private boolean calculated;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.totalPrice</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private double totalPrice;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.totalTax</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private double totalTax;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.subtotal</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private double subtotal;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.deliveryCost</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private double deliveryCost;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.expirationTime</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Date expirationTime;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.entries</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<OrderEntryRequestWsDTO> entries;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.paymentTransactions</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<PaymentTransactionWsDTO> paymentTransactions;

	/** <i>Generated property</i> for <code>OrderRequestWsDTO.allPromotionResults</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Set<PromotionResultWsDTO> allPromotionResults;
	
	public OrderRequestWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setExternalOrderCode(final String externalOrderCode)
	{
		this.externalOrderCode = externalOrderCode;
	}

		
	
	public String getExternalOrderCode() 
	{
		return externalOrderCode;
	}
	
		
	
	public void setName(final String name)
	{
		this.name = name;
	}

		
	
	public String getName() 
	{
		return name;
	}
	
		
	
	public void setDescription(final String description)
	{
		this.description = description;
	}

		
	
	public String getDescription() 
	{
		return description;
	}
	
		
	
	public void setGuid(final String guid)
	{
		this.guid = guid;
	}

		
	
	public String getGuid() 
	{
		return guid;
	}
	
		
	
	public void setUser(final UserWsDTO user)
	{
		this.user = user;
	}

		
	
	public UserWsDTO getUser() 
	{
		return user;
	}
	
		
	
	public void setSiteUid(final String siteUid)
	{
		this.siteUid = siteUid;
	}

		
	
	public String getSiteUid() 
	{
		return siteUid;
	}
	
		
	
	public void setStoreUid(final String storeUid)
	{
		this.storeUid = storeUid;
	}

		
	
	public String getStoreUid() 
	{
		return storeUid;
	}
	
		
	
	public void setCurrencyIsocode(final String currencyIsocode)
	{
		this.currencyIsocode = currencyIsocode;
	}

		
	
	public String getCurrencyIsocode() 
	{
		return currencyIsocode;
	}
	
		
	
	public void setLanguageIsocode(final String languageIsocode)
	{
		this.languageIsocode = languageIsocode;
	}

		
	
	public String getLanguageIsocode() 
	{
		return languageIsocode;
	}
	
		
	
	public void setDeliveryAddress(final AddressWsDTO deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}

		
	
	public AddressWsDTO getDeliveryAddress() 
	{
		return deliveryAddress;
	}
	
		
	
	public void setPaymentAddress(final AddressWsDTO paymentAddress)
	{
		this.paymentAddress = paymentAddress;
	}

		
	
	public AddressWsDTO getPaymentAddress() 
	{
		return paymentAddress;
	}
	
		
	
	public void setDeliveryModeCode(final String deliveryModeCode)
	{
		this.deliveryModeCode = deliveryModeCode;
	}

		
	
	public String getDeliveryModeCode() 
	{
		return deliveryModeCode;
	}
	
		
	
	public void setDeliveryStatus(final String deliveryStatus)
	{
		this.deliveryStatus = deliveryStatus;
	}

		
	
	public String getDeliveryStatus() 
	{
		return deliveryStatus;
	}
	
		
	
	public void setNet(final boolean net)
	{
		this.net = net;
	}

		
	
	public boolean isNet() 
	{
		return net;
	}
	
		
	
	public void setCalculated(final boolean calculated)
	{
		this.calculated = calculated;
	}

		
	
	public boolean isCalculated() 
	{
		return calculated;
	}
	
		
	
	public void setTotalPrice(final double totalPrice)
	{
		this.totalPrice = totalPrice;
	}

		
	
	public double getTotalPrice() 
	{
		return totalPrice;
	}
	
		
	
	public void setTotalTax(final double totalTax)
	{
		this.totalTax = totalTax;
	}

		
	
	public double getTotalTax() 
	{
		return totalTax;
	}
	
		
	
	public void setSubtotal(final double subtotal)
	{
		this.subtotal = subtotal;
	}

		
	
	public double getSubtotal() 
	{
		return subtotal;
	}
	
		
	
	public void setDeliveryCost(final double deliveryCost)
	{
		this.deliveryCost = deliveryCost;
	}

		
	
	public double getDeliveryCost() 
	{
		return deliveryCost;
	}
	
		
	
	public void setExpirationTime(final Date expirationTime)
	{
		this.expirationTime = expirationTime;
	}

		
	
	public Date getExpirationTime() 
	{
		return expirationTime;
	}
	
		
	
	public void setEntries(final List<OrderEntryRequestWsDTO> entries)
	{
		this.entries = entries;
	}

		
	
	public List<OrderEntryRequestWsDTO> getEntries() 
	{
		return entries;
	}
	
		
	
	public void setPaymentTransactions(final List<PaymentTransactionWsDTO> paymentTransactions)
	{
		this.paymentTransactions = paymentTransactions;
	}

		
	
	public List<PaymentTransactionWsDTO> getPaymentTransactions() 
	{
		return paymentTransactions;
	}
	
		
	
	public void setAllPromotionResults(final Set<PromotionResultWsDTO> allPromotionResults)
	{
		this.allPromotionResults = allPromotionResults;
	}

		
	
	public Set<PromotionResultWsDTO> getAllPromotionResults() 
	{
		return allPromotionResults;
	}
	


}
