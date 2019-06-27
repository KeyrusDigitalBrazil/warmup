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
package de.hybris.platform.warehousingfacades.order.data;

import java.io.Serializable;
import de.hybris.platform.warehousing.enums.DeclineReason;

public  class DeclineEntryData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>DeclineEntryData.productCode</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String productCode;

	/** <i>Generated property</i> for <code>DeclineEntryData.quantity</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Long quantity;

	/** <i>Generated property</i> for <code>DeclineEntryData.reallocationWarehouseCode</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String reallocationWarehouseCode;

	/** <i>Generated property</i> for <code>DeclineEntryData.reason</code> property defined at extension <code>warehousingfacades</code>. */
		
	private DeclineReason reason;

	/** <i>Generated property</i> for <code>DeclineEntryData.comment</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String comment;
	
	public DeclineEntryData()
	{
		// default constructor
	}
	
		
	
	public void setProductCode(final String productCode)
	{
		this.productCode = productCode;
	}

		
	
	public String getProductCode() 
	{
		return productCode;
	}
	
		
	
	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Long getQuantity() 
	{
		return quantity;
	}
	
		
	
	public void setReallocationWarehouseCode(final String reallocationWarehouseCode)
	{
		this.reallocationWarehouseCode = reallocationWarehouseCode;
	}

		
	
	public String getReallocationWarehouseCode() 
	{
		return reallocationWarehouseCode;
	}
	
		
	
	public void setReason(final DeclineReason reason)
	{
		this.reason = reason;
	}

		
	
	public DeclineReason getReason() 
	{
		return reason;
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
