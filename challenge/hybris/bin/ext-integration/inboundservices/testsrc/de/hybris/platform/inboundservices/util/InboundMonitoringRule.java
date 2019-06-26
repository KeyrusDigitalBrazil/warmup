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

package de.hybris.platform.inboundservices.util;

import de.hybris.platform.core.Registry;
import de.hybris.platform.inboundservices.config.InboundServicesConfiguration;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.junit.rules.ExternalResource;

/**
 * A rule for enabling/disabling inbound request monitoring in integration tests.
 */
public class InboundMonitoringRule extends ExternalResource
{
	private final ConfigurationService configuration;
	private final boolean originalMonitoringState;

	private InboundMonitoringRule()
	{
		configuration = getConfigurationService();
		originalMonitoringState = getInboundServicesConfiguration().isMonitoringEnabled();
	}

	/**
	 * Creates a rule that disables inbound request monitoring feature.
	 * @return a monitoring rule with monitoring disabled.
	 */
	public static InboundMonitoringRule disabled()
	{
		return new InboundMonitoringRule().disableMonitoring();
	}

	/**
	 * Creates a rule that enables inbound request monitoring feature.
	 * @return a monitoring rule with monitoring enabled.
	 */
	public static InboundMonitoringRule enabled()
	{
		return new InboundMonitoringRule().enableMonitoring();
	}

	private InboundMonitoringRule enableMonitoring()
	{
		setMonitoring(true);
		return this;
	}

	private InboundMonitoringRule disableMonitoring()
	{
		setMonitoring(false);
		return this;
	}

	private void setMonitoring(final boolean value)
	{
		configuration.getConfiguration().setProperty("inboundservices.monitoring.enabled", String.valueOf(value));
	}

	@Override
	protected void after()
	{
		setMonitoring(originalMonitoringState);
	}

	private static ConfigurationService getConfigurationService()
	{
		return Registry.getApplicationContext()
				.getBean("configurationService", ConfigurationService.class);
	}

	private static InboundServicesConfiguration getInboundServicesConfiguration()
	{
		return Registry.getApplicationContext()
				.getBean("inboundServicesConfiguration", InboundServicesConfiguration.class);
	}
}
