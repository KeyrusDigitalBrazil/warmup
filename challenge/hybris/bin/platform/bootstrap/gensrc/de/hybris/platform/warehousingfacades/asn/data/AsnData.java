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
package de.hybris.platform.warehousingfacades.asn.data;

import java.io.Serializable;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousingfacades.asn.data.AsnEntryData;
import java.util.Date;
import java.util.List;

public  class AsnData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>AsnData.externalId</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String externalId;

	/** <i>Generated property</i> for <code>AsnData.warehouseCode</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String warehouseCode;

	/** <i>Generated property</i> for <code>AsnData.asnEntries</code> property defined at extension <code>warehousingfacades</code>. */
		
	private List<AsnEntryData> asnEntries;

	/** <i>Generated property</i> for <code>AsnData.pointOfServiceName</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String pointOfServiceName;

	/** <i>Generated property</i> for <code>AsnData.comment</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String comment;

	/** <i>Generated property</i> for <code>AsnData.releaseDate</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Date releaseDate;

	/** <i>Generated property</i> for <code>AsnData.internalId</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String internalId;

	/** <i>Generated property</i> for <code>AsnData.status</code> property defined at extension <code>warehousingfacades</code>. */
		
	private AsnStatus status;
	
	public AsnData()
	{
		// default constructor
	}
	
		
	
	public void setExternalId(final String externalId)
	{
		this.externalId = externalId;
	}

		
	
	public String getExternalId() 
	{
		return externalId;
	}
	
		
	
	public void setWarehouseCode(final String warehouseCode)
	{
		this.warehouseCode = warehouseCode;
	}

		
	
	public String getWarehouseCode() 
	{
		return warehouseCode;
	}
	
		
	
	public void setAsnEntries(final List<AsnEntryData> asnEntries)
	{
		this.asnEntries = asnEntries;
	}

		
	
	public List<AsnEntryData> getAsnEntries() 
	{
		return asnEntries;
	}
	
		
	
	public void setPointOfServiceName(final String pointOfServiceName)
	{
		this.pointOfServiceName = pointOfServiceName;
	}

		
	
	public String getPointOfServiceName() 
	{
		return pointOfServiceName;
	}
	
		
	
	public void setComment(final String comment)
	{
		this.comment = comment;
	}

		
	
	public String getComment() 
	{
		return comment;
	}
	
		
	
	public void setReleaseDate(final Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

		
	
	public Date getReleaseDate() 
	{
		return releaseDate;
	}
	
		
	
	public void setInternalId(final String internalId)
	{
		this.internalId = internalId;
	}

		
	
	public String getInternalId() 
	{
		return internalId;
	}
	
		
	
	public void setStatus(final AsnStatus status)
	{
		this.status = status;
	}

		
	
	public AsnStatus getStatus() 
	{
		return status;
	}
	


}
