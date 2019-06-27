/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:04
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
package de.hybris.platform.warehousing.data.allocation;

import java.io.Serializable;
import java.util.Collection;

public  class DeclineEntries  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>DeclineEntries.entries</code> property defined at extension <code>warehousing</code>. */
		
	private Collection<DeclineEntry> entries;
	
	public DeclineEntries()
	{
		// default constructor
	}
	
		
	
	public void setEntries(final Collection<DeclineEntry> entries)
	{
		this.entries = entries;
	}

		
	
	public Collection<DeclineEntry> getEntries() 
	{
		return entries;
	}
	


}
