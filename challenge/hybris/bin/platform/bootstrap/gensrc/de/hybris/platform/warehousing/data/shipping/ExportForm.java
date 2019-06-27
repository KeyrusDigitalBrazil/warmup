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
package de.hybris.platform.warehousing.data.shipping;

import java.io.Serializable;
import de.hybris.platform.warehousing.data.shipping.ExportFormEntry;
import java.math.BigDecimal;
import java.util.List;

public  class ExportForm  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ExportForm.formEntries</code> property defined at extension <code>warehousing</code>. */
		
	private List<ExportFormEntry> formEntries;

	/** <i>Generated property</i> for <code>ExportForm.totalPrice</code> property defined at extension <code>warehousing</code>. */
		
	private BigDecimal totalPrice;
	
	public ExportForm()
	{
		// default constructor
	}
	
		
	
	public void setFormEntries(final List<ExportFormEntry> formEntries)
	{
		this.formEntries = formEntries;
	}

		
	
	public List<ExportFormEntry> getFormEntries() 
	{
		return formEntries;
	}
	
		
	
	public void setTotalPrice(final BigDecimal totalPrice)
	{
		this.totalPrice = totalPrice;
	}

		
	
	public BigDecimal getTotalPrice() 
	{
		return totalPrice;
	}
	


}
