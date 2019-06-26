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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

public interface ConfigurationDependencyHandler
{
	/**
	 * Copies dependencies from the source ProductConfiguration with given Id to the target one ProductConfiguration
	 *
	 * @param sourceConfigId
	 *           source configuration Id
	 * @param targetConfigId
	 *           configuration Id
	 */
	void copyProductConfigurationDependency(String sourceConfigId, String targetConfigId);

}
