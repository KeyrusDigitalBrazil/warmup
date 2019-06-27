/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.monitoring;

/**
 * Defines configuration for monitoring
 */
public interface MonitoringConfiguration
{
	/**
	 * Determines current setting of payload retention for successful requests.
	 *
	 * @return true or false depending if the property is enabled or not.
	 */
	boolean isPayloadRetentionForSuccessEnabled();

	/**
	 * Determines current setting of payload retention for non successful requests.
	 *
	 * @return true or false depending if the property is enabled or not.
	 */
	boolean isPayloadRetentionForErrorEnabled();

	/**
	 * Determines if monitoring is enabled
	 *
	 * @return true if monitoring is enabled, false otherwise
	 */
	boolean isMonitoringEnabled();
}
