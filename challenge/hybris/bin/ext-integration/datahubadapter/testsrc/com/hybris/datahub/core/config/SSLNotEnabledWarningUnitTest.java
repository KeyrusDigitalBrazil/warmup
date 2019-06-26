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
package com.hybris.datahub.core.config;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SSLNotEnabledWarningUnitTest
{
	private SSLNotEnabledWarning warning;

	@Mock
	private Logger logger;

	@Before
	public void setup() throws NoSuchFieldException, IllegalAccessException
	{
		warning = new SSLNotEnabledWarning();
		setMockLogger();
	}

	@Test
	public void testLoggingSslEnabled()
	{
		warning.setSslEnabled(true);
		warning.afterPropertiesSet();
		verify(logger, times(0)).warn(anyString());
	}

	@Test
	public void testLoggingSslDisabled()
	{
		warning.setSslEnabled(false);
		warning.afterPropertiesSet();
		verify(logger).warn(contains("Data Hub Adapter is running in HTTP mode"));
	}

	private void setMockLogger() throws NoSuchFieldException, IllegalAccessException
	{
		final Field loggerField = warning.getClass().getDeclaredField("LOGGER");
		removeFinalModifier(loggerField);
		loggerField.setAccessible(true);
		loggerField.set(null, logger);
	}

	private static void removeFinalModifier(final Field field) throws NoSuchFieldException, IllegalAccessException
	{
		final Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	}
}
