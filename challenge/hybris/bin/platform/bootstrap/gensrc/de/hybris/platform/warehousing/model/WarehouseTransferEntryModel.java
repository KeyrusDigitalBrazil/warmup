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
import de.hybris.platform.warehousing.model.WarehouseTransferModel;

/**
 * Generated model class for type WarehouseTransferEntry first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class WarehouseTransferEntryModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "WarehouseTransferEntry";
	
	/**<i>Generated relation code constant for relation <code>WarehouseBin2WarehouseTransferEntrySrcRelation</code> defining source attribute <code>source</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSEBIN2WAREHOUSETRANSFERENTRYSRCRELATION = "WarehouseBin2WarehouseTransferEntrySrcRelation";
	
	/**<i>Generated relation code constant for relation <code>WarehouseBin2WarehouseTransferEntryDestRelation</code> defining source attribute <code>destination</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSEBIN2WAREHOUSETRANSFERENTRYDESTRELATION = "WarehouseBin2WarehouseTransferEntryDestRelation";
	
	/**<i>Generated relation code constant for relation <code>WarehouseTransfer2WarehouseTransferEntryRelation</code> defining source attribute <code>warehouseTransfer</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSETRANSFER2WAREHOUSETRANSFERENTRYRELATION = "WarehouseTransfer2WarehouseTransferEntryRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.productCode</code> attribute defined at extension <code>warehousing</code>. */
	public static final String PRODUCTCODE = "productCode";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.quantityRequested</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYREQUESTED = "quantityRequested";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.quantityAccepted</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYACCEPTED = "quantityAccepted";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.quantityDeclined</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITYDECLINED = "quantityDeclined";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.source</code> attribute defined at extension <code>warehousing</code>. */
	public static final String SOURCE = "source";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.destination</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DESTINATION = "destination";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransferEntry.warehouseTransfer</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSETRANSFER = "warehouseTransfer";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public WarehouseTransferEntryModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public WarehouseTransferEntryModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _destination initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _productCode initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _quantityAccepted initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _quantityDeclined initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _quantityRequested initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _source initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _warehouseTransfer initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public WarehouseTransferEntryModel(final WarehouseBinModel _destination, final String _productCode, final int _quantityAccepted, final int _quantityDeclined, final int _quantityRequested, final WarehouseBinModel _source, final WarehouseTransferModel _warehouseTransfer)
	{
		super();
		setDestination(_destination);
		setProductCode(_productCode);
		setQuantityAccepted(_quantityAccepted);
		setQuantityDeclined(_quantityDeclined);
		setQuantityRequested(_quantityRequested);
		setSource(_source);
		setWarehouseTransfer(_warehouseTransfer);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _destination initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _productCode initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _quantityAccepted initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _quantityDeclined initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _quantityRequested initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _source initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 * @param _warehouseTransfer initial attribute declared by type <code>WarehouseTransferEntry</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public WarehouseTransferEntryModel(final WarehouseBinModel _destination, final ItemModel _owner, final String _productCode, final int _quantityAccepted, final int _quantityDeclined, final int _quantityRequested, final WarehouseBinModel _source, final WarehouseTransferModel _warehouseTransfer)
	{
		super();
		setDestination(_destination);
		setOwner(_owner);
		setProductCode(_productCode);
		setQuantityAccepted(_quantityAccepted);
		setQuantityDeclined(_quantityDeclined);
		setQuantityRequested(_quantityRequested);
		setSource(_source);
		setWarehouseTransfer(_warehouseTransfer);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.destination</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the destination
	 */
	@Accessor(qualifier = "destination", type = Accessor.Type.GETTER)
	public WarehouseBinModel getDestination()
	{
		return getPersistenceContext().getPropertyValue(DESTINATION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.productCode</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the productCode
	 */
	@Accessor(qualifier = "productCode", type = Accessor.Type.GETTER)
	public String getProductCode()
	{
		return getPersistenceContext().getPropertyValue(PRODUCTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.quantityAccepted</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityAccepted
	 */
	@Accessor(qualifier = "quantityAccepted", type = Accessor.Type.GETTER)
	public int getQuantityAccepted()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(QUANTITYACCEPTED));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.quantityDeclined</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityDeclined
	 */
	@Accessor(qualifier = "quantityDeclined", type = Accessor.Type.GETTER)
	public int getQuantityDeclined()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(QUANTITYDECLINED));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.quantityRequested</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantityRequested
	 */
	@Accessor(qualifier = "quantityRequested", type = Accessor.Type.GETTER)
	public int getQuantityRequested()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(QUANTITYREQUESTED));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.source</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the source
	 */
	@Accessor(qualifier = "source", type = Accessor.Type.GETTER)
	public WarehouseBinModel getSource()
	{
		return getPersistenceContext().getPropertyValue(SOURCE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransferEntry.warehouseTransfer</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the warehouseTransfer
	 */
	@Accessor(qualifier = "warehouseTransfer", type = Accessor.Type.GETTER)
	public WarehouseTransferModel getWarehouseTransfer()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSETRANSFER);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.destination</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the destination
	 */
	@Accessor(qualifier = "destination", type = Accessor.Type.SETTER)
	public void setDestination(final WarehouseBinModel value)
	{
		getPersistenceContext().setPropertyValue(DESTINATION, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.productCode</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the productCode
	 */
	@Accessor(qualifier = "productCode", type = Accessor.Type.SETTER)
	public void setProductCode(final String value)
	{
		getPersistenceContext().setPropertyValue(PRODUCTCODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.quantityAccepted</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantityAccepted
	 */
	@Accessor(qualifier = "quantityAccepted", type = Accessor.Type.SETTER)
	public void setQuantityAccepted(final int value)
	{
		getPersistenceContext().setPropertyValue(QUANTITYACCEPTED, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.quantityDeclined</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantityDeclined
	 */
	@Accessor(qualifier = "quantityDeclined", type = Accessor.Type.SETTER)
	public void setQuantityDeclined(final int value)
	{
		getPersistenceContext().setPropertyValue(QUANTITYDECLINED, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.quantityRequested</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantityRequested
	 */
	@Accessor(qualifier = "quantityRequested", type = Accessor.Type.SETTER)
	public void setQuantityRequested(final int value)
	{
		getPersistenceContext().setPropertyValue(QUANTITYREQUESTED, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.source</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the source
	 */
	@Accessor(qualifier = "source", type = Accessor.Type.SETTER)
	public void setSource(final WarehouseBinModel value)
	{
		getPersistenceContext().setPropertyValue(SOURCE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransferEntry.warehouseTransfer</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouseTransfer
	 */
	@Accessor(qualifier = "warehouseTransfer", type = Accessor.Type.SETTER)
	public void setWarehouseTransfer(final WarehouseTransferModel value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSETRANSFER, value);
	}
	
}
