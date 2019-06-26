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
package de.hybris.platform.timedaccesspromotionengineservices.order.hooks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultFlashBuyCommercePlaceOrderMethodHookTest}
 */
@UnitTest
public class DefaultFlashBuyCommercePlaceOrderMethodHookTest
{

	private DefaultFlashBuyCommercePlaceOrderMethodHook flashBuyCommercePlaceOrderMethodHook;
	private CommerceCheckoutParameter commerceCheckoutParameter;
	private CommerceOrderResult commerceOrderResult;
	private OrderModel order;
	private PromotionSourceRuleModel promotionSourceRule;
	private List<PromotionSourceRuleModel> promotionSourceRules;
	private FlashBuyCouponModel flashBuyCoupon;

	private static final String couponCode = "c1";
	private static final String productCode = "p1";
	private static final String productCode2 = "p1";
	private static final String promotionCode = "promotionSourceRule1";
	private static final String promotionCode2 = "promotionSourceRule2";

	@Mock
	FlashBuyService flashBuyService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		order = new OrderModel();
		order.setCode("testOrder");
		final ProductModel product1 = new ProductModel();
		product1.setCode(productCode);

		final OrderEntryModel orderEntry1 = new OrderEntryModel();
		orderEntry1.setProduct(product1);
		final List<AbstractOrderEntryModel> orderEntrys = new ArrayList<>();
		orderEntrys.add(orderEntry1);
		order.setEntries(orderEntrys);
		commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceOrderResult = new CommerceOrderResult();
		commerceOrderResult.setOrder(order);
		promotionSourceRule = new PromotionSourceRuleModel();
		promotionSourceRule.setCode(promotionCode);
		flashBuyCommercePlaceOrderMethodHook = Mockito.spy(new DefaultFlashBuyCommercePlaceOrderMethodHook());
		flashBuyCommercePlaceOrderMethodHook.setFlashBuyService(flashBuyService);
		promotionSourceRules = new ArrayList<>();
		flashBuyCoupon = new FlashBuyCouponModel();
		flashBuyCoupon.setCouponId(couponCode);
	}

	@Test
	public void testAfterPlaceOrder() throws InvalidCartException
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = new ArrayList<>();
		promotionSourceRules.add(promotionSourceRule);
		final Optional<FlashBuyCouponModel> flashBuyCouponOptional = Optional.of(flashBuyCoupon);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(Mockito.any())).thenReturn(flashBuyCouponOptional);
		Mockito.doReturn(promotionSourceRules).when(flashBuyService).getPromotionSourceRulesByProductCode(Mockito.any());
		Mockito.doReturn(new Boolean(true)).when(flashBuyCommercePlaceOrderMethodHook).isFlashBuyCouponCompleted(Mockito.any(),
				Mockito.any());
		Mockito.doNothing().when(flashBuyService).performFlashBuyCronJob(Mockito.any());

		flashBuyCommercePlaceOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, commerceOrderResult);

		Mockito.verify(flashBuyService, Mockito.times(1)).performFlashBuyCronJob(Mockito.any());
	}

	@Test
	public void testAfterPlaceOrder_nuon_orderEntries() throws InvalidCartException
	{
		final List<AbstractOrderEntryModel> orderEntrys = new ArrayList<>();
		order.setEntries(orderEntrys);

		Mockito.verify(flashBuyService, Mockito.times(0)).getPromotionSourceRulesByProductCode(Mockito.any());
	}

	@Test
	public void testAfterPlaceOrder_multi_orderEntries() throws InvalidCartException
	{
		final ProductModel product1 = new ProductModel();
		product1.setCode(productCode);
		final ProductModel product2 = new ProductModel();
		product1.setCode(productCode2);

		final OrderEntryModel orderEntry1 = new OrderEntryModel();
		orderEntry1.setProduct(product1);
		final OrderEntryModel orderEntry2 = new OrderEntryModel();
		orderEntry2.setProduct(product2);
		final List<AbstractOrderEntryModel> orderEntrys = new ArrayList<>();
		orderEntrys.add(orderEntry1);
		orderEntrys.add(orderEntry2);
		order.setEntries(orderEntrys);

		final List<PromotionSourceRuleModel> promotionSourceRules = new ArrayList<>();
		promotionSourceRules.add(promotionSourceRule);
		final Optional<FlashBuyCouponModel> flashBuyCouponOptional = Optional.of(flashBuyCoupon);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(Mockito.any())).thenReturn(flashBuyCouponOptional);
		Mockito.doReturn(promotionSourceRules).when(flashBuyService).getPromotionSourceRulesByProductCode(Mockito.any());
		Mockito.doReturn(new Boolean(true)).when(flashBuyCommercePlaceOrderMethodHook).isFlashBuyCouponCompleted(Mockito.any(),
				Mockito.any());
		Mockito.doNothing().when(flashBuyService).performFlashBuyCronJob(Mockito.any());

		flashBuyCommercePlaceOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, commerceOrderResult);

		Mockito.verify(flashBuyService, Mockito.times(2)).performFlashBuyCronJob(Mockito.any());
	}


	@Test
	public void testAfterPlaceOrder_multi_promotionSourceRules() throws InvalidCartException
	{
		final ProductModel product2 = new ProductModel();
		product2.setCode(productCode);
		final List<PromotionSourceRuleModel> promotionSourceRules = new ArrayList<>();
		promotionSourceRules.add(promotionSourceRule);
		final PromotionSourceRuleModel promotionSourceRule2 = new PromotionSourceRuleModel();
		promotionSourceRule2.setCode(promotionCode2);
		promotionSourceRules.add(promotionSourceRule2);

		final Optional<FlashBuyCouponModel> flashBuyCouponOptional = Optional.of(flashBuyCoupon);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(Mockito.any())).thenReturn(flashBuyCouponOptional);
		Mockito.doReturn(promotionSourceRules).when(flashBuyService).getPromotionSourceRulesByProductCode(Mockito.any());
		Mockito.doReturn(new Boolean(true)).when(flashBuyCommercePlaceOrderMethodHook).isFlashBuyCouponCompleted(Mockito.any(),
				Mockito.any());
		Mockito.doNothing().when(flashBuyService).performFlashBuyCronJob(Mockito.any());


		flashBuyCommercePlaceOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, commerceOrderResult);

		Mockito.verify(flashBuyService, Mockito.times(2)).performFlashBuyCronJob(Mockito.any());
	}

	@Test
	public void testAfterPlaceOrder_non_flashBuyCoupon() throws InvalidCartException
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = new ArrayList<>();
		final Optional<FlashBuyCouponModel> flashBuyCouponOptional = Optional.of(flashBuyCoupon);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(Mockito.any())).thenReturn(flashBuyCouponOptional);
		promotionSourceRules.add(promotionSourceRule);
		Mockito.doReturn(promotionSourceRules).when(flashBuyService).getPromotionSourceRulesByProductCode(productCode);
		Mockito.doReturn(new Boolean(false)).when(flashBuyCommercePlaceOrderMethodHook).isFlashBuyCouponCompleted(Mockito.any(),
				Mockito.any());

		flashBuyCommercePlaceOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, commerceOrderResult);

		Mockito.verify(flashBuyService, Mockito.times(0)).performFlashBuyCronJob(Mockito.any());
	}

	@Test
	public void testAfterPlaceOrder_non_promotionSourceRule() throws InvalidCartException
	{
		Mockito.doReturn(promotionSourceRules).when(flashBuyService).getPromotionSourceRulesByProductCode(productCode);

		flashBuyCommercePlaceOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, commerceOrderResult);

		Mockito.verify(flashBuyService, Mockito.times(0)).performFlashBuyCronJob(Mockito.any());
	}

	@Test
	public void testAfterPlaceOrder_unredeemed_flashBuyCoupon() throws InvalidCartException
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = new ArrayList<>();
		final Optional<FlashBuyCouponModel> flashBuyCouponOptional = Optional.of(flashBuyCoupon);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(Mockito.any())).thenReturn(flashBuyCouponOptional);
		promotionSourceRules.add(promotionSourceRule);
		Mockito.doReturn(promotionSourceRules).when(flashBuyService).getPromotionSourceRulesByProductCode(Mockito.any());
		Mockito.doReturn(new Boolean(false)).when(flashBuyCommercePlaceOrderMethodHook).isFlashBuyCouponCompleted(Mockito.any(),
				Mockito.any());

		flashBuyCommercePlaceOrderMethodHook.afterPlaceOrder(commerceCheckoutParameter, commerceOrderResult);

		Mockito.verify(flashBuyService, Mockito.times(0)).performFlashBuyCronJob(Mockito.any());
	}

}
