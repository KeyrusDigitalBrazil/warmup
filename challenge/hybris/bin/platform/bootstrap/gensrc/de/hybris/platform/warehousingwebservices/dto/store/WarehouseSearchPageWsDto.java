/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:10
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
package de.hybris.platform.warehousingwebservices.dto.store;

import java.io.Serializable;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.SortWsDTO;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseWsDto;
import java.util.List;

public  class WarehouseSearchPageWsDto  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>WarehouseSearchPageWsDto.warehouses</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<WarehouseWsDto> warehouses;

	/** <i>Generated property</i> for <code>WarehouseSearchPageWsDto.sorts</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<SortWsDTO> sorts;

	/** <i>Generated property</i> for <code>WarehouseSearchPageWsDto.pagination</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private PaginationWsDTO pagination;
	
	public WarehouseSearchPageWsDto()
	{
		// default constructor
	}
	
		
	
	public void setWarehouses(final List<WarehouseWsDto> warehouses)
	{
		this.warehouses = warehouses;
	}

		
	
	public List<WarehouseWsDto> getWarehouses() 
	{
		return warehouses;
	}
	
		
	
	public void setSorts(final List<SortWsDTO> sorts)
	{
		this.sorts = sorts;
	}

		
	
	public List<SortWsDTO> getSorts() 
	{
		return sorts;
	}
	
		
	
	public void setPagination(final PaginationWsDTO pagination)
	{
		this.pagination = pagination;
	}

		
	
	public PaginationWsDTO getPagination() 
	{
		return pagination;
	}
	


}
