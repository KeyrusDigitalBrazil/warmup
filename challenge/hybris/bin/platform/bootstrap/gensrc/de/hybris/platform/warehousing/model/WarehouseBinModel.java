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
import de.hybris.platform.warehousing.model.WarehouseBinEntryModel;
import de.hybris.platform.warehousing.model.WarehouseTransferEntryModel;
import java.util.Set;

/**
 * Generated model class for type WarehouseBin first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class WarehouseBinModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "WarehouseBin";
	
	/**<i>Generated relation code constant for relation <code>Warehouse2WarehouseBinRelation</code> defining source attribute <code>warehouse</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSE2WAREHOUSEBINRELATION = "Warehouse2WarehouseBinRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.code</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CODE = "code";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.volume</code> attribute defined at extension <code>warehousing</code>. */
	public static final String VOLUME = "volume";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.maxEntries</code> attribute defined at extension <code>warehousing</code>. */
	public static final String MAXENTRIES = "maxEntries";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.row</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ROW = "row";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.position</code> attribute defined at extension <code>warehousing</code>. */
	public static final String POSITION = "position";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.warehouse</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSE = "warehouse";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.warehouseBinEntries</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSEBINENTRIES = "warehouseBinEntries";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.sourceWarehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. */
	public static final String SOURCEWAREHOUSETRANSFERENTRIES = "sourceWarehouseTransferEntries";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseBin.destinationWarehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DESTINATIONWAREHOUSETRANSFERENTRIES = "destinationWarehouseTransferEntries";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public WarehouseBinModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public WarehouseBinModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _code initial attribute declared by type <code>WarehouseBin</code> at extension <code>warehousing</code>
	 * @param _warehouse initial attribute declared by type <code>WarehouseBin</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public WarehouseBinModel(final String _code, final WarehouseModel _warehouse)
	{
		super();
		setCode(_code);
		setWarehouse(_warehouse);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _code initial attribute declared by type <code>WarehouseBin</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _warehouse initial attribute declared by type <code>WarehouseBin</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public WarehouseBinModel(final String _code, final ItemModel _owner, final WarehouseModel _warehouse)
	{
		super();
		setCode(_code);
		setOwner(_owner);
		setWarehouse(_warehouse);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.code</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the code
	 */
	@Accessor(qualifier = "code", type = Accessor.Type.GETTER)
	public String getCode()
	{
		return getPersistenceContext().getPropertyValue(CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.destinationWarehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the destinationWarehouseTransferEntries
	 */
	@Accessor(qualifier = "destinationWarehouseTransferEntries", type = Accessor.Type.GETTER)
	public Set<WarehouseTransferEntryModel> getDestinationWarehouseTransferEntries()
	{
		return getPersistenceContext().getPropertyValue(DESTINATIONWAREHOUSETRANSFERENTRIES);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.maxEntries</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the maxEntries
	 */
	@Accessor(qualifier = "maxEntries", type = Accessor.Type.GETTER)
	public int getMaxEntries()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(MAXENTRIES));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.position</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the position
	 */
	@Accessor(qualifier = "position", type = Accessor.Type.GETTER)
	public String getPosition()
	{
		return getPersistenceContext().getPropertyValue(POSITION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.row</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the row
	 */
	@Accessor(qualifier = "row", type = Accessor.Type.GETTER)
	public String getRow()
	{
		return getPersistenceContext().getPropertyValue(ROW);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.sourceWarehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the sourceWarehouseTransferEntries
	 */
	@Accessor(qualifier = "sourceWarehouseTransferEntries", type = Accessor.Type.GETTER)
	public Set<WarehouseTransferEntryModel> getSourceWarehouseTransferEntries()
	{
		return getPersistenceContext().getPropertyValue(SOURCEWAREHOUSETRANSFERENTRIES);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.volume</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the volume
	 */
	@Accessor(qualifier = "volume", type = Accessor.Type.GETTER)
	public double getVolume()
	{
		return toPrimitive((Double)getPersistenceContext().getPropertyValue(VOLUME));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.warehouse</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the warehouse
	 */
	@Accessor(qualifier = "warehouse", type = Accessor.Type.GETTER)
	public WarehouseModel getWarehouse()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseBin.warehouseBinEntries</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the warehouseBinEntries
	 */
	@Accessor(qualifier = "warehouseBinEntries", type = Accessor.Type.GETTER)
	public Set<WarehouseBinEntryModel> getWarehouseBinEntries()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSEBINENTRIES);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.code</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the code
	 */
	@Accessor(qualifier = "code", type = Accessor.Type.SETTER)
	public void setCode(final String value)
	{
		getPersistenceContext().setPropertyValue(CODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.destinationWarehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the destinationWarehouseTransferEntries
	 */
	@Accessor(qualifier = "destinationWarehouseTransferEntries", type = Accessor.Type.SETTER)
	public void setDestinationWarehouseTransferEntries(final Set<WarehouseTransferEntryModel> value)
	{
		getPersistenceContext().setPropertyValue(DESTINATIONWAREHOUSETRANSFERENTRIES, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.maxEntries</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the maxEntries
	 */
	@Accessor(qualifier = "maxEntries", type = Accessor.Type.SETTER)
	public void setMaxEntries(final int value)
	{
		getPersistenceContext().setPropertyValue(MAXENTRIES, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.position</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the position
	 */
	@Accessor(qualifier = "position", type = Accessor.Type.SETTER)
	public void setPosition(final String value)
	{
		getPersistenceContext().setPropertyValue(POSITION, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.row</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the row
	 */
	@Accessor(qualifier = "row", type = Accessor.Type.SETTER)
	public void setRow(final String value)
	{
		getPersistenceContext().setPropertyValue(ROW, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.sourceWarehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the sourceWarehouseTransferEntries
	 */
	@Accessor(qualifier = "sourceWarehouseTransferEntries", type = Accessor.Type.SETTER)
	public void setSourceWarehouseTransferEntries(final Set<WarehouseTransferEntryModel> value)
	{
		getPersistenceContext().setPropertyValue(SOURCEWAREHOUSETRANSFERENTRIES, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.volume</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the volume
	 */
	@Accessor(qualifier = "volume", type = Accessor.Type.SETTER)
	public void setVolume(final double value)
	{
		getPersistenceContext().setPropertyValue(VOLUME, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.warehouse</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouse
	 */
	@Accessor(qualifier = "warehouse", type = Accessor.Type.SETTER)
	public void setWarehouse(final WarehouseModel value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseBin.warehouseBinEntries</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouseBinEntries
	 */
	@Accessor(qualifier = "warehouseBinEntries", type = Accessor.Type.SETTER)
	public void setWarehouseBinEntries(final Set<WarehouseBinEntryModel> value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSEBINENTRIES, value);
	}
	
}
