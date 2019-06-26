/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:11
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
package de.hybris.platform.commercefacades.order.data;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import java.util.Date;
import java.util.List;

public  class OrderData extends AbstractOrderData 
{

 

	/** <i>Generated property</i> for <code>OrderData.created</code> property defined at extension <code>commercefacades</code>. */
		
	private Date created;

	/** <i>Generated property</i> for <code>OrderData.status</code> property defined at extension <code>commercefacades</code>. */
		
	private OrderStatus status;

	/** <i>Generated property</i> for <code>OrderData.statusDisplay</code> property defined at extension <code>commercefacades</code>. */
		
	private String statusDisplay;

	/** <i>Generated property</i> for <code>OrderData.guestCustomer</code> property defined at extension <code>commercefacades</code>. */
		
	private boolean guestCustomer;

	/** <i>Generated property</i> for <code>OrderData.consignments</code> property defined at extension <code>commercefacades</code>. */
		
	private List<ConsignmentData> consignments;

	/** <i>Generated property</i> for <code>OrderData.deliveryStatus</code> property defined at extension <code>commercefacades</code>. */
		
	private DeliveryStatus deliveryStatus;

	/** <i>Generated property</i> for <code>OrderData.deliveryStatusDisplay</code> property defined at extension <code>commercefacades</code>. */
		
	private String deliveryStatusDisplay;

	/** <i>Generated property</i> for <code>OrderData.unconsignedEntries</code> property defined at extension <code>commercefacades</code>. */
		
	private List<OrderEntryData> unconsignedEntries;

	/** <i>Generated property</i> for <code>OrderData.placedBy</code> property defined at extension <code>commercefacades</code>. */
		
	private String placedBy;

	/** <i>Generated property</i> for <code>OrderData.quoteCode</code> property defined at extension <code>commercefacades</code>. */
		
	private String quoteCode;

	/** <i>Generated property</i> for <code>OrderData.cancellable</code> property defined at extension <code>ordermanagementfacades</code>. */
		
	private boolean cancellable;

	/** <i>Generated property</i> for <code>OrderData.returnable</code> property defined at extension <code>ordermanagementfacades</code>. */
		
	private boolean returnable;
	
	public OrderData()
	{
		// default constructor
	}
	
		
	
	public void setCreated(final Date created)
	{
		this.created = created;
	}

		
	
	public Date getCreated() 
	{
		return created;
	}
	
		
	
	public void setStatus(final OrderStatus status)
	{
		this.status = status;
	}

		
	
	public OrderStatus getStatus() 
	{
		return status;
	}
	
		
	
	public void setStatusDisplay(final String statusDisplay)
	{
		this.statusDisplay = statusDisplay;
	}

		
	
	public String getStatusDisplay() 
	{
		return statusDisplay;
	}
	
		
	
	public void setGuestCustomer(final boolean guestCustomer)
	{
		this.guestCustomer = guestCustomer;
	}

		
	
	public boolean isGuestCustomer() 
	{
		return guestCustomer;
	}
	
		
	
	public void setConsignments(final List<ConsignmentData> consignments)
	{
		this.consignments = consignments;
	}

		
	
	public List<ConsignmentData> getConsignments() 
	{
		return consignments;
	}
	
		
	
	public void setDeliveryStatus(final DeliveryStatus deliveryStatus)
	{
		this.deliveryStatus = deliveryStatus;
	}

		
	
	public DeliveryStatus getDeliveryStatus() 
	{
		return deliveryStatus;
	}
	
		
	
	public void setDeliveryStatusDisplay(final String deliveryStatusDisplay)
	{
		this.deliveryStatusDisplay = deliveryStatusDisplay;
	}

		
	
	public String getDeliveryStatusDisplay() 
	{
		return deliveryStatusDisplay;
	}
	
		
	
	public void setUnconsignedEntries(final List<OrderEntryData> unconsignedEntries)
	{
		this.unconsignedEntries = unconsignedEntries;
	}

		
	
	public List<OrderEntryData> getUnconsignedEntries() 
	{
		return unconsignedEntries;
	}
	
		
	
	public void setPlacedBy(final String placedBy)
	{
		this.placedBy = placedBy;
	}

		
	
	public String getPlacedBy() 
	{
		return placedBy;
	}
	
		
	
	public void setQuoteCode(final String quoteCode)
	{
		this.quoteCode = quoteCode;
	}

		
	
	public String getQuoteCode() 
	{
		return quoteCode;
	}
	
		
	
	public void setCancellable(final boolean cancellable)
	{
		this.cancellable = cancellable;
	}

		
	
	public boolean isCancellable() 
	{
		return cancellable;
	}
	
		
	
	public void setReturnable(final boolean returnable)
	{
		this.returnable = returnable;
	}

		
	
	public boolean isReturnable() 
	{
		return returnable;
	}
	


}
