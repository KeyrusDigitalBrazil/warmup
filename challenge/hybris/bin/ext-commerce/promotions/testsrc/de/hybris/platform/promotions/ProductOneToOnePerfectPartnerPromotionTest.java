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
package de.hybris.platform.promotions;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for ProductOneToOnePerfectPartnerPromotion.
 */
public class ProductOneToOnePerfectPartnerPromotionTest extends AbstractPromotionTest
{

	private ProductModel baseProduct;
	private ProductModel partnerProduct;

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private PromotionsService defaultPromotionsService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		baseProduct = productService.getProductForCode(version, "HW2110-0019");
		partnerProduct = productService.getProductForCode(version, "HW2200-0561");
		final UserModel user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		final CurrencyModel currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
	}

	/**
	 * HW2110-0019(base product): 381.64 Euro, and HW2200-0561(partner product): 86.80 Euro. The
	 * ProductOneToOnePerfectPartnerPromotion will be fired if both of them are in the cart and the price is 700 Euro.
	 * <ul>
	 * <li>adds the HW2110-0019 in cart, and tests the total price,</li>
	 * <li>updates with the ProductOneToOnePerfectPartnerPromotion, and checks the total price,</li>
	 * <li>now adds the HW2200-0561, and checks the total price,</li>
	 * <li>updates with the ProductOneToOnePerfectPartnerPromotion that should be fired now, and checks the total price.
	 * </li>
	 * </ul>
	 */
	@Test
	public void testProductOneToOnePerfectPartnerPromotion() throws CalculationException
	{
		final CartModel cart = cartService.getSessionCart();
		cartService.addNewEntry(cart, baseProduct, 1, baseProduct.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);
		assertEquals("before updatePromotions(ProductOneToOnePerfectPartnerPromotion)", 381.64d, cart.getTotalPrice().doubleValue(),
				0.01);

		final PromotionGroupModel promotionGroup = defaultPromotionsService.getPromotionGroup("prGroup5");
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(promotionGroup);
		defaultPromotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("without partner product", 381.64d, cart.getTotalPrice().doubleValue(), 0.01);

		cartService.addNewEntry(cart, partnerProduct, 1, partnerProduct.getUnit());
		modelService.saveAll();
		calculationService.calculate(cart);
		assertEquals("with partner product, but without promotion", 468.44d, cart.getTotalPrice().doubleValue(), 0.01);

		defaultPromotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		modelService.refresh(cart);
		assertEquals("with partner product now", 400.00d, cart.getTotalPrice().doubleValue(), 0.01);
	}

}
