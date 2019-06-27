/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:10
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
import de.hybris.platform.warehousing.data.sourcing.SourcingFactorIdentifiersEnum;

public  class SourcingFactor  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>SourcingFactor.factorId</code> property defined at extension <code>warehousing</code>. */
		
	private SourcingFactorIdentifiersEnum factorId;

	/** <i>Generated property</i> for <code>SourcingFactor.weight</code> property defined at extension <code>warehousing</code>. */
		
	private int weight;
	
	public SourcingFactor()
	{
		// default constructor
	}
	
		
	
	public void setFactorId(final SourcingFactorIdentifiersEnum factorId)
	{
		this.factorId = factorId;
	}

		
	
	public SourcingFactorIdentifiersEnum getFactorId() 
	{
		return factorId;
	}
	
		
	
	public void setWeight(final int weight)
	{
		this.weight = weight;
	}

		
	
	public int getWeight() 
	{
		return weight;
	}
	


}
