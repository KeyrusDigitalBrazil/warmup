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

import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationSessionContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


/**
 * Default implementation of {@link ConfigurationSessionContainer}
 */
public class ConfigurationSessionContainerImpl implements ConfigurationSessionContainer
{
	private final Map<String, IConfigSession> sessionMap = new HashMap<>();
	private static final Logger LOG = Logger.getLogger(ConfigurationSessionContainerImpl.class);


	@Override
	public Map<String, IConfigSession> getSessionMap()
	{
		return sessionMap;
	}

	@Override
	public void storeConfiguration(final String qualifiedId, final IConfigSession configSession)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Store configuration for: " + qualifiedId);
		}
		sessionMap.put(qualifiedId, configSession);
	}

	@Override
	public IConfigSession retrieveConfigSession(final String qualifiedId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Retrieve configuration for: " + qualifiedId);
		}
		final IConfigSession configSession = sessionMap.get(qualifiedId);

		if (configSession == null)
		{
			throw new IllegalStateException(new StringBuilder().append("Session for id ").append(qualifiedId)
					.append(" does not exist and could not be retrieved").toString());
		}
		return configSession;
	}

	@Override
	public void releaseSession(final String qualifiedId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Release configuration for: " + qualifiedId);
			LOG.debug("Release called from: " + getTopLinesOfStacktrace(10).toString());
		}
		final IConfigSession removedSession = sessionMap.remove(qualifiedId);
		if (null != removedSession)
		{
			removedSession.closeSession();
		}
	}

	protected StringBuilder getTopLinesOfStacktrace(final int numberOfLines)
	{

		final List<StackTraceElement> stackTrace = Arrays.asList(Thread.currentThread().getStackTrace());
		final StringBuilder result = new StringBuilder("\n");
		int counter = 0;
		for (final StackTraceElement line : stackTrace)
		{
			if (++counter > 3)
			{
				result.append(line).append("\n");
			}
			if (counter > numberOfLines + 3)
			{
				break;
			}
		}
		return result;
	}
}
