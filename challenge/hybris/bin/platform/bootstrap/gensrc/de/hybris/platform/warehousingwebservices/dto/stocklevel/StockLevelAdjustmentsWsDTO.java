/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:09
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
package de.hybris.platform.warehousingwebservices.dto.stocklevel;

import java.io.Serializable;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentWsDTO;
import java.util.List;

public  class StockLevelAdjustmentsWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>StockLevelAdjustmentsWsDTO.stockLevelAdjustments</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<StockLevelAdjustmentWsDTO> stockLevelAdjustments;
	
	public StockLevelAdjustmentsWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setStockLevelAdjustments(final List<StockLevelAdjustmentWsDTO> stockLevelAdjustments)
	{
		this.stockLevelAdjustments = stockLevelAdjustments;
	}

		
	
	public List<StockLevelAdjustmentWsDTO> getStockLevelAdjustments() 
	{
		return stockLevelAdjustments;
	}
	


}
