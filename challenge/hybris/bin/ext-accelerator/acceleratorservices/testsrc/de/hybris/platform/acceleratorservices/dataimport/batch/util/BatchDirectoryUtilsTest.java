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
package de.hybris.platform.acceleratorservices.dataimport.batch.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for {@link BatchDirectoryUtils}
 */
@UnitTest
public class BatchDirectoryUtilsTest
{
	private static final String ARCHIVE_DIRECTORY = "archive";
	private static final String ERROR_DIRECTORY = "error";
	private File f;
	private File parent;

	@Before
	public void setUp()
	{
		f = new File("test1" + File.separator + "test2" + File.separator + "test3");
		parent = new File("test1");
	}

	@Test
	public void verifyTest()
	{
		Assert.assertEquals("testdir", BatchDirectoryUtils.verify("testdir"));
	}

	@Test
	public void getRelativeErrorDirectoryTest()
	{
		Assert.assertEquals(parent.getAbsolutePath() + File.separator + ERROR_DIRECTORY,
				BatchDirectoryUtils.getRelativeErrorDirectory(f));
	}

	@Test
	public void getRelativeArchiveDirectoryTest()
	{
		Assert.assertEquals(parent.getAbsolutePath() + File.separator + ARCHIVE_DIRECTORY,
				BatchDirectoryUtils.getRelativeArchiveDirectory(f));
	}

	@Test
	public void getRelativeBaseDirectoryTest()
	{
		Assert.assertEquals(parent.getAbsolutePath(), BatchDirectoryUtils.getRelativeBaseDirectory(f));
	}

}
