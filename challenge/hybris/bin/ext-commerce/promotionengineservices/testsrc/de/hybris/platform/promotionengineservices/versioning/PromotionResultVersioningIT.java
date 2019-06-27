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
package de.hybris.platform.promotionengineservices.versioning;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotionengineservices.dao.PromotionDao;
import de.hybris.platform.promotionengineservices.promotionengine.impl.DefaultPromotionEngineService;
import de.hybris.platform.promotionengineservices.promotionengine.impl.PromotionEngineServiceBaseTest;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.init.InitializationFuture;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.test.RuleEngineTestSupportService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;


/**
 * Integration test suite for PromotionResult and RuleBasedPromotion versioning functionality
 */

@IntegrationTest
public class PromotionResultVersioningIT extends PromotionEngineServiceBaseTest
{

	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private RuleEngineTestSupportService ruleEngineTestSupportService;
	@Resource
	private CartService cartService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private RuleEngineContextDao ruleEngineContextDao;
	@Resource
	private RuleEngineService commerceRuleEngineService;
	@Resource
	private PromotionDao promotionDao;
	@Resource
	private DefaultPromotionEngineService promotionEngineService;
	@Resource
	private CurrencyDao currencyDao;

	private CartModel cart;

	private DroolsKIEBaseModel kieBaseModel;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		super.importCsv("/promotionengineservices/test/promotionenginesetup.impex", "UTF-8");
		super.importCsv("/promotionengineservices/test/hardwareCatalogMapping.impex", "UTF-8");

		final UserModel user = userService.getAnonymousUser();
		userService.setCurrentUser(user);

		final List<CurrencyModel> currencyList = currencyDao.findCurrenciesByCode("USD");

		cart = cartService.getSessionCart();
		cart.setCurrency(currencyList.get(0));

