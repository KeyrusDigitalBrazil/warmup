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

package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.model.RestockConfigModel;
import de.hybris.platform.warehousing.returns.RestockException;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;
import de.hybris.platform.warehousing.returns.strategy.RestockWarehouseSelectionStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class InitialReturnActionTest
{
	@InjectMocks
	private InitialReturnAction action;

	@Mock
	private RestockConfigService restockConfigService;

	@Mock
	private ModelService modelService;

	@Mock
	private RestockWarehouseSelectionStrategy restockWarehouseSelectionStrategy;

	@Mock
	private ReturnProcessModel returnProcessModel;

	@Mock
	private ReturnRequestModel returnRequest;

	@Mock
	private WarehouseModel returnWarehouse;

	@Mock
	private ReturnEntryModel returnEntry;

	@Mock
	private RestockConfigModel restockConfig;

	@Before
	public void setup() throws RestockException
	{
		List<ReturnEntryModel> returnEntries = new ArrayList<>();
		returnEntries.add(returnEntry);

		when(returnProcessModel.getReturnRequest()).thenReturn(returnRequest);
		when(returnRequest.getReturnEntries()).thenReturn(returnEntries);
		when(restockConfigService.getRestockConfig()).thenReturn(restockConfig);
		when(restockConfig.getIsUpdateStockAfterReturn()).thenReturn(Boolean.TRUE);
		when(restockWarehouseSelectionStrategy.performStrategy(returnRequest)).thenReturn(returnWarehouse);

		doNothing().when(modelService).save(any());
	}

	@Test
	public void shouldRedirectToPayment() throws Exception
	{
		when(returnEntry.getAction()).thenReturn(ReturnAction.IMMEDIATE);
		String transition = action.execute(returnProcessModel);
		assertEquals(InitialReturnAction.Transition.INSTORE.toString(), transition);
	}

	@Test
	public void shouldRedirectToApproval() throws Exception
	{
		when(returnEntry.getAction()).thenReturn(ReturnAction.HOLD);
		String transition = action.execute(returnProcessModel);
		assertEquals(InitialReturnAction.Transition.ONLINE.toString(), transition);
		verify(returnRequest).setReturnWarehouse(returnWarehouse);
	}
}
