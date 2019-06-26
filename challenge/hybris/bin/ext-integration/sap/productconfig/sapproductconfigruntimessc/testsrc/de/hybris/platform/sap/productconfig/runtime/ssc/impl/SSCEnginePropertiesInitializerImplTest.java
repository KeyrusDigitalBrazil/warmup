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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SSCEnginePropertiesInitializerImplTest
{
	private static class DummyConfigSessionNewAPI
	{
		private static final Logger LOG = Logger.getLogger(DummyConfigSessionNewAPI.class);

		public static boolean isEnginePropertiesInitialized()
		{
			return false;
		}

		public static void setEngineProperties(final Properties properties)
		{
			LOG.info("setEngineProperties has been called with properties: " + properties.toString());
		}
	}

	private static class DummyConfigSessionOldAPI
	{
		private static final Logger LOG = Logger.getLogger(DummyConfigSessionOldAPI.class);

		public static void dummyMethod()
		{
			LOG.info("dummyMethod has been called");
		}
	}

	private SSCEnginePropertiesInitializerImpl classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new SSCEnginePropertiesInitializerImpl();
	}

	@Test
	public void testIsInjectPropertiesRequiredNewAPI() throws IllegalAccessException, InvocationTargetException
	{
		assertTrue(classUnderTest.isInjectPropertiesRequired(DummyConfigSessionNewAPI.class));
	}

	@Test
	public void testIsInjectPropertiesRequiredOldAPI() throws IllegalAccessException, InvocationTargetException
	{
		assertFalse(classUnderTest.isInjectPropertiesRequired(DummyConfigSessionOldAPI.class));
	}

	@Test(expected = NoSuchMethodException.class)
	public void testInjectPropertiesOldAPI() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		classUnderTest.injectProperties(DummyConfigSessionOldAPI.class);
	}
}
