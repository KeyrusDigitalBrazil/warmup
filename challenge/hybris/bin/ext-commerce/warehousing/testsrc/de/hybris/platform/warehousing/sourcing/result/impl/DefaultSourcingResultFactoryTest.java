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
package de.hybris.platform.warehousing.sourcing.result.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Sets;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSourcingResultFactoryTest
{
	private final DefaultSourcingResultFactory factory = new DefaultSourcingResultFactory();

	@Mock
	private OrderEntryModel orderEntry1;

	@Mock
	private OrderEntryModel orderEntry2;

	@Test
	public void shouldCreateSourcingResult_singleOrderEntry()
	{
		when(orderEntry1.getQuantityUnallocated()).thenReturn(Long.valueOf(0L));


		final SourcingLocation location = new SourcingLocation();
		final WarehouseModel warehouse = new WarehouseModel();
		location.setWarehouse(warehouse);
		final Long quantity = Long.valueOf(10L);

		final SourcingResult result = factory.create(orderEntry1, location, quantity);
		assertTrue(orderEntry1 == result.getAllocation().keySet().iterator().next());
		assertTrue(warehouse == result.getWarehouse());
		assertTrue(quantity == result.getAllocation().values().iterator().next());
	}

	@Test
	public void shouldCreateSourcingResult_MultiOrderEntries()
	{
		final SourcingLocation location = new SourcingLocation();
		final WarehouseModel warehouse = new WarehouseModel();
		location.setWarehouse(warehouse);

		when(orderEntry1.getQuantity()).thenReturn(Long.valueOf(2L));
		when(orderEntry1.getQuantityUnallocated()).thenReturn(Long.valueOf(2L));

		when(orderEntry2.getQuantity()).thenReturn(Long.valueOf(3L));
		when(orderEntry2.getQuantityUnallocated()).thenReturn(Long.valueOf(3L));

		final SourcingResult result = factory.create(Arrays.asList(orderEntry1, orderEntry2), location);
		assertEquals(warehouse, result.getWarehouse());
		assertNotNull(result.getAllocation());
		assertFalse(result.getAllocation().isEmpty());
		assertEquals(2, result.getAllocation().size());
		assertTrue(result.getAllocation().containsKey(orderEntry1));
		assertTrue(2L == result.getAllocation().get(orderEntry1).longValue());
		assertTrue(result.getAllocation().containsKey(orderEntry2));
		assertTrue(3L == result.getAllocation().get(orderEntry2).longValue());
		assertTrue(warehouse == result.getWarehouse());
	}

	@Test
	public void shouldMergeResults()
	{
		final SourcingResults results1 = new SourcingResults();
		final SourcingResult result1 = new SourcingResult();
		final SourcingResult result2 = new SourcingResult();
		final WarehouseModel warehouse = new WarehouseModel();

		final Map<AbstractOrderEntryModel, Long> allocation1 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation1.put(new AbstractOrderEntryModel(), Long.valueOf(5l));
		result1.setAllocation(allocation1);
		result1.setWarehouse(warehouse);

		final Map<AbstractOrderEntryModel, Long> allocation2 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation2.put(new AbstractOrderEntryModel(), Long.valueOf(5l));
		result2.setWarehouse(warehouse);
		result2.setAllocation(allocation2);
		results1.setResults(Sets.newHashSet(result1, result2));

		final SourcingResults results2 = new SourcingResults();
		final SourcingResult result3 = new SourcingResult();
		final SourcingResult result4 = new SourcingResult();
		final Map<AbstractOrderEntryModel, Long> allocation3 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation3.put(new AbstractOrderEntryModel(), Long.valueOf(5l));
		result3.setAllocation(allocation3);
		result3.setWarehouse(warehouse);

		final Map<AbstractOrderEntryModel, Long> allocation4 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation4.put(new AbstractOrderEntryModel(), Long.valueOf(5l));
		result4.setAllocation(allocation4);
		result4.setWarehouse(warehouse);
		results2.setResults(Sets.newHashSet(result3, result4));

		final SourcingResults results = factory.create(Sets.newHashSet(results1, results2));
		assertEquals(4, results.getResults().size());
		assertTrue(results.getResults().contains(result1));
		assertTrue(results.getResults().contains(result2));
		assertTrue(results.getResults().contains(result3));
		assertTrue(results.getResults().contains(result4));
	}

	@Test
	public void shouldMergeComplete_AllComplete()
	{
		final WarehouseModel warehouse = new WarehouseModel();
		final Map<AbstractOrderEntryModel, Long> allocation1 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation1.put(new AbstractOrderEntryModel(), Long.valueOf(5l));

		final SourcingResults results1 = new SourcingResults();
		final SourcingResult result1 = new SourcingResult();
		result1.setWarehouse(warehouse);
		result1.setAllocation(allocation1);

		final SourcingResult result2 = new SourcingResult();
		result2.setWarehouse(warehouse);
		result2.setAllocation(allocation1);
		results1.setResults(Sets.newHashSet(result1, result2));
		results1.setComplete(true);

		final SourcingResults results2 = new SourcingResults();
		final SourcingResult result3 = new SourcingResult();
		result3.setWarehouse(warehouse);
		result3.setAllocation(allocation1);
		final SourcingResult result4 = new SourcingResult();
		result4.setWarehouse(warehouse);
		result4.setAllocation(allocation1);
		results2.setResults(Sets.newHashSet(result3, result4));
		results2.setComplete(true);

		final SourcingResults results = factory.create(Sets.newHashSet(results1, results2));
		assertTrue(results.isComplete());
	}

	@Test
	public void shouldMergeIncomplete_AllIncomplete()
	{
		final WarehouseModel warehouse = new WarehouseModel();
		final Map<AbstractOrderEntryModel, Long> allocation1 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation1.put(new AbstractOrderEntryModel(), Long.valueOf(5l));

		final SourcingResults results1 = new SourcingResults();
		final SourcingResult result1 = new SourcingResult();
		result1.setWarehouse(warehouse);
		result1.setAllocation(allocation1);

		final SourcingResult result2 = new SourcingResult();
		result2.setWarehouse(warehouse);
		result2.setAllocation(allocation1);
		results1.setResults(Sets.newHashSet(result1, result2));
		results1.setComplete(false);


		final SourcingResults results2 = new SourcingResults();
		final SourcingResult result3 = new SourcingResult();
		final SourcingResult result4 = new SourcingResult();
		results2.setResults(Sets.newHashSet(result3, result4));
		results2.setComplete(false);
		result3.setWarehouse(warehouse);
		result3.setAllocation(allocation1);
		result4.setWarehouse(warehouse);
		result4.setAllocation(allocation1);
		final SourcingResults results = factory.create(Sets.newHashSet(results1, results2));
		assertFalse(results.isComplete());
	}

	@Test
	public void shouldMergeIncomplete_PartialComplete()
	{
		final WarehouseModel warehouse = new WarehouseModel();
		final Map<AbstractOrderEntryModel, Long> allocation1 = new HashMap<AbstractOrderEntryModel, Long>();
		allocation1.put(new AbstractOrderEntryModel(), Long.valueOf(5l));

		final SourcingResults results1 = new SourcingResults();
		final SourcingResult result1 = new SourcingResult();
		result1.setWarehouse(warehouse);
		result1.setAllocation(allocation1);
		final SourcingResult result2 = new SourcingResult();
		result2.setWarehouse(warehouse);
		result2.setAllocation(allocation1);

		results1.setResults(Sets.newHashSet(result1, result2));
		results1.setComplete(true);

		final SourcingResults results2 = new SourcingResults();
		final SourcingResult result3 = new SourcingResult();
		result3.setWarehouse(warehouse);
		result3.setAllocation(allocation1);
		final SourcingResult result4 = new SourcingResult();
		result4.setWarehouse(warehouse);
		result4.setAllocation(allocation1);
		results2.setResults(Sets.newHashSet(result3, result4));
		results2.setComplete(false);

		final SourcingResults results = factory.create(Sets.newHashSet(results1, results2));
		assertFalse(results.isComplete());
	}

	@Test
	public void shouldDoNothing_Incomplete()
	{
		final SourcingResults results = factory.create(Sets.newHashSet());
		assertFalse(results.isComplete());
	}
}
