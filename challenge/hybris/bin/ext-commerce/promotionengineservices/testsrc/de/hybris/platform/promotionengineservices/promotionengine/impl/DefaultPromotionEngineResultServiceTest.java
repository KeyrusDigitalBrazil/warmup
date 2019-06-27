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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionMessageParameterResolutionStrategy;
import de.hybris.platform.promotionengineservices.promotionengine.coupons.CouponCodeRetrievalStrategy;
import de.hybris.platform.promotionengineservices.util.PromotionResultUtils;
import de.hybris.platform.promotions.impl.DefaultPromotionResultService;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPromotionEngineResultServiceTest
{
	private static final String RULE_PARAMETER_TYPE1 = "TYPE1";
	private static final String RULE_PARAMETER_TYPE2 = "TYPE2";
	private static final String FREE_GIFT_CODE = "product1234";
	private static final String FREE_GIFT_NAME = "the free gift of laughter";
	private static final String GIVE_AWAY_COUPON_CODE = "BUYMORE16";

	@InjectMocks
	private DefaultPromotionEngineResultService defaultPromotionEngineResultService;

	@Mock
	private DefaultPromotionResultService defaultPromotionResultService;

	@Mock
	private RuleParametersService ruleParametersService;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleBasedPromotionModel ruleBasedPromotion;

	@Mock
	private AbstractOrderModel order;

	@Mock
	private CurrencyModel currency;

	@Mock
	private AbstractPromotionModel legacyPromotion;

	@Mock
	private AbstractRuleEngineRuleModel engineRule;

	@Mock
	private ProductService productService;

	@Mock
	private ProductModel freeGift;

	@Mock
	private CouponCodeRetrievalStrategy couponCodeRetrievalStrategy;

	@Mock
	private ModelService modelService;

	@Mock
	private CartService cartService;

	// YTODO turn this into a mock once PromotionOrderResults is no longer final @Mock
	private final PromotionOrderResults promotionOrderResults = new PromotionOrderResults(null, null, null, 0d);

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final Map<String, PromotionMessageParameterResolutionStrategy> resolutionStrategies = new HashMap<>();
		final DefaultCurrencyAmountResolutionStrategy currencyResolutionStrategy = new DefaultCurrencyAmountResolutionStrategy();
		final PromotionResultUtils promotionResultUtils = new PromotionResultUtils();
		promotionResultUtils.setCartService(cartService);
		when(new Boolean(cartService.hasSessionCart())).thenReturn(Boolean.FALSE);
		currencyResolutionStrategy.setPromotionResultUtils(promotionResultUtils);
		final DefaultProductResolutionStrategy productResolutionStrategy = new DefaultProductResolutionStrategy();
		productResolutionStrategy.setProductService(productService);
		when(productService.getProductForCode(FREE_GIFT_CODE)).thenReturn(freeGift);
		when(freeGift.getName()).thenReturn(FREE_GIFT_NAME);
		when(couponCodeRetrievalStrategy.getCouponCodesFromPromotion(promotionResult)).thenReturn(Optional.empty());
		resolutionStrategies.put(RULE_PARAMETER_TYPE1, currencyResolutionStrategy);
		resolutionStrategies.put(RULE_PARAMETER_TYPE2, productResolutionStrategy);
		defaultPromotionEngineResultService.setResolutionStrategies(resolutionStrategies);

		// by default return the legacy promotion
		when(promotionResult.getPromotion()).thenReturn(legacyPromotion);
	}

	@Test
	public void testLegacyIsApplied()
	{
		final boolean result = defaultPromotionEngineResultService.isApplied(promotionResult);
		verify(defaultPromotionResultService, times(1)).isApplied(promotionResult);
		assertFalse(result);
	}

	@Test
	public void testLegacyIsAppliedToOrder()
	{
		final boolean result = defaultPromotionEngineResultService.isAppliedToOrder(promotionResult);
		verify(defaultPromotionResultService, times(1)).isAppliedToOrder(promotionResult);
		assertFalse(result);
	}

	@Test
	public void testLegacyApply()
	{
		final boolean result = defaultPromotionEngineResultService.apply(promotionResult);
		verify(defaultPromotionResultService, times(1)).apply(promotionResult);
		assertFalse(result);
	}

	@Test
	public void testLegacyUndo()
	{
		final boolean result = defaultPromotionEngineResultService.undo(promotionResult);
		verify(defaultPromotionResultService, times(1)).undo(promotionResult);
		assertFalse(result);
	}

	@Test
	public void testLegacyGetFired()
	{
		final boolean result = defaultPromotionEngineResultService.getFired(promotionResult);
		verify(defaultPromotionResultService, times(1)).getFired(promotionResult);
		assertFalse(result);
	}

	@Test
	public void testLegacyGetCouldFire()
	{
		final boolean result = defaultPromotionEngineResultService.getCouldFire(promotionResult);
		verify(defaultPromotionResultService, times(1)).getCouldFire(promotionResult);
		assertFalse(result);
	}

	@Test
	public void testLegacyGetConsumedCount()
	{
		final long result = defaultPromotionEngineResultService.getConsumedCount(promotionResult, false);
		verify(defaultPromotionResultService, times(1)).getConsumedCount(promotionResult, false);
		assertEquals(0L, result);
	}

	@Test
	public void testLegacyGetTotalDiscount()
	{
		final double result = defaultPromotionEngineResultService.getTotalDiscount(promotionResult);
		verify(defaultPromotionResultService, times(1)).getTotalDiscount(promotionResult);
		assertEquals(0d, result, 0d);
	}

	@Test
	public void testLegacyGetPotentialProductPromotions()
	{
		final List<PromotionResultModel> result = defaultPromotionEngineResultService
				.getPotentialProductPromotions(promotionOrderResults, legacyPromotion);
		verify(defaultPromotionResultService, times(1)).getPotentialProductPromotions(promotionOrderResults, legacyPromotion);
		assertNotNull(result);
	}

	@Test
	public void testLegacyGetPotentialOrderPromotions()
	{
		final List<PromotionResultModel> result = defaultPromotionEngineResultService
				.getPotentialOrderPromotions(promotionOrderResults, legacyPromotion);
		verify(defaultPromotionResultService, times(1)).getPotentialOrderPromotions(promotionOrderResults, legacyPromotion);
		assertNotNull(result);
	}

	@Test
	public void testLegacyGetFiredProductPromotions()
	{
		final List<PromotionResultModel> result = defaultPromotionEngineResultService
				.getFiredProductPromotions(promotionOrderResults, legacyPromotion);
		verify(defaultPromotionResultService, times(1)).getFiredProductPromotions(promotionOrderResults, legacyPromotion);
		assertNotNull(result);
	}

	@Test
	public void testLegacyGetFiredOrderPromotions()
	{
		final List<PromotionResultModel> result = defaultPromotionEngineResultService.getFiredOrderPromotions(
				promotionOrderResults,
				legacyPromotion);
		verify(defaultPromotionResultService, times(1)).getFiredOrderPromotions(promotionOrderResults, legacyPromotion);
		assertNotNull(result);
	}

	@Test
	public void testLegacyGetDescription()
	{
		when(Boolean.valueOf(modelService.isNew(promotionResult))).thenReturn(Boolean.FALSE);

		final String RESULT_STRING = "LEGACY_GET_DESCRIPTION"; // NOSONAR
		when(defaultPromotionResultService.getDescription(promotionResult, Locale.US)).thenReturn(RESULT_STRING);
		final String result = defaultPromotionEngineResultService.getDescription(promotionResult, Locale.US);
		verify(defaultPromotionResultService, times(1)).getDescription(promotionResult, Locale.US);
		assertEquals(RESULT_STRING, result);
	}

	@Test
	public void testLegacyGetDescriptionPromotionResultNotPersisted()
	{
		when(Boolean.valueOf(modelService.isNew(promotionResult))).thenReturn(Boolean.TRUE);

		final String RESULT_STRING = "LEGACY_GET_DESCRIPTION"; // NOSONAR
		when(defaultPromotionResultService.getDescription(promotionResult, Locale.US)).thenReturn(RESULT_STRING);
		final String result = defaultPromotionEngineResultService.getDescription(promotionResult, Locale.US);
		verify(defaultPromotionResultService, times(0)).getDescription(promotionResult, Locale.US);
		Assertions.assertThat(result).isNull();
	}

	@Test
	public void testGetDescription() throws RuleConverterException
	{
		final String NOTHING_TO_REPLACE = "Nothing to replace"; // NOSONAR

		when(promotionResult.getPromotion()).thenReturn(ruleBasedPromotion);
		when(ruleBasedPromotion.getRule()).thenReturn(engineRule);
		when(promotionResult.getOrder()).thenReturn(order);
		when(order.getCurrency()).thenReturn(currency);
		when(currency.getIsocode()).thenReturn("USD");
		when(currency.getDigits()).thenReturn(Integer.valueOf(2));
		// message parameter setup
		final String UUID_1 = "1234-5678";
		final String UUID_2 = "5678-9012";

		final List<RuleParameterData> parameters = newArrayList(
				createRuleParameterData(UUID_1, RULE_PARAMETER_TYPE1, singletonMap("USD", new BigDecimal("20.00"))),
				createRuleParameterData(UUID_2, RULE_PARAMETER_TYPE2, FREE_GIFT_CODE));
		final String PARAMETERS_STRING = "parametersString"; // NOSONAR
		when(engineRule.getRuleParameters()).thenReturn(PARAMETERS_STRING);
		when(ruleParametersService.convertParametersFromString(PARAMETERS_STRING)).thenReturn(parameters);


		// message fired without parameter references
		when(ruleBasedPromotion.getMessageFired()).thenReturn(NOTHING_TO_REPLACE);
		final String resultNoReplacement = defaultPromotionEngineResultService.getDescription(promotionResult, Locale.US);
		assertEquals(NOTHING_TO_REPLACE, resultNoReplacement);

		// message fired with two parameters resolved
		when(ruleBasedPromotion.getMessageFired())
				.thenReturn("You spent {" + UUID_1 + "} and qualify for {" + UUID_2 + "}. Yay you!");
		final String resultWithReplacements = defaultPromotionEngineResultService.getDescription(promotionResult, Locale.US);
		assertEquals("You spent $20.00 and qualify for " + FREE_GIFT_NAME + ". Yay you!", resultWithReplacements);

	}

	@Test
	public void shouldGeneratePromotionDescriptionWithoutRemovingUnresolvedPlaceholders() throws RuleConverterException
	{
		//given
		final String firedMessage = "Message with placeholder that cannot be resolved to replace {missing-placeholder-data} and {another-placeholder-data}";
		given(promotionResult.getPromotion()).willReturn(ruleBasedPromotion);
		given(ruleBasedPromotion.getRule()).willReturn(engineRule);
		given(ruleBasedPromotion.getMessageFired()).willReturn(firedMessage);
		//when
		final String unresolvedPlaceholderMessage = defaultPromotionEngineResultService.getDescription(promotionResult, Locale.US);
		//then
		assertThat(firedMessage).isEqualTo(unresolvedPlaceholderMessage);
	}

	@Test
	public void shouldGeneratePromotionDescriptionWhenSomePlaceholderCannotBeResolved() throws RuleConverterException
	{
		//given
		final String firedMessage = "Message with {resolved-placeholder} that can be resolved and some ara {unresolved-placeholder}";
		final String expectedMessage = "Message with RESOLVED PLACEHOLDER that can be resolved and some ara {unresolved-placeholder}";

		given(promotionResult.getPromotion()).willReturn(ruleBasedPromotion);
		given(ruleBasedPromotion.getRule()).willReturn(engineRule);
		given(ruleBasedPromotion.getMessageFired()).willReturn(firedMessage);

		final String PARAMETERS_STRING = "parametersString"; // NOSONAR
		final List<RuleParameterData> parameters = newArrayList(
				createRuleParameterData("resolved-placeholder", RULE_PARAMETER_TYPE2, "RESOLVED PLACEHOLDER"));
		given(engineRule.getRuleParameters()).willReturn(PARAMETERS_STRING);
		given(ruleParametersService.convertParametersFromString(PARAMETERS_STRING)).willReturn(parameters);

		//when
		final String partiallyResolvedPlaceholderMessage = defaultPromotionEngineResultService.getDescription(promotionResult,
				Locale.US);
		//then
		assertThat(partiallyResolvedPlaceholderMessage).isEqualTo(expectedMessage);
	}

	protected RuleParameterData createRuleParameterData(final String uuid, final String type, final Object value)
	{
		final RuleParameterData data = new RuleParameterData();
		data.setUuid(uuid);
		data.setType(type);
		data.setValue(value);
		return data;
	}

	@Test
	public void testGetCouponCodeFromPromotion() throws RuleConverterException
	{
		when(promotionResult.getPromotion()).thenReturn(ruleBasedPromotion);
		when(couponCodeRetrievalStrategy.getCouponCodesFromPromotion(promotionResult))
				.thenReturn(Optional.of(Collections.singleton(GIVE_AWAY_COUPON_CODE)));

		final Optional<Set<String>> giveAwayCouponCodeList = defaultPromotionEngineResultService
				.getCouponCodesFromPromotion(promotionResult);
		assertEquals(GIVE_AWAY_COUPON_CODE, giveAwayCouponCodeList.get().stream().findFirst().get());
	}

	@Test
	public void testGetEmptyCouponCodeFromPromotion() throws RuleConverterException
	{
		final Set<AbstractPromotionActionModel> promotionActions = new HashSet<>();
		final AbstractRuleBasedPromotionActionModel ruleBasedAction = new AbstractRuleBasedPromotionActionModel();
		promotionActions.add(ruleBasedAction);

		when(promotionResult.getPromotion()).thenReturn(ruleBasedPromotion);

		when(promotionResult.getAllPromotionActions()).thenReturn(promotionActions);

		final Optional<Set<String>> giveAwayCouponCodeList = defaultPromotionEngineResultService
				.getCouponCodesFromPromotion(promotionResult);
		assertFalse(giveAwayCouponCodeList.isPresent());
	}

	@Test
	public void testGetCouponCodeFromPromotionWhenNull() throws RuleConverterException
	{
		final Set<AbstractPromotionActionModel> promotionActions = new HashSet<>();
		final AbstractRuleBasedPromotionActionModel ruleBasedAction = new AbstractRuleBasedPromotionActionModel();
		promotionActions.add(ruleBasedAction);


		when(promotionResult.getPromotion()).thenReturn(ruleBasedPromotion);

		when(promotionResult.getAllPromotionActions()).thenReturn(null);

		final Optional<Set<String>> giveAwayCouponCodeList = defaultPromotionEngineResultService
				.getCouponCodesFromPromotion(promotionResult);
		assertFalse(giveAwayCouponCodeList.isPresent());
	}
}
