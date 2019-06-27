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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


/**
 *
 */
@UnitTest
public class ConfigurationSessionContainerImplTest
{
	ConfigurationSessionContainerImpl classUnderTest = new ConfigurationSessionContainerImpl();
	private static final String qualifiedId = "A";

	@Mock
	private IConfigSession configSession;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testStore()
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		assertEquals(configSession, classUnderTest.retrieveConfigSession(qualifiedId));
	}

	@Test(expected = IllegalStateException.class)
	public void testReleaseSession()
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		classUnderTest.releaseSession(qualifiedId);
		classUnderTest.retrieveConfigSession(qualifiedId);
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveEmptyMap()
	{
		classUnderTest.retrieveConfigSession(qualifiedId);
	}

	@Test
	public void testStackTrace()
	{
		final StringBuilder topLinesOfStacktrace = classUnderTest.getTopLinesOfStacktrace(5);
		assertNotNull(topLinesOfStacktrace);
		assertTrue(topLinesOfStacktrace.toString().length() > 0);
		assertFalse("We don't want to see the first two entries", topLinesOfStacktrace.toString().contains("java.lang.Thread"));
		System.out.println(topLinesOfStacktrace);
	}

	@Test
	public void testReleaseSession_isClosed()
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		classUnderTest.releaseSession(qualifiedId);
		verify(configSession).closeSession();
	}

	@Test
	public void testReleaseSession_noError()
	{
		classUnderTest.releaseSession(qualifiedId);
	}



}
