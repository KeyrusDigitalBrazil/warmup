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
package de.hybris.platform.warehousingfacades.stocklevel.data;

import java.io.Serializable;
import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;

public  class StockLevelAdjustmentData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>StockLevelAdjustmentData.reason</code> property defined at extension <code>warehousingfacades</code>. */
		
	private StockLevelAdjustmentReason reason;

	/** <i>Generated property</i> for <code>StockLevelAdjustmentData.quantity</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Long quantity;

	/** <i>Generated property</i> for <code>StockLevelAdjustmentData.comment</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String comment;
	
	public StockLevelAdjustmentData()
	{
		// default constructor
	}
	
		
	
	public void setReason(final StockLevelAdjustmentReason reason)
	{
		this.reason = reason;
	}

		
	
	public StockLevelAdjustmentReason getReason() 
	{
		return reason;
	}
	
		
	
	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Long getQuantity() 
	{
		return quantity;
	}
	
		
	
	public void setComment(final String comment)
	{
		this.comment = comment;
	}

		
	
	public String getComment() 
	{
		return comment;
	}
	


}
