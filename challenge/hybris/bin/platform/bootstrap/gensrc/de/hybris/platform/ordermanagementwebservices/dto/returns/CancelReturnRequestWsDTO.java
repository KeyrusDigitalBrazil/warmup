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
package de.hybris.platform.ordermanagementwebservices.dto.returns;

import java.io.Serializable;

public  class CancelReturnRequestWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>CancelReturnRequestWsDTO.code</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String code;

	/** <i>Generated property</i> for <code>CancelReturnRequestWsDTO.cancelReason</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String cancelReason;

	/** <i>Generated property</i> for <code>CancelReturnRequestWsDTO.notes</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String notes;
	
	public CancelReturnRequestWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setCode(final String code)
	{
		this.code = code;
	}

		
	
	public String getCode() 
	{
		return code;
	}
	
		
	
	public void setCancelReason(final String cancelReason)
	{
		this.cancelReason = cancelReason;
	}

		
	
	public String getCancelReason() 
	{
		return cancelReason;
	}
	
		
	
	public void setNotes(final String notes)
	{
		this.notes = notes;
	}

		
	
	public String getNotes() 
	{
		return notes;
	}
	


}
