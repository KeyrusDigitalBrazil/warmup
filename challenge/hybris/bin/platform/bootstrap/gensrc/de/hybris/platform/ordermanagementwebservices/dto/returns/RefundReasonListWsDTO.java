/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:02
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
import java.util.List;

public  class RefundReasonListWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>RefundReasonListWsDTO.refundReasons</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<String> refundReasons;
	
	public RefundReasonListWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setRefundReasons(final List<String> refundReasons)
	{
		this.refundReasons = refundReasons;
	}

		
	
	public List<String> getRefundReasons() 
	{
		return refundReasons;
	}
	


}
