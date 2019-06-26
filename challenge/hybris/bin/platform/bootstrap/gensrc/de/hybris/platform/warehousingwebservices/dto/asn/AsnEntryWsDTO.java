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
package de.hybris.platform.warehousingwebservices.dto.asn;

import java.io.Serializable;

public  class AsnEntryWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>AsnEntryWsDTO.productCode</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private String productCode;

	/** <i>Generated property</i> for <code>AsnEntryWsDTO.quantity</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Integer quantity;
	
	public AsnEntryWsDTO()
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
	
		
	
	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Integer getQuantity() 
	{
		return quantity;
	}
	


}
