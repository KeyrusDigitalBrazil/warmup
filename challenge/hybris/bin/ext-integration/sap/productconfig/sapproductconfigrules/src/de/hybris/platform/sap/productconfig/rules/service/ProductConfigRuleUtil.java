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
package de.hybris.platform.sap.productconfig.rules.service;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.List;
import java.util.Map;


/**
 * ProductConfigRuleUtil provided characteristics of a configurable product as a flat list or map.
 */
public interface ProductConfigRuleUtil
{
	/**
	 * Retrieves flat characteristic list of configurable product. In case of multilevel product sub-instance cstics are
	 * considered as well. If a cstic is available in more than one (sub-)instance, only its first occurrence is used.
	 *
	 * @param source
	 *           configuration model
	 * @return characteristic list of configurable product
	 */
	List<CsticModel> getCstics(final ConfigModel source);

	/**
	 * Retrieves characteristics of configurable product as a map. In case of multilevel product sub-instance cstics are
	 * considered as well. If a cstic is available in more than one (sub-)instance, only its first occurrence is used.
	 *
	 * @param source
	 *           configuration model
	 * @return characteristics of configurable product as a map
	 */
	Map<String, CsticModel> getCsticMap(final ConfigModel source);

	/**
	 * Retrieves characteristic list of configurable product for characteristics with given name . In case of multilevel
	 * product sub-instance cstics are considered as well. If a cstic is available in more than one (sub-)instance, all
	 * occurrences are retrieved.
	 *
	 * @param source
	 *           configuration model
	 * @param csticName
	 *           characteristic name
	 * @return characteristic list of configurable product
	 */
	List<CsticModel> getCsticsForCsticName(ConfigModel source, String csticName);
}
