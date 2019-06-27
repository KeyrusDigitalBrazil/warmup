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
package de.hybris.platform.warehousing.data.allocation;

import java.io.Serializable;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.enums.DeclineReason;

public  class DeclineEntry  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>DeclineEntry.consignmentEntry</code> property defined at extension <code>warehousing</code>. */
		
	private ConsignmentEntryModel consignmentEntry;

	/** <i>Generated property</i> for <code>DeclineEntry.quantity</code> property defined at extension <code>warehousing</code>. */
		
	private Long quantity;

	/** <i>Generated property</i> for <code>DeclineEntry.reason</code> property defined at extension <code>warehousing</code>. */
		
	private DeclineReason reason;

	/** <i>Generated property</i> for <code>DeclineEntry.notes</code> property defined at extension <code>warehousing</code>. */
		
	private String notes;

	/** <i>Generated property</i> for <code>DeclineEntry.reallocationWarehouse</code> property defined at extension <code>warehousing</code>. */
		
	private WarehouseModel reallocationWarehouse;
	
	public DeclineEntry()
	{
		// default constructor
	}
	
		
	
	public void setConsignmentEntry(final ConsignmentEntryModel consignmentEntry)
	{
		this.consignmentEntry = consignmentEntry;
	}

		
	
	public ConsignmentEntryModel getConsignmentEntry() 
	{
		return consignmentEntry;
	}
	
		
	
	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Long getQuantity() 
	{
		return quantity;
	}
	
		
	
	public void setReason(final DeclineReason reason)
	{
		this.reason = reason;
	}

		
	
	public DeclineReason getReason() 
	{
		return reason;
	}
	
		
	
	public void setNotes(final String notes)
	{
		this.notes = notes;
	}

		
	
	public String getNotes() 
	{
		return notes;
	}
	
		
	
	public void setReallocationWarehouse(final WarehouseModel reallocationWarehouse)
	{
		this.reallocationWarehouse = reallocationWarehouse;
	}

		
	
	public WarehouseModel getReallocationWarehouse() 
	{
		return reallocationWarehouse;
	}
	


}
