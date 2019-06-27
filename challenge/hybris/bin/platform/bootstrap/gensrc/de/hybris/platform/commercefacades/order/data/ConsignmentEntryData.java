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

import java.io.Serializable;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

public  class ConsignmentEntryData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ConsignmentEntryData.orderEntry</code> property defined at extension <code>commercefacades</code>. */
		
	private OrderEntryData orderEntry;

	/** <i>Generated property</i> for <code>ConsignmentEntryData.quantity</code> property defined at extension <code>commercefacades</code>. */
		
	private Long quantity;

	/** <i>Generated property</i> for <code>ConsignmentEntryData.shippedQuantity</code> property defined at extension <code>commercefacades</code>. */
		
	private Long shippedQuantity;

	/** <i>Generated property</i> for <code>ConsignmentEntryData.quantityDeclined</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Long quantityDeclined;

	/** <i>Generated property</i> for <code>ConsignmentEntryData.quantityPending</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Long quantityPending;

	/** <i>Generated property</i> for <code>ConsignmentEntryData.quantityShipped</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Long quantityShipped;
	
	public ConsignmentEntryData()
	{
		// default constructor
	}
	
		
	
	public void setOrderEntry(final OrderEntryData orderEntry)
	{
		this.orderEntry = orderEntry;
	}

		
	
	public OrderEntryData getOrderEntry() 
	{
		return orderEntry;
	}
	
		
	
	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Long getQuantity() 
	{
		return quantity;
	}
	
		
	
	public void setShippedQuantity(final Long shippedQuantity)
	{
		this.shippedQuantity = shippedQuantity;
	}

		
	
	public Long getShippedQuantity() 
	{
		return shippedQuantity;
	}
	
		
	
	public void setQuantityDeclined(final Long quantityDeclined)
	{
		this.quantityDeclined = quantityDeclined;
	}

		
	
	public Long getQuantityDeclined() 
	{
		return quantityDeclined;
	}
	
		
	
	public void setQuantityPending(final Long quantityPending)
	{
		this.quantityPending = quantityPending;
	}

		
	
	public Long getQuantityPending() 
	{
		return quantityPending;
	}
	
		
	
	public void setQuantityShipped(final Long quantityShipped)
	{
		this.quantityShipped = quantityShipped;
	}

		
	
	public Long getQuantityShipped() 
	{
		return quantityShipped;
	}
	


}
