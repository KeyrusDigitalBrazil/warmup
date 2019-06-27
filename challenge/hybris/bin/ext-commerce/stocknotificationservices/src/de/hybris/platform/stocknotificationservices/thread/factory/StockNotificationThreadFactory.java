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
package de.hybris.platform.stocknotificationservices.thread.factory;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.jalo.JaloSession;

import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Scope;


/**
 * Thread factory used to create threads
 */
@Scope(value = "tenant")
public class StockNotificationThreadFactory implements ThreadFactory
{
	private final Tenant currentTenant;

	public StockNotificationThreadFactory()
	{
		currentTenant = Registry.getCurrentTenant();
	}

	@Override
	public Thread newThread(final Runnable runnable)
	{
		return new RegistrableThread()
		{
			@Override
			public void internalRun()
			{
				try
				{
					Registry.setCurrentTenant(currentTenant);
					JaloSession.getCurrentSession().activate();
					runnable.run();
				}
				finally
				{
					JaloSession.getCurrentSession().close();
					JaloSession.deactivate();
					Registry.unsetCurrentTenant();
				}
			}
		};
	}

}
