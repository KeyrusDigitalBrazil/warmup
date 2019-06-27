/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;

import java.util.Collections;
import java.util.List;


/**
 * Represents the instance value model.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface InstanceModel extends BaseModel
{
	/**
	 * General group, used for all cstic, which are not assigned to another group
	 */
	String GENERAL_GROUP_NAME = "_GEN";

	/**
	 * @return the instance id
	 */
	String getId();

	/**
	 * @param id
	 *           instance id
	 */
	void setId(String id);

	/**
	 * @return the instance name
	 */
	String getName();

	/**
	 * @param name
	 *           instance name
	 */
	void setName(String name);

	/**
	 * @return the instance language dependent name
	 */
	String getLanguageDependentName();

	/**
	 * @param languageDependentName
	 *           instance language dependent name
	 */
	void setLanguageDependentName(String languageDependentName);

	/**
	 * @param csticName
	 *           characteristic name
	 * @return the characteristic model for the given characteristic name
	 */
	CsticModel getCstic(String csticName);

	/**
	 * @param cstic
	 *           characteristic model
	 */
	void addCstic(CsticModel cstic);

	/**
	 * @param cstic
	 *           characteristic model
	 * @return true if the cstics was part of the list
	 */
	boolean removeCstic(CsticModel cstic);

	/**
	 * @return unmodifiable list of characteristic models of this instance, to add/remove cstics, use manipulators of
	 *         this interface
	 */
	List<CsticModel> getCstics();

	/**
	 * @param cstic
	 *           list of characteristic models
	 */
	void setCstics(List<CsticModel> cstic);

	/**
	 * @param subInstanceId
	 *           subinstance id
	 * @return the subinstance model for the given subinstance id
	 */
	InstanceModel getSubInstance(String subInstanceId);

	/**
	 * removes the subinstance from the subinstance list for the given subinstance id
	 *
	 * @param subInstanceId
	 *           subinstance id
	 * @return removed subinstance model
	 */
	InstanceModel removeSubInstance(String subInstanceId);

	/**
	 * @param subInstance
	 *           subinstance model
	 */
	void setSubInstance(InstanceModel subInstance);

	/**
	 * @return the list of subinstance models of this instance
	 */
	List<InstanceModel> getSubInstances();

	/**
	 * @param subInstances
	 *           list of subinstance models
	 */
	void setSubInstances(List<InstanceModel> subInstances);

	/**
	 * @return true if this instance is consistent
	 */
	boolean isConsistent();

	/**
	 * @param consistent
	 *           flag indicating whether this instance is consistent
	 */
	void setConsistent(boolean consistent);

	/**
	 * @return true if this instance is complete
	 */
	boolean isComplete();

	/**
	 * @param complete
	 *           flag indicating whether this instance is complete
	 */
	void setComplete(boolean complete);

	/**
	 * @return true if this instance is a root instance
	 */
	boolean isRootInstance();

	/**
	 * @param rootInstance
	 *           flag indicating whether this instance is a root instance
	 */
	void setRootInstance(boolean rootInstance);

	/**
	 * @param csticGroups
	 *           list of characteristic group models
	 */
	void setCsticGroups(List<CsticGroupModel> csticGroups);

	/**
	 * @return the list of characteristic group models from this instance
	 */
	List<CsticGroupModel> getCsticGroups();

	/**
	 * @return the list of <code>CsticGroup</code> from this instance
	 */
	List<CsticGroup> retrieveCsticGroupsWithCstics();

	/**
	 * @return the BOM position of this instance
	 */
	String getPosition();

	/**
	 * @param position
	 *           BOM position of this instance
	 */
	void setPosition(String position);

	/**
	 * @return list of variant conditions
	 */
	default List<VariantConditionModel> getVariantConditions()
	{
		return Collections.emptyList();
	}


	/**
	 * @param variantConditions
	 *           variant conditions for the configuration
	 */
	default void setVariantConditions(final List<VariantConditionModel> variantConditions)
	{
		//empty
	}
}
