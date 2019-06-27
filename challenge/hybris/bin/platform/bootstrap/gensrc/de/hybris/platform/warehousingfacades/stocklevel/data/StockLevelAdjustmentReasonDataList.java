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
package de.hybris.platform.warehousingfacades.stocklevel.data;

import java.io.Serializable;
import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;
import java.util.List;

public  class StockLevelAdjustmentReasonDataList  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>StockLevelAdjustmentReasonDataList.reasons</code> property defined at extension <code>warehousingfacades</code>. */
		
	private List<StockLevelAdjustmentReason> reasons;
	
	public StockLevelAdjustmentReasonDataList()
	{
		// default constructor
	}
	
		
	
	public void setReasons(final List<StockLevelAdjustmentReason> reasons)
	{
		this.reasons = reasons;
	}

		
	
	public List<StockLevelAdjustmentReason> getReasons() 
	{
		return reasons;
	}
	


}
