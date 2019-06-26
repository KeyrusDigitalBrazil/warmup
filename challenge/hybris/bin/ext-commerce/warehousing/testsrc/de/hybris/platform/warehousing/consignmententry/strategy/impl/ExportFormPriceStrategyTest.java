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
package de.hybris.platform.warehousing.consignmententry.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.warehousing.labels.strategy.ExportFormPriceStrategy;
import de.hybris.platform.warehousing.labels.strategy.impl.DefaultExportFormPriceStrategy;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit test for {@link DefaultExportFormPriceStrategy} class
 */
@UnitTest
public class ExportFormPriceStrategyTest
{

	@InjectMocks
	private final ExportFormPriceStrategy defaultExportFormPriceStrategy = new DefaultExportFormPriceStrategy();

	private final Double productPrice = 10d;
	private final Long productQuantity = 2L;
	private final BigDecimal calculatedProductPrice = BigDecimal.valueOf(productPrice);
	private final BigDecimal calculatedTotalPrice = calculatedProductPrice.multiply(BigDecimal.valueOf(productQuantity));

	@Mock
	private ConsignmentEntryModel consignmentEntryWithPrice;

	@Mock
	private ConsignmentEntryModel consignmentEntryWithoutPrice;

	@Mock
	private OrderEntryModel orderEntryWithPrice;

	@Mock
	private OrderEntryModel orderEntryWithoutPrice;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		when(consignmentEntryWithPrice.getOrderEntry()).thenReturn(orderEntryWithPrice);
		when(consignmentEntryWithPrice.getQuantity()).thenReturn(productQuantity);
		when(orderEntryWithPrice.getBasePrice()).thenReturn(productPrice);

		when(consignmentEntryWithoutPrice.getOrderEntry()).thenReturn(orderEntryWithoutPrice);
		when(orderEntryWithoutPrice.getBasePrice()).thenReturn(null);
	}

	/**
	 * Testing behavior of {@link DefaultExportFormPriceStrategy} for null argument
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCalculateProductPrice()
	{
		//When
		defaultExportFormPriceStrategy.calculateProductPrice(null);
	}

	/**
	 * Testing behavior of {@link DefaultExportFormPriceStrategy#calculateProductPrice(ConsignmentEntryModel)} for
	 * {@link ConsignmentEntryModel} with null basePrice
	 */
	@Test
	public void shouldCalculateZeroPrice()
	{
		final BigDecimal basePrice = defaultExportFormPriceStrategy.calculateProductPrice(consignmentEntryWithoutPrice);
		assertEquals(basePrice, BigDecimal.ZERO);
	}

	/**
	 * Testing behavior of {@link DefaultExportFormPriceStrategy#calculateTotalPrice(BigDecimal, ConsignmentEntryModel)}
	 * for null consignmentEntry argument
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCalculateTotalPrice()
	{
		defaultExportFormPriceStrategy.calculateTotalPrice(calculatedProductPrice, null);
	}

	/**
	 * Testing behavior of {@link DefaultExportFormPriceStrategy#calculateProductPrice(ConsignmentEntryModel)} for
	 * {@link ConsignmentEntryModel} with non-null base price
	 */
	@Test
	public void shouldCalculateProductPrice()
	{
		final BigDecimal basePrice = defaultExportFormPriceStrategy.calculateProductPrice(consignmentEntryWithPrice);
		assertEquals(basePrice, calculatedProductPrice);
	}

	/**
	 * Testing behavior of {@link DefaultExportFormPriceStrategy#calculateTotalPrice(BigDecimal, ConsignmentEntryModel)}
	 * for {@link ConsignmentEntryModel} with non-null base price
	 */
	@Test
	public void shouldCalculateTotalPrice()
	{
		final BigDecimal basePrice = defaultExportFormPriceStrategy.calculateTotalPrice(calculatedProductPrice,
				consignmentEntryWithPrice);
		assertEquals(basePrice, calculatedTotalPrice);
	}
}
