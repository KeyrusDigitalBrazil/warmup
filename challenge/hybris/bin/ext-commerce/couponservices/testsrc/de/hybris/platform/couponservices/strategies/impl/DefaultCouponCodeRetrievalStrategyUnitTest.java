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
package de.hybris.platform.couponservices.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.RuleBasedAddCouponActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponCodeRetrievalStrategyUnitTest
{

	private static final String GIVE_AWAY_COUPON_CODE = "BUYMORE16";

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleBasedPromotionModel ruleBasedPromotion;

	private DefaultCouponCodeRetrievalStrategy strategy;


	@Before
	public void setup()
	{
		strategy = new DefaultCouponCodeRetrievalStrategy();
	}

	@Test
	public void testGetCouponCodeFromPromotion() throws RuleConverterException
	{
		final Set<AbstractPromotionActionModel> promotionActions = new HashSet<>();
		final RuleBasedAddCouponActionModel addCouponAction = new RuleBasedAddCouponActionModel();
		addCouponAction.setCouponCode(GIVE_AWAY_COUPON_CODE);
		promotionActions.add(addCouponAction);

		when(promotionResult.getPromotion()).thenReturn(ruleBasedPromotion);
		when(promotionResult.getAllPromotionActions()).thenReturn(promotionActions);

		final Optional<Set<String>> giveAwayCouponCodeList = strategy.getCouponCodesFromPromotion(promotionResult);
		assertEquals(GIVE_AWAY_COUPON_CODE, giveAwayCouponCodeList.get().stream().findFirst().get());
	}

}
