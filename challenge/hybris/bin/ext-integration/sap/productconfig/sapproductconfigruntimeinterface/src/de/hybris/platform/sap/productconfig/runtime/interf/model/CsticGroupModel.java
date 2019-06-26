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

import java.util.List;


/**
 * Represents the characteristic group model.
 */
public interface CsticGroupModel extends BaseModel
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
	 * @return the list of characteristic names of this group
	 */
	List<String> getCsticNames();

	/**
	 * @param csticNames
	 *           the list of characteristic names to set
	 */
	void setCsticNames(final List<String> csticNames);
}
