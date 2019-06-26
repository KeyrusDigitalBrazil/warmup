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
package de.hybris.platform.warehousing.labels.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.labels.strategy.PrintExportFormStrategy;
import de.hybris.platform.warehousing.sourcing.context.impl.FirstPosSelectionStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PrintExportFormStrategyTest
{
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private AddressModel sameAddressModel;
	@Mock
	private CountryModel sameCountryModel;
	@Mock
	private AddressModel differentAddressModel;
	@Mock
	private CountryModel differentCountryModel;
	@Mock
	private FirstPosSelectionStrategy posSelectionStrategy;
	@Mock
	private PointOfServiceModel pointOfServiceModel;
	@Mock
	private WarehouseModel warehouseModel;

	@InjectMocks
	private final PrintExportFormStrategy printExportFormStrategy = new DefaultPrintExportFormStrategy();

	@Before
	public void setup()
	{
		when(consignmentModel.getShippingAddress()).thenReturn(sameAddressModel);
		when(sameAddressModel.getCountry()).thenReturn(sameCountryModel);
		when(sameCountryModel.getIsocode()).thenReturn("US");
		when(consignmentModel.getOrder()).thenReturn(orderModel);
		when(consignmentModel.getWarehouse()).thenReturn(warehouseModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_NullConsignment()
	{
		printExportFormStrategy.canPrintExportForm(null);
	}

	@Test
	public void shouldReturnFalse_PickupOrder()
	{
		//Given
		when(posSelectionStrategy.getPointOfService(orderModel, warehouseModel)).thenReturn(pointOfServiceModel);
		when(consignmentModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);

		//When
		final boolean shouldPrint = printExportFormStrategy.canPrintExportForm(consignmentModel);

		//Then
		assertFalse(shouldPrint);
	}

	@Test
	public void shouldReturnFalse_SameShippingCountry()
	{
		//Given
		when(consignmentModel.getDeliveryPointOfService()).thenReturn(null);
		when(posSelectionStrategy.getPointOfService(orderModel, warehouseModel)).thenReturn(pointOfServiceModel);
		when(pointOfServiceModel.getAddress()).thenReturn(sameAddressModel);

		//When
		final boolean shouldPrint = printExportFormStrategy.canPrintExportForm(consignmentModel);

		//Then
		assertFalse(shouldPrint);
	}

	@Test
	public void shouldReturnTrue_NullPointOfService()
	{
		//Given
		when(consignmentModel.getDeliveryPointOfService()).thenReturn(null);

		//When
		final boolean shouldPrint = printExportFormStrategy.canPrintExportForm(consignmentModel);

		//Then
		assertTrue(shouldPrint);
	}

	@Test
	public void shouldReturnTrue_NoPointOfServiceAssignedToWarehouse()
	{
		//Given
		when(consignmentModel.getDeliveryPointOfService()).thenReturn(null);
		when(posSelectionStrategy.getPointOfService(orderModel, warehouseModel)).thenReturn(null);

		//When
		final boolean shouldPrint = printExportFormStrategy.canPrintExportForm(consignmentModel);

		//Then
		assertTrue(shouldPrint);
	}

	@Test
	public void shouldReturnTrue_DifferentShippingCountry()
	{
		//Given
		when(consignmentModel.getDeliveryPointOfService()).thenReturn(null);
		when(posSelectionStrategy.getPointOfService(orderModel, warehouseModel)).thenReturn(pointOfServiceModel);
		when(pointOfServiceModel.getAddress()).thenReturn(differentAddressModel);
		when(differentAddressModel.getCountry()).thenReturn(differentCountryModel);
		when(differentCountryModel.getIsocode()).thenReturn("CA");

		//When
		final boolean shouldPrint = printExportFormStrategy.canPrintExportForm(consignmentModel);

		//Then
		assertTrue(shouldPrint);
	}
}
