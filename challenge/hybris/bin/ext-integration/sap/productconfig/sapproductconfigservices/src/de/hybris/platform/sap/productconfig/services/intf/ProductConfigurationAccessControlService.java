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
package de.hybris.platform.sap.productconfig.services.intf;

/**
 * Responsible for access control of runtime product configuration entities.
 */
public interface ProductConfigurationAccessControlService
{

	/**
	 * Check if a configuration update is allowed for a configuration specified by its ID
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Allowed!
	 */
	boolean isUpdateAllowed(String configId);

	/**
	 * Check if reading configuration is allowed for a configuration specified by its ID
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Allowed!
	 */
	boolean isReadAllowed(String configId);

	/**
	 * Check if a configuration release is allowed for a configuration specified by its ID
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Allowed!
	 */
	boolean isReleaseAllowed(String configId);

}
