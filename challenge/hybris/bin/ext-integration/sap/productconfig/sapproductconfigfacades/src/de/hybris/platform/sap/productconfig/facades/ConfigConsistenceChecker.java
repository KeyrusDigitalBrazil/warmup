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


/**
 * Callback-Interface for manipulating the product configuration data transfer objects after they have been updated from
 * the model.
 */
public interface ConfigConsistenceChecker
{

	/**
	 * This method will be called after the product configuration DAO has been updated from the model.
	 *
	 * @param configData
	 *           original DAO
	 */
	void checkConfiguration(ConfigurationData configData);
}
