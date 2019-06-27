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
package de.hybris.platform.warehousingwebservices.dto.store;

import java.io.Serializable;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.SortWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import java.util.List;

public  class PointOfServiceSearchPageWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>PointOfServiceSearchPageWsDTO.pointsOfService</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<PointOfServiceWsDTO> pointsOfService;

	/** <i>Generated property</i> for <code>PointOfServiceSearchPageWsDTO.sorts</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<SortWsDTO> sorts;

	/** <i>Generated property</i> for <code>PointOfServiceSearchPageWsDTO.pagination</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private PaginationWsDTO pagination;
	
	public PointOfServiceSearchPageWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setPointsOfService(final List<PointOfServiceWsDTO> pointsOfService)
	{
		this.pointsOfService = pointsOfService;
	}

		
	
	public List<PointOfServiceWsDTO> getPointsOfService() 
	{
		return pointsOfService;
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
