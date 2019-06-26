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
package de.hybris.platform.promotionengineservices.concurrent;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
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
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotionengineservices.dao.PromotionDao;
import de.hybris.platform.promotionengineservices.promotionengine.impl.DefaultPromotionEngineService;
import de.hybris.platform.promotionengineservices.promotionengine.impl.PromotionEngineServiceBaseTest;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.test.RuleEngineTestSupportService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.test.TestThreadsHolder;
import de.hybris.platform.util.DiscountValue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for multi-threaded updatePromotions calls
 */
@IntegrationTest
public class ConcurrentPromotionEngineServiceTest extends PromotionEngineServiceBaseTest
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
	private PromotionDao promotionDao;

	@Resource
	private RuleEngineService commerceRuleEngineService;

	@Resource
	private RuleEngineContextDao ruleEngineContextDao;

	@Resource
	private DefaultPromotionEngineService defaultPromotionEngineService;

	@Resource
	private SessionService sessionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private RuleEngineTestSupportService ruleEngineTestSupportService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();

		super.importCsv("/promotionengineservices/test/promotionenginesetup0.impex", "UTF-8");
		super.importCsv("/promotionengineservices/test/hardwareCatalogMapping.impex", "UTF-8");
	}

	@Test
	public void testConcurrentPromotionUpdate() throws Exception
	{
		final Double ORIG_PRICE_EXPECTED = Double.valueOf(253);
		final Double DISCOUNTED_PRICE_EXPECTED = Double.valueOf(204.93);
		final UserModel user = userService.getAnonymousUser();
		userService.setCurrentUser(user);
		assertNotNull(user);

		final AbstractRuleEngineRuleModel rule = getRuleForFile("percentageDiscountCameraAccessories.drl",
				"/promotionengineservices/test/rules/");
		assertNotNull(rule);
		modelService.save(rule);

		final CartModel cart = cartService.getSessionCart();
		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");

		final PromotionGroupModel promoGroup1 = promotionDao.findPromotionGroupByCode("promoGroup1");

		assertNotNull(cart);
		assertFalse(modelService.isNew(cart));
		assertTrue(modelService.isUpToDate(cart));
		assertNotNull(product);
		assertNotNull(product.getUnit());
		assertNotNull(promoGroup1);

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

		initializeRuleEngine(rule);
		assertEquals("promotions-module-junit", ruleEngineTestSupportService.getTestModuleName(rule));

		final int THREADS = 50;

		final TestThreadsHolder runners = new TestThreadsHolder(THREADS,
				new ConcurrentUpdatePromotionsRunner(cart, JaloSession.getCurrentSession(), promoGroup1), true);
		runners.startAll();

		assertTrue("not all worker finished after 120s", runners.waitForAll(120, TimeUnit.SECONDS));
		assertEquals(
				"some workers got errors - note that there may be additional errors *after* this test finished due to workers still runnung !",
				Collections.emptyMap(), runners.getErrors());

		// make sure we final get the 'fresh' cart (and entries)
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
		assertEquals("since the percentage discount is converted to absolute discount, the currency should be set", "EUR", discountValue.getCurrencyIsoCode());
		assertEquals("wrong discount value", 48.07, discountValue.getValue(), 0.00);
		assertTrue("discount should be absolute", discountValue.isAbsolute());

		// Promotion checks
		assertNotNull("no promotion results found", cartFresh.getAllPromotionResults());
		assertEquals("wrong size for promo results", 1, cartFresh.getAllPromotionResults().size());
		final Iterator promoResultIter = cartFresh.getAllPromotionResults().iterator();
		final PromotionResultModel promoResult1 = (PromotionResultModel) promoResultIter.next();

		assertEquals("percentageDiscountCameraAccessories.drl", promoResult1.getPromotion().getCode());
		assertNotNull("no consumed entries for promo result 1 found", promoResult1.getConsumedEntries());
		assertEquals("wrong size for consumed entries 1", 1, promoResult1.getConsumedEntries().size());
		final PromotionOrderEntryConsumedModel entryConsumed1 = promoResult1.getConsumedEntries().iterator().next();
		assertEquals(cartEntryFresh, entryConsumed1.getOrderEntry());
		assertEquals("wrong adjusted price in consumed entry 1", DISCOUNTED_PRICE_EXPECTED, entryConsumed1.getAdjustedUnitPrice());

	}

	class ConcurrentUpdatePromotionsRunner implements Runnable
	{
		private final JaloSession jaloSession;
		private final PK cartPK;
		private final PK promoGrpPK;

		ConcurrentUpdatePromotionsRunner(final CartModel cart, final JaloSession jSession, final PromotionGroupModel promoGrp)
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

			defaultPromotionEngineService.updatePromotions(Collections.singleton(promoGrp), cart);
		}
	}

	@Test
	public void testConcurrentPromotionUpdateMultipleUsers() throws Exception
	{
		//multiple parallel requests (cart modifications) for different users

		final Double ORIG_PRICE_EXPECTED = Double.valueOf(253);
		final Double DISCOUNTED_PRICE_EXPECTED = Double.valueOf(204.93);
		final UserModel user = userService.getAnonymousUser();
		userService.setCurrentUser(user);
		assertNotNull(user);

		final AbstractRuleEngineRuleModel rule = getRuleForFile("percentageDiscountCameraAccessories.drl",
				"/promotionengineservices/test/rules/");
		assertNotNull(rule);
		modelService.save(rule);

		initializeRuleEngine(rule);
		assertEquals("promotions-module-junit", ruleEngineTestSupportService.getTestModuleName(rule));

		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");

		final PromotionGroupModel promoGroup1 = promotionDao.findPromotionGroupByCode("promoGroup1");

		assertNotNull(product);
		assertNotNull(product.getUnit());
		assertNotNull(promoGroup1);


		final int THREADS = 50;

		final TestThreadsHolder runners = new TestThreadsHolder(THREADS, new ConcurrentAddToCartRunner(product, promoGroup1), true);
		runners.startAll();

		assertTrue("not all worker finished after 120s", runners.waitForAll(120, TimeUnit.SECONDS));
		assertEquals(
				"some workers got errors - note that there may be additional errors *after* this test finished due to workers still runnung !",
				Collections.emptyMap(), runners.getErrors());

		// make sure we final get the 'fresh' cart (and entries)
		modelService.detachAll();

		final List<CartModel> carts = findCarts();
		assertEquals("Number of carts should be equal to number of threads", THREADS, carts.size());

		final ProductModel productFresh = modelService.get(product.getPk());

		for (final CartModel cart : carts)
		{
			assertEquals(DISCOUNTED_PRICE_EXPECTED, cart.getTotalPrice());
			assertEquals(cart.getTotalPrice(), cart.getSubtotal());
			assertEquals(Double.valueOf(0.00), cart.getTotalDiscounts());

			// Entry checks
			final List<CartEntryModel> entries = cartService.getEntriesForProduct(cart, productFresh);
			assertNotNull(entries);
			assertEquals("more than one entry created for single product", 1, entries.size());
			final CartEntryModel cartEntryFresh = entries.get(0);
			assertEquals("wrong quantity in entry", Long.valueOf(1), entries.get(0).getQuantity());
			assertEquals(ORIG_PRICE_EXPECTED, cartEntryFresh.getBasePrice());
			assertEquals(DISCOUNTED_PRICE_EXPECTED, cartEntryFresh.getTotalPrice());
			assertNotNull("no discount values for entry found", cartEntryFresh.getDiscountValues());
			assertEquals("wrong size for discount values", 1, cartEntryFresh.getDiscountValues().size());
			final DiscountValue discountValue = cartEntryFresh.getDiscountValues().get(0);
			assertEquals("since the percentage discount is converted to absolute discount, the currency should be set", "EUR", discountValue.getCurrencyIsoCode());
			assertEquals("wrong discount value", 48.07, discountValue.getValue(), 0.00);
			assertTrue("discount should be absolute", discountValue.isAbsolute());
			// Promotion checks
			assertNotNull("no promotion results found", cart.getAllPromotionResults());
			assertEquals("wrong size for promo results", 1, cart.getAllPromotionResults().size());
			final Iterator promoResultIter = cart.getAllPromotionResults().iterator();
			final PromotionResultModel promoResult1 = (PromotionResultModel) promoResultIter.next();

			assertEquals("percentageDiscountCameraAccessories.drl", promoResult1.getPromotion().getCode());
			assertNotNull("no consumed entries for promo result 1 found", promoResult1.getConsumedEntries());
			assertEquals("wrong size for consumed entries 1", 1, promoResult1.getConsumedEntries().size());
			final PromotionOrderEntryConsumedModel entryConsumed1 = promoResult1.getConsumedEntries().iterator().next();
			assertEquals(cartEntryFresh, entryConsumed1.getOrderEntry());
			assertEquals("wrong adjusted price in consumed entry 1", DISCOUNTED_PRICE_EXPECTED,
					entryConsumed1.getAdjustedUnitPrice());

		}
	}

	class ConcurrentAddToCartRunner implements Runnable
	{
		private final PK productPK;
		private final PK promoGrpPK;

		ConcurrentAddToCartRunner(final ProductModel product, final PromotionGroupModel promoGrp)
		{
			this.productPK = product.getPk();
			this.promoGrpPK = promoGrp.getPk();
		}

		@Override
		public void run()
		{
			sessionService.createNewSession();
			commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
			final CartModel cart = cartService.getSessionCart();

			final ProductModel product = modelService.get(productPK);
			final PromotionGroupModel promoGrp = modelService.get(promoGrpPK);

			cartService.addNewEntry(cart, product, 1, product.getUnit());
			modelService.save(cart);

			try
			{
				calculationService.calculate(cart);
			}
			catch (final CalculationException e)
			{
				e.printStackTrace(); // NOPMD
			}

			defaultPromotionEngineService.updatePromotions(Collections.singleton(promoGrp), cart);
		}
	}

	protected AbstractRuleEngineRuleModel getRuleForFile(final String fileName, final String path) throws IOException
	{
		final DroolsKIEBaseModel kieBaseModel = getKieBaseModel("promotions-base-junit");

		final DroolsRuleModel rule = (DroolsRuleModel) ruleEngineTestSupportService.createRuleModel();
		rule.setCode(fileName);
		rule.setUuid(fileName.substring(0, fileName.length() - 4));
		rule.setActive(Boolean.TRUE);
		rule.setRuleContent(readRuleFile(fileName, path));
		rule.setRuleType(RuleType.PROMOTION);
		rule.setMaxAllowedRuns(Integer.valueOf(1));
		ruleEngineTestSupportService.decorateRuleForTest(new HashMap<String, String>()
		{
			{
				put("ruleOrderEntryPercentageDiscountAction", "ruleOrderEntryPercentageDiscountAction");
			}
		}).accept(rule);
		rule.setKieBase(kieBaseModel);
		return rule;
	}

	protected void initializeRuleEngine(final AbstractRuleEngineRuleModel... rules)
	{
		final AbstractRuleEngineContextModel abstractContext = ruleEngineContextDao
				.findRuleEngineContextByName("promotions-junit-context");
		final List<RuleEngineActionResult> results = commerceRuleEngineService
				.initialize(Collections.singletonList(ruleEngineTestSupportService.getTestRulesModule(abstractContext, stream(rules).collect(toSet()))), false, false).waitForInitializationToFinish().getResults();
		if(CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		if (result.isActionFailed())
		{
			Assert.fail("rule engine initialization failed with errors: " + result.getMessagesAsString(MessageLevel.ERROR));
		}
	}

	protected List<CartModel> findCarts()
	{
		final String FIND_ORDERS_BY_CODE_QUERY = "SELECT {" + CartModel.PK + "} FROM {" + CartModel._TYPECODE + "} ";
		final SearchResult<CartModel> search = flexibleSearchService.search(new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_QUERY));
		return search.getResult();
	}

}
