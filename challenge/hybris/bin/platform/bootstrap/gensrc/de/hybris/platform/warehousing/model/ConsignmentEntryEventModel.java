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
import de.hybris.platform.servicelayer.model.ItemModelContext;

/**
 * Generated model class for type ConsignmentEntryEvent first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class ConsignmentEntryEventModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "ConsignmentEntryEvent";
	
	/** <i>Generated constant</i> - Attribute key of <code>ConsignmentEntryEvent.quantity</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITY = "quantity";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public ConsignmentEntryEventModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public ConsignmentEntryEventModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _quantity initial attribute declared by type <code>ConsignmentEntryEvent</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public ConsignmentEntryEventModel(final Long _quantity)
	{
		super();
		setQuantity(_quantity);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _quantity initial attribute declared by type <code>ConsignmentEntryEvent</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public ConsignmentEntryEventModel(final ItemModel _owner, final Long _quantity)
	{
		super();
		setOwner(_owner);
		setQuantity(_quantity);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>ConsignmentEntryEvent.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.GETTER)
	public Long getQuantity()
	{
		return getPersistenceContext().getPropertyValue(QUANTITY);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>ConsignmentEntryEvent.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.SETTER)
	public void setQuantity(final Long value)
	{
		getPersistenceContext().setPropertyValue(QUANTITY, value);
	}
	
}
