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
package de.hybris.platform.warehousingfacades.asn.data;

import java.io.Serializable;

public  class AsnEntryData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>AsnEntryData.productCode</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String productCode;

	/** <i>Generated property</i> for <code>AsnEntryData.quantity</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Integer quantity;
	
	public AsnEntryData()
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
