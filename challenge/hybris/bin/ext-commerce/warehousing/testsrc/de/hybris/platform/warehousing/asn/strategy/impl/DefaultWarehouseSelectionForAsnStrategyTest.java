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
package de.hybris.platform.warehousing.asn.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.asn.strategy.WarehouseSelectionForAsnStrategy;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehouseSelectionForAsnStrategyTest
{
	@InjectMocks
	private DefaultWarehouseSelectionForAsnStrategy defaultWarehouseSelectionForAsnStrategy;
	@Mock
	private AdvancedShippingNoticeModel advancedShippingNotice;
	@Mock
	private PointOfServiceModel montrealPoS;
	@Mock
	private WarehouseModel montrealWarehouse;
	@Mock
	private WarehouseModel lavalWarehouse;

	@Before
	public void setup()
	{
		when(advancedShippingNotice.getPointOfService()).thenReturn(montrealPoS);
		when(montrealPoS.getWarehouses()).thenReturn(Arrays.asList(montrealWarehouse, lavalWarehouse));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNullPoS()
	{
		//When
		defaultWarehouseSelectionForAsnStrategy.getDefaultWarehouse(null);
	}

	@Test
	public void shouldReturnNullWhenNoWarehouseAssignedToPoS()
	{
		//Given
		when(montrealPoS.getWarehouses()).thenReturn(new ArrayList<>());

		//When
		final WarehouseModel warehouse = defaultWarehouseSelectionForAsnStrategy.getDefaultWarehouse(advancedShippingNotice);

		//Then
		assertNull(warehouse);
	}

	@Test
	public void shouldReturnWarehouse()
	{
		//When
		final WarehouseModel warehouse = defaultWarehouseSelectionForAsnStrategy.getDefaultWarehouse(advancedShippingNotice);

		//Then
		Assert.assertEquals(montrealWarehouse, warehouse);
	}
}
