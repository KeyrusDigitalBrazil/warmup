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

package de.hybris.platform.inboundservices.config;

import de.hybris.platform.integrationservices.config.BaseIntegrationServicesConfiguration;

/**
 * Provides access methods to configurations related to the Inbound Services
 */
public class DefaultInboundServicesConfiguration extends BaseIntegrationServicesConfiguration implements InboundServicesConfiguration
{
	private static final String PAYLOAD_RETENTION_SUCCESS_KEY = "inboundservices.monitoring.success.payload.retention";
	private static final String PAYLOAD_RETENTION_ERROR_KEY = "inboundservices.monitoring.error.payload.retention";

	private static final boolean PAYLOAD_RETENTION_SUCCESS_FALLBACK = false;
	private static final boolean PAYLOAD_RETENTION_ERROR_FALLBACK = true;

	private static final String MONITORING_ENABLED_KEY = "inboundservices.monitoring.enabled";
	private static final boolean MONITORING_ENABLED_FALLBACK = true;

	@Override
	public boolean isPayloadRetentionForSuccessEnabled()
	{
		return getBooleanProperty(PAYLOAD_RETENTION_SUCCESS_KEY, PAYLOAD_RETENTION_SUCCESS_FALLBACK);
	}

	@Override
	public boolean isPayloadRetentionForErrorEnabled()
	{
		return getBooleanProperty(PAYLOAD_RETENTION_ERROR_KEY, PAYLOAD_RETENTION_ERROR_FALLBACK);
	}

	@Override
	public boolean isMonitoringEnabled()
	{
		return getBooleanProperty(MONITORING_ENABLED_KEY, MONITORING_ENABLED_FALLBACK);
	}
}
