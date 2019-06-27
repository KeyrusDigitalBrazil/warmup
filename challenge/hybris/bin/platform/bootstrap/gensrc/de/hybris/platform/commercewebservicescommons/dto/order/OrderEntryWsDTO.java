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
package de.hybris.platform.commercewebservicescommons.dto.order;

import java.io.Serializable;
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import java.util.List;

public  class OrderEntryWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.entryNumber</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private Integer entryNumber;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantity</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private Long quantity;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.basePrice</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private PriceWsDTO basePrice;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.totalPrice</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private PriceWsDTO totalPrice;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.product</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private ProductWsDTO product;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.updateable</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private Boolean updateable;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.deliveryMode</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private DeliveryModeWsDTO deliveryMode;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.configurationInfos</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private List<ConfigurationInfoWsDTO> configurationInfos;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.deliveryPointOfService</code> property defined at extension <code>commercewebservicescommons</code>. */
		
	private PointOfServiceWsDTO deliveryPointOfService;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.url</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String url;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantityAllocated</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Long quantityAllocated;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantityUnallocated</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Long quantityUnallocated;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantityCancelled</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Long quantityCancelled;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantityPending</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Long quantityPending;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantityShipped</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Long quantityShipped;

	/** <i>Generated property</i> for <code>OrderEntryWsDTO.quantityReturned</code> property defined at extension <code>warehousingwebservices</code>. */
		
	private Long quantityReturned;
	
	public OrderEntryWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setEntryNumber(final Integer entryNumber)
	{
		this.entryNumber = entryNumber;
	}

		
	
	public Integer getEntryNumber() 
	{
		return entryNumber;
	}
	
		
	
	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

		
	
	public Long getQuantity() 
	{
		return quantity;
	}
	
		
	
	public void setBasePrice(final PriceWsDTO basePrice)
	{
		this.basePrice = basePrice;
	}

		
	
	public PriceWsDTO getBasePrice() 
	{
		return basePrice;
	}
	
		
	
	public void setTotalPrice(final PriceWsDTO totalPrice)
	{
		this.totalPrice = totalPrice;
	}

		
	
	public PriceWsDTO getTotalPrice() 
	{
		return totalPrice;
	}
	
		
	
	public void setProduct(final ProductWsDTO product)
	{
		this.product = product;
	}

		
	
	public ProductWsDTO getProduct() 
	{
		return product;
	}
	
		
	
	public void setUpdateable(final Boolean updateable)
	{
		this.updateable = updateable;
	}

		
	
	public Boolean getUpdateable() 
	{
		return updateable;
	}
	
		
	
	public void setDeliveryMode(final DeliveryModeWsDTO deliveryMode)
	{
		this.deliveryMode = deliveryMode;
	}

		
	
	public DeliveryModeWsDTO getDeliveryMode() 
	{
		return deliveryMode;
	}
	
		
	
	public void setConfigurationInfos(final List<ConfigurationInfoWsDTO> configurationInfos)
	{
		this.configurationInfos = configurationInfos;
	}

		
	
	public List<ConfigurationInfoWsDTO> getConfigurationInfos() 
	{
		return configurationInfos;
	}
	
		
	
	public void setDeliveryPointOfService(final PointOfServiceWsDTO deliveryPointOfService)
	{
		this.deliveryPointOfService = deliveryPointOfService;
	}

		
	
	public PointOfServiceWsDTO getDeliveryPointOfService() 
	{
		return deliveryPointOfService;
	}
	
		
	
	public void setUrl(final String url)
	{
		this.url = url;
	}

		
	
	public String getUrl() 
	{
		return url;
	}
	
		
	
	public void setQuantityAllocated(final Long quantityAllocated)
	{
		this.quantityAllocated = quantityAllocated;
	}

		
	
	public Long getQuantityAllocated() 
	{
		return quantityAllocated;
	}
	
		
	
	public void setQuantityUnallocated(final Long quantityUnallocated)
	{
		this.quantityUnallocated = quantityUnallocated;
	}

		
	
	public Long getQuantityUnallocated() 
	{
		return quantityUnallocated;
	}
	
		
	
	public void setQuantityCancelled(final Long quantityCancelled)
	{
		this.quantityCancelled = quantityCancelled;
	}

		
	
	public Long getQuantityCancelled() 
	{
		return quantityCancelled;
	}
	
		
	
	public void setQuantityPending(final Long quantityPending)
	{
		this.quantityPending = quantityPending;
	}

		
	
	public Long getQuantityPending() 
	{
		return quantityPending;
	}
	
		
	
	public void setQuantityShipped(final Long quantityShipped)
	{
		this.quantityShipped = quantityShipped;
	}

		
	
	public Long getQuantityShipped() 
	{
		return quantityShipped;
	}
	
		
	
	public void setQuantityReturned(final Long quantityReturned)
	{
		this.quantityReturned = quantityReturned;
	}

		
	
	public Long getQuantityReturned() 
	{
		return quantityReturned;
	}
	


}
