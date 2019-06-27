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

/**
 * Conflicting assumption (part of a {@link SolvableConflictModel} which can be retracted
 */
public interface ConflictingAssumptionModel
{

	/**
	 * @param csticName
	 */
	void setCsticName(String csticName);

	/**
	 * @return Language independent name of cstic which causes the conflicting assumption
	 */
	String getCsticName();

	/**
	 * @param valueName
	 */
	void setValueName(String valueName);

	/**
	 * @return Language independent name of value which causes the conflicting assumption
	 */
	String getValueName();

	/**
	 * @param instanceId
	 */
	void setInstanceId(String instanceId);

	/**
	 * @return the instanceId
	 */
	String getInstanceId();

	/**
	 * @param assumptionId
	 */
	void setId(String assumptionId);

	/**
	 * @return Assumption ID
	 */
	String getId();

}
