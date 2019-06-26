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
package de.hybris.platform.acceleratorservices.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.exceptions.PathTraversalException;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


@UnitTest
public class PathTraversalResourceUtilsTest
{

	@Test
	public void testAssertPathSegmentIsSecureSuccess()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure("simpleName");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("123");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("8yGH853weYrt");

		PathTraversalResourceUtils.assertPathSegmentIsSecure("fileNameWith.ending");
		PathTraversalResourceUtils.assertPathSegmentIsSecure(".hiddenFileName");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("name_with_underscores");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("name-with-dashes");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("8n..W4.-88_7c.D-q");

		PathTraversalResourceUtils.assertPathSegmentIsSecure("periods..inside..segment");
		PathTraversalResourceUtils.assertPathSegmentIsSecure(".more.periods.......");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("...");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("back\\slash");
		PathTraversalResourceUtils.assertPathSegmentIsSecure("co:lon");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssertPathSegmentIsSecureNullPathSegment()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure(null);
	}

	@Test(expected = PathTraversalException.class)
	public void testAssertPathSegmentIsSecureFailEmptyString()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure(StringUtils.EMPTY);
	}

	@Test(expected = PathTraversalException.class)
	public void testAssertPathSegmentIsSecureFailForSimpleUnixtDirectoryTraversal()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure("../");
	}

	@Test(expected = PathTraversalException.class)
	public void testAssertPathSegmentIsSecureFailForComplexUnixDirectoryTraversal()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure("./.././../../");
	}

	@Test(expected = PathTraversalException.class)
	public void testAssertPathSegmentIsSecureFailForSimpleWindowsDirectoryTraversal()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure("..\\");
	}

	@Test(expected = PathTraversalException.class)
	public void testAssertPathSegmentIsSecureFailForComplexWindowsDirectoryTraversal()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure(".\\..\\.\\..\\..\\");
	}

	@Test(expected = PathTraversalException.class)
	public void testAssertPathSegmentIsSecureFailForComplexDirectoryTraversal()
	{
		PathTraversalResourceUtils.assertPathSegmentIsSecure("one/..\\two/three");
	}

}