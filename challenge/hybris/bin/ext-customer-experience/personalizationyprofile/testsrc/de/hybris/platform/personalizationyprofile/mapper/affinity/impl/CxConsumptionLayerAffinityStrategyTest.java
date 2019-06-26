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
/**
 *
 */
package de.hybris.platform.personalizationyprofile.mapper.affinity.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationyprofile.mapper.affinity.CxConsumptionLayerAffinityStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class CxConsumptionLayerAffinityStrategyTest
{
	private static final String MESSAGE = "extracted affinit does not match expected";

	@Test
	public void testCurrentAffinityForNull()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerCurrentAffinityStrategy();
		final BigDecimal expected = BigDecimal.ZERO;
		final Affinity affinity = null;

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testCurrentAffinityForEmpty()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerCurrentAffinityStrategy();
		final BigDecimal expected = BigDecimal.ZERO;
		final Affinity affinity = makeAffinity(null, null);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testCurrentAffinity()
	{
		//given

		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerCurrentAffinityStrategy();
		final String value = "785.961";
		final BigDecimal expected = new BigDecimal(value);
		final Affinity affinity = makeAffinity(null, value);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testPreviousAffinityForNull()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerPreviousAffinityStrategy();
		final BigDecimal expected = BigDecimal.ZERO;
		final Affinity affinity = null;

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testPreviousAffinityForEmpty()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerPreviousAffinityStrategy();
		final BigDecimal expected = BigDecimal.ZERO;
		final Affinity affinity = makeAffinity(null, null);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testPreviousAffinity()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerPreviousAffinityStrategy();
		final String value = "785.961";
		final BigDecimal expected = new BigDecimal(value);
		final Affinity affinity = makeAffinity(value, null);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testSumAffinityForNull()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerSumAffinityStrategy();
		final BigDecimal expected = BigDecimal.ZERO;
		final Affinity affinity = null;

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testSumAffinityForEmptyEmpty()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerSumAffinityStrategy();
		final BigDecimal expected = BigDecimal.ZERO;
		final Affinity affinity = makeAffinity(null, null);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testSumAffinityForEmptyValue()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerSumAffinityStrategy();
		final String value = "785.961";
		final BigDecimal expected = new BigDecimal(value);
		final Affinity affinity = makeAffinity(null, value);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testSumAffinityForValueEmpty()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerSumAffinityStrategy();
		final String value = "785.961";
		final BigDecimal expected = new BigDecimal(value);
		final Affinity affinity = makeAffinity(value, null);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	@Test
	public void testSumAffinity()
	{
		//given
		final CxConsumptionLayerAffinityStrategy strategy = new CxConsumptionLayerSumAffinityStrategy();
		final String value = "785.961";
		final BigDecimal expected = new BigDecimal(value).multiply(new BigDecimal(2));
		final Affinity affinity = makeAffinity(value, value);

		//when
		final BigDecimal actual = strategy.extract(affinity);

		//then
		Assert.assertEquals(MESSAGE, expected, actual);
	}

	private Affinity makeAffinity(final String score, final String recentScore)
	{
		final Affinity result = new Affinity();
		if (score != null)
		{
			result.setScore(new BigDecimal(score));
		}
		if (recentScore != null)
		{
			result.setRecentScore(new BigDecimal(recentScore));
		}
		result.setRecentViewCount(null);
		return result;
	}
}
