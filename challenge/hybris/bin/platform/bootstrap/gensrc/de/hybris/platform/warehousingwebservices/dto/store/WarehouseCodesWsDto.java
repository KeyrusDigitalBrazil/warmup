/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:03
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
import java.util.List;

public  class WarehouseCodesWsDto  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>WarehouseCodesWsDto.codes</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private List<String> codes;
	
	public WarehouseCodesWsDto()
	{
		// default constructor
	}
	
		
	
	public void setCodes(final List<String> codes)
	{
		this.codes = codes;
	}

		
	
	public List<String> getCodes() 
	{
		return codes;
	}
	


}
