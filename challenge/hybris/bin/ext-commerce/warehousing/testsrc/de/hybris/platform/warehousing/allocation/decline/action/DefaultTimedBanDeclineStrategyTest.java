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
package de.hybris.platform.warehousing.allocation.decline.action;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.allocation.decline.action.impl.DefaultTimedBanDeclineStrategy;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.sourcing.ban.service.SourcingBanService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTimedBanDeclineStrategyTest
{
	@Mock
	private SourcingBanService sourcingBanService;
	@InjectMocks
	private ConsignmentEntryModel consignmentEntryModel;
	@InjectMocks
	private ConsignmentModel consignmentModel;
	@InjectMocks
	private DefaultTimedBanDeclineStrategy defaultDeclineActionBanStrategy;

	private WarehouseModel warehouseToBan;
	private DeclineEntry declineEntry;


	@Before
	public void setUp()
	{
		warehouseToBan = new WarehouseModel();
		declineEntry = new DeclineEntry();
		declineEntry.setConsignmentEntry(consignmentEntryModel);
		consignmentEntryModel.setConsignment(consignmentModel);
		consignmentModel.setWarehouse(warehouseToBan);
	}

	@Test
	public void shouldExecute()
	{
		// when
		defaultDeclineActionBanStrategy.execute(declineEntry);

		// Then
		verify(sourcingBanService, times(1)).createSourcingBan(warehouseToBan);
	}

	@Test
	public void shouldExecuteEntries()
	{
		//Given
		final DeclineEntry autoDeclineEntry = new DeclineEntry();
		autoDeclineEntry.setConsignmentEntry(consignmentEntryModel);

		// when
		defaultDeclineActionBanStrategy.execute(Arrays.asList(declineEntry, autoDeclineEntry));

		// Then
		verify(sourcingBanService, times(1)).createSourcingBan(warehouseToBan);
	}
}
