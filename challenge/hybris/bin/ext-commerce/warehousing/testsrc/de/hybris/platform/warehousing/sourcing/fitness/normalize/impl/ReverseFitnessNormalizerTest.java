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
package de.hybris.platform.warehousing.sourcing.fitness.normalize.impl;

import org.junit.Assert;
import org.junit.Test;


public class ReverseFitnessNormalizerTest
{
	private final ReverseFitnessNormalizer normalizer = new ReverseFitnessNormalizer();

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailNormalize_TotalNull()
	{
		normalizer.normalize(5.0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailNormalize_TotalZero()
	{
		normalizer.normalize(5.0, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailNormalize_TotalNegative()
	{
		normalizer.normalize(5.0, -5.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailNormalize_FitnessNull()
	{
		normalizer.normalize(null, 5.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailNormalize_FitnessNegative()
	{
		normalizer.normalize(-5.0, 5.0);
	}

	@Test
	public void shouldNormalize_FitnessZero()
	{
		final Double value = normalizer.normalize(0.0, 10.0);
		Assert.assertEquals(Double.valueOf(100.0), value);
	}

	@Test
	public void shouldNormalize_FitnessNan()
	{
		final Double value = normalizer.normalize(Double.NaN, 10.0);
		Assert.assertEquals(Double.valueOf(0.0), value);
	}

	@Test
	public void shouldNormalize_TotalNan()
	{
		final Double value = normalizer.normalize(10.0, Double.NaN);
		Assert.assertEquals(Double.valueOf(0.0), value);
	}

	@Test
	public void shouldNormalize()
	{
		final Double value = normalizer.normalize(5.0, 20.0);
		Assert.assertEquals(Double.valueOf(75.0), value);
	}
}
