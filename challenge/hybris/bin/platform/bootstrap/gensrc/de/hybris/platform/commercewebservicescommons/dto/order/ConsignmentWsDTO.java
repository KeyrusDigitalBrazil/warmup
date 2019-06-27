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
package de.hybris.platform.commercewebservicescommons.dto.order;

import java.io.Serializable;
import de.hybris.platform.commercewebservicescommons.dto.order.ConsignmentEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.warehousingwebservices.dto.order.PackagingInfoWsDTO;
import java.util.Date;
import java.util.List;

public  class ConsignmentWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.code</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private String code;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.trackingID</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private String trackingID;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.status</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private String status;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.statusDate</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private Date statusDate;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.entries</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private List<ConsignmentEntryWsDTO> entries;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.shippingAddress</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private AddressWsDTO shippingAddress;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.deliveryPointOfService</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private PointOfServiceWsDTO deliveryPointOfService;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.orderCode</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private String orderCode;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.shippingDate</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Date shippingDate;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.deliveryMode</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private DeliveryModeWsDTO deliveryMode;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.warehouseCode</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private String warehouseCode;

	/** <i>Generated property</i> for <code>ConsignmentWsDTO.packagingInfo</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private PackagingInfoWsDTO packagingInfo;
	
	public ConsignmentWsDTO()
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
	
		
	
	public void setTrackingID(final String trackingID)
	{
		this.trackingID = trackingID;
	}

		
	
	public String getTrackingID() 
	{
		return trackingID;
	}
	
		
	
	public void setStatus(final String status)
	{
		this.status = status;
	}

		
	
	public String getStatus() 
	{
		return status;
	}
	
		
	
	public void setStatusDate(final Date statusDate)
	{
		this.statusDate = statusDate;
	}

		
	
	public Date getStatusDate() 
	{
		return statusDate;
	}
	
		
	
	public void setEntries(final List<ConsignmentEntryWsDTO> entries)
	{
		this.entries = entries;
	}

		
	
	public List<ConsignmentEntryWsDTO> getEntries() 
	{
		return entries;
	}
	
		
	
	public void setShippingAddress(final AddressWsDTO shippingAddress)
	{
		this.shippingAddress = shippingAddress;
	}

		
	
	public AddressWsDTO getShippingAddress() 
	{
		return shippingAddress;
	}
	
		
	
	public void setDeliveryPointOfService(final PointOfServiceWsDTO deliveryPointOfService)
	{
		this.deliveryPointOfService = deliveryPointOfService;
	}

		
	
	public PointOfServiceWsDTO getDeliveryPointOfService() 
	{
		return deliveryPointOfService;
	}
	
		
	
	public void setOrderCode(final String orderCode)
	{
		this.orderCode = orderCode;
	}

		
	
	public String getOrderCode() 
	{
		return orderCode;
	}
	
		
	
	public void setShippingDate(final Date shippingDate)
	{
		this.shippingDate = shippingDate;
	}

		
	
	public Date getShippingDate() 
	{
		return shippingDate;
	}
	
		
	
	public void setDeliveryMode(final DeliveryModeWsDTO deliveryMode)
	{
		this.deliveryMode = deliveryMode;
	}

		
	
	public DeliveryModeWsDTO getDeliveryMode() 
	{
		return deliveryMode;
	}
	
		
	
	public void setWarehouseCode(final String warehouseCode)
	{
		this.warehouseCode = warehouseCode;
	}

		
	
	public String getWarehouseCode() 
	{
		return warehouseCode;
	}
	
		
	
	public void setPackagingInfo(final PackagingInfoWsDTO packagingInfo)
	{
		this.packagingInfo = packagingInfo;
	}

		
	
	public PackagingInfoWsDTO getPackagingInfo() 
	{
		return packagingInfo;
	}
	


}
