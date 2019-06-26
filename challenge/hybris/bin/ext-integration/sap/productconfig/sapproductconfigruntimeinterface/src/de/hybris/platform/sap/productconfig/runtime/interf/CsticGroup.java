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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.List;


/**
 * Represents a characteristic group including the group content - the list of <code>CsticModel</code>.
 * 
 */
public interface CsticGroup
{

	/**
	 * @return the characteristic group name
	 */
	String getName();

	/**
	 * @param name
	 *           the characteristic group name to set
	 */
	void setName(String name);

	/**
	 * @return the characteristic group description
	 */
	String getDescription();

	/**
	 * @param description
	 *           the characteristic group description to set
	 */
	void setDescription(String description);

	/**
	 * @return the characteristic list of this characteristic group
	 */
	List<CsticModel> getCstics();

	/**
	 * @param cstics
	 *           the characteristic list to set
	 */
	void setCstics(List<CsticModel> cstics);

}