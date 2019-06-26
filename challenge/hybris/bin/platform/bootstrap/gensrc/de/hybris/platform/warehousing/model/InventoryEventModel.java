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
package de.hybris.platform.warehousing.model;

import de.hybris.bootstrap.annotations.Accessor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import java.util.Date;

/**
 * Generated model class for type InventoryEvent first defined at extension warehousing.
 * <p>
 * Holds events that affect OMS Inventory and therefore ATP (Available To Promise).
 */
@SuppressWarnings("all")
public class InventoryEventModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "InventoryEvent";
	
	/**<i>Generated relation code constant for relation <code>StockLevel2InventoryEventRelation</code> defining source attribute <code>stockLevel</code> in extension <code>warehousing</code>.</i>*/
	public static final String _STOCKLEVEL2INVENTORYEVENTRELATION = "StockLevel2InventoryEventRelation";
	
	/**<i>Generated relation code constant for relation <code>ConsignmentEntry2InventoryEventRelation</code> defining source attribute <code>consignmentEntry</code> in extension <code>warehousing</code>.</i>*/
	public static final String _CONSIGNMENTENTRY2INVENTORYEVENTRELATION = "ConsignmentEntry2InventoryEventRelation";
	
	/**<i>Generated relation code constant for relation <code>OrderEntry2InventoryEventRelation</code> defining source attribute <code>orderEntry</code> in extension <code>warehousing</code>.</i>*/
	public static final String _ORDERENTRY2INVENTORYEVENTRELATION = "OrderEntry2InventoryEventRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>InventoryEvent.quantity</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITY = "quantity";
	
	/** <i>Generated constant</i> - Attribute key of <code>InventoryEvent.eventDate</code> attribute defined at extension <code>warehousing</code>. */
	public static final String EVENTDATE = "eventDate";
	
	/** <i>Generated constant</i> - Attribute key of <code>InventoryEvent.stockLevel</code> attribute defined at extension <code>warehousing</code>. */
	public static final String STOCKLEVEL = "stockLevel";
	
	/** <i>Generated constant</i> - Attribute key of <code>InventoryEvent.consignmentEntry</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CONSIGNMENTENTRY = "consignmentEntry";
	
	/** <i>Generated constant</i> - Attribute key of <code>InventoryEvent.orderEntry</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ORDERENTRY = "orderEntry";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public InventoryEventModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public InventoryEventModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _stockLevel initial attribute declared by type <code>InventoryEvent</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public InventoryEventModel(final StockLevelModel _stockLevel)
	{
		super();
		setStockLevel(_stockLevel);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _stockLevel initial attribute declared by type <code>InventoryEvent</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public InventoryEventModel(final ItemModel _owner, final StockLevelModel _stockLevel)
	{
		super();
		setOwner(_owner);
		setStockLevel(_stockLevel);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>InventoryEvent.consignmentEntry</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the consignmentEntry
	 */
	@Accessor(qualifier = "consignmentEntry", type = Accessor.Type.GETTER)
	public ConsignmentEntryModel getConsignmentEntry()
	{
		return getPersistenceContext().getPropertyValue(CONSIGNMENTENTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>InventoryEvent.eventDate</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the eventDate
	 */
	@Accessor(qualifier = "eventDate", type = Accessor.Type.GETTER)
	public Date getEventDate()
	{
		return getPersistenceContext().getPropertyValue(EVENTDATE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>InventoryEvent.orderEntry</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the orderEntry
	 */
	@Accessor(qualifier = "orderEntry", type = Accessor.Type.GETTER)
	public OrderEntryModel getOrderEntry()
	{
		return getPersistenceContext().getPropertyValue(ORDERENTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>InventoryEvent.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.GETTER)
	public long getQuantity()
	{
		return toPrimitive((Long)getPersistenceContext().getPropertyValue(QUANTITY));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>InventoryEvent.stockLevel</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the stockLevel
	 */
	@Accessor(qualifier = "stockLevel", type = Accessor.Type.GETTER)
	public StockLevelModel getStockLevel()
	{
		return getPersistenceContext().getPropertyValue(STOCKLEVEL);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>InventoryEvent.consignmentEntry</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the consignmentEntry
	 */
	@Accessor(qualifier = "consignmentEntry", type = Accessor.Type.SETTER)
	public void setConsignmentEntry(final ConsignmentEntryModel value)
	{
		getPersistenceContext().setPropertyValue(CONSIGNMENTENTRY, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>InventoryEvent.eventDate</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the eventDate
	 */
	@Accessor(qualifier = "eventDate", type = Accessor.Type.SETTER)
	public void setEventDate(final Date value)
	{
		getPersistenceContext().setPropertyValue(EVENTDATE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>InventoryEvent.orderEntry</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the orderEntry
	 */
	@Accessor(qualifier = "orderEntry", type = Accessor.Type.SETTER)
	public void setOrderEntry(final OrderEntryModel value)
	{
		getPersistenceContext().setPropertyValue(ORDERENTRY, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>InventoryEvent.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.SETTER)
	public void setQuantity(final long value)
	{
		getPersistenceContext().setPropertyValue(QUANTITY, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>InventoryEvent.stockLevel</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the stockLevel
	 */
	@Accessor(qualifier = "stockLevel", type = Accessor.Type.SETTER)
	public void setStockLevel(final StockLevelModel value)
	{
		getPersistenceContext().setPropertyValue(STOCKLEVEL, value);
	}
	
}
