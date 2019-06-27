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

import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;

import java.util.List;
import java.util.Map;


/**
 * Utility class to read/delete persisted rules result data.
 */
public interface ProductConfigRulesResultUtil
{

	/**
	 * Returns a list of VariantConditionModifications objects containing the modifications for the given configuration
	 * id for variant conditions calculated by rules.
	 *
	 * @param configId
	 *           Configuration Id
	 * @return List of variant condition modifications calculated by rules
	 */
	List<ProductConfigurationDiscount> retrieveRulesBasedVariantConditionModifications(String configId);

	/**
	 * Deletes the rules results for the provided configuration id.
	 *
	 * @param configId
	 *           Configuration Id
	 */
	void deleteRulesResultsByConfigId(String configId);

	/**
	 * Returns all discount messages for the given configuration as structured data. The outer Map has the cstic name as
	 * key, while the inner map has the cstic value name as key.
	 *
	 * @param configId
	 *           Configuration Id
	 * @return discount messages, structured by cstic name and cstic value name
	 *
	 */
	Map<String, Map<String, List<ProductConfigMessage>>> retrieveDiscountMessages(String configId);



}
