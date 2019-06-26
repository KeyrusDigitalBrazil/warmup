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

import de.hybris.platform.warehousing.data.sourcing.SourcingFactorIdentifiersEnum;
import de.hybris.platform.warehousing.sourcing.fitness.evaluation.FitnessEvaluator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultFitnessEvaluatorFactoryTest
{
	private DefaultFitnessEvaluatorFactory fitnessEvaluatorFactory;

	@Before
	public void setup()
	{
		fitnessEvaluatorFactory = new DefaultFitnessEvaluatorFactory();
		final FitnessEvaluator distanceEvaluator = new DistanceEvaluator();
		final Map<SourcingFactorIdentifiersEnum, FitnessEvaluator> fitnessEvaluatorMap = new HashMap<>();
		fitnessEvaluatorMap.put(SourcingFactorIdentifiersEnum.DISTANCE, distanceEvaluator);
		fitnessEvaluatorFactory.setFitnessEvaluatorMap(fitnessEvaluatorMap);
	}

	@Test
	public void shouldReturnDistanceEvaluator()
	{
		final FitnessEvaluator testEvaluator = fitnessEvaluatorFactory.getEvaluator(SourcingFactorIdentifiersEnum.DISTANCE);
		Assert.assertTrue(testEvaluator instanceof DistanceEvaluator);
	}

	@Test
	public void shouldReturndNullWhenNoEvaluatorFoundForEnum()
	{
		final FitnessEvaluator testEvaluator = fitnessEvaluatorFactory.getEvaluator(SourcingFactorIdentifiersEnum.ALLOCATION);
		Assert.assertTrue(null == testEvaluator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfMapIsEmpty() throws Exception
	{
		fitnessEvaluatorFactory.setFitnessEvaluatorMap(new HashMap<SourcingFactorIdentifiersEnum, FitnessEvaluator>());
		fitnessEvaluatorFactory.afterPropertiesSet();
	}
}
