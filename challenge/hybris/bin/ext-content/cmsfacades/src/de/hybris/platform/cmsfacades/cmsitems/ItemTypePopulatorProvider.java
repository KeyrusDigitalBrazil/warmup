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
package de.hybris.platform.cmsfacades.cmsitems;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;

import java.util.Map;
import java.util.Optional;

/**
 * Interface to provide the custom {@link Populator} for a given typeCode.   
 */
public interface ItemTypePopulatorProvider
{

	/**
	 * Interface method to optionally return a {@link Populator} given the typeCode. 
	 * @param typeCode the ItemModel type code
	 * @return the Populator registered for the typeCode. 
	 */
	Optional<Populator<Map<String, Object>, ItemModel>> getItemTypePopulator(final String typeCode);
	
}
