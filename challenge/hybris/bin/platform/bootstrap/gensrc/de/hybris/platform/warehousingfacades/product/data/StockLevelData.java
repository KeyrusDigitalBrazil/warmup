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
package de.hybris.platform.warehousingfacades.product.data;

import java.io.Serializable;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import java.util.Date;

public  class StockLevelData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>StockLevelData.productCode</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String productCode;

	/** <i>Generated property</i> for <code>StockLevelData.bin</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String bin;

	/** <i>Generated property</i> for <code>StockLevelData.initialQuantityOnHand</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Integer initialQuantityOnHand;

	/** <i>Generated property</i> for <code>StockLevelData.releaseDate</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Date releaseDate;

	/** <i>Generated property</i> for <code>StockLevelData.inStockStatus</code> property defined at extension <code>warehousingfacades</code>. */
		
	private InStockStatus inStockStatus;

	/** <i>Generated property</i> for <code>StockLevelData.warehouse</code> property defined at extension <code>warehousingfacades</code>. */
		
	private WarehouseData warehouse;
	
	public StockLevelData()
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
	
		
	
	public void setBin(final String bin)
	{
		this.bin = bin;
	}

		
	
	public String getBin() 
	{
		return bin;
	}
	
		
	
	public void setInitialQuantityOnHand(final Integer initialQuantityOnHand)
	{
		this.initialQuantityOnHand = initialQuantityOnHand;
	}

		
	
	public Integer getInitialQuantityOnHand() 
	{
		return initialQuantityOnHand;
	}
	
		
	
	public void setReleaseDate(final Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

		
	
	public Date getReleaseDate() 
	{
		return releaseDate;
	}
	
		
	
	public void setInStockStatus(final InStockStatus inStockStatus)
	{
		this.inStockStatus = inStockStatus;
	}

		
	
	public InStockStatus getInStockStatus() 
	{
		return inStockStatus;
	}
	
		
	
	public void setWarehouse(final WarehouseData warehouse)
	{
		this.warehouse = warehouse;
	}

		
	
	public WarehouseData getWarehouse() 
	{
		return warehouse;
	}
	


}
