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
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.model.ConsignmentEntryEventModel;

/**
 * Generated model class for type DeclineConsignmentEntryEvent first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class DeclineConsignmentEntryEventModel extends ConsignmentEntryEventModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "DeclineConsignmentEntryEvent";
	
	/** <i>Generated constant</i> - Attribute key of <code>DeclineConsignmentEntryEvent.reason</code> attribute defined at extension <code>warehousing</code>. */
	public static final String REASON = "reason";
	
	/** <i>Generated constant</i> - Attribute key of <code>DeclineConsignmentEntryEvent.reallocatedWarehouse</code> attribute defined at extension <code>warehousing</code>. */
	public static final String REALLOCATEDWAREHOUSE = "reallocatedWarehouse";
	
	/** <i>Generated constant</i> - Attribute key of <code>DeclineConsignmentEntryEvent.consignmentEntry</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CONSIGNMENTENTRY = "consignmentEntry";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public DeclineConsignmentEntryEventModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public DeclineConsignmentEntryEventModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _quantity initial attribute declared by type <code>ConsignmentEntryEvent</code> at extension <code>warehousing</code>
	 * @param _reason initial attribute declared by type <code>DeclineConsignmentEntryEvent</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public DeclineConsignmentEntryEventModel(final Long _quantity, final DeclineReason _reason)
	{
		super();
		setQuantity(_quantity);
		setReason(_reason);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _quantity initial attribute declared by type <code>ConsignmentEntryEvent</code> at extension <code>warehousing</code>
	 * @param _reason initial attribute declared by type <code>DeclineConsignmentEntryEvent</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public DeclineConsignmentEntryEventModel(final ItemModel _owner, final Long _quantity, final DeclineReason _reason)
	{
		super();
		setOwner(_owner);
		setQuantity(_quantity);
		setReason(_reason);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>DeclineConsignmentEntryEvent.consignmentEntry</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the consignmentEntry
	 */
	@Accessor(qualifier = "consignmentEntry", type = Accessor.Type.GETTER)
	public ConsignmentEntryModel getConsignmentEntry()
	{
		return getPersistenceContext().getPropertyValue(CONSIGNMENTENTRY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>DeclineConsignmentEntryEvent.reallocatedWarehouse</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the reallocatedWarehouse - Specifies the warehouse to where the consignment entry items are to be reallocated to.
	 */
	@Accessor(qualifier = "reallocatedWarehouse", type = Accessor.Type.GETTER)
	public WarehouseModel getReallocatedWarehouse()
	{
		return getPersistenceContext().getPropertyValue(REALLOCATEDWAREHOUSE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>DeclineConsignmentEntryEvent.reason</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the reason - Specifies the reason for this decline.
	 */
	@Accessor(qualifier = "reason", type = Accessor.Type.GETTER)
	public DeclineReason getReason()
	{
		return getPersistenceContext().getPropertyValue(REASON);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>DeclineConsignmentEntryEvent.consignmentEntry</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the consignmentEntry
	 */
	@Accessor(qualifier = "consignmentEntry", type = Accessor.Type.SETTER)
	public void setConsignmentEntry(final ConsignmentEntryModel value)
	{
		getPersistenceContext().setPropertyValue(CONSIGNMENTENTRY, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>DeclineConsignmentEntryEvent.reallocatedWarehouse</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the reallocatedWarehouse - Specifies the warehouse to where the consignment entry items are to be reallocated to.
	 */
	@Accessor(qualifier = "reallocatedWarehouse", type = Accessor.Type.SETTER)
	public void setReallocatedWarehouse(final WarehouseModel value)
	{
		getPersistenceContext().setPropertyValue(REALLOCATEDWAREHOUSE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>DeclineConsignmentEntryEvent.reason</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the reason - Specifies the reason for this decline.
	 */
	@Accessor(qualifier = "reason", type = Accessor.Type.SETTER)
	public void setReason(final DeclineReason value)
	{
		getPersistenceContext().setPropertyValue(REASON, value);
	}
	
}
