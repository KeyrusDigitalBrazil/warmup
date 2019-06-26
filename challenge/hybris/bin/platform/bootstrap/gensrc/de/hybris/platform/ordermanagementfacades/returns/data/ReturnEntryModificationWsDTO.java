/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:08
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
package de.hybris.platform.ordermanagementfacades.returns.data;

import java.io.Serializable;
import java.math.BigDecimal;

public  class ReturnEntryModificationWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ReturnEntryModificationWsDTO.productCode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String productCode;

	/** <i>Generated property</i> for <code>ReturnEntryModificationWsDTO.deliveryModeCode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String deliveryModeCode;

	/** <i>Generated property</i> for <code>ReturnEntryModificationWsDTO.refundAmount</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private BigDecimal refundAmount;
	
	public ReturnEntryModificationWsDTO()
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
	
		
	
	public void setDeliveryModeCode(final String deliveryModeCode)
	{
		this.deliveryModeCode = deliveryModeCode;
	}

		
	
	public String getDeliveryModeCode() 
	{
		return deliveryModeCode;
	}
	
		
	
	public void setRefundAmount(final BigDecimal refundAmount)
	{
		this.refundAmount = refundAmount;
	}

		
	
	public BigDecimal getRefundAmount() 
	{
		return refundAmount;
	}
	


}
