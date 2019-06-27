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
package de.hybris.platform.consignmenttrackingservices.delivery.data;

import java.io.Serializable;
import java.util.Date;

public  class ConsignmentEventData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ConsignmentEventData.eventDate</code> property defined at extension <code>consignmenttrackingservices</code>. */
		
	private Date eventDate;

	/** <i>Generated property</i> for <code>ConsignmentEventData.detail</code> property defined at extension <code>consignmenttrackingservices</code>. */
		
	private String detail;

	/** <i>Generated property</i> for <code>ConsignmentEventData.location</code> property defined at extension <code>consignmenttrackingservices</code>. */
		
	private String location;

	/** <i>Generated property</i> for <code>ConsignmentEventData.referenceCode</code> property defined at extension <code>consignmenttrackingservices</code>. */
		
	private String referenceCode;
	
	public ConsignmentEventData()
	{
		// default constructor
	}
	
		
	
	public void setEventDate(final Date eventDate)
	{
		this.eventDate = eventDate;
	}

		
	
	public Date getEventDate() 
	{
		return eventDate;
	}
	
		
	
	public void setDetail(final String detail)
	{
		this.detail = detail;
	}

		
	
	public String getDetail() 
	{
		return detail;
	}
	
		
	
	public void setLocation(final String location)
	{
		this.location = location;
	}

		
	
	public String getLocation() 
	{
		return location;
	}
	
		
	
	public void setReferenceCode(final String referenceCode)
	{
		this.referenceCode = referenceCode;
	}

		
	
	public String getReferenceCode() 
	{
		return referenceCode;
	}
	


}
