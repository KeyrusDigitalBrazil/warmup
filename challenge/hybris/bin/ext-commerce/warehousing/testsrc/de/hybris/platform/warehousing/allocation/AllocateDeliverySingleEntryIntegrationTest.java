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
package de.hybris.platform.warehousing.allocation;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.allocation.util.DeclineEntryBuilder;
import de.hybris.platform.warehousing.data.allocation.DeclineEntries;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.model.AllocationEventModel;
import de.hybris.platform.warehousing.sourcing.util.SourcingResultBuilder;
import de.hybris.platform.warehousing.util.BaseWarehousingIntegrationTest;
import de.hybris.platform.warehousing.util.models.*;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@IntegrationTest
public class AllocateDeliverySingleEntryIntegrationTest extends BaseWarehousingIntegrationTest
{
	private static final Long MONTREAL_CAMERA_QTY = Long.valueOf(3);
	private static final Long BOSTON_CAMERA_QTY = Long.valueOf(4);
	private static final Long REALLOCATE_MONTREAL_CAMERA_QTY = Long.valueOf(2);

	@Resource
	private AllocationService allocationService;
	@Resource
	private Orders orders;
	@Resource
	private Warehouses warehouses;
	@Resource
	private PointsOfService pointsOfService;
	@Resource
	private StockLevels stockLevels;
	@Resource
	private AllocationEvents allocationEvents;

	private OrderModel order;
	private SourcingResults sourcingResults;

	@Test
	public void shouldAllocate_From1Pos1Warehouse()
	{
		order = orders.Camera_Shipped(MONTREAL_CAMERA_QTY);
		final WarehouseModel warehouse = warehouses.Montreal();
		warehouse.setPointsOfService(Arrays.asList(pointsOfService.Montreal_Downtown()));
		stockLevels.Camera(warehouse, MONTREAL_CAMERA_QTY.intValue());

		final SourcingResult sourcingResult = SourcingResultBuilder.aResult() //
				.withAllocation(order.getEntries().get(0), MONTREAL_CAMERA_QTY) //
				.withWarehouse(warehouse) //
				.build();
		sourcingResults = new SourcingResults();
		sourcingResults.setResults(Sets.newHashSet(sourcingResult));
		sourcingResults.setComplete(true);

		final Collection<ConsignmentModel> consignments = allocationService.createConsignments(order, "con", sourcingResults);

		assertEquals(1, consignments.size());
		assertQuantityAllocated(MONTREAL_CAMERA_QTY, MONTREAL_CAMERA_QTY, consignments.iterator().next());
	}

	@Test
	public void shouldAllocate_From2Pos2Warehouse()
	{
		order = orders.Camera_Shipped(Long.valueOf(MONTREAL_CAMERA_QTY.longValue() + BOSTON_CAMERA_QTY.longValue()));
		final WarehouseModel montrealWarehouse = warehouses.Montreal();
		final WarehouseModel bostonWarehouse = warehouses.Boston();
		montrealWarehouse.setPointsOfService(Arrays.asList(pointsOfService.Montreal_Downtown()));
		bostonWarehouse.setPointsOfService(Arrays.asList(pointsOfService.Boston()));
		stockLevels.Camera(montrealWarehouse, MONTREAL_CAMERA_QTY.intValue());
		stockLevels.Camera(bostonWarehouse, BOSTON_CAMERA_QTY.intValue());

		final SourcingResult sourcingResult1 = SourcingResultBuilder.aResult() //
				.withAllocation(order.getEntries().get(0), MONTREAL_CAMERA_QTY) //
				.withWarehouse(montrealWarehouse) //
				.build();

		final SourcingResult sourcingResult2 = SourcingResultBuilder.aResult() //
				.withAllocation(order.getEntries().get(0), BOSTON_CAMERA_QTY) //
				.withWarehouse(bostonWarehouse) //
				.build();

		sourcingResults = new SourcingResults();
		sourcingResults.setResults(Sets.newHashSet(sourcingResult1, sourcingResult2));
		sourcingResults.setComplete(true);

		final Collection<ConsignmentModel> consignments = allocationService.createConsignments(order, "con", sourcingResults);
		final Iterator<ConsignmentModel> iterator = consignments.iterator();

		assertEquals(2, consignments.size());

		do
		{
			final ConsignmentModel consignment = iterator.next();

			if (consignment.getWarehouse().getCode() == montrealWarehouse.getCode())
			{
				assertQuantityAllocated(MONTREAL_CAMERA_QTY, Long.valueOf(MONTREAL_CAMERA_QTY.longValue() + BOSTON_CAMERA_QTY.longValue()), consignment);
			}
			else
			{
				assertQuantityAllocated(BOSTON_CAMERA_QTY, Long.valueOf(MONTREAL_CAMERA_QTY.longValue() + BOSTON_CAMERA_QTY.longValue()), consignment);
			}
		}
		while (iterator.hasNext());
	}

	@Test
	public void shouldReallocateConsignment_From1Pos1Warehouse()
	{
		final WarehouseModel warehouseBoston = warehouses.Boston();
		warehouses.Boston().setPointsOfService(Arrays.asList(pointsOfService.Boston()));
		stockLevels.Camera(warehouseBoston, MONTREAL_CAMERA_QTY.intValue());
		final StockLevelModel stockLevel = stockLevels.Camera(warehouses.Montreal(), MONTREAL_CAMERA_QTY.intValue());
		final AllocationEventModel allocationEvent = allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(MONTREAL_CAMERA_QTY,
				stockLevel);

		final DeclineEntries declineEntries = new DeclineEntries();
		final DeclineEntry declineEntry = DeclineEntryBuilder.anEntry() //
				.withConsignmentEntry(allocationEvent.getConsignmentEntry()) //
				.withQuantity(REALLOCATE_MONTREAL_CAMERA_QTY) //
				.withReason(DeclineReason.DAMAGED) //
				.withReallocationWarehouse(warehouses.Boston()) //
				.build();

		declineEntries.setEntries(Arrays.asList(declineEntry));

		final Collection<ConsignmentModel> reallocatedConsignments = allocationService.manualReallocate(declineEntries);

		assertNotNull(reallocatedConsignments);
		assertEquals(warehouseBoston.getCode(), reallocatedConsignments.iterator().next().getWarehouse().getCode());
		assertEquals(REALLOCATE_MONTREAL_CAMERA_QTY, reallocatedConsignments.iterator().next().getConsignmentEntries().iterator().next().getQuantity());
		assertEquals(REALLOCATE_MONTREAL_CAMERA_QTY, allocationEvent.getConsignmentEntry().getQuantityDeclined());
	}

	private void assertQuantityAllocated(final Long quantityExpected, final Long totalQuantityExpected,
			final ConsignmentModel consignment)
	{
		assertEquals(ConsignmentStatus.READY, consignment.getStatus());
		assertEquals(quantityExpected, consignment.getConsignmentEntries().iterator().next().getQuantity());
		OrderEntryModel orderEntryModel = (OrderEntryModel) order.getEntries().get(0);
		assertEquals(totalQuantityExpected, orderEntryModel.getQuantityAllocated());
	}

}
