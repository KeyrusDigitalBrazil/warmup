/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:10
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
package de.hybris.platform.ordermanagementwebservices.dto.returns;

import java.io.Serializable;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import java.util.Date;

public  class ReturnEntryWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.expectedQuantity</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Long expectedQuantity;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.receivedQuantity</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Long receivedQuantity;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.reachedDate</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Date reachedDate;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.orderEntry</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private OrderEntryWsDTO orderEntry;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.notes</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String notes;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.action</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String action;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.refundReason</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String refundReason;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.replacementReason</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String replacementReason;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.refundAmount</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private PriceWsDTO refundAmount;

	/** <i>Generated property</i> for <code>ReturnEntryWsDTO.refundedDate</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Date refundedDate;
	
	public ReturnEntryWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setExpectedQuantity(final Long expectedQuantity)
	{
		this.expectedQuantity = expectedQuantity;
	}

		
	
	public Long getExpectedQuantity() 
	{
		return expectedQuantity;
	}
	
		
	
	public void setReceivedQuantity(final Long receivedQuantity)
	{
		this.receivedQuantity = receivedQuantity;
	}

		
	
	public Long getReceivedQuantity() 
	{
		return receivedQuantity;
	}
	
		
	
	public void setReachedDate(final Date reachedDate)
	{
		this.reachedDate = reachedDate;
	}

		
	
	public Date getReachedDate() 
	{
		return reachedDate;
	}
	
		
	
	public void setOrderEntry(final OrderEntryWsDTO orderEntry)
	{
		this.orderEntry = orderEntry;
	}

		
	
	public OrderEntryWsDTO getOrderEntry() 
	{
		return orderEntry;
	}
	
		
	
	public void setNotes(final String notes)
	{
		this.notes = notes;
	}

		
	
	public String getNotes() 
	{
		return notes;
	}
	
		
	
	public void setAction(final String action)
	{
		this.action = action;
	}

		
	
	public String getAction() 
	{
		return action;
	}
	
		
	
	public void setRefundReason(final String refundReason)
	{
		this.refundReason = refundReason;
	}

		
	
	public String getRefundReason() 
	{
		return refundReason;
	}
	
		
	
	public void setReplacementReason(final String replacementReason)
	{
		this.replacementReason = replacementReason;
	}

		
	
	public String getReplacementReason() 
	{
		return replacementReason;
	}
	
		
	
	public void setRefundAmount(final PriceWsDTO refundAmount)
	{
		this.refundAmount = refundAmount;
	}

		
	
	public PriceWsDTO getRefundAmount() 
	{
		return refundAmount;
	}
	
		
	
	public void setRefundedDate(final Date refundedDate)
	{
		this.refundedDate = refundedDate;
	}

		
	
	public Date getRefundedDate() 
	{
		return refundedDate;
	}
	


}
