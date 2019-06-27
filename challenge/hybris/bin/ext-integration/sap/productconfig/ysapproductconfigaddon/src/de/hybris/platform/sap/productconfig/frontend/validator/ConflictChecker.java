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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;

import org.springframework.validation.BindingResult;


/**
 * Utility class to update status information within a configuration object and generate corresponding UI errors.
 */
public interface ConflictChecker
{
	/**
	 * Checks whether the given configuration object contains conflicts, and if so updates the group/cstic status
	 * accordingly.<br>
	 * Creates for every conflict and UI-Error.
	 *
	 * @param config
	 *           config to check
	 * @param bindingResult
	 *           storage for UI errors
	 */
	void checkConflicts(ConfigurationData config, BindingResult bindingResult);

	/**
	 * Checks whether the given configuration object contains mandatory fields without any value, and if so updates the
	 * group/cstic status accordingly.<br>
	 * Creates for every missing field and UI-Error.
	 *
	 * @param config
	 *           config to check
	 * @param bindingResult
	 *           storage for UI errors
	 */
	void checkMandatoryFields(ConfigurationData config, BindingResult bindingResult);

	/**
	 * Checks whether a UI-Group is considered complete, and if so sets the group status accordingly.
	 *
	 * @param config
	 *           configuration to check
	 */
	void checkCompletness(ConfigurationData config);
}
