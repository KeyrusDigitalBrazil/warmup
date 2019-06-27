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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.ssc.SSCEnginePopertiesInitializer;
import de.hybris.platform.util.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.log4j.Logger;
import com.sap.custdev.projects.fbs.slc.cfg.ConfigSession;



/**
 * Default implementation of the {@link SSCEnginePopertiesInitializer}
 */
public class SSCEnginePropertiesInitializerImpl implements SSCEnginePopertiesInitializer
{
	private static final String SSC_DATABASE_TYPE = "crm.system_type";
	private static final String SSC_DATABASE_HOSTNAME = "crm.database_hostname";
	private static final String SSC_DATABASE_NAME = "crm.database";
	private static final String SSC_DATABASE_PORT = "crm.database_port";
	private static final String SSC_DATABASE_USER = "crm.database_user";
	private static final String SSC_DATABASE_PASSWORD = "crm.database_password";
	private static final String SSC_JNDI_USAGE = "crm.ssc_jndi_usage";
	private static final String SSC_JNDI_DATASOURCE = "crm.ssc_jndi_datasource";
	private static final Logger LOG = Logger.getLogger(SSCEnginePropertiesInitializerImpl.class);


	@Override
	public void initializeEngineProperties()
	{
		try
		{
			if (isInjectPropertiesRequired(ConfigSession.class))
			{
				injectProperties(ConfigSession.class);
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex)
		{
			throw new IllegalStateException("inavlid SSC API", ex);
		}

	}

	protected boolean isInjectPropertiesRequired(final Class clazz) throws IllegalAccessException, InvocationTargetException
	{

		Boolean isInit = Boolean.FALSE;
		boolean initMethodExists = false;
		for (final Method method : clazz.getMethods())
		{
			if ("isEnginePropertiesInitialized".equals(method.getName()))
			{
				isInit = (Boolean) method.invoke(null, null);
				initMethodExists = true;
			}
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("isEnginePropertiesInitialized exists in SSC API: " + initMethodExists);
			if (initMethodExists)
			{
				LOG.debug("Engine Properties Initialized: " + isInit);
			}
		}
		return !isInit.booleanValue() && initMethodExists;
	}

	protected void injectProperties(final Class clazz) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException
	{
		final Class[] partypes =
		{ Properties.class };
		final Method setPropertiesMethod = clazz.getMethod("setEngineProperties", partypes);
		final Properties engineProperties = fetchEngineProperties();
		final Object[] arguments =
		{ engineProperties };
		setPropertiesMethod.invoke(null, arguments);
	}

	protected Properties fetchEngineProperties()
	{
		final Properties properties = new Properties();
		properties.setProperty(SSC_DATABASE_TYPE, Config.getParameter(SSC_DATABASE_TYPE));
		properties.setProperty(SSC_DATABASE_HOSTNAME, Config.getParameter(SSC_DATABASE_HOSTNAME));
		properties.setProperty(SSC_DATABASE_NAME, Config.getParameter(SSC_DATABASE_NAME));
		properties.setProperty(SSC_DATABASE_PORT, Config.getParameter(SSC_DATABASE_PORT));
		properties.setProperty(SSC_DATABASE_USER, Config.getParameter(SSC_DATABASE_USER));
		properties.setProperty(SSC_DATABASE_PASSWORD, Config.getParameter(SSC_DATABASE_PASSWORD));
		properties.setProperty(SSC_JNDI_USAGE, Config.getParameter(SSC_JNDI_USAGE));
		properties.setProperty(SSC_JNDI_DATASOURCE, Config.getParameter(SSC_JNDI_DATASOURCE));
		return properties;
	}

}
