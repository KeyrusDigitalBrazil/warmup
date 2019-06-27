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
package de.hybris.platform.ordersplitting.model;

import de.hybris.bootstrap.annotations.Accessor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.warehousing.model.DeclineConsignmentEntryEventModel;
import de.hybris.platform.warehousing.model.InventoryEventModel;
import java.util.Collection;
import java.util.Set;

/**
 * Generated model class for type ConsignmentEntry first defined at extension basecommerce.
 */
@SuppressWarnings("all")
public class ConsignmentEntryModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "ConsignmentEntry";
	
	/**<i>Generated relation code constant for relation <code>DeclineConsignmentEntryEventConsignmentEntryRelation</code> defining source attribute <code>declineEntryEvents</code> in extension <code>warehousing</code>.</i>*/
	public static final String _DECLINECONSIGNMENTENTRYEVENTCONSIGNMENTENTRYRELATION = "DeclineConsignmentEntryEventConsignmentEntryRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.quantity</code> attribute defined at extension <code>basecommerce</code>. */
	public static final String QUANTITY = "quantity";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.shippedQuantity</code> attribute defined at extension <code>basecommerce</code>. */
	public static final String SHIPPEDQUANTITY = "shippedQuantity";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.orderEntry</code> attribute defined at extension <code>basecommerce</code>. */
	public static final String ORDERENTRY = "orderEntry";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.consignment</code> attribute defined at extension <code>basecommerce</code>. */
	public static final String CONSIGNMENT = "consignment";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.quantityDeclined</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYDECLINED = "quantityDeclined";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.quantityPending</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYPENDING = "quantityPending";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.quantityShipped</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYSHIPPED = "quantityShipped";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.declineEntryEvents</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DECLINEENTRYEVENTS = "declineEntryEvents";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntry.inventoryEvents</code> attribute defined at extension <code>warehousing</code>. */
	public static final String INVENTORYEVENTS = "inventoryEvents";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public ConsignmentEntryModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public ConsignmentEntryModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _consignment initial attribute declared by type <code>ConsignmentEntry</code> at extension <code>basecommerce</code>
	 * @param _orderEntry initial attribute declared by type <code>ConsignmentEntry</code> at extension <code>basecommerce</code>
	 * @param _quantity initial attribute declared by type <code>ConsignmentEntry</code> at extension <code>basecommerce</code>
	 */
	@Deprecated
	public ConsignmentEntryModel(final ConsignmentModel _consignment, final AbstractOrderEntryModel _orderEntry, final Long _quantity)
	{
		super();
		setConsignment(_consignment);
		setOrderEntry(_orderEntry);
		setQuantity(_quantity);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _consignment initial attribute declared by type <code>ConsignmentEntry</code> at extension <code>basecommerce</code>
	 * @param _orderEntry initial attribute declared by type <code>ConsignmentEntry</code> at extension <code>basecommerce</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _quantity initial attribute declared by type <code>ConsignmentEntry</code> at extension <code>basecommerce</code>
	 */
	@Deprecated
	public ConsignmentEntryModel(final ConsignmentModel _consignment, final AbstractOrderEntryModel _orderEntry, final ItemModel _owner, final Long _quantity)
	{
		super();
		setConsignment(_consignment);
		setOrderEntry(_orderEntry);
		setOwner(_owner);
		setQuantity(_quantity);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.consignment</code> attribute defined at extension <code>basecommerce</code>. 
	 * @return the consignment
	 */
	@Accessor(qualifier = "consignment", type = Accessor.Type.GETTER)
	public ConsignmentModel getConsignment()
	{
		return getPersistenceContext().getPropertyValue(CONSIGNMENT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.declineEntryEvents</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the declineEntryEvents
	 */
	@Accessor(qualifier = "declineEntryEvents", type = Accessor.Type.GETTER)
	public Set<DeclineConsignmentEntryEventModel> getDeclineEntryEvents()
	{
		return getPersistenceContext().getPropertyValue(DECLINEENTRYEVENTS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.inventoryEvents</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the inventoryEvents
	 */
	@Accessor(qualifier = "inventoryEvents", type = Accessor.Type.GETTER)
	public Collection<InventoryEventModel> getInventoryEvents()
	{
		return getPersistenceContext().getPropertyValue(INVENTORYEVENTS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.orderEntry</code> attribute defined at extension <code>basecommerce</code>. 
	 * @return the orderEntry
	 */
	@Accessor(qualifier = "orderEntry", type = Accessor.Type.GETTER)
	public AbstractOrderEntryModel getOrderEntry()
	{
		return getPersistenceContext().getPropertyValue(ORDERENTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.quantity</code> attribute defined at extension <code>basecommerce</code>. 
	 * @return the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.GETTER)
	public Long getQuantity()
	{
		return getPersistenceContext().getPropertyValue(QUANTITY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.quantityDeclined</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityDeclined
	 */
	@Accessor(qualifier = "quantityDeclined", type = Accessor.Type.GETTER)
	public Long getQuantityDeclined()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYDECLINED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.quantityPending</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityPending
	 */
	@Accessor(qualifier = "quantityPending", type = Accessor.Type.GETTER)
	public Long getQuantityPending()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYPENDING);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.quantityShipped</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityShipped
	 */
	@Accessor(qualifier = "quantityShipped", type = Accessor.Type.GETTER)
	public Long getQuantityShipped()
	{
		return getPersistenceContext().getDynamicValue(this,QUANTITYSHIPPED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntry.shippedQuantity</code> attribute defined at extension <code>basecommerce</code>. 
	 * @return the shippedQuantity
	 */
	@Accessor(qualifier = "shippedQuantity", type = Accessor.Type.GETTER)
	public Long getShippedQuantity()
	{
		return getPersistenceContext().getPropertyValue(SHIPPEDQUANTITY);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ConsignmentEntry.consignment</code> attribute defined at extension <code>basecommerce</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the consignment
	 */
	@Accessor(qualifier = "consignment", type = Accessor.Type.SETTER)
	public void setConsignment(final ConsignmentModel value)
	{
		getPersistenceContext().setPropertyValue(CONSIGNMENT, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>ConsignmentEntry.declineEntryEvents</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the declineEntryEvents
	 */
	@Accessor(qualifier = "declineEntryEvents", type = Accessor.Type.SETTER)
	public void setDeclineEntryEvents(final Set<DeclineConsignmentEntryEventModel> value)
	{
		getPersistenceContext().setPropertyValue(DECLINEENTRYEVENTS, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>ConsignmentEntry.inventoryEvents</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the inventoryEvents
	 */
	@Accessor(qualifier = "inventoryEvents", type = Accessor.Type.SETTER)
	public void setInventoryEvents(final Collection<InventoryEventModel> value)
	{
		getPersistenceContext().setPropertyValue(INVENTORYEVENTS, value);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>ConsignmentEntry.orderEntry</code> attribute defined at extension <code>basecommerce</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the orderEntry
	 */
	@Accessor(qualifier = "orderEntry", type = Accessor.Type.SETTER)
	public void setOrderEntry(final AbstractOrderEntryModel value)
	{
		getPersistenceContext().setPropertyValue(ORDERENTRY, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>ConsignmentEntry.quantity</code> attribute defined at extension <code>basecommerce</code>. 
	 *  
	 * @param value the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.SETTER)
	public void setQuantity(final Long value)
	{
		getPersistenceContext().setPropertyValue(QUANTITY, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>ConsignmentEntry.shippedQuantity</code> attribute defined at extension <code>basecommerce</code>. 
	 *  
	 * @param value the shippedQuantity
	 */
	@Accessor(qualifier = "shippedQuantity", type = Accessor.Type.SETTER)
	public void setShippedQuantity(final Long value)
	{
		getPersistenceContext().setPropertyValue(SHIPPEDQUANTITY, value);
	}
	
}
