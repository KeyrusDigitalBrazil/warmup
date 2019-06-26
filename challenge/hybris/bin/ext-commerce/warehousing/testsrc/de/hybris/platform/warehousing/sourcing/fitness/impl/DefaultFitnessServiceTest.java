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
package de.hybris.platform.warehousing.sourcing.fitness.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.data.sourcing.FitSourcingLocation;
import de.hybris.platform.warehousing.data.sourcing.SourcingContext;
import de.hybris.platform.warehousing.data.sourcing.SourcingFactor;
import de.hybris.platform.warehousing.data.sourcing.SourcingFactorIdentifiersEnum;
import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;
import de.hybris.platform.warehousing.sourcing.factor.SourcingFactorService;
import de.hybris.platform.warehousing.sourcing.fitness.evaluation.FitnessEvaluator;
import de.hybris.platform.warehousing.sourcing.fitness.evaluation.FitnessEvaluatorFactory;
import de.hybris.platform.warehousing.sourcing.fitness.normalize.FitnessNormalizer;
import de.hybris.platform.warehousing.sourcing.fitness.normalize.FitnessNormalizerFactory;
import de.hybris.platform.warehousing.sourcing.util.SourcingLocationBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFitnessServiceTest
{
	private static final Integer ZERO = Integer.valueOf(0);
	private static final Integer FIFTY = Integer.valueOf(50);
	private static final Integer ONE_HUNDRED = Integer.valueOf(100);

	private Set<SourcingFactor> factors;
	private SourcingLocation location1;
	private SourcingLocation location2;
	private SourcingLocation location3;
	private Collection<SourcingLocation> sourcingLocations;
	private Comparator<FitSourcingLocation> fitnessComparator;
	private SourcingContext sourcingContext;

	@Mock
	private SourcingFactorService sourcingFactorService;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private OrderEntryModel orderEntryModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private FitnessEvaluatorFactory fitnessEvaluatorFactory;
	@Mock
	private FitnessNormalizerFactory fitnessNormalizerFactory;
	@Mock
	private FitnessEvaluator evaluator1;
	@Mock
	private FitnessEvaluator evaluator2;
	@Mock
	private FitnessEvaluator evaluator3;
	@Mock
	private FitnessNormalizer fitnessNormalizer;
	@Mock
	private SourcingFactor factor1;
	@Mock
	private SourcingFactor factor2;
	@Mock
	private SourcingFactor factor3;

	@InjectMocks
	private final DefaultFitnessService fitnessService = new DefaultFitnessService();

	@Before
	public void setUp()
	{
		factors = new HashSet<>();

		when(factor1.getFactorId()).thenReturn(SourcingFactorIdentifiersEnum.DISTANCE);
		when(factor1.getWeight()).thenReturn(FIFTY);

		when(factor2.getFactorId()).thenReturn(SourcingFactorIdentifiersEnum.PRIORITY);
		when(factor2.getWeight()).thenReturn(FIFTY);

		when(factor3.getFactorId()).thenReturn(SourcingFactorIdentifiersEnum.SCORE);
		when(factor3.getWeight()).thenReturn(ZERO);

		factors.add(factor1);
		factors.add(factor2);
		factors.add(factor3);

		location1 = SourcingLocationBuilder.aSourcingLocation().withWarehouseCode("loc1").build();
		location2 = SourcingLocationBuilder.aSourcingLocation().withWarehouseCode("loc2").build();
		location3 = SourcingLocationBuilder.aSourcingLocation().withWarehouseCode("loc3").build();

		sourcingLocations = new ArrayList<>();
		sourcingLocations.add(location1);
		sourcingLocations.add(location2);
		sourcingLocations.add(location3);

		sourcingContext = new SourcingContext();
		sourcingContext.setSourcingLocations(sourcingLocations);
		sourcingContext.setOrderEntries(Arrays.asList(orderEntryModel));

		when(orderEntryModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getStore()).thenReturn(baseStore);

		fitnessComparator = new FitnessComparator();
		fitnessService.setFitnessComparator(fitnessComparator);

		when(sourcingFactorService.getAllSourcingFactorsForBaseStore(baseStore)).thenReturn(factors);

		when(fitnessEvaluatorFactory.getEvaluator(SourcingFactorIdentifiersEnum.DISTANCE)).thenReturn(evaluator1);
		when(fitnessEvaluatorFactory.getEvaluator(SourcingFactorIdentifiersEnum.PRIORITY)).thenReturn(evaluator2);
		when(fitnessEvaluatorFactory.getEvaluator(SourcingFactorIdentifiersEnum.SCORE)).thenReturn(evaluator3);
		when(fitnessNormalizerFactory.getNormalizer(SourcingFactorIdentifiersEnum.DISTANCE)).thenReturn(fitnessNormalizer);
		when(fitnessNormalizerFactory.getNormalizer(SourcingFactorIdentifiersEnum.PRIORITY)).thenReturn(fitnessNormalizer);
		when(fitnessNormalizerFactory.getNormalizer(SourcingFactorIdentifiersEnum.SCORE)).thenReturn(fitnessNormalizer);

		when(evaluator1.evaluate(location1)).thenReturn(100d);
		when(evaluator1.evaluate(location2)).thenReturn(300d);
		when(evaluator1.evaluate(location3)).thenReturn(50d);

		when(evaluator2.evaluate(location1)).thenReturn(3d);
		when(evaluator2.evaluate(location2)).thenReturn(1d);
		when(evaluator2.evaluate(location3)).thenReturn(2d);

		when(evaluator3.evaluate(location1)).thenReturn(5d);
		when(evaluator3.evaluate(location2)).thenReturn(10d);
		when(evaluator3.evaluate(location3)).thenReturn(1d);

		when(fitnessNormalizer.normalize(100d, 450d)).thenReturn(100d / 450d * 100d); // 22.22
		when(fitnessNormalizer.normalize(300d, 450d)).thenReturn(300d / 450d * 100d); // 66.66
		when(fitnessNormalizer.normalize(50d, 450d)).thenReturn(50d / 450d * 100d); // 11.11

		when(fitnessNormalizer.normalize(3d, 6d)).thenReturn(3d / 6d * 100d); // 50.00
		when(fitnessNormalizer.normalize(1d, 6d)).thenReturn(1d / 6d * 100d); // 16.66
		when(fitnessNormalizer.normalize(2d, 6d)).thenReturn(2d / 6d * 100d); // 33.33

		when(fitnessNormalizer.normalize(5d, 16d)).thenReturn(5d / 16d * 100d); // 31.25
		when(fitnessNormalizer.normalize(10d, 16d)).thenReturn(10d / 16d * 100d); // 62.5
		when(fitnessNormalizer.normalize(1d, 16d)).thenReturn(1d / 16d * 100d);	// 6.25
	}

	@Test
	public void shouldCalculateFitness_SingleFactor_100Percent()
	{
		when(factor1.getWeight()).thenReturn(ONE_HUNDRED);
		when(factor2.getWeight()).thenReturn(ZERO);

		final FitSourcingLocation[] result = fitnessService.calculateFitness(sourcingContext);

		Assert.assertEquals("loc1", result[0].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(22.22222222222222d), result[0].getFitness());
		Assert.assertEquals("loc2", result[1].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(66.66666666666666d), result[1].getFitness());
		Assert.assertEquals("loc3", result[2].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(11.11111111111111d), result[2].getFitness());
	}

	@Test
	public void shouldCalculateFitnessWithHigherScore() {
		when(factor1.getWeight()).thenReturn(ZERO);
		when(factor2.getWeight()).thenReturn(ZERO);
		when(factor3.getWeight()).thenReturn(ONE_HUNDRED);

		final List<SourcingLocation> result = fitnessService.sortByFitness(sourcingContext);

		Assert.assertEquals("loc2", result.get(0).getWarehouse().getCode());
		Assert.assertEquals("loc1", result.get(1).getWarehouse().getCode());
		Assert.assertEquals("loc3", result.get(2).getWarehouse().getCode());
	}

	@Test
	public void shouldNotCalculateFitness_SingleFactor_100Percent()
	{
		sourcingContext.setOrderEntries(Arrays.asList(orderEntryModel));
		when(factor1.getWeight()).thenReturn(ONE_HUNDRED);
		when(factor2.getWeight()).thenReturn(ZERO);

		final FitSourcingLocation[] result = fitnessService.calculateFitness(sourcingContext);

		Assert.assertEquals("loc1", result[0].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(22.22222222222222d), result[0].getFitness());
		Assert.assertEquals("loc2", result[1].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(66.66666666666666d), result[1].getFitness());
		Assert.assertEquals("loc3", result[2].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(11.11111111111111d), result[2].getFitness());
	}

	@Test
	public void shouldCalculateFitness_SingleFactor_50Percent()
	{
		when(factor1.getWeight()).thenReturn(FIFTY);
		when(factor2.getWeight()).thenReturn(ZERO);

		final FitSourcingLocation[] result = fitnessService.calculateFitness(sourcingContext);

		Assert.assertEquals("loc1", result[0].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(11.11111111111111d), result[0].getFitness());
		Assert.assertEquals("loc2", result[1].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(33.33333333333333d), result[1].getFitness());
		Assert.assertEquals("loc3", result[2].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(5.555555555555555d), result[2].getFitness());
	}

	@Test
	public void shouldCalculateFitness_MultipleFactors_0Percent()
	{
		when(factor1.getWeight()).thenReturn(ZERO);
		when(factor2.getWeight()).thenReturn(ZERO);

		final FitSourcingLocation[] result = fitnessService.calculateFitness(sourcingContext);

		Assert.assertEquals("loc1", result[0].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(0d), result[0].getFitness());
		Assert.assertEquals("loc2", result[1].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(0d), result[1].getFitness());
		Assert.assertEquals("loc3", result[2].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(0d), result[2].getFitness());
	}

	@Test
	public void shouldCalculateFitness_MultipleFactors_50Percent()
	{
		when(factor1.getWeight()).thenReturn(FIFTY);
		when(factor2.getWeight()).thenReturn(FIFTY);

		final FitSourcingLocation[] result = fitnessService.calculateFitness(sourcingContext);

		Assert.assertEquals("loc1", result[0].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(36.111111111111114d), result[0].getFitness());
		Assert.assertEquals("loc2", result[1].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(41.66666666666666d), result[1].getFitness());
		Assert.assertEquals("loc3", result[2].getWarehouse().getCode());
		Assert.assertEquals(Double.valueOf(22.22222222222222d), result[2].getFitness());
	}

	@Test
	public void shouldSortResultsByFittest()
	{
		when(factor1.getWeight()).thenReturn(ONE_HUNDRED);
		when(factor2.getWeight()).thenReturn(ZERO);

		final List<SourcingLocation> result = fitnessService.sortByFitness(sourcingContext);
		Assert.assertEquals("loc2", result.get(0).getWarehouse().getCode());
		Assert.assertEquals("loc1", result.get(1).getWarehouse().getCode());
		Assert.assertEquals("loc3", result.get(2).getWarehouse().getCode());
	}
}
