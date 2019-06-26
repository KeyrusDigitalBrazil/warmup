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
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

/**
 * Generated model class for type SourcingBan first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class SourcingBanModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "SourcingBan";
	
	/**<i>Generated relation code constant for relation <code>Warehouse2SourcingBanRelation</code> defining source attribute <code>warehouse</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSE2SOURCINGBANRELATION = "Warehouse2SourcingBanRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingBan.warehouse</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSE = "warehouse";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public SourcingBanModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public SourcingBanModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated
	public SourcingBanModel(final ItemModel _owner)
	{
		super();
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingBan.warehouse</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the warehouse
	 */
	@Accessor(qualifier = "warehouse", type = Accessor.Type.GETTER)
	public WarehouseModel getWarehouse()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSE);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingBan.warehouse</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouse
	 */
	@Accessor(qualifier = "warehouse", type = Accessor.Type.SETTER)
	public void setWarehouse(final WarehouseModel value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSE, value);
	}
	
}
