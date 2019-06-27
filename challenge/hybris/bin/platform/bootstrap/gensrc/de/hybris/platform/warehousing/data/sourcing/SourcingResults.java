/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:12
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
package de.hybris.platform.warehousing.data.sourcing;

import java.io.Serializable;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import java.util.Set;

public  class SourcingResults  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>SourcingResults.results</code> property defined at extension <code>warehousing</code>. */
		
	private Set<SourcingResult> results;

	/** <i>Generated property</i> for <code>SourcingResults.complete</code> property defined at extension <code>warehousing</code>. */
		
	private boolean complete;
	
	public SourcingResults()
	{
		// default constructor
	}
	
		
	
	public void setResults(final Set<SourcingResult> results)
	{
		this.results = results;
	}

		
	
	public Set<SourcingResult> getResults() 
	{
		return results;
	}
	
		
	
	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}

		
	
	public boolean isComplete() 
	{
		return complete;
	}
	


}
