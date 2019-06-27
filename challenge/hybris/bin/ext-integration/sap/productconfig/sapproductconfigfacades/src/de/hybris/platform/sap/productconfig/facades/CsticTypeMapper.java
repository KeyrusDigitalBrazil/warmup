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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.Map;


/**
 * Populator like helper object to map a single characteristic and all child objects, such as domain values, from the
 * product configuration model to the corresponding DAOs and vice versa.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface CsticTypeMapper
{
	/**
	 * Characteristics that are assigned by the system author may not be chnaged by the front-end user, henc ethey should
	 * be considered read-only.
	 */
	String READ_ONLY_AUTHOR = "S";


	/**
	 * Maps a single characteristic. Model -> DTO.
	 *
	 * @param model
	 *           source - characteristic Model
	 * @param groupName
	 *           name of the group, this characteristic belongs to
	 * @param nameMap
	 *           cache for hybris classification system access
	 * @return target - characteristic DTO
	 */
	CsticData mapCsticModelToData(CsticModel model, String groupName,
			Map<String, ClassificationSystemCPQAttributesContainer> nameMap);


	/**
	 * Updates a single characteristic. DTO -> Model.
	 *
	 * @param data
	 *           source - characteristic DTO
	 * @param model
	 *           target - characteristic Model
	 */
	void updateCsticModelValuesFromData(CsticData data, CsticModel model);


	/**
	 * Generates a key that identifies this characteristic uniquely within this configuration.
	 *
	 * @param model
	 *           characteristic model
	 * @param groupName
	 *           ui group name the cstic belongs to
	 *
	 * @return unique key
	 */
	String generateUniqueKey(CsticModel model, String groupName);

}
