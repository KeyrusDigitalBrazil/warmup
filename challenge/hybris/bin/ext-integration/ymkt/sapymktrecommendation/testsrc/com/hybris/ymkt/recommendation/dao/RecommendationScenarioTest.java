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
package com.hybris.ymkt.recommendation.dao;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.YMKTTestTool;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;


@UnitTest
public class RecommendationScenarioTest
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
	public void test()
	{
		YMKTTestTool.assertEqualsHashCode( //
				new BasketObject("t1", "id1"), //
				new BasketObject("t1", "id1"), //
				new BasketObject("t1", "id2"), //
				new BasketObject("t2", "id1"), //

				new LeadingObject("t1", "id1"), //
				new LeadingObject("t1", "id1"), //
				new LeadingObject("t1", "id2"), //
				new LeadingObject("t2", "id1"));

	}

}
