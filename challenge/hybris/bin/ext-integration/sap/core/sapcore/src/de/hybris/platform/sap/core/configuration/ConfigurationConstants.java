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
package de.hybris.platform.sap.core.configuration;

/**
 * Constants for configuration.
 */
public class ConfigurationConstants
{

	private ConfigurationConstants() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Default SAP backend type.
	 */
	public static final String DEFAULT_SAP_BACKEND_TYPE = "ERP";

	/**
	 * Default RFC destination name.
	 */
	public static final String DEFAULT_RFC_DESTINATION_NAME = "SAP_DEFAULT";

	/**
	 * SAP runtime configuration name.
	 */
	public static final String SAP_CONFIGURATION_NAME_ATTRIBUTE = "core_name";

}
