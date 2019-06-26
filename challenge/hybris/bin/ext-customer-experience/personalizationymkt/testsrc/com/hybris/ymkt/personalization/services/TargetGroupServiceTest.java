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
/**
 *
 */
package com.hybris.ymkt.personalization.services;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class TargetGroupServiceTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void partitionTest()
	{
		final List<String> a = Arrays.asList("a");
		final List<String> ab = Arrays.asList("a", "b");
		final List<String> b = Arrays.asList("b");

		Assert.assertEquals(Collections.emptyList(), TargetGroupService.partition(Collections.emptyList(), 1));

		Assert.assertEquals(Arrays.asList(a), TargetGroupService.partition(a, 1));
		Assert.assertEquals(Arrays.asList(ab), TargetGroupService.partition(ab, 2));
		Assert.assertEquals(Arrays.asList(a, b), TargetGroupService.partition(ab, 1));
	}



}
