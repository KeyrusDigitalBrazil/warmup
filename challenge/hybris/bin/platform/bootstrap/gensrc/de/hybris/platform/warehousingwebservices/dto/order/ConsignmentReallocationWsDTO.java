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
package de.hybris.platform.warehousingwebservices.dto.order;

import java.io.Serializable;
import de.hybris.platform.warehousingwebservices.dto.order.DeclineEntryWsDTO;
import java.util.List;

public  class ConsignmentReallocationWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ConsignmentReallocationWsDTO.declineEntries</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<DeclineEntryWsDTO> declineEntries;

	/** <i>Generated property</i> for <code>ConsignmentReallocationWsDTO.globalReallocationWarehouseCode</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private String globalReallocationWarehouseCode;

	/** <i>Generated property</i> for <code>ConsignmentReallocationWsDTO.globalReason</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private String globalReason;

	/** <i>Generated property</i> for <code>ConsignmentReallocationWsDTO.globalComment</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private String globalComment;
	
	public ConsignmentReallocationWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setDeclineEntries(final List<DeclineEntryWsDTO> declineEntries)
	{
		this.declineEntries = declineEntries;
	}

		
	
	public List<DeclineEntryWsDTO> getDeclineEntries() 
	{
		return declineEntries;
	}
	
		
	
	public void setGlobalReallocationWarehouseCode(final String globalReallocationWarehouseCode)
	{
		this.globalReallocationWarehouseCode = globalReallocationWarehouseCode;
	}

		
	
	public String getGlobalReallocationWarehouseCode() 
	{
		return globalReallocationWarehouseCode;
	}
	
		
	
	public void setGlobalReason(final String globalReason)
	{
		this.globalReason = globalReason;
	}

		
	
	public String getGlobalReason() 
	{
		return globalReason;
	}
	
		
	
	public void setGlobalComment(final String globalComment)
	{
		this.globalComment = globalComment;
	}

		
	
	public String getGlobalComment() 
	{
		return globalComment;
	}
	


}
