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
package de.hybris.platform.warehousing.atp.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;




@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousingStockLevelStatusStrategyTest
{
	@InjectMocks
	private WarehousingStockLevelStatusStrategy warehousingStockLevelStatusStrategy;

	@Mock
	private CommerceAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;
	@Mock
	private StockLevelModel forcedInStockStatusLevel;
	@Mock
	private StockLevelModel forcedOutOfStockStatusLevel;
	@Mock
	private StockLevelModel noStockStatusLevel;

	private Collection<StockLevelModel> stockLevels;

	@Before
	public void setUp()
	{
		stockLevels = Sets.newHashSet(forcedInStockStatusLevel, forcedOutOfStockStatusLevel, noStockStatusLevel);

		when(forcedInStockStatusLevel.getInStockStatus()).thenReturn(InStockStatus.FORCEINSTOCK);
		when(forcedOutOfStockStatusLevel.getInStockStatus()).thenReturn(InStockStatus.FORCEOUTOFSTOCK);
		when(noStockStatusLevel.getInStockStatus()).thenReturn(null);
	}

	@Test
	public void shouldReturnInStockStatusWhenStockLevelIsForceInStock()
	{
		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy
				.checkStatus(Collections.singleton(forcedInStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.INSTOCK, result);
	}

	@Test
	public void shouldReturnInStockStatusWhenMixOfAllStockLevelStatus()
	{
		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy.checkStatus(stockLevels);

		//Then
		assertEquals(StockLevelStatus.INSTOCK, result);
	}

	@Test
	public void shouldReturnInStockStatusWhenForceOutOfStockAndNoStockLevelStatus()
	{
		//Given
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singleton(noStockStatusLevel)))
				.thenReturn(10L);

		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy
				.checkStatus(Sets.newHashSet(forcedOutOfStockStatusLevel, noStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.INSTOCK, result);
	}

	@Test
	public void shouldReturnInstockWhenAvailabilityIsBiggerThanDefaultLowStockThreshold()
	{
		//Given
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singleton(noStockStatusLevel)))
				.thenReturn(10L);

		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy.checkStatus(Collections.singleton(noStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.INSTOCK, result);
	}

	@Test
	public void shouldReturnLowStockWhenAvailabilityIsBiggerThan0AndLessOrEqualDefaultLowStockThreshold()
	{
		//Given
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singleton(noStockStatusLevel)))
				.thenReturn(2L);

		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy.checkStatus(Collections.singleton(noStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.LOWSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenStockLevelIsForceOutOfStock()
	{
		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy
				.checkStatus(Collections.singleton(forcedOutOfStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityIsLessThan0()
	{
		//Given
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singleton(noStockStatusLevel)))
				.thenReturn(-1L);

		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy.checkStatus(Collections.singleton(noStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityIsEqual0()
	{
		//Given
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singleton(noStockStatusLevel)))
				.thenReturn(0L);

		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy.checkStatus(Collections.singleton(noStockStatusLevel));

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenNoStockLevelPassed()
	{
		//When
		final StockLevelStatus result = warehousingStockLevelStatusStrategy.checkStatus(Collections.EMPTY_SET);

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}
}
