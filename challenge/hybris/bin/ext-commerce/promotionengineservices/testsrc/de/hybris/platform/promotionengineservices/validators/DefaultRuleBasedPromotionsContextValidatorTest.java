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
package de.hybris.platform.promotionengineservices.validators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotionengineservices.validators.impl.DefaultRuleBasedPromotionsContextValidator;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleBasedPromotionsContextValidatorTest
{
	private DefaultRuleBasedPromotionsContextValidator validator = new DefaultRuleBasedPromotionsContextValidator();
	@Mock
	private RuleBasedPromotionModel promotion;
	@Mock
	private CatalogVersionModel catalogVersion;
	private RuleType ruleType = RuleType.PROMOTION;

	@Before
	public void setUp() throws Exception
	{
		given(promotion.getRule()).willReturn(new DroolsRuleModel());
	}

	@Test
	public void shouldRaiseExceptionWhenPromoResultIsMissing()
	{
		//when
		final Throwable throwable = ThrowableAssert
				.catchThrowable(() -> validator.isApplicable(null, catalogVersion, ruleType));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The provided ruleBasedPromotion cannot be NULL here");
	}

	@Test
	public void shouldRaiseExceptionWhenCatalogVersionIsMissing()
	{
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validator.isApplicable(promotion, null, RuleType.PROMOTION));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The provided catalogVersion cannot be NULL here");
	}

	@Test
	public void shouldRaiseExceptionWhenRuleTypeIsMissing()
	{
		//when
		final Throwable throwable = ThrowableAssert
				 .catchThrowable(() ->validator.isApplicable(promotion, catalogVersion, null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
					 .hasMessage("The provided ruleType cannot be NULL here");
	}

	@Test
	public void shouldBeFalseIfNonDroolsRuleRelatedInspection()
	{
		//given
		given(promotion.getRule()).willReturn(new AbstractRuleEngineRuleModel());
		//when
		final boolean applicable = validator.isApplicable(promotion, catalogVersion, RuleType.PROMOTION);
		//then
		assertThat(applicable).isFalse();
	}
}
