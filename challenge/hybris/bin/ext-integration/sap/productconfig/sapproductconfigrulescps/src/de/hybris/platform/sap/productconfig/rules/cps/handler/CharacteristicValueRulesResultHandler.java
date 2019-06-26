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
package de.hybris.platform.sap.productconfig.rules.cps.handler;

import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;

import java.util.List;


/**
 * Handles rules result for characteristic values.
 */
public interface CharacteristicValueRulesResultHandler
{

	/**
	 * Returns the list of CharacteristicValueResultModel objects associated with given configuration id.
	 *
	 * @param configId
	 *           Configuration id
	 * @return list of CharacteristicValueResultModel objects
	 */
	List<CharacteristicValueRulesResultModel> getRulesResultsByConfigId(String configId);

	/**
	 *
	 * @param configId
	 *           Configuration id
	 */
	void deleteRulesResultsByConfigId(String configId);

	/**
	 * Persists a CharacteristicValueRulesResult with the given data. Adds it to the list of rules result at the
	 * configuration model identified by given id. If a rules result already exists for the given characteristic/value
	 * pair the discount is added to the existing one. The discount sum cannot exceed 100%.
	 *
	 * @param result
	 *           to persist
	 *
	 * @param configId
	 *           id of the configuration to which this result belongs to
	 */
	void mergeDiscountAndPersistResults(CharacteristicValueRulesResultModel result, String configId);


	/**
	 * @param message
	 *           discount message instance
	 * @param configIg
	 *           configuration id
	 * @param csticName
	 *           name of cstic for which the message is persistet
	 * @param csticValueName
	 *           name of cstic value for which the message is persistet - may be null
	 *
	 */
	void addMessageToRulesResult(DiscountMessageRulesResultModel message, String configIg, String csticName,
			String csticValueName);

	/**
	 * creates an empty instance
	 *
	 * @return a new instance
	 */
	CharacteristicValueRulesResultModel createInstance();

	/**
	 * creates an empty instance
	 *
	 * @return a new instance
	 */
	DiscountMessageRulesResultModel createMessageInstance();


	/**
	 * Copies the CharacteristicValueRulesResult from the source ProductConfiguration and persists it for the target
	 * ProductConfiguration
	 *
	 * @param sourceConfigId
	 *           source configuration Id
	 * @param targetConfigId
	 *           configuration Id
	 */
	void copyAndPersistRuleResults(String sourceConfigId, String targetConfigId);

}
