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
 * Generated model class for type RestockConfig first defined at extension warehousing.
 * <p>
 * Represents the attributes of restock configurations.
 */
@SuppressWarnings("all")
public class RestockConfigModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "RestockConfig";
	
	/** <i>Generated constant</i> - Attribute key of <code>RestockConfig.isUpdateStockAfterReturn</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ISUPDATESTOCKAFTERRETURN = "isUpdateStockAfterReturn";
	
	/** <i>Generated constant</i> - Attribute key of <code>RestockConfig.returnedBinCode</code> attribute defined at extension <code>warehousing</code>. */
	public static final String RETURNEDBINCODE = "returnedBinCode";
	
	/** <i>Generated constant</i> - Attribute key of <code>RestockConfig.delayDaysBeforeRestock</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DELAYDAYSBEFORERESTOCK = "delayDaysBeforeRestock";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public RestockConfigModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public RestockConfigModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated
	public RestockConfigModel(final ItemModel _owner)
	{
		super();
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RestockConfig.delayDaysBeforeRestock</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the delayDaysBeforeRestock
	 */
	@Accessor(qualifier = "delayDaysBeforeRestock", type = Accessor.Type.GETTER)
	public int getDelayDaysBeforeRestock()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(DELAYDAYSBEFORERESTOCK));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RestockConfig.isUpdateStockAfterReturn</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the isUpdateStockAfterReturn
	 */
	@Accessor(qualifier = "isUpdateStockAfterReturn", type = Accessor.Type.GETTER)
	public Boolean getIsUpdateStockAfterReturn()
	{
		return getPersistenceContext().getPropertyValue(ISUPDATESTOCKAFTERRETURN);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>RestockConfig.returnedBinCode</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the returnedBinCode
	 */
	@Accessor(qualifier = "returnedBinCode", type = Accessor.Type.GETTER)
	public String getReturnedBinCode()
	{
		return getPersistenceContext().getPropertyValue(RETURNEDBINCODE);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>RestockConfig.delayDaysBeforeRestock</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the delayDaysBeforeRestock
	 */
	@Accessor(qualifier = "delayDaysBeforeRestock", type = Accessor.Type.SETTER)
	public void setDelayDaysBeforeRestock(final int value)
	{
		getPersistenceContext().setPropertyValue(DELAYDAYSBEFORERESTOCK, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>RestockConfig.isUpdateStockAfterReturn</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the isUpdateStockAfterReturn
	 */
	@Accessor(qualifier = "isUpdateStockAfterReturn", type = Accessor.Type.SETTER)
	public void setIsUpdateStockAfterReturn(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(ISUPDATESTOCKAFTERRETURN, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>RestockConfig.returnedBinCode</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the returnedBinCode
	 */
	@Accessor(qualifier = "returnedBinCode", type = Accessor.Type.SETTER)
	public void setReturnedBinCode(final String value)
	{
		getPersistenceContext().setPropertyValue(RETURNEDBINCODE, value);
	}
	
}
