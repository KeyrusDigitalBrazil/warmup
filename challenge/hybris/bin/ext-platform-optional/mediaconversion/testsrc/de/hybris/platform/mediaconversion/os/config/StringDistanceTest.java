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
package de.hybris.platform.mediaconversion.os.config;

import de.hybris.bootstrap.annotations.UnitTest;

import junit.framework.Assert;

import org.junit.Test;


/**
 * @author pohl
 */
@UnitTest
public class StringDistanceTest
{

	@Test
	public void testCountSimilar()
	{
		Assert.assertEquals(3, StringDistance.countSimilar("GUMBO", "GAMBOL"));
		Assert.assertEquals(0, StringDistance.countSimilar("", "GAMBOL"));
		Assert.assertEquals(7, StringDistance.countSimilar("Windows", "XP Windows"));
		Assert.assertEquals(7, StringDistance.countSimilar("XP Windows", "Windows"));
		Assert.assertEquals(7, StringDistance.countSimilar("Windows", "Windows"));
		Assert.assertEquals(3, StringDistance.countSimilar("CMD", "CMD"));
	}

	@Test
	public void testLevenshtein()
	{
		Assert.assertEquals(2, StringDistance.levenshtein("GUMBO", "GAMBOL"));
		Assert.assertEquals(6, StringDistance.levenshtein("", "GAMBOL"));
		Assert.assertEquals(3, StringDistance.levenshtein("Windows", "XP Windows"));
		Assert.assertEquals(3, StringDistance.levenshtein("XP Windows", "Windows"));
		Assert.assertEquals(0, StringDistance.levenshtein("Windows", "Windows"));
	}

}
