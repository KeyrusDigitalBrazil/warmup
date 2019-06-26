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
package de.hybris.platform.warehousing.stock.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.warehousing.model.AllocationEventModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStockLevelSelectionStrategyTest
{
	@InjectMocks
	private DefaultStockLevelSelectionStrategy stockLevelSelectionStrategy;
	@Mock
	private CommerceAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;
	@Mock
	private StockLevelModel stockLevelModel1;
	@Mock
	private StockLevelModel stockLevelModel2;
	@Mock
	private StockLevelModel stockLevelModel3;
	@Mock
	private AllocationEventModel allocationEventModel1;
	@Mock
	private AllocationEventModel allocationEventModel2;
	@Mock
	private AllocationEventModel allocationEventModel3;

	@Before
	public void setUp()
	{
		stockLevelSelectionStrategy.setCommerceStockLevelCalculationStrategy(commerceAvailabilityCalculationStrategy);

		when(allocationEventModel1.getStockLevel()).thenReturn(stockLevelModel1);
		when(allocationEventModel2.getStockLevel()).thenReturn(stockLevelModel2);
		when(allocationEventModel3.getStockLevel()).thenReturn(stockLevelModel3);

		// Set dates for Stocks. Ascending would be 2 -> 1 -> 3
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2000, 1, 1);
		when(stockLevelModel1.getReleaseDate()).thenReturn(calendar.getTime());
		when(stockLevelModel2.getReleaseDate()).thenReturn(null);
		calendar.set(2020, 1, 1);
		when(stockLevelModel3.getReleaseDate()).thenReturn(calendar.getTime());

		when(stockLevelModel1.getAsnEntry()).thenReturn(null);
		when(stockLevelModel2.getAsnEntry()).thenReturn(null);
		when(stockLevelModel3.getAsnEntry()).thenReturn(null);
	}

	@Test
	public void shouldGetStockLevelsForAllocation()
	{
		// Should sort by nulls first, ascending order.
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singletonList(stockLevelModel1)))
				.thenReturn(2L);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singletonList(stockLevelModel2)))
				.thenReturn(3L);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singletonList(stockLevelModel3)))
				.thenReturn(1L);

		final Map<StockLevelModel, Long> allocationMap = stockLevelSelectionStrategy
				.getStockLevelsForAllocation(Arrays.asList(stockLevelModel1, stockLevelModel2, stockLevelModel3), 5L);

		assertEquals(2L, allocationMap.get(stockLevelModel1).longValue());
		assertEquals(3L, allocationMap.get(stockLevelModel2).longValue());
		assertFalse(allocationMap.containsKey(stockLevelModel3));
	}

	@Test
	public void shouldGetStockLevelsForAllocation_NotEnoughStock()
	{
		// Should sort by nulls first, ascending order.
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singletonList(stockLevelModel1)))
				.thenReturn(2L);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singletonList(stockLevelModel2)))
				.thenReturn(3L);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Collections.singletonList(stockLevelModel3)))
				.thenReturn(1L);

		final Map<StockLevelModel, Long> stockLevelMap = stockLevelSelectionStrategy
				.getStockLevelsForAllocation(Arrays.asList(stockLevelModel1, stockLevelModel2, stockLevelModel3), 7L);

		assertEquals(2L, stockLevelMap.get(stockLevelModel1).longValue());
		assertEquals(4L, stockLevelMap.get(stockLevelModel2).longValue()); // Appended 1 to the first stock in the list
		assertEquals(1L, stockLevelMap.get(stockLevelModel3).longValue());
	}

	@Test
	public void shouldGetStockLevelsForCancellation()
	{
		// Should sort by nulls last, reverse order.
		// AllocationEvent1 is Mapped to StockLevel1, 2 to 2, 3 to 3.
		when(allocationEventModel1.getQuantity()).thenReturn(2L);
		when(allocationEventModel2.getQuantity()).thenReturn(10L);
		when(allocationEventModel3.getQuantity()).thenReturn(2L);

		final Map<StockLevelModel, Long> stockLevelMap = stockLevelSelectionStrategy
				.getStockLevelsForCancellation(Arrays.asList(allocationEventModel1, allocationEventModel2, allocationEventModel3),
						4L);

		assertEquals(2L, stockLevelMap.get(stockLevelModel3).longValue());
		assertEquals(2L, stockLevelMap.get(stockLevelModel1).longValue());
		assertFalse(stockLevelMap.containsKey(stockLevelModel2));
	}

	@Test
	public void shouldGetStockLevelsForCancellation_NotEnoughStock()
	{
		// Should sort by nulls last, reverse order.
		// AllocationEvent1 is Mapped to StockLevel1, 2 to 2, 3 to 3.
		when(allocationEventModel1.getQuantity()).thenReturn(2L);
		when(allocationEventModel2.getQuantity()).thenReturn(10L);
		when(allocationEventModel3.getQuantity()).thenReturn(2L);

		final Map<StockLevelModel, Long> stockLevelMap = stockLevelSelectionStrategy
				.getStockLevelsForCancellation(Arrays.asList(allocationEventModel1, allocationEventModel2, allocationEventModel3),
						15L);

		assertEquals(3L, stockLevelMap.get(stockLevelModel3).longValue()); // Appended 1 to the first stock in the list
		assertEquals(2L, stockLevelMap.get(stockLevelModel1).longValue());
		assertEquals(10L, stockLevelMap.get(stockLevelModel2).longValue());
	}
}
