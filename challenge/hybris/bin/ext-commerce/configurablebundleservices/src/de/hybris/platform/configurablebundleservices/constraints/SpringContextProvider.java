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

package de.hybris.platform.configurablebundleservices.constraints;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Provides current spring {@link ApplicationContext} for non-bean classes.
 */
public class SpringContextProvider implements ApplicationContextAware
{
    private static ApplicationContext context;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		setContextThreadSafe(applicationContext);
	}

	public static ApplicationContext getContext()
	{
		return context;
	}

	protected static synchronized void setContextThreadSafe(final ApplicationContext applicationContext)
          throws BeansException
    {
        context = applicationContext;
    }
}
