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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.dao.PromotionSourceRuleDao;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotionengineservices.validators.RuleBasedPromotionsContextValidator;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextForCatalogVersionsFinderStrategy;
import de.hybris.platform.ruleengine.strategies.impl.DefaultRuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengineservices.converters.populator.CartModelBuilder;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.ruleengineservices.util.ProductUtils;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class DefaultPromotionEngineUnitTest
{
	private static final String RULE_BASED_PROMOTION_DESC = "RuleBasedPromotion description";
	private static final String NOT_RULE_BASED_PROMOTION_DESC = "Not RuleBasedPromotion description";
	private static final String PROMOTION_CODE_1 = "prom1";
	private static final String PROMOTION_CODE_2 = "prom2";
	private static final String VARIANT_PRODUCT_CODE = "111111";
	private static final String BASE_PRODUCT_CODE = "222222";

	@Mock
	private RuleBasedPromotionModel ruleBasedPromotion;

	@Mock
	private AbstractPromotionModel abstractPromotion;

	@Mock
	private RuleEngineContextDao ruleEngineContextDao;

	@Mock
	private DroolsRuleEngineContextModel legacyTestContext;

	@Mock
	private FactContextFactory factContextFactory;

	@Mock
	private FactContext factContext;

	@Mock
	private RuleEngineContextForCatalogVersionsFinderStrategy ruleEngineContextForCatalogVersionsFinderStrategy;

	@Mock
	private DroolsRuleEngineContextModel context1;

	@Mock
	private DroolsRuleEngineContextModel context2;

	@Mock
	private RuleEngineContextFinderStrategy fallbackRuleEngineContextFinderStrategy;

	@Mock
	private RuleEngineService commerceRuleEngineService;

	@Mock
	private TimeService timeService;

	@Mock
	private DefaultRuleEngineContextFinderStrategy ruleEngineContextFinderStrategy;

	@Mock
	private PromotionSourceRuleDao promotionSourceRuleDao;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private RuleBasedPromotionsContextValidator ruleBasedPromotionsContextValidator;

	@Mock
	private ProductUtils productUtils;

	@InjectMocks
	private DefaultPromotionEngineService defaultPromotionEngineService;
	private ProductModel baseProduct;
	private ProductModel variantProduct;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(ruleBasedPromotion.getPromotionDescription()).thenReturn(RULE_BASED_PROMOTION_DESC);
		when(abstractPromotion.getDescription()).thenReturn(NOT_RULE_BASED_PROMOTION_DESC);
		when(timeService.getCurrentTime()).thenReturn(new Date());

		final Configuration configuration = new BaseConfiguration();
		configuration.setProperty("promotionengineservices.getpromotionsforproduct.disable", Boolean.FALSE);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(Boolean.valueOf(ruleBasedPromotionsContextValidator.isApplicable(any(), any(), any()))).thenReturn(Boolean.TRUE);
		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		baseProduct = new ProductModel();
		baseProduct.setCode(BASE_PRODUCT_CODE);
		baseProduct.setCatalogVersion(catalogVersion);
		variantProduct = new VariantProductModel();
		variantProduct.setCode(VARIANT_PRODUCT_CODE);
		variantProduct.setCatalogVersion(catalogVersion);
		((VariantProductModel) variantProduct).setBaseProduct(baseProduct);
	}

	@Test
	public void testGetPromotionDescriptionRuleBasedPromotion()
	{
		final String result = defaultPromotionEngineService.getPromotionDescription(ruleBasedPromotion);
		assertEquals(RULE_BASED_PROMOTION_DESC, result);
	}

	@Test
	public void testGetPromotionDescriptionNotRuleBasedPromotion()
	{
		final String result = defaultPromotionEngineService.getPromotionDescription(abstractPromotion);
		assertEquals(NOT_RULE_BASED_PROMOTION_DESC, result);
	}

	@Test
	public void testEvaluationFailsWhenLessThanOneContextMappedIsFoundAndNoContextByRuleModuleFound()
	{

		final List<AbstractRuleEngineContextModel> contexts1 = new ArrayList<>();

		when(factContextFactory.createFactContext(Mockito.any(FactContextType.class), Mockito.anyCollection())).thenReturn(
				factContext);
		when(factContext.getFacts()).thenReturn(Collections.emptyList());
		when(ruleEngineContextForCatalogVersionsFinderStrategy.findRuleEngineContexts(Mockito.anyCollection(), Mockito.any()))
				.thenReturn(contexts1);
		when(fallbackRuleEngineContextFinderStrategy.findRuleEngineContext(Mockito.any())).thenReturn(Optional.empty());

		final AbstractOrderModel cart = CartModelBuilder.newCart("cart").addProduct("product", 1, 10d, 0).getModel();

		when(ruleEngineContextFinderStrategy.findRuleEngineContext(cart, RuleType.PROMOTION)).thenReturn(Optional.empty());

		final RuleEvaluationResult result = defaultPromotionEngineService.evaluate(cart, Collections.emptyList());
		assertTrue(result.isEvaluationFailed());
		assertNotNull(result.getErrorMessage());
		assertNull(result.getResult());
	}


	@Test
	public void testEvaluationPassWhenLessThanOneContextMappedIsFoundAndOneContextByRuleModuleFound()
	{

		final List<AbstractRuleEngineContextModel> contexts1 = new ArrayList<>();
		when(context1.getName()).thenReturn("context1");

		when(ruleEngineContextForCatalogVersionsFinderStrategy.findRuleEngineContexts(Mockito.anyCollection(), Mockito.any()))
				.thenReturn(contexts1);
		when(fallbackRuleEngineContextFinderStrategy.findRuleEngineContext(Mockito.any())).thenReturn(Optional.of(context1));

		final AbstractOrderModel cart = CartModelBuilder.newCart("cart").addProduct("product", 1, 10d, 0).getModel();
		when(ruleEngineContextFinderStrategy.findRuleEngineContext(cart, RuleType.PROMOTION)).thenReturn(Optional.of(context1));

		final AbstractRuleEngineContextModel context = defaultPromotionEngineService.determineRuleEngineContext(cart);
		assertEquals(context1, context);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBaseProductHasPromotion()
	{
		final RuleBasedPromotionModel variantPromotion = createPromotion(PROMOTION_CODE_1);
		final RuleBasedPromotionModel basePromotion = createPromotion(PROMOTION_CODE_2);

		when(productUtils.getAllBaseProducts(eq(variantProduct)))
				.thenReturn(Sets.newHashSet(baseProduct));
		when(promotionSourceRuleDao.findPromotions(anyCollection(), eq(VARIANT_PRODUCT_CODE), anySet()))
				.thenReturn(Collections.singletonList(variantPromotion));
		when(promotionSourceRuleDao.findPromotions(anyCollection(), eq(BASE_PRODUCT_CODE), anySet()))
				.thenReturn(Collections.singletonList(basePromotion));

		final List<? extends AbstractPromotionModel> promotions = defaultPromotionEngineService
				.getAbstractProductPromotions(new ArrayList<>(), variantProduct, true, null);
		final List<String> promotionCodes = promotions.stream().map(AbstractPromotionModel::getCode).collect(Collectors.toList());

		assertEquals(2, promotions.size());
		assertTrue("Should contain both base and variant promotions",
				promotionCodes.containsAll(Arrays.asList(PROMOTION_CODE_1, PROMOTION_CODE_2)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBaseAndVariantProductHasSamePromotion()
	{
		final RuleBasedPromotionModel promotion = createPromotion(PROMOTION_CODE_1);
		when(promotionSourceRuleDao.findPromotions(anyCollection(), any(), anySet()))
				.thenReturn(Collections.singletonList(promotion));

		final List<? extends AbstractPromotionModel> promotions = defaultPromotionEngineService
				.getAbstractProductPromotions(new ArrayList<>(), variantProduct, true, null);
		final List<String> promotionCodes = promotions.stream().map(AbstractPromotionModel::getCode).collect(Collectors.toList());

		assertEquals(1, promotions.size());
		assertTrue("Should contain single promotion", promotionCodes.containsAll(Arrays.asList(PROMOTION_CODE_1)));
	}

	private RuleBasedPromotionModel createPromotion(final String code) {
		final RuleBasedPromotionModel promotion = new RuleBasedPromotionModel();
		promotion.setCode(code);
		promotion.setPriority(Integer.valueOf(1));

		return promotion;
	}
}
