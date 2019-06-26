/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:12
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

public  class OrderCancelEntryWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>OrderCancelEntryWsDTO.orderEntryNumber</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String orderEntryNumber;

	/** <i>Generated property</i> for <code>OrderCancelEntryWsDTO.cancelQuantity</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Long cancelQuantity;

	/** <i>Generated property</i> for <code>OrderCancelEntryWsDTO.notes</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String notes;

	/** <i>Generated property</i> for <code>OrderCancelEntryWsDTO.cancelReason</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String cancelReason;
	
	public OrderCancelEntryWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setOrderEntryNumber(final String orderEntryNumber)
	{
		this.orderEntryNumber = orderEntryNumber;
	}

		
	
	public String getOrderEntryNumber() 
	{
		return orderEntryNumber;
	}
	
		
	
	public void setCancelQuantity(final Long cancelQuantity)
	{
		this.cancelQuantity = cancelQuantity;
	}

		
	
	public Long getCancelQuantity() 
	{
		return cancelQuantity;
	}
	
		
	
	public void setNotes(final String notes)
	{
		this.notes = notes;
	}

		
	
	public String getNotes() 
	{
		return notes;
	}
	
		
	
	public void setCancelReason(final String cancelReason)
	{
		this.cancelReason = cancelReason;
	}

		
	
	public String getCancelReason() 
	{
		return cancelReason;
	}
	


}
