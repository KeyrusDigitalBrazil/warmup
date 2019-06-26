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
package de.hybris.platform.commerceservices.stock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
public class SubscriptionCommerceStockServiceTest
{
	@InjectMocks
	private SubscriptionCommerceStockService commerceStockService = new SubscriptionCommerceStockService();
	@Mock
	private SubscriptionProductService subscriptionProductService;
	@Mock
	private StockService stockService;
	@Mock
	private WarehouseSelectionStrategy warehouseSelectionStrategy;
	@Mock
	private CommerceAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;

	@Before
	public void startup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void storeShouldHaveStatusInStockForSubscriptionProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		final StockLevelStatus status = commerceStockService.getStockLevelStatusForProductAndBaseStore(null, null);
		assertEquals(StockLevelStatus.INSTOCK, status);
	}

	@Test
	public void storeShouldHaveActualStatusForRegularProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.FALSE);
		commerceStockService.getStockLevelStatusForProductAndBaseStore(new ProductModel(), new BaseStoreModel());
		verify(stockService).getProductStatus(any(ProductModel.class), any(Collection.class));
	}

	@Test
	public void storeShouldHaveUnlimitedQuantityForSubscriptionProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		final Long stockLevel = commerceStockService.getStockLevelForProductAndBaseStore(null, null);
		assertNull(stockLevel);
	}

	@Test
	public void storeShouldHaveActualQuantityForRegularProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.FALSE);
		commerceStockService.getStockLevelForProductAndBaseStore(new ProductModel(), new BaseStoreModel());
		verify(stockService).getStockLevels(any(ProductModel.class), any(Collection.class));
	}

	@Test
	public void posShouldHaveStatusInStockForSubscriptionProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		final StockLevelStatus status = commerceStockService.getStockLevelStatusForProductAndPointOfService(null, null);
		assertEquals(StockLevelStatus.INSTOCK, status);
	}

	@Test
	public void posShouldHaveActualStatusForRegularProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.FALSE);
		final PointOfServiceModel pointOfService = new PointOfServiceModel();
		pointOfService.setWarehouses(Collections.singletonList(new WarehouseModel()));
		commerceStockService.getStockLevelStatusForProductAndPointOfService(new ProductModel(), pointOfService);
		verify(stockService).getProductStatus(any(ProductModel.class), any(Collection.class));
	}

	@Test
	public void posShouldHaveUnlimitedQuantityForSubscriptionProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		final Long stockLevel
				= commerceStockService.getStockLevelForProductAndPointOfService(new ProductModel(), new PointOfServiceModel());
		assertNull(stockLevel);
	}

	@Test
	public void posShouldHaveActualQuantityForRegularProducts()
	{
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.FALSE);
		commerceStockService.getStockLevelForProductAndPointOfService(new ProductModel(), new PointOfServiceModel());
		verify(commerceAvailabilityCalculationStrategy).calculateAvailability(any());
	}
}
