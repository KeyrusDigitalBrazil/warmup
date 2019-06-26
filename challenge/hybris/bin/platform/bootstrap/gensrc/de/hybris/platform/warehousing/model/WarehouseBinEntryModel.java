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
import de.hybris.platform.warehousing.model.WarehouseBinModel;

/**
 * Generated model class for type WarehouseBinEntry first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class WarehouseBinEntryModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "WarehouseBinEntry";
	
	/**<i>Generated relation code constant for relation <code>WarehouseBin2WarehouseBinEntryRelation</code> defining source attribute <code>warehouseBin</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSEBIN2WAREHOUSEBINENTRYRELATION = "WarehouseBin2WarehouseBinEntryRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBinEntry.productCode</code> attribute defined at extension <code>warehousing</code>. */
	public static final String PRODUCTCODE = "productCode";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBinEntry.quantity</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITY = "quantity";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBinEntry.warehouseBin</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSEBIN = "warehouseBin";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public WarehouseBinEntryModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public WarehouseBinEntryModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _productCode initial attribute declared by type <code>WarehouseBinEntry</code> at extension <code>warehousing</code>
	 * @param _quantity initial attribute declared by type <code>WarehouseBinEntry</code> at extension <code>warehousing</code>
	 * @param _warehouseBin initial attribute declared by type <code>WarehouseBinEntry</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public WarehouseBinEntryModel(final String _productCode, final int _quantity, final WarehouseBinModel _warehouseBin)
	{
		super();
		setProductCode(_productCode);
		setQuantity(_quantity);
		setWarehouseBin(_warehouseBin);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _productCode initial attribute declared by type <code>WarehouseBinEntry</code> at extension <code>warehousing</code>
	 * @param _quantity initial attribute declared by type <code>WarehouseBinEntry</code> at extension <code>warehousing</code>
	 * @param _warehouseBin initial attribute declared by type <code>WarehouseBinEntry</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public WarehouseBinEntryModel(final ItemModel _owner, final String _productCode, final int _quantity, final WarehouseBinModel _warehouseBin)
	{
		super();
		setOwner(_owner);
		setProductCode(_productCode);
		setQuantity(_quantity);
		setWarehouseBin(_warehouseBin);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBinEntry.productCode</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the productCode
	 */
	@Accessor(qualifier = "productCode", type = Accessor.Type.GETTER)
	public String getProductCode()
	{
		return getPersistenceContext().getPropertyValue(PRODUCTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBinEntry.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.GETTER)
	public int getQuantity()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(QUANTITY));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBinEntry.warehouseBin</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the warehouseBin
	 */
	@Accessor(qualifier = "warehouseBin", type = Accessor.Type.GETTER)
	public WarehouseBinModel getWarehouseBin()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSEBIN);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBinEntry.productCode</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the productCode
	 */
	@Accessor(qualifier = "productCode", type = Accessor.Type.SETTER)
	public void setProductCode(final String value)
	{
		getPersistenceContext().setPropertyValue(PRODUCTCODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBinEntry.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantity
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.SETTER)
	public void setQuantity(final int value)
	{
		getPersistenceContext().setPropertyValue(QUANTITY, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBinEntry.warehouseBin</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouseBin
	 */
	@Accessor(qualifier = "warehouseBin", type = Accessor.Type.SETTER)
	public void setWarehouseBin(final WarehouseBinModel value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSEBIN, value);
	}
	
}
