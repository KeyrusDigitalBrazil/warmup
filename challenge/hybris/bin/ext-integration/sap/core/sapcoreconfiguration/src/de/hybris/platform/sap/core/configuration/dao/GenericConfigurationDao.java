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
package de.hybris.platform.sap.core.configuration.dao;

import de.hybris.platform.core.model.ItemModel;

import java.util.List;


/**
 * Interface for generic access to the requested item.
 */
public interface GenericConfigurationDao
{

	/**
	 * Retrieves all models for the given type code.
	 * 
	 * @param code
	 *           item type code (String)
	 * @return list of found models
	 */
	public List<ItemModel> getAllModelsForCode(final String code);

}
