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
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

/**
 * Generated model class for type PackagingInfo first defined at extension warehousing.
 * <p>
 * Represents the attributes of packages in a consignment.
 */
@SuppressWarnings("all")
public class PackagingInfoModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "PackagingInfo";
	
	/**<i>Generated relation code constant for relation <code>Consignment2PackagingInfoRelation</code> defining source attribute <code>consignment</code> in extension <code>warehousing</code>.</i>*/
	public static final String _CONSIGNMENT2PACKAGINGINFORELATION = "Consignment2PackagingInfoRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.width</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WIDTH = "width";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.height</code> attribute defined at extension <code>warehousing</code>. */
	public static final String HEIGHT = "height";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.length</code> attribute defined at extension <code>warehousing</code>. */
	public static final String LENGTH = "length";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.grossWeight</code> attribute defined at extension <code>warehousing</code>. */
	public static final String GROSSWEIGHT = "grossWeight";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.dimensionUnit</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DIMENSIONUNIT = "dimensionUnit";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.weightUnit</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WEIGHTUNIT = "weightUnit";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.insuredValue</code> attribute defined at extension <code>warehousing</code>. */
	public static final String INSUREDVALUE = "insuredValue";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.consignmentPOS</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CONSIGNMENTPOS = "consignmentPOS";
	
	/** <i>Generated constant</i> - Attribute key of <code>PackagingInfo.consignment</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CONSIGNMENT = "consignment";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public PackagingInfoModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public PackagingInfoModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _consignment initial attribute declared by type <code>PackagingInfo</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public PackagingInfoModel(final ConsignmentModel _consignment)
	{
		super();
		setConsignment(_consignment);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _consignment initial attribute declared by type <code>PackagingInfo</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated
	public PackagingInfoModel(final ConsignmentModel _consignment, final ItemModel _owner)
	{
		super();
		setConsignment(_consignment);
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.consignment</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the consignment
	 */
	@Accessor(qualifier = "consignment", type = Accessor.Type.GETTER)
	public ConsignmentModel getConsignment()
	{
		return getPersistenceContext().getPropertyValue(CONSIGNMENT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.dimensionUnit</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the dimensionUnit
	 */
	@Accessor(qualifier = "dimensionUnit", type = Accessor.Type.GETTER)
	public String getDimensionUnit()
	{
		return getPersistenceContext().getPropertyValue(DIMENSIONUNIT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.grossWeight</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the grossWeight
	 */
	@Accessor(qualifier = "grossWeight", type = Accessor.Type.GETTER)
	public String getGrossWeight()
	{
		return getPersistenceContext().getPropertyValue(GROSSWEIGHT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.height</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the height
	 */
	@Accessor(qualifier = "height", type = Accessor.Type.GETTER)
	public String getHeight()
	{
		return getPersistenceContext().getPropertyValue(HEIGHT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.insuredValue</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the insuredValue
	 */
	@Accessor(qualifier = "insuredValue", type = Accessor.Type.GETTER)
	public String getInsuredValue()
	{
		return getPersistenceContext().getPropertyValue(INSUREDVALUE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.length</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the length
	 */
	@Accessor(qualifier = "length", type = Accessor.Type.GETTER)
	public String getLength()
	{
		return getPersistenceContext().getPropertyValue(LENGTH);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.weightUnit</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the weightUnit
	 */
	@Accessor(qualifier = "weightUnit", type = Accessor.Type.GETTER)
	public String getWeightUnit()
	{
		return getPersistenceContext().getPropertyValue(WEIGHTUNIT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>PackagingInfo.width</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the width
	 */
	@Accessor(qualifier = "width", type = Accessor.Type.GETTER)
	public String getWidth()
	{
		return getPersistenceContext().getPropertyValue(WIDTH);
	}
	
	/**
	 * <i>Generated method</i> - Initial setter of <code>PackagingInfo.consignment</code> attribute defined at extension <code>warehousing</code>. Can only be used at creation of model - before first save.  
	 *  
	 * @param value the consignment
	 */
	@Accessor(qualifier = "consignment", type = Accessor.Type.SETTER)
	public void setConsignment(final ConsignmentModel value)
	{
		getPersistenceContext().setPropertyValue(CONSIGNMENT, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.dimensionUnit</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the dimensionUnit
	 */
	@Accessor(qualifier = "dimensionUnit", type = Accessor.Type.SETTER)
	public void setDimensionUnit(final String value)
	{
		getPersistenceContext().setPropertyValue(DIMENSIONUNIT, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.grossWeight</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the grossWeight
	 */
	@Accessor(qualifier = "grossWeight", type = Accessor.Type.SETTER)
	public void setGrossWeight(final String value)
	{
		getPersistenceContext().setPropertyValue(GROSSWEIGHT, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.height</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the height
	 */
	@Accessor(qualifier = "height", type = Accessor.Type.SETTER)
	public void setHeight(final String value)
	{
		getPersistenceContext().setPropertyValue(HEIGHT, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.insuredValue</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the insuredValue
	 */
	@Accessor(qualifier = "insuredValue", type = Accessor.Type.SETTER)
	public void setInsuredValue(final String value)
	{
		getPersistenceContext().setPropertyValue(INSUREDVALUE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.length</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the length
	 */
	@Accessor(qualifier = "length", type = Accessor.Type.SETTER)
	public void setLength(final String value)
	{
		getPersistenceContext().setPropertyValue(LENGTH, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.weightUnit</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the weightUnit
	 */
	@Accessor(qualifier = "weightUnit", type = Accessor.Type.SETTER)
	public void setWeightUnit(final String value)
	{
		getPersistenceContext().setPropertyValue(WEIGHTUNIT, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>PackagingInfo.width</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the width
	 */
	@Accessor(qualifier = "width", type = Accessor.Type.SETTER)
	public void setWidth(final String value)
	{
		getPersistenceContext().setPropertyValue(WIDTH, value);
	}
	
}
