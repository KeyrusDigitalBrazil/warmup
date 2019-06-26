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
package de.hybris.platform.sap.productconfig.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.mockito.Mock;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Unit Tests
 */
@SuppressWarnings("javadoc")
@UnitTest
public class DataLoaderManagerContainerImplTest
{
	DataLoaderManagerContainerImpl classUnderTest = new DataLoaderManagerContainerImpl();

	@Mock
	DataloaderManager manager;

	@Test
	public void testDataLoaderManager()
	{
		classUnderTest.setDataLoaderManager(manager);
		assertEquals(manager, classUnderTest.getDataLoaderManager());
	}


	@Test
	public void testResumeWasPerformed()
	{
		classUnderTest.setResumePerformed(true);
		assertTrue(classUnderTest.isResumePerformed());
	}
}