		kieBaseModel = getKieBaseModel("promotions-base-junit");
	}

	@Test
	public void testPromotionResultVersion() throws Exception
	{
		final AbstractRuleEngineRuleModel rule = getRuleFromResource(
				"promotionengineservices/test/rules/percentageDiscountCameraAccessories.drl",
				"percentageDiscountCameraAccessories.drl", "percentageDiscountCameraAccessories");

		assertNotNull(rule);
		modelService.save(rule);

		assertThat(rule.getVersion()).isEqualTo(Long.valueOf(1));

		final CartModel cart = cartService.getSessionCart();

		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");

		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);

		calculationService.calculate(cart);
		initializeRuleEngine(rule);

		final PromotionGroupModel promoGroup1 = promotionDao.findPromotionGroupByCode("promoGroup1");
		promotionEngineService.updatePromotions(singleton(promoGroup1), cart);

		// make sure we final get the 'fresh' cart (and entries)
		modelService.detachAll();
		final CartModel cartFresh = modelService.get(cart.getPk());
		final Set<PromotionResultModel> allPromotionResults = cartFresh.getAllPromotionResults();

		assertThat(allPromotionResults).isNotEmpty().hasSize(1);
		final PromotionResultModel promotionResult = allPromotionResults.iterator().next();
		assertThat(promotionResult.getRuleVersion()).isEqualTo(Long.valueOf(1));
		assertThat(promotionResult.getModuleVersion()).isEqualTo(Long.valueOf(1));
	}

	@Test
	public void testPromotionResultVersionIsCorrectlyAligned() throws Exception
	{
		final AbstractRuleEngineRuleModel rule1 = getRuleFromResource(
				"promotionengineservices/test/rules/percentageDiscountCameraAccessories.drl",
				"percentageDiscountCameraAccessories.drl", "percentageDiscountCameraAccessories");

		modelService.save(rule1);

		assertThat(rule1.getVersion()).isEqualTo(Long.valueOf(1));

		final CartModel cart1 = cartService.getSessionCart();
		final ProductModel product = productService
				.getProductForCode(catalogVersionService.getCatalogVersion("hwcatalog", "Online"), "HW1210-3411");

		cartService.addNewEntry(cart1, product, 1, product.getUnit());
		modelService.save(cart1);

		calculationService.calculate(cart1);
		initializeRuleEngine(rule1);

		final PromotionGroupModel promoGroup1 = promotionDao.findPromotionGroupByCode("promoGroup1");
		promotionEngineService.updatePromotions(singleton(promoGroup1), cart1);

		final AbstractRuleEngineRuleModel rule2 = getRuleFromResource(
				"promotionengineservices/test/rules/percentageDiscountCameraAccessories2.drl",
				"percentageDiscountCameraAccessories.drl", "percentageDiscountCameraAccessories2");

		modelService.save(rule2);
		assertThat(rule2.getVersion()).isEqualTo(Long.valueOf(2));
		final CartModel cart2 = cartService.getSessionCart();
		cartService.addNewEntry(cart2, product, 1, product.getUnit());
		modelService.save(cart2);

		initializeRuleEngine(rule2);

		promotionEngineService.updatePromotions(singleton(promoGroup1), cart2);

		modelService.detachAll();
		final CartModel cartFresh = modelService.get(cart2.getPk());
		final Set<PromotionResultModel> allPromotionResults = cartFresh.getAllPromotionResults();

		assertThat(allPromotionResults).isNotEmpty().hasSize(1);
		final PromotionResultModel promotionResult = allPromotionResults.iterator().next();
		assertThat(promotionResult.getRuleVersion()).isEqualTo(Long.valueOf(2));
		assertThat(promotionResult.getModuleVersion()).isEqualTo(Long.valueOf(2));
	}

	@Test
	public void testRuleBasedPromotionVersionIsCorrectlyAligned() throws Exception
	{
		final AbstractRuleEngineRuleModel rule1 = getRuleFromResource(
				"promotionengineservices/test/rules/percentageDiscountCameraAccessories.drl",
				"percentageDiscountCameraAccessories.drl", "percentageDiscountCameraAccessories");
		modelService.save(rule1);
		initializeRuleEngine(rule1);

		assertThat(rule1.getVersion()).isEqualTo(Long.valueOf(1));
		assertThat(rule1.getPromotion().getRuleVersion()).isEqualTo(Long.valueOf(1));

		final AbstractRuleEngineRuleModel rule2 = getRuleFromResource(
				"promotionengineservices/test/rules/percentageDiscountCameraAccessories2.drl",
				"percentageDiscountCameraAccessories.drl", "percentageDiscountCameraAccessories2");
		modelService.save(rule2);
		initializeRuleEngine(rule2);

		assertThat(rule2.getVersion()).isEqualTo(Long.valueOf(2));
		assertThat(rule2.getPromotion().getRuleVersion()).isEqualTo(Long.valueOf(2));

		assertThat(rule1.getPromotion()).isNotEqualTo(rule2.getPromotion());
	}

	private String readFromResource(final String resourceName) throws IOException
	{
		final URL url = Resources.getResource(resourceName);
		return Resources.toString(url, Charsets.UTF_8);
	}

	protected AbstractRuleEngineRuleModel getRuleFromResource(final String resourceName, final String ruleCode,
			final String ruleUUID) throws IOException
	{
		final DroolsRuleModel rule = (DroolsRuleModel) createEmptyRule(ruleCode, ruleUUID);
		rule.setActive(Boolean.TRUE);
		rule.setRuleContent(readFromResource(resourceName));
		rule.setKieBase(kieBaseModel);
		return rule;
	}

	protected AbstractRuleEngineRuleModel createEmptyRule(final String ruleCode, final String ruleUUID) throws IOException
	{
		final DroolsRuleModel rule = (DroolsRuleModel) ruleEngineTestSupportService.createRuleModel();
		rule.setRuleType(RuleType.PROMOTION);
		rule.setCode(ruleCode);
		rule.setUuid(ruleUUID);
		rule.setActive(Boolean.FALSE);
		rule.setMaxAllowedRuns(valueOf(1));
		rule.setKieBase(kieBaseModel);
		ruleEngineTestSupportService.decorateRuleForTest(new HashMap<String, String>()
		{
			{
				put("ruleOrderEntryPercentageDiscountAction", "ruleOrderEntryPercentageDiscountAction");
			}
		}).accept(rule);
		return rule;
	}

	protected void initializeRuleEngine(final AbstractRuleEngineRuleModel... rules)
	{
		final AbstractRuleEngineContextModel abstractContext = ruleEngineContextDao
				.findRuleEngineContextByName("promotions-junit-context");
		final InitializationFuture future = commerceRuleEngineService
				.initialize(Lists.newArrayList(
						ruleEngineTestSupportService.getTestRulesModule(abstractContext, stream(rules).collect(toSet()))), true, false);

		future.waitForInitializationToFinish();
		final List<RuleEngineActionResult> results = future.getResults();

		final List<RuleEngineActionResult> failedResults = results.stream().filter(RuleEngineActionResult::isActionFailed)
				.collect(toList());
		if (CollectionUtils.isNotEmpty(failedResults))
		{
			final StringBuilder failedMessage = new StringBuilder();
			failedResults.forEach(r -> failedMessage.append(r.getMessagesAsString(MessageLevel.ERROR)).append(", "));
			fail("rule engine initialization failed with errors: " + failedMessage);
		}
	}

}
