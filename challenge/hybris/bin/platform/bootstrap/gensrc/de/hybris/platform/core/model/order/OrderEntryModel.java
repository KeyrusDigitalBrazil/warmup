/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 26/06/2019 16:55:50                         ---
 * ----------------------------------------------------------------
 *  
 * [y] hybris Platform
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.core.model.order;

import de.hybris.bootstrap.annotations.Accessor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.warehousing.model.InventoryEventModel;
import java.util.Collection;

/**
 * Generated model class for type OrderEntry first defined at extension core.
 */
@SuppressWarnings("all")
public class OrderEntryModel extends AbstractOrderEntryModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "OrderEntry";
	
	/**<i>Generated relation code constant for relation <code>AbstractOrder2AbstractOrderEntry</code> defining source attribute <code>order</code> in extension <code>core</code>.</i>*/
	public static final String _ABSTRACTORDER2ABSTRACTORDERENTRY = "AbstractOrder2AbstractOrderEntry";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.quantityAllocated</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYALLOCATED = "quantityAllocated";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.quantityUnallocated</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYUNALLOCATED = "quantityUnallocated";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.quantityCancelled</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYCANCELLED = "quantityCancelled";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.quantityPending</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYPENDING = "quantityPending";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.quantityShipped</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYSHIPPED = "quantityShipped";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.quantityReturned</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYRETURNED = "quantityReturned";
	
	/** <i>Generated constant</i> - Attribute key of <code>OrderEntry.inventoryEvents</code> attribute defined at extension <code>warehousing</code>. */
	public static final String INVENTORYEVENTS = "inventoryEvents";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public OrderEntryModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public OrderEntryModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _product initial attribute declared by type <code>AbstractOrderEntry</code> at extension <code>core</code>
	 * @param _quantity initial attribute declared by type <code>AbstractOrderEntry</code> at extension <code>core</code>
	 * @param _unit initial attribute declared by type <code>AbstractOrderEntry</code> at extension <code>core</code>
	 */
	@Deprecated
	public OrderEntryModel(final ProductModel _product, final Long _quantity, final UnitModel _unit)
	{
		super();
		setProduct(_product);
		setQuantity(_quantity);
		setUnit(_unit);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _product initial attribute declared by type <code>AbstractOrderEntry</code> at extension <code>core</code>
	 * @param _quantity initial attribute declared by type <code>AbstractOrderEntry</code> at extension <code>core</code>
	 * @param _unit initial attribute declared by type <code>AbstractOrderEntry</code> at extension <code>core</code>
	 */
	@Deprecated
	public OrderEntryModel(final ItemModel _owner, final ProductModel _product, final Long _quantity, final UnitModel _unit)
	{
		super();
		setOwner(_owner);
		setProduct(_product);
		setQuantity(_quantity);
		setUnit(_unit);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.inventoryEvents</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the inventoryEvents
	 */
	@Accessor(qualifier = "inventoryEvents", type = Accessor.Type.GETTER)
	public Collection<InventoryEventModel> getInventoryEvents()
	{
		return getPersistenceContext().getPropertyValue(INVENTORYEVENTS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AbstractOrderEntry.order</code> attribute defined at extension <code>core</code> and redeclared at extension <code>core</code>. 
	 * @return the order
	 */
	@Override
	@Accessor(qualifier = "order", type = Accessor.Type.GETTER)
	public OrderModel getOrder()
	{
		return (OrderModel) super.getOrder();
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.quantityAllocated</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityAllocated
	 */
	@Accessor(qualifier = "quantityAllocated", type = Accessor.Type.GETTER)
	public Long getQuantityAllocated()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYALLOCATED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.quantityCancelled</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityCancelled
	 */
	@Accessor(qualifier = "quantityCancelled", type = Accessor.Type.GETTER)
	public Long getQuantityCancelled()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYCANCELLED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.quantityPending</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityPending
	 */
	@Accessor(qualifier = "quantityPending", type = Accessor.Type.GETTER)
	public Long getQuantityPending()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYPENDING);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.quantityReturned</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityReturned
	 */
	@Accessor(qualifier = "quantityReturned", type = Accessor.Type.GETTER)
	public Long getQuantityReturned()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYRETURNED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.quantityShipped</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityShipped
	 */
	@Accessor(qualifier = "quantityShipped", type = Accessor.Type.GETTER)
	public Long getQuantityShipped()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYSHIPPED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>OrderEntry.quantityUnallocated</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityUnallocated
	 */
	@Accessor(qualifier = "quantityUnallocated", type = Accessor.Type.GETTER)
	public Long getQuantityUnallocated()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYUNALLOCATED);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>OrderEntry.inventoryEvents</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the inventoryEvents
	 */
	@Accessor(qualifier = "inventoryEvents", type = Accessor.Type.SETTER)
	public void setInventoryEvents(final Collection<InventoryEventModel> value)
	{
		getPersistenceContext().setPropertyValue(INVENTORYEVENTS, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>AbstractOrderEntry.order</code> attribute defined at extension <code>core</code> and redeclared at extension <code>core</code>. Can only be used at creation of model - before first save. Will only accept values of type {@link de.hybris.platform.core.model.order.OrderModel}.  
	 *  
	 * @param value the order
	 */
	@Override
	@Accessor(qualifier = "order", type = Accessor.Type.SETTER)
	public void setOrder(final AbstractOrderModel value)
	{
		if( value == null || value instanceof OrderModel)
		{
			super.setOrder(value);
		}
		else
		{
			throw new IllegalArgumentException("Given value is not instance of de.hybris.platform.core.model.order.OrderModel");
		}
	}
	
}
