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
package de.hybris.platform.warehousing.data.pickslip;

import java.io.Serializable;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.warehousing.model.AllocationEventModel;
import java.util.List;

public  class ConsolidatedPickSlipFormEntry  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ConsolidatedPickSlipFormEntry.product</code> property defined at extension <code>warehousing</code>. */
		
	private ProductModel product;

	/** <i>Generated property</i> for <code>ConsolidatedPickSlipFormEntry.bin</code> property defined at extension <code>warehousing</code>. */
		
	private String bin;

	/** <i>Generated property</i> for <code>ConsolidatedPickSlipFormEntry.quantity</code> property defined at extension <code>warehousing</code>. */
		
	private Long quantity;

	/** <i>Generated property</i> for <code>ConsolidatedPickSlipFormEntry.allocationEvents</code> property defined at extension <code>warehousing</code>. */
		
	private List<AllocationEventModel> allocationEvents;
	
	public ConsolidatedPickSlipFormEntry()
	{
		// default constructor
	}
	
		
	
	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

		
	
	public ProductModel getProduct() 
	{
		return product;
	}
	
		
	
	public void setBin(final String bin)
	{
		this.bin = bin;
	}

		
	
	public String getBin() 
	{
		return bin;
	}
	
		
	
	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Long getQuantity() 
	{
		return quantity;
	}
	
		
	
	public void setAllocationEvents(final List<AllocationEventModel> allocationEvents)
	{
		this.allocationEvents = allocationEvents;
	}

		
	
	public List<AllocationEventModel> getAllocationEvents() 
	{
		return allocationEvents;
	}
	


}
