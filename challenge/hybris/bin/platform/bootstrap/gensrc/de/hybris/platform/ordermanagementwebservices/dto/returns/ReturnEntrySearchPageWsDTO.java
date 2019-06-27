/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:13
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
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.SortWsDTO;
import de.hybris.platform.ordermanagementwebservices.dto.returns.ReturnEntryWsDTO;
import java.util.List;

public  class ReturnEntrySearchPageWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ReturnEntrySearchPageWsDTO.sorts</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<SortWsDTO> sorts;

	/** <i>Generated property</i> for <code>ReturnEntrySearchPageWsDTO.pagination</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private PaginationWsDTO pagination;

	/** <i>Generated property</i> for <code>ReturnEntrySearchPageWsDTO.returnEntries</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<ReturnEntryWsDTO> returnEntries;
	
	public ReturnEntrySearchPageWsDTO()
	{
		// default constructor
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
	
		
	
	public void setReturnEntries(final List<ReturnEntryWsDTO> returnEntries)
	{
		this.returnEntries = returnEntries;
	}

		
	
	public List<ReturnEntryWsDTO> getReturnEntries() 
	{
		return returnEntries;
	}
	


}
