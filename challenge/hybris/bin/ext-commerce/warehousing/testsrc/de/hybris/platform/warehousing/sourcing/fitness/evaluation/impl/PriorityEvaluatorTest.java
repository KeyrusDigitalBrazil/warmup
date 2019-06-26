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
 *
 */
package de.hybris.platform.warehousing.sourcing.fitness.evaluation.impl;

import static org.hamcrest.CoreMatchers.is;

import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PriorityEvaluatorTest
{
	private PriorityEvaluator evaluator;
	private SourcingLocation location;

	@Before
	public void setup()
	{
		evaluator = new PriorityEvaluator();
		location = new SourcingLocation();
	}

	@Test
	public void shouldEvaluatePriority()
	{
		location.setPriority(5);

		final Double loc1Eval = evaluator.evaluate(location);
		Assert.assertThat(5.0, is(loc1Eval));
	}

	@Test
	public void shouldReturnNanIfLocationPriorityIsNull()
	{
		location.setPriority(null);

		final Double loc1Eval = evaluator.evaluate(location);
		Assert.assertThat(Double.NaN, is(loc1Eval));
	}
}
