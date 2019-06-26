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
 * Generated model class for type SourcingConfig first defined at extension warehousing.
 */
@SuppressWarnings("all")
public class SourcingConfigModel extends ItemModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "SourcingConfig";
	
	/**<i>Generated relation code constant for relation <code>BaseStore2SourcingConfigRelation</code> defining source attribute <code>baseStores</code> in extension <code>warehousing</code>.</i>*/
	public static final String _BASESTORE2SOURCINGCONFIGRELATION = "BaseStore2SourcingConfigRelation";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingConfig.code</code> attribute defined at extension <code>warehousing</code>. */
	public static final String CODE = "code";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingConfig.distanceWeightFactor</code> attribute defined at extension <code>warehousing</code>. */
	public static final String DISTANCEWEIGHTFACTOR = "distanceWeightFactor";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingConfig.allocationWeightFactor</code> attribute defined at extension <code>warehousing</code>. */
	public static final String ALLOCATIONWEIGHTFACTOR = "allocationWeightFactor";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingConfig.priorityWeightFactor</code> attribute defined at extension <code>warehousing</code>. */
	public static final String PRIORITYWEIGHTFACTOR = "priorityWeightFactor";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingConfig.scoreWeightFactor</code> attribute defined at extension <code>warehousing</code>. */
	public static final String SCOREWEIGHTFACTOR = "scoreWeightFactor";
	
	/** <i>Generated constant</i> - Attribute key of <code>SourcingConfig.baseStores</code> attribute defined at extension <code>warehousing</code>. */
	public static final String BASESTORES = "baseStores";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public SourcingConfigModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public SourcingConfigModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _code initial attribute declared by type <code>SourcingConfig</code> at extension <code>warehousing</code>
	 */
	@Deprecated
	public SourcingConfigModel(final String _code)
	{
		super();
		setCode(_code);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _code initial attribute declared by type <code>SourcingConfig</code> at extension <code>warehousing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated
	public SourcingConfigModel(final String _code, final ItemModel _owner)
	{
		super();
		setCode(_code);
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingConfig.allocationWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the allocationWeightFactor - Determines the weightage of the ATP during sourcing
	 */
	@Accessor(qualifier = "allocationWeightFactor", type = Accessor.Type.GETTER)
	public int getAllocationWeightFactor()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(ALLOCATIONWEIGHTFACTOR));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingConfig.baseStores</code> attribute defined at extension <code>warehousing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the baseStores
	 */
	@Accessor(qualifier = "baseStores", type = Accessor.Type.GETTER)
	public Set<BaseStoreModel> getBaseStores()
	{
		return getPersistenceContext().getPropertyValue(BASESTORES);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingConfig.code</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the code
	 */
	@Accessor(qualifier = "code", type = Accessor.Type.GETTER)
	public String getCode()
	{
		return getPersistenceContext().getPropertyValue(CODE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingConfig.distanceWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the distanceWeightFactor - Determines the weightage of the warehouse distance during sourcing
	 */
	@Accessor(qualifier = "distanceWeightFactor", type = Accessor.Type.GETTER)
	public int getDistanceWeightFactor()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(DISTANCEWEIGHTFACTOR));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingConfig.priorityWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the priorityWeightFactor - Determines the weightage of the warehouse priority during sourcing
	 */
	@Accessor(qualifier = "priorityWeightFactor", type = Accessor.Type.GETTER)
	public int getPriorityWeightFactor()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(PRIORITYWEIGHTFACTOR));
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>SourcingConfig.scoreWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 * @return the scoreWeightFactor - Determines the weightage of the warehouse score during sourcing
	 */
	@Accessor(qualifier = "scoreWeightFactor", type = Accessor.Type.GETTER)
	public int getScoreWeightFactor()
	{
		return toPrimitive((Integer)getPersistenceContext().getPropertyValue(SCOREWEIGHTFACTOR));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingConfig.allocationWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the allocationWeightFactor - Determines the weightage of the ATP during sourcing
	 */
	@Accessor(qualifier = "allocationWeightFactor", type = Accessor.Type.SETTER)
	public void setAllocationWeightFactor(final int value)
	{
		getPersistenceContext().setPropertyValue(ALLOCATIONWEIGHTFACTOR, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingConfig.baseStores</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the baseStores
	 */
	@Accessor(qualifier = "baseStores", type = Accessor.Type.SETTER)
	public void setBaseStores(final Set<BaseStoreModel> value)
	{
		getPersistenceContext().setPropertyValue(BASESTORES, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingConfig.code</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the code
	 */
	@Accessor(qualifier = "code", type = Accessor.Type.SETTER)
	public void setCode(final String value)
	{
		getPersistenceContext().setPropertyValue(CODE, value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingConfig.distanceWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the distanceWeightFactor - Determines the weightage of the warehouse distance during sourcing
	 */
	@Accessor(qualifier = "distanceWeightFactor", type = Accessor.Type.SETTER)
	public void setDistanceWeightFactor(final int value)
	{
		getPersistenceContext().setPropertyValue(DISTANCEWEIGHTFACTOR, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingConfig.priorityWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the priorityWeightFactor - Determines the weightage of the warehouse priority during sourcing
	 */
	@Accessor(qualifier = "priorityWeightFactor", type = Accessor.Type.SETTER)
	public void setPriorityWeightFactor(final int value)
	{
		getPersistenceContext().setPropertyValue(PRIORITYWEIGHTFACTOR, toObject(value));
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>SourcingConfig.scoreWeightFactor</code> attribute defined at extension <code>warehousing</code>. 
	 *  
	 * @param value the scoreWeightFactor - Determines the weightage of the warehouse score during sourcing
	 */
	@Accessor(qualifier = "scoreWeightFactor", type = Accessor.Type.SETTER)
	public void setScoreWeightFactor(final int value)
	{
		getPersistenceContext().setPropertyValue(SCOREWEIGHTFACTOR, toObject(value));
	}
	
}
