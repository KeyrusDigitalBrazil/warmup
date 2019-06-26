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
package de.hybris.platform.warehousing.sourcing.factor.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingFactor;
import de.hybris.platform.warehousing.data.sourcing.SourcingFactorIdentifiersEnum;
import de.hybris.platform.warehousing.model.SourcingConfigModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSourcingFactorServiceTest
{
	private static final int WEIGHT_ALLOCATION = 50;
	private static final int WEIGHT_DISTANCE = 20;
	private static final int WEIGHT_PRIORITY = 20;
	private static final int WEIGHT_SCORE = 10;
	private static final int WEIGHT_TOO_LARGE = 30;

	@Spy
	private final DefaultSourcingFactorService sourcingFactorService = new DefaultSourcingFactorService();
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private SourcingConfigModel sourcingConfig;

	@Before
	public void setUp() throws InvocationTargetException, IllegalAccessException
	{
		when(baseStore.getSourcingConfig()).thenReturn(sourcingConfig);
		when(sourcingConfig.getAllocationWeightFactor()).thenReturn(WEIGHT_ALLOCATION);
		when(sourcingConfig.getDistanceWeightFactor()).thenReturn(WEIGHT_DISTANCE);
		when(sourcingConfig.getPriorityWeightFactor()).thenReturn(WEIGHT_PRIORITY);
		when(sourcingConfig.getScoreWeightFactor()).thenReturn(WEIGHT_SCORE);

		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.ALLOCATION, sourcingConfig))
				.thenReturn(WEIGHT_ALLOCATION);
		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.DISTANCE, sourcingConfig))
				.thenReturn(WEIGHT_DISTANCE);
		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.PRIORITY, sourcingConfig))
				.thenReturn(WEIGHT_PRIORITY);
		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.SCORE, sourcingConfig))
				.thenReturn(WEIGHT_SCORE);

	}

	@Test
	public void shouldGetSourcingFactor()
	{
		//When
		final SourcingFactor sourcingFactor = sourcingFactorService
				.getSourcingFactor(SourcingFactorIdentifiersEnum.ALLOCATION, baseStore);

		//Then
		verify(sourcingFactorService).getSourcingFactorsMapForBaseStore(baseStore);
		assertEquals(SourcingFactorIdentifiersEnum.ALLOCATION, sourcingFactor.getFactorId());
		assertEquals(WEIGHT_ALLOCATION, sourcingFactor.getWeight());
	}

	@Test
	public void shouldGetAllFactorsForBaseStore()
	{
		//When
		final Set<SourcingFactor> sourcingFactors = sourcingFactorService.getAllSourcingFactorsForBaseStore(baseStore);

		//Then
		verify(sourcingFactorService).getSourcingFactorsMapForBaseStore(baseStore);
		assertEquals(4, sourcingFactors.size());
	}


	@Test
	public void shouldGetSourcingFactorsMapForBaseStore()
	{
		//When
		final Map<SourcingFactorIdentifiersEnum, SourcingFactor> sourcingFactorsMapForBaseStore = sourcingFactorService
				.getSourcingFactorsMapForBaseStore(baseStore);

		//Then
		assertEquals(4, sourcingFactorsMapForBaseStore.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailSourcingFactorsMapForBaseStore_FactorWeightGreaterThan100() throws Exception
	{
		//Given
		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.DISTANCE, sourcingConfig))
				.thenReturn(WEIGHT_TOO_LARGE);

		//When
		final Map<SourcingFactorIdentifiersEnum, SourcingFactor> sourcingFactorsMapForBaseStore = sourcingFactorService
				.getSourcingFactorsMapForBaseStore(baseStore);
	}

	@Test
	public void shouldCreateSourcingFactorFromSourcingConfig()
	{
		//When
		final SourcingFactor sourcingFactor = sourcingFactorService
				.createSourcingFactorFromSourcingConfig(SourcingFactorIdentifiersEnum.ALLOCATION, sourcingConfig);

		//Then
		assertEquals(SourcingFactorIdentifiersEnum.ALLOCATION, sourcingFactor.getFactorId());
		assertEquals(WEIGHT_ALLOCATION, sourcingFactor.getWeight());
	}

	@Test
	public void shouldLoadFactorValue()
	{
		//When
		final int weight = sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.ALLOCATION, sourcingConfig);

		//Then
		verify(sourcingConfig).getAllocationWeightFactor();
		assertEquals(WEIGHT_ALLOCATION, weight);
	}

	@Test
	public void shouldLoadFactorValue_Fail_OverHundred()
	{
		//When
		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.DISTANCE, sourcingConfig))
				.thenReturn(WEIGHT_TOO_LARGE);

		try
		{
			final SourcingFactor sourcingFactor = sourcingFactorService
					.getSourcingFactor(SourcingFactorIdentifiersEnum.DISTANCE, baseStore);
		}
		catch (IllegalArgumentException e) //NOSONAR
		{
			//Then
			assertEquals("Factor weights are percentages, therefore the sum of the factor weights should equal 100.", e.getMessage());
		}
	}

	@Test
	public void shouldLoadFactorValue_Fail_Negative()
	{
		//When
		when(sourcingFactorService.loadFactorValue(SourcingFactorIdentifiersEnum.ALLOCATION, sourcingConfig))
				.thenReturn(-10);

		try
		{
			final SourcingFactor sourcingFactor = sourcingFactorService
					.getSourcingFactor(SourcingFactorIdentifiersEnum.ALLOCATION, baseStore);
		}
		catch (IllegalArgumentException e) //NOSONAR
		{
			//Then
			assertEquals("Negative weight has been found in sourcing factor, please reset to positive.", e.getMessage());
		}
	}

}
