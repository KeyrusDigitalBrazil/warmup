/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.apiregistryservices.jmx;

import de.hybris.platform.apiregistryservices.jmx.service.SpringIntegrationJmxService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.TenantListener;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Bean waits for context init and then runs jmx beans registration
 */
public class SpringIntegrationQueueRegistrator implements ApplicationListener<ContextRefreshedEvent>
{
	private static final String BLACKLISTED_TENANTS_PROP = "apiregistryservices.jmx.blacklisted.tenants";
	private Class beanInterface;
	private String jmxPath;
	private SpringIntegrationJmxService springIntegrationJmxService;
	private TenantListener tenantListener;

	public SpringIntegrationQueueRegistrator()
	{
		tenantListener = new TenantListener()
		{
			@Override
			public void afterTenantStartUp(final Tenant tenant)
			{
				if (!getBlacklistedTenantIds().contains(tenant.getTenantID()))
				{
					getSpringIntegrationJmxService().registerAllSpringQueues(getJmxPath(), getBeanInterface());
				}
			}

			@Override
			public void beforeTenantShutDown(final Tenant tenant)
			{
				// nothing
			}

			@Override
			public void afterSetActivateSession(final Tenant tenant)
			{
				// nothing
			}

			@Override
			public void beforeUnsetActivateSession(final Tenant tenant)
			{
				// nothing
			}
		};
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent)
	{
		if (!Registry.getTenantListeners().contains(getTenantListener()))
		{
			Registry.registerTenantListener(getTenantListener());
		}
	}

	protected List<String> getBlacklistedTenantIds()
	{
		return Arrays.asList(Config.getString(BLACKLISTED_TENANTS_PROP,"junit").split(","));
	}

	public TenantListener getTenantListener()
	{
		return tenantListener;
	}

	public Class getBeanInterface()
	{
		return beanInterface;
	}

	@Required
	public void setBeanInterface(final Class beanInterface)
	{
		this.beanInterface = beanInterface;
	}

	public String getJmxPath()
	{
		return jmxPath;
	}

	public void setJmxPath(final String jmxPath)
	{
		this.jmxPath = jmxPath;
	}

	protected SpringIntegrationJmxService getSpringIntegrationJmxService()
	{
		return springIntegrationJmxService;
	}

	@Required
	public void setSpringIntegrationJmxService(final SpringIntegrationJmxService springIntegrationJmxService)
	{
		this.springIntegrationJmxService = springIntegrationJmxService;
	}
}
