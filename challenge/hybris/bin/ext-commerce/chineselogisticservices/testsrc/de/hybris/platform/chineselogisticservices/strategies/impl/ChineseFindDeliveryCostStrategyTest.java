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
package de.hybris.platform.chineselogisticservices.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chineselogisticservices.delivery.impl.ChineseDeliveryService;
import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.PriceValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseFindDeliveryCostStrategyTest
{

	private static final String CURRENCY_ISO_CODE = "zh";

	private ChineseFindDeliveryCostStrategy strategy;

	@Mock
	private ChineseDeliveryModeLookupStrategy chineseDeliveryModeLookupStrategy;
	@Mock
	private ChineseDeliveryService chineseDeliveryService;
	@Mock
	private ModelService modelService;

	private AbstractOrderModel order;
	private BaseStoreModel store;
	private DeliveryModeModel deliveryMode;
	private DeliveryModeModel deliveryModeModel;
	private PriceValue priceValue;
	private CurrencyModel currency;
	private PickupInStoreMode pickupInStoreMode;

	private List<DeliveryModeModel> deliveryModes;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new ChineseFindDeliveryCostStrategy();
		strategy.setChineseDeliveryModeLookupStrategy(chineseDeliveryModeLookupStrategy);
		strategy.setChineseDeliveryService(chineseDeliveryService);
		strategy.setModelService(modelService);

		deliveryMode = new DeliveryModeModel();
		deliveryModeModel = new DeliveryModeModel();
		pickupInStoreMode = PickupInStoreMode.BUY_AND_COLLECT;
		store = new BaseStoreModel();
		store.setPickupInStoreMode(pickupInStoreMode);
		currency = new CurrencyModel();
		currency.setIsocode(CURRENCY_ISO_CODE);
		order = new OrderModel();
		order.setStore(store);
		order.setDeliveryMode(deliveryModeModel);
		order.setCurrency(currency);
		order.setNet(Boolean.TRUE);
		priceValue = new PriceValue(CURRENCY_ISO_CODE, 1.0, true);

		deliveryModes = new ArrayList<>();

		doNothing().when(modelService).save(order);
		given(chineseDeliveryModeLookupStrategy.getSelectableDeliveryModesForOrder(order)).willReturn(deliveryModes);
	}

	@Test
	public void testGetDeliveryCost()
	{
		deliveryModes.add(deliveryMode);

		given(chineseDeliveryService.getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryModeModel, order)).willReturn(
				priceValue);

		final PriceValue result = strategy.getDeliveryCost(order);
		Assert.assertEquals(priceValue, result);
	}

	@Test
	public void testGetDeliveryCostWithNullDeliveryModes()
	{
		final PriceValue result = strategy.getDeliveryCost(order);
		Assert.assertEquals(CURRENCY_ISO_CODE, result.getCurrencyIso());
		Assert.assertEquals(0.0, result.getValue(), 0.01);
		Assert.assertTrue(result.isNet());
	}
}
