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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import static de.hybris.platform.ruleengineservices.converters.populator.CartModelBuilder.newCart;
import static java.util.Arrays.stream;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotionengineservices.dao.PromotionDao;
import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.test.RuleEngineTestSupportService;
import de.hybris.platform.ruleengineservices.order.dao.ExtendedOrderDao;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;


@PerformanceTest(executions = 0)
public class DefaultPromotionEnginePerfTest extends PromotionEngineServiceBaseTest
{

	private static final Logger logger = LoggerFactory.getLogger(DefaultPromotionEnginePerfTest.class);

	private static final int SAMPLE_SIZE = 200;

	@Resource
	private PromotionDao promotionDao;
	@Resource
	private RuleEngineService commerceRuleEngineService;
	@Resource
	private RuleEngineContextDao ruleEngineContextDao;
	@Resource
	private ModelService modelService;
	@Resource
	private DefaultPromotionEngineService defaultPromotionEngineService;
	@Resource
	private ExtendedOrderDao extendedOrderDao;

	@Resource
	private DefaultPromotionsService defaultPromotionsService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CartService cartService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private UserService userService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private RuleEngineTestSupportService ruleEngineTestSupportService;

	private CurrencyModel currencyModel;
	private UserModel user;

	private static StopWatch stopWatch = new StopWatch("Comparative performance test");

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		importCsv("/test/promotionTestData.csv", "utf-8");

		// setup with promotionsenginesetup impex
		super.importCsv("/promotionengineservices/test/promotionenginesetup.impex", "UTF-8");
		currencyModel = commonI18NService.getCurrency("EUR");
		user = userService.getAnonymousUser();
		userService.setCurrentUser(user);
	}

	@Test
	public void testPromotionEngineServiceUpdatePromotions() throws IOException
	{
		initializeRule();

		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");
		final PromotionGroupModel group = promotionDao.findPromotionGroupByCode("promoGroup1");
		final Collection<PromotionGroupModel> groupList = Collections.singletonList(group);

		final Collection<CartModel> carts = IntStream.range(0, SAMPLE_SIZE).boxed().map(i -> createAndSaveCart(product))
				.collect(Collectors.toList());

		// evaluate promotions for cart:
		stopWatch.start("Applying rule-based promotion to " + SAMPLE_SIZE + " carts");
		carts.stream().forEach(c -> evaluatePromotionForCart(groupList, c));
	}

	@Test
	public void testPromotionServiceUpdatePromotions() throws Exception
	{

		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");
		final PromotionGroupModel prGroup3 = defaultPromotionsService.getPromotionGroup("prGroup3");
		final Collection<PromotionGroupModel> groupList = Collections.singletonList(prGroup3);

		final Collection<CartModel> carts = IntStream.range(0, SAMPLE_SIZE).boxed().map(i -> createAndSaveCart(product))
				.collect(Collectors.toList());

		stopWatch.start("Applying legacy promotion to " + SAMPLE_SIZE + " carts");
		carts.stream().forEach(c -> evaluateLegacyPromotionForCart(groupList, c));
	}

	protected void initializeRule() throws IOException
	{
		final AbstractRuleEngineRuleModel rule = getRuleForFile("orderPercentageDiscount.drl",
				"/promotionengineservices/test/rules/");
		rule.setCode("orderPercentageDiscount");
		ruleEngineTestSupportService.decorateRuleForTest(new HashMap<String, String>()
		{
			{
				put("addOrderDiscountRAOAction", "addOrderDiscountRAOAction");
			}
		}).accept(rule);
		modelService.save(rule);
		initializeRuleEngine(rule);
	}

	protected CartModel createAndSaveCart(final ProductModel product)
	{
		final CartModel cart = buildCartForUserWithCodeAndCurrency(UUID.randomUUID().toString());
		cart.setUser(user);
		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);
		return cart;
	}

	protected void evaluateLegacyPromotionForCart(final Collection<PromotionGroupModel> groupList, final CartModel cart)
			throws RuntimeException
	{
		defaultPromotionsService.updatePromotions(groupList, cart);
	}

	protected void evaluatePromotionForCart(final Collection<PromotionGroupModel> groupList, final CartModel cart)
	{
		defaultPromotionEngineService.updatePromotions(groupList, cart);
	}

	@After
	public void tierDown()
	{
		stopWatch.stop();
	}

	@AfterClass
	public static void afterTest()
	{
		logger.info(stopWatch.prettyPrint());
	}

	protected AbstractRuleEngineRuleModel getRuleForFile(final String fileName, final String path) throws IOException
	{
		final AbstractRuleEngineRuleModel rule = ruleEngineTestSupportService.createRuleModel();
		rule.setCode(fileName);
		rule.setUuid(fileName.substring(0, fileName.length() - 4));
		rule.setActive(Boolean.TRUE);
		rule.setRuleContent(readRuleFile(fileName, path));
		rule.setRuleType(RuleType.PROMOTION);
		return rule;
	}

	protected void initializeRuleEngine(final AbstractRuleEngineRuleModel... rules)
	{
		final AbstractRuleEngineContextModel abstractContext = ruleEngineContextDao
				.findRuleEngineContextByName("promotions-junit-context");
		final List<RuleEngineActionResult> results = commerceRuleEngineService.initialize(Collections.singletonList(
				ruleEngineTestSupportService.getTestRulesModule(abstractContext, stream(rules).collect(Collectors.toSet()))), true,
				false).waitForInitializationToFinish().getResults();
		if (CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		if (result.isActionFailed())
		{
			Assert.fail("rule engine initialization failed with errors: " + result.getMessagesAsString(MessageLevel.ERROR));
		}
	}

	protected CartModel buildCartForUserWithCodeAndCurrency(final String code)
	{
		return buildCartForUserWithCodeProductAndCurrency(code, null, 0);
	}

	protected CartModel buildCartForUserWithCodeProductAndCurrency(final String code, final String productCode, final int items)
	{
		final CartModel cart = productCode == null ? buildCartWithCode(code) : buildCartWithCodeProduct(code, productCode, items);
		cart.setCurrency(currencyModel);
		final UserModel user = new UserModel();
		user.setUid(UUID.randomUUID().toString());
		cart.setUser(user);
		cart.setDate(new Date());
		return cart;
	}

	protected CartModel buildCartWithCode(final String code)
	{
		return newCart(code).getModel();
	}

	protected CartModel buildCartWithCodeProduct(final String code, final String productCode, final int items)
	{
		return newCart(code).addProduct(productCode, 1, 1.0, items).getModel();
	}

}
