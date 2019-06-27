/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:06
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
import java.util.List;

public  class CancelReasonListWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>CancelReasonListWsDTO.reasons</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<String> reasons;
	
	public CancelReasonListWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setReasons(final List<String> reasons)
	{
		this.reasons = reasons;
	}

		
	
	public List<String> getReasons() 
	{
		return reasons;
	}
	


}
