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
package de.hybris.platform.warehousingfacades.storelocator.data;

import java.io.Serializable;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import java.util.List;

public  class WarehouseData  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>WarehouseData.code</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String code;

	/** <i>Generated property</i> for <code>WarehouseData.isDefault</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Boolean isDefault;

	/** <i>Generated property</i> for <code>WarehouseData.url</code> property defined at extension <code>warehousingfacades</code>. */
		
	private String url;

	/** <i>Generated property</i> for <code>WarehouseData.consignments</code> property defined at extension <code>warehousingfacades</code>. */
		
	private List<ConsignmentData> consignments;

	/** <i>Generated property</i> for <code>WarehouseData.pointsOfServices</code> property defined at extension <code>warehousingfacades</code>. */
		
	private List<PointOfServiceData> pointsOfServices;

	/** <i>Generated property</i> for <code>WarehouseData.priority</code> property defined at extension <code>warehousingfacades</code>. */
		
	private Integer priority;

	/** <i>Generated property</i> for <code>WarehouseData.deliveryModes</code> property defined at extension <code>warehousingfacades</code>. */
		
	private List<DeliveryModeData> deliveryModes;

	/** <i>Generated property</i> for <code>WarehouseData.external</code> property defined at extension <code>warehousingfacades</code>. */
		
	private boolean external;
	
	public WarehouseData()
	{
		// default constructor
	}
	
		
	
	public void setCode(final String code)
	{
		this.code = code;
	}

		
	
	public String getCode() 
	{
		return code;
	}
	
		
	
	public void setIsDefault(final Boolean isDefault)
	{
		this.isDefault = isDefault;
	}

		
	
	public Boolean getIsDefault() 
	{
		return isDefault;
	}
	
		
	
	public void setUrl(final String url)
	{
		this.url = url;
	}

		
	
	public String getUrl() 
	{
		return url;
	}
	
		
	
	public void setConsignments(final List<ConsignmentData> consignments)
	{
		this.consignments = consignments;
	}

		
	
	public List<ConsignmentData> getConsignments() 
	{
		return consignments;
	}
	
		
	
	public void setPointsOfServices(final List<PointOfServiceData> pointsOfServices)
	{
		this.pointsOfServices = pointsOfServices;
	}

		
	
	public List<PointOfServiceData> getPointsOfServices() 
	{
		return pointsOfServices;
	}
	
		
	
	public void setPriority(final Integer priority)
	{
		this.priority = priority;
	}

		
	
	public Integer getPriority() 
	{
		return priority;
	}
	
		
	
	public void setDeliveryModes(final List<DeliveryModeData> deliveryModes)
	{
		this.deliveryModes = deliveryModes;
	}

		
	
	public List<DeliveryModeData> getDeliveryModes() 
	{
		return deliveryModes;
	}
	
		
	
	public void setExternal(final boolean external)
	{
		this.external = external;
	}

		
	
	public boolean isExternal() 
	{
		return external;
	}
	


}
