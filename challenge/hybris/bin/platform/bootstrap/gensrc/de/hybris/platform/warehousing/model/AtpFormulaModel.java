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
import de.hybris.platform.store.BaseStoreModel;
import java.util.Set;

/**
 * Generated model class for type AtpFormula first defined at extension warehousing.
 * <p>
 * Represents the attributes of an ATP formula.
 */
@SuppressWarnings("all")
public class AtpFormulaModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "AtpFormula";
	
	/**<i>Generated relation code constant for relation <code>BaseStore2AtpFormulaRelation</code> defining source attribute <code>baseStores</code> in extension <code>warehousing</code>.</i>*/
	public static final String _BASESTORE2ATPFORMULARELATION = "BaseStore2AtpFormulaRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.code</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CODE = "code";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.availability</code> attribute defined at extension <code>warehousing</code>. */
	public static final String AVAILABILITY = "availability";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.allocation</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ALLOCATION = "allocation";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.cancellation</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CANCELLATION = "cancellation";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.increase</code> attribute defined at extension <code>warehousing</code>. */
	public static final String INCREASE = "increase";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.reserved</code> attribute defined at extension <code>warehousing</code>. */
	public static final String RESERVED = "reserved";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.shrinkage</code> attribute defined at extension <code>warehousing</code>. */
	public static final String SHRINKAGE = "shrinkage";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.wastage</code> attribute defined at extension <code>warehousing</code>. */
	public static final String WASTAGE = "wastage";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.returned</code> attribute defined at extension <code>warehousing</code>. */
	public static final String RETURNED = "returned";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.external</code> attribute defined at extension <code>warehousing</code>. */
	public static final String EXTERNAL = "external";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.formulaString</code> attribute defined at extension <code>warehousing</code>. */
	public static final String FORMULASTRING = "formulaString";
	
	/** <i>Generated constant</i> - Attribute key of <code>AtpFormula.baseStores</code> attribute defined at extension <code>warehousing</code>. */
	public static final String BASESTORES = "baseStores";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public AtpFormulaModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public AtpFormulaModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _code initial attribute declared by type <code>AtpFormula</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public AtpFormulaModel(final String _code)
	{
		super();
		setCode(_code);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _code initial attribute declared by type <code>AtpFormula</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated
	public AtpFormulaModel(final String _code, final ItemModel _owner)
	{
		super();
		setCode(_code);
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.allocation</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the allocation - A flag to indicate if allocation events are included in the ATP calculation
	 */
	@Accessor(qualifier = "allocation", type = Accessor.Type.GETTER)
	public Boolean getAllocation()
	{
		return getPersistenceContext().getPropertyValue(ALLOCATION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.availability</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the availability - A flag to indicate if availability events are included in the ATP calculation
	 */
	@Accessor(qualifier = "availability", type = Accessor.Type.GETTER)
	public Boolean getAvailability()
	{
		return getPersistenceContext().getPropertyValue(AVAILABILITY);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.baseStores</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the baseStores
	 */
	@Accessor(qualifier = "baseStores", type = Accessor.Type.GETTER)
	public Set<BaseStoreModel> getBaseStores()
	{
		return getPersistenceContext().getPropertyValue(BASESTORES);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.cancellation</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the cancellation - A flag to indicate if cancellation events are included in the ATP calculation
	 */
	@Accessor(qualifier = "cancellation", type = Accessor.Type.GETTER)
	public Boolean getCancellation()
	{
		return getPersistenceContext().getPropertyValue(CANCELLATION);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.code</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the code - A unique name of the formula.
	 */
	@Accessor(qualifier = "code", type = Accessor.Type.GETTER)
	public String getCode()
	{
		return getPersistenceContext().getPropertyValue(CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.external</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the external - A flag indicating that the external availability is included in the ATP calculation
	 */
	@Accessor(qualifier = "external", type = Accessor.Type.GETTER)
	public Boolean getExternal()
	{
		return getPersistenceContext().getPropertyValue(EXTERNAL);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.formulaString</code> dynamic attribute defined at extension <code>warehousing</code>. 
	 * @return the formulaString - String representation of the AtpFormula
	 */
	@Accessor(qualifier = "formulaString", type = Accessor.Type.GETTER)
	public String getFormulaString()
	{
		return getPersistenceContext().getDynamicValue(this,FORMULASTRING);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.increase</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the increase - A flag to indicate if increase events are included in the ATP calculation
	 */
	@Accessor(qualifier = "increase", type = Accessor.Type.GETTER)
	public Boolean getIncrease()
	{
		return getPersistenceContext().getPropertyValue(INCREASE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.reserved</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the reserved - A flag to indicate if reserved events are included in the ATP calculation
	 */
	@Accessor(qualifier = "reserved", type = Accessor.Type.GETTER)
	public Boolean getReserved()
	{
		return getPersistenceContext().getPropertyValue(RESERVED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.returned</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the returned - A flag to indicate if returned events are included in the ATP calculation
	 */
	@Accessor(qualifier = "returned", type = Accessor.Type.GETTER)
	public Boolean getReturned()
	{
		return getPersistenceContext().getPropertyValue(RETURNED);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.shrinkage</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the shrinkage - A flag to indicate if shrinkage events are included in the ATP calculation
	 */
	@Accessor(qualifier = "shrinkage", type = Accessor.Type.GETTER)
	public Boolean getShrinkage()
	{
		return getPersistenceContext().getPropertyValue(SHRINKAGE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>AtpFormula.wastage</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the wastage - A flag to indicate if wastage events are included in the ATP calculation
	 */
	@Accessor(qualifier = "wastage", type = Accessor.Type.GETTER)
	public Boolean getWastage()
	{
		return getPersistenceContext().getPropertyValue(WASTAGE);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.allocation</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the allocation - A flag to indicate if allocation events are included in the ATP calculation
	 */
	@Accessor(qualifier = "allocation", type = Accessor.Type.SETTER)
	public void setAllocation(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(ALLOCATION, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.availability</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the availability - A flag to indicate if availability events are included in the ATP calculation
	 */
	@Accessor(qualifier = "availability", type = Accessor.Type.SETTER)
	public void setAvailability(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(AVAILABILITY, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.baseStores</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the baseStores
	 */
	@Accessor(qualifier = "baseStores", type = Accessor.Type.SETTER)
	public void setBaseStores(final Set<BaseStoreModel> value)
	{
		getPersistenceContext().setPropertyValue(BASESTORES, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.cancellation</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the cancellation - A flag to indicate if cancellation events are included in the ATP calculation
	 */
	@Accessor(qualifier = "cancellation", type = Accessor.Type.SETTER)
	public void setCancellation(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(CANCELLATION, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.code</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the code - A unique name of the formula.
	 */
	@Accessor(qualifier = "code", type = Accessor.Type.SETTER)
	public void setCode(final String value)
	{
		getPersistenceContext().setPropertyValue(CODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.external</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the external - A flag indicating that the external availability is included in the ATP calculation
	 */
	@Accessor(qualifier = "external", type = Accessor.Type.SETTER)
	public void setExternal(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(EXTERNAL, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.increase</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the increase - A flag to indicate if increase events are included in the ATP calculation
	 */
	@Accessor(qualifier = "increase", type = Accessor.Type.SETTER)
	public void setIncrease(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(INCREASE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.reserved</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the reserved - A flag to indicate if reserved events are included in the ATP calculation
	 */
	@Accessor(qualifier = "reserved", type = Accessor.Type.SETTER)
	public void setReserved(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(RESERVED, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.returned</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the returned - A flag to indicate if returned events are included in the ATP calculation
	 */
	@Accessor(qualifier = "returned", type = Accessor.Type.SETTER)
	public void setReturned(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(RETURNED, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.shrinkage</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the shrinkage - A flag to indicate if shrinkage events are included in the ATP calculation
	 */
	@Accessor(qualifier = "shrinkage", type = Accessor.Type.SETTER)
	public void setShrinkage(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(SHRINKAGE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>AtpFormula.wastage</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the wastage - A flag to indicate if wastage events are included in the ATP calculation
	 */
	@Accessor(qualifier = "wastage", type = Accessor.Type.SETTER)
	public void setWastage(final Boolean value)
	{
		getPersistenceContext().setPropertyValue(WASTAGE, value);
	}
	
}
