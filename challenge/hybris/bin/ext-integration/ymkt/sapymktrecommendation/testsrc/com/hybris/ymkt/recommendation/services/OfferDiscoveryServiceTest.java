/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationContext;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario;


@UnitTest
public class OfferDiscoveryServiceTest
{
	@Mock
	private CartService cartService;

	@Mock
	private OfferDiscoveryService offerDiscoveryService;

	@Mock
	private UserContextService userContextService;

	@Mock
	private UserService userService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private LanguageModel languageModel;

	@Mock
	private RecentViewedItemsService recentViewedItemsService;

	private static final String LEADING_PRODUCT_ID = "12345678";

	private final OfferRecommendationContext context = new OfferRecommendationContext();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		context.setCartItemDSType("CUAN_PRODUCT");
		context.setContentPosition("Home");
		context.setLeadingItemDSType("CUAN_PRODUCT");
		context.setLeadingItemType("P");
		context.setLeadingProductId(LEADING_PRODUCT_ID);
		context.setScenarioId("PHX_OFFER_SCENARIO_2");

		Mockito.doCallRealMethod().when(offerDiscoveryService).setUserContextService(Mockito.any(UserContextService.class));
		Mockito.doCallRealMethod().when(offerDiscoveryService).setCommonI18NService(Mockito.any(CommonI18NService.class));
		Mockito.doCallRealMethod().when(offerDiscoveryService)
				.setRecentViewedItemsService(Mockito.any(RecentViewedItemsService.class));

		Mockito.when(userContextService.getUserId()).thenReturn("6de4ae57e795a737");
		Mockito.when(userContextService.getUserOrigin()).thenReturn("COOKIE_ID");
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		Mockito.when(languageModel.getIsocode()).thenReturn("EN");

		List<String> recentViewedItems = new ArrayList<>();
		recentViewedItems.add("recentViewedItem");
		Mockito.when(this.recentViewedItemsService.getRecentViewedProducts()).thenReturn(recentViewedItems);

		List<String> cartItems = new ArrayList<>();
		cartItems.add("cartItem");
		Mockito.doReturn(cartItems).when(offerDiscoveryService).getCartItemsFromSession();
		Mockito.when(offerDiscoveryService.createOfferRecommendationScenario(context)).thenCallRealMethod();

		offerDiscoveryService.setCommonI18NService(commonI18NService);
		offerDiscoveryService.setCartService(cartService);
		offerDiscoveryService.setUserContextService(userContextService);
		offerDiscoveryService.setRecentViewedItemsService(recentViewedItemsService);
		userContextService.setUserService(userService);
	}

	/**
	 * Testing the includeCart() = false logic
	 */
	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeCartIsFalse()
	{
		context.setIncludeCart(false);
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);
		Assert.assertEquals(1, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		Assert.assertTrue(
				offerRecoScenario.getLeadingObjects().stream().allMatch(i -> i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	/**
	 * Testing the includeCart() = true logic
	 */
	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeCartIsTrue()
	{
		context.setIncludeCart(true);
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);
		Assert.assertTrue(context.isIncludeCart());
		Assert.assertEquals(2, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		Assert.assertTrue(offerRecoScenario.getLeadingObjects().stream()
				.anyMatch(i -> i.getLeadingObjectId().equals("cartItem") || i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenCartItemDSTypeIsNotBlank()
	{
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);
		Assert.assertTrue(StringUtils.isNotBlank(context.getCartItemDSType()));
		Assert.assertEquals(1, offerRecoScenario.getBasketObjects().size());

		//validates the entries in the leadingObjects
		Assert.assertTrue(offerRecoScenario.getBasketObjects().stream()
				.anyMatch(i -> i.getBasketObjectId().equals("cartItem") || i.getBasketObjectType().equals("CUAN_PRODUCT")));
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenCartItemDSTypeIsBlank()
	{
		context.setCartItemDSType("");
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		Assert.assertTrue(StringUtils.isBlank(context.getCartItemDSType()));
		Assert.assertEquals(0, offerRecoScenario.getBasketObjects().size());
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeRecentIsTrue()
	{
		context.setIncludeRecent(true);
		Assert.assertTrue(context.isIncludeRecent());
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		Assert.assertEquals(2, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		Assert.assertTrue(offerRecoScenario.getLeadingObjects().stream().anyMatch(
				i -> i.getLeadingObjectId().equals("recentViewedItem") || i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeRecentIsFalse()
	{
		Assert.assertFalse(context.isIncludeRecent());
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		Assert.assertEquals(1, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		Assert.assertTrue(
				offerRecoScenario.getLeadingObjects().stream().anyMatch(i -> i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	/**
	 * Validates the context params creation
	 */
	@Test
	public void createOfferRecommendationScenarioTestContextParams()
	{
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		Assert.assertEquals(3, offerRecoScenario.getContextParams().size());
		Assert.assertEquals(1, offerRecoScenario.getContextParams().get(0).getContextId());
		Assert.assertEquals("P_COMM_MEDIUM", offerRecoScenario.getContextParams().get(0).getName());
		Assert.assertEquals("ONLINE_SHOP", offerRecoScenario.getContextParams().get(0).getValue());

		Assert.assertEquals(2, offerRecoScenario.getContextParams().get(1).getContextId());
		Assert.assertEquals("P_LANGUAGE", offerRecoScenario.getContextParams().get(1).getName());
		Assert.assertEquals("EN", offerRecoScenario.getContextParams().get(1).getValue());

		Assert.assertEquals(3, offerRecoScenario.getContextParams().get(2).getContextId());
		Assert.assertEquals("P_POSITION", offerRecoScenario.getContextParams().get(2).getName());
		Assert.assertEquals("Home", offerRecoScenario.getContextParams().get(2).getValue());
	}

}
