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
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import java.util.Set;

/**
 * Generated model class for type AdvancedShippingNoticeEntry first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class AdvancedShippingNoticeEntryModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "AdvancedShippingNoticeEntry";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNoticeEntry.productCode</code> attribute defined at extension <code>warehousing</code>. */
	public static final String PRODUCTCODE = "productCode";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNoticeEntry.quantity</code> attribute defined at extension <code>warehousing</code>. */
	public static final String QUANTITY = "quantity";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNoticeEntry.stockLevels</code> attribute defined at extension <code>warehousing</code>. */
	public static final String STOCKLEVELS = "stockLevels";
	
	/** <i>Generated constant</i> - Attribute key of <code>AdvancedShippingNoticeEntry.asn</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ASN = "asn";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public AdvancedShippingNoticeEntryModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public AdvancedShippingNoticeEntryModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _asn initial attribute declared by type <code>AdvancedShippingNoticeEntry</code> at extension <code>warehousing</code>
	 * @param _productCode initial attribute declared by type <code>AdvancedShippingNoticeEntry</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public AdvancedShippingNoticeEntryModel(final AdvancedShippingNoticeModel _asn, final String _productCode)
	{
		super();
		setAsn(_asn);
		setProductCode(_productCode);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _asn initial attribute declared by type <code>AdvancedShippingNoticeEntry</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 * @param _productCode initial attribute declared by type <code>AdvancedShippingNoticeEntry</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public AdvancedShippingNoticeEntryModel(final AdvancedShippingNoticeModel _asn, final ItemModel _owner, final String _productCode)
	{
		super();
		setAsn(_asn);
		setOwner(_owner);
		setProductCode(_productCode);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNoticeEntry.asn</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the asn
	 */
	@Accessor(qualifier = "asn", type = Accessor.Type.GETTER)
	public AdvancedShippingNoticeModel getAsn()
	{
		return getPersistenceContext().getPropertyValue(ASN);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNoticeEntry.productCode</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the productCode - Determines the product code of given ASNEntry
	 */
	@Accessor(qualifier = "productCode", type = Accessor.Type.GETTER)
	public String getProductCode()
	{
		return getPersistenceContext().getPropertyValue(PRODUCTCODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNoticeEntry.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the quantity - Quantity of product
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.GETTER)
	public int getQuantity()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(QUANTITY));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AdvancedShippingNoticeEntry.stockLevels</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the stockLevels
	 */
	@Accessor(qualifier = "stockLevels", type = Accessor.Type.GETTER)
	public Set<StockLevelModel> getStockLevels()
	{
		return getPersistenceContext().getPropertyValue(STOCKLEVELS);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNoticeEntry.asn</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the asn
	 */
	@Accessor(qualifier = "asn", type = Accessor.Type.SETTER)
	public void setAsn(final AdvancedShippingNoticeModel value)
	{
		getPersistenceContext().setPropertyValue(ASN, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNoticeEntry.productCode</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the productCode - Determines the product code of given ASNEntry
	 */
	@Accessor(qualifier = "productCode", type = Accessor.Type.SETTER)
	public void setProductCode(final String value)
	{
		getPersistenceContext().setPropertyValue(PRODUCTCODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNoticeEntry.quantity</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the quantity - Quantity of product
	 */
	@Accessor(qualifier = "quantity", type = Accessor.Type.SETTER)
	public void setQuantity(final int value)
	{
		getPersistenceContext().setPropertyValue(QUANTITY, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AdvancedShippingNoticeEntry.stockLevels</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the stockLevels
	 */
	@Accessor(qualifier = "stockLevels", type = Accessor.Type.SETTER)
	public void setStockLevels(final Set<StockLevelModel> value)
	{
		getPersistenceContext().setPropertyValue(STOCKLEVELS, value);
	}
	
}
