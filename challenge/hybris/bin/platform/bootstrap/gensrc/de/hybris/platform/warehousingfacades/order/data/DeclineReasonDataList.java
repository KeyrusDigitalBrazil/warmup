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
import java.util.List;

public  class DeclineReasonDataList  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>DeclineReasonDataList.reasons</code> property defined at extension <code>warehousingfacades</code>. */
		
	private List<DeclineReason> reasons;
	
	public DeclineReasonDataList()
	{
		// default constructor
	}
	
		
	
	public void setReasons(final List<DeclineReason> reasons)
	{
		this.reasons = reasons;
	}

		
	
	public List<DeclineReason> getReasons() 
	{
		return reasons;
	}
	


}