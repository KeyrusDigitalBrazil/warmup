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
package de.hybris.platform.promotions.impl;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.test.TestThreadsHolder;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.DiscountValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for multi-threaded updatePromotions calls
 */
@IntegrationTest
public class ConcurrentPromotionUpdateTest extends ServicelayerTest
{

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CartService cartService;

	@Resource
	private CalculationService calculationService;

	@Resource
	private DefaultPromotionsService defaultPromotionsService;

	protected final PropertyConfigSwitcher itemSyncEnabledSwitcher = new PropertyConfigSwitcher("itemsync.enabled");

	@Before
	public void setUp() throws Exception
	{
		itemSyncEnabledSwitcher.switchToValue("true");
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		importCsv("/test/promotionTestData.csv", "utf-8");
	}

	@After
	public void tearDown() throws Exception
	{
		itemSyncEnabledSwitcher.switchBackToDefault();
	}

	@Test
	public void testConcurrentPromotionUpdate() throws Exception
	{
		final Double ORIG_PRICE_EXPECTED = Double.valueOf(253);
		final Double DISCOUNTED_PRICE_EXPECTED = Double.valueOf(204.93);
		final UserModel user = userService.getAnonymousUser();
		userService.setCurrentUser(user);

		final CartModel cart = cartService.getSessionCart();
		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");
		final PromotionGroupModel prGroup3 = defaultPromotionsService.getPromotionGroup("prGroup3");

		assertNotNull(cart);
		assertFalse(modelService.isNew(cart));
		assertTrue(modelService.isUpToDate(cart));
		assertNotNull(product);
		assertNotNull(product.getUnit());
		assertNotNull(prGroup3);

		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);
		assertNotNull(cart.getEntries());
		assertEquals(1, cart.getEntries().size());

		calculationService.calculate(cart);

		assertEquals(ORIG_PRICE_EXPECTED, cart.getSubtotal());
		assertEquals(cart.getTotalPrice(), cart.getSubtotal());
		assertEquals(Double.valueOf(0.00), cart.getTotalDiscounts());
		final CartEntryModel cartEntry = (CartEntryModel) cart.getEntries().get(0);
		assertEquals(ORIG_PRICE_EXPECTED, cartEntry.getBasePrice());
		assertEquals(cartEntry.getBasePrice(), cartEntry.getTotalPrice());

		final int THREADS = 50;

		final TestThreadsHolder runners = new TestThreadsHolder(THREADS,
				new ConcurrentAddToCartRunner(cart, JaloSession.getCurrentSession(), prGroup3), true);
		runners.startAll();

		assertTrue("not all worker finished after 120s", runners.waitForAll(120, TimeUnit.SECONDS));
		assertEquals(
				"some workers got errors - note that there may be additional errors *after* this test finished due to workers still runnung !",
				Collections.emptyMap(), runners.getErrors());

		// make sure we get the 'fresh' cart (and entries)
		modelService.detachAll();
		final CartModel cartFresh = modelService.get(cart.getPk());
		assertNotSame(cartFresh, cart);
		final ProductModel productFresh = modelService.get(product.getPk());

		assertEquals(DISCOUNTED_PRICE_EXPECTED, cartFresh.getTotalPrice());
		assertEquals(cartFresh.getTotalPrice(), cartFresh.getSubtotal());
		assertEquals(Double.valueOf(0.00), cartFresh.getTotalDiscounts());

		// Entry checks
		final List<CartEntryModel> entries = cartService.getEntriesForProduct(cartFresh, productFresh);
		assertNotNull(entries);
		assertEquals("more than one entry created for single product", 1, entries.size());
		final CartEntryModel cartEntryFresh = entries.get(0);
		assertEquals("wrong product in entry", productFresh, cartEntryFresh.getProduct());
		assertEquals("wrong quantity in entry", Long.valueOf(1), entries.get(0).getQuantity());
		assertEquals(ORIG_PRICE_EXPECTED, cartEntryFresh.getBasePrice());
		assertEquals(DISCOUNTED_PRICE_EXPECTED, cartEntryFresh.getTotalPrice());
		assertNotNull("no discount values for entry found", cartEntryFresh.getDiscountValues());
		assertEquals("wrong size for discount values", 1, cartEntryFresh.getDiscountValues().size());
		final DiscountValue discountValue = cartEntryFresh.getDiscountValues().get(0);
		assertEquals("wrong discount value", 48.07, discountValue.getValue(), 0.00);

		// Promotion checks
		assertNotNull("no promotion results found", cartFresh.getAllPromotionResults());
		assertEquals("wrong size for promo results", 2, cartFresh.getAllPromotionResults().size());

		final Map<String, PromotionResultModel> promotionResults = cartFresh.getAllPromotionResults().stream()
				.collect(toMap(r -> r.getPromotion().getCode(), identity()));

		// this is just a potential promotion, which does not change the prices
		assertThat(promotionResults.keySet()).isNotEmpty().hasSize(2).containsOnly("Product_Stepped_Multi_Buy",
				"PercentageDiscount_19");
		final PromotionResultModel promoResult1 = promotionResults.get("Product_Stepped_Multi_Buy");
		assertNotNull("no consumed entries for promo result 1 found", promoResult1.getConsumedEntries());
		assertEquals("wrong size for consumed entries 1", 1, promoResult1.getConsumedEntries().size());
		final PromotionOrderEntryConsumedModel entryConsumed1 = promoResult1.getConsumedEntries().iterator().next();
		assertEquals(cartEntryFresh, entryConsumed1.getOrderEntry());
		assertEquals("wrong adjusted price in consumed entry 1", ORIG_PRICE_EXPECTED, entryConsumed1.getAdjustedUnitPrice());

		final PromotionResultModel promoResult2 = promotionResults.get("PercentageDiscount_19");
		assertEquals("PercentageDiscount_19", promoResult2.getPromotion().getCode());
		assertNotNull("no consumed entries for promo result 2 found", promoResult2.getConsumedEntries());
		assertEquals("wrong size for consumed entries 2", 1, promoResult2.getConsumedEntries().size());
		final PromotionOrderEntryConsumedModel entryConsumed2 = promoResult2.getConsumedEntries().iterator().next();
		assertEquals(cartEntryFresh, entryConsumed2.getOrderEntry());
		assertEquals("wrong adjusted price in consumed entry 2", DISCOUNTED_PRICE_EXPECTED, entryConsumed2.getAdjustedUnitPrice());
	}

	class ConcurrentAddToCartRunner implements Runnable
	{
		private final JaloSession jaloSession;
		private final PK cartPK;
		private final PK promoGrpPK;

		ConcurrentAddToCartRunner(final CartModel cart, final JaloSession jSession, final PromotionGroupModel promoGrp)
		{
			this.jaloSession = jSession;
			this.cartPK = cart.getPk();
			this.promoGrpPK = promoGrp.getPk();
		}

		@Override
		public void run()
		{
			jaloSession.activate();
			final CartModel cart = modelService.get(cartPK);
			final PromotionGroupModel promoGrp = modelService.get(promoGrpPK);

			defaultPromotionsService.updatePromotions(Collections.singleton(promoGrp), cart);
		}
	}

}
