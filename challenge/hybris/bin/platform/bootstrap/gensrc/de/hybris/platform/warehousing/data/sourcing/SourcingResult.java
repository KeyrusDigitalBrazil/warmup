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
package de.hybris.platform.warehousing.data.sourcing;

import java.io.Serializable;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import java.util.Map;

public  class SourcingResult  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>SourcingResult.allocation</code> property defined at extension <code>warehousing</code>. */
		
	private Map<AbstractOrderEntryModel, Long> allocation;

	/** <i>Generated property</i> for <code>SourcingResult.warehouse</code> property defined at extension <code>warehousing</code>. */
		
	private WarehouseModel warehouse;
	
	public SourcingResult()
	{
		// default constructor
	}
	
		
	
	public void setAllocation(final Map<AbstractOrderEntryModel, Long> allocation)
	{
		this.allocation = allocation;
	}

		
	
	public Map<AbstractOrderEntryModel, Long> getAllocation() 
	{
		return allocation;
	}
	
		
	
	public void setWarehouse(final WarehouseModel warehouse)
	{
		this.warehouse = warehouse;
	}

		
	
	public WarehouseModel getWarehouse() 
	{
		return warehouse;
	}
	


}
