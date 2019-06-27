/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:07
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
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderCancelEntryWsDTO;
import java.util.List;

public  class OrderCancelRequestWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>OrderCancelRequestWsDTO.entries</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<OrderCancelEntryWsDTO> entries;

	/** <i>Generated property</i> for <code>OrderCancelRequestWsDTO.userId</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String userId;
	
	public OrderCancelRequestWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setEntries(final List<OrderCancelEntryWsDTO> entries)
	{
		this.entries = entries;
	}

		
	
	public List<OrderCancelEntryWsDTO> getEntries() 
	{
		return entries;
	}
	
		
	
	public void setUserId(final String userId)
	{
		this.userId = userId;
	}

		
	
	public String getUserId() 
	{
		return userId;
	}
	


}
