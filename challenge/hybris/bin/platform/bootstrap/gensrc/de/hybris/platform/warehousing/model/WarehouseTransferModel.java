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
import de.hybris.platform.warehousing.model.WarehouseTransferEntryModel;
import java.util.Collection;
import java.util.Date;

/**
 * Generated model class for type WarehouseTransfer first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class WarehouseTransferModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "WarehouseTransfer";
	
	/**<i>Generated relation code constant for relation <code>WarehouseTransfer2WarehouseTransferRelation</code> defining source attribute <code>parentTransfer</code> in extension <code>warehousing</code>.</i>*/
	public static final String _WAREHOUSETRANSFER2WAREHOUSETRANSFERRELATION = "WarehouseTransfer2WarehouseTransferRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransfer.completionDate</code> attribute defined at extension <code>warehousing</code>. */
	public static final String COMPLETIONDATE = "completionDate";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransfer.warehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WAREHOUSETRANSFERENTRIES = "warehouseTransferEntries";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransfer.parentTransfer</code> attribute defined at extension <code>warehousing</code>. */
	public static final String PARENTTRANSFER = "parentTransfer";
	
	/** <i>Generated constant</i> - Attribute key of <code>WarehouseTransfer.dependentTransfers</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DEPENDENTTRANSFERS = "dependentTransfers";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public WarehouseTransferModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public WarehouseTransferModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated
	public WarehouseTransferModel(final ItemModel _owner)
	{
		super();
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransfer.completionDate</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the completionDate
	 */
	@Accessor(qualifier = "completionDate", type = Accessor.Type.GETTER)
	public Date getCompletionDate()
	{
		return getPersistenceContext().getPropertyValue(COMPLETIONDATE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransfer.dependentTransfers</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the dependentTransfers
	 */
	@Accessor(qualifier = "dependentTransfers", type = Accessor.Type.GETTER)
	public Collection<WarehouseTransferModel> getDependentTransfers()
	{
		return getPersistenceContext().getPropertyValue(DEPENDENTTRANSFERS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransfer.parentTransfer</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the parentTransfer
	 */
	@Accessor(qualifier = "parentTransfer", type = Accessor.Type.GETTER)
	public WarehouseTransferModel getParentTransfer()
	{
		return getPersistenceContext().getPropertyValue(PARENTTRANSFER);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>WarehouseTransfer.warehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the warehouseTransferEntries
	 */
	@Accessor(qualifier = "warehouseTransferEntries", type = Accessor.Type.GETTER)
	public Collection<WarehouseTransferEntryModel> getWarehouseTransferEntries()
	{
		return getPersistenceContext().getPropertyValue(WAREHOUSETRANSFERENTRIES);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransfer.completionDate</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the completionDate
	 */
	@Accessor(qualifier = "completionDate", type = Accessor.Type.SETTER)
	public void setCompletionDate(final Date value)
	{
		getPersistenceContext().setPropertyValue(COMPLETIONDATE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransfer.dependentTransfers</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the dependentTransfers
	 */
	@Accessor(qualifier = "dependentTransfers", type = Accessor.Type.SETTER)
	public void setDependentTransfers(final Collection<WarehouseTransferModel> value)
	{
		getPersistenceContext().setPropertyValue(DEPENDENTTRANSFERS, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransfer.parentTransfer</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the parentTransfer
	 */
	@Accessor(qualifier = "parentTransfer", type = Accessor.Type.SETTER)
	public void setParentTransfer(final WarehouseTransferModel value)
	{
		getPersistenceContext().setPropertyValue(PARENTTRANSFER, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>WarehouseTransfer.warehouseTransferEntries</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the warehouseTransferEntries
	 */
	@Accessor(qualifier = "warehouseTransferEntries", type = Accessor.Type.SETTER)
	public void setWarehouseTransferEntries(final Collection<WarehouseTransferEntryModel> value)
	{
		getPersistenceContext().setPropertyValue(WAREHOUSETRANSFERENTRIES, value);
	}
	
}
