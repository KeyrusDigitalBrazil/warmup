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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;


import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotionengineservices.promotionengine.PromotionMessageParameterResolutionStrategy;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.servicelayer.config.ConfigurationService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultListResolutionStrategyTest
{
	public static final String RESOLVED_PREFIX = "Resolved ";
	public static final String FIRST_ELEMENT = "A";
	public static final String RESOLVED_FIRST_ELEMENT = RESOLVED_PREFIX + FIRST_ELEMENT;
	public static final String SECOND_ELEMENT = "B";
	public static final String RESOLVED_SECOND_ELEMENT = RESOLVED_PREFIX + SECOND_ELEMENT;

	private DefaultListResolutionStrategy listResolutionStrategy = new DefaultListResolutionStrategy();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;

	private PromotionResultModel promotionResults = new PromotionResultModel();
	private Locale locale = Locale.ENGLISH;

	private PromotionMessageParameterResolutionStrategy resolutionStrategy = new PromotionMessageParameterResolutionStrategy(){
		@Override public Object getValue(final RuleParameterData data, final PromotionResultModel promotionResult,
				final Locale locale)
		{
			return RESOLVED_PREFIX + data.getValue();
		}
	};

	@Before
	public void setUp() throws Exception
	{
		listResolutionStrategy.setResolutionStrategy(resolutionStrategy);
		listResolutionStrategy.setConfigurationService(configurationService);

		given(configurationService.getConfiguration().getString(DefaultListResolutionStrategy.LIST_ITEMS_SEPARATOR_KEY,",")).willReturn(",");

	}

	@Test
	public void shouldIterateOverItemsAndCollectResolutionOfEach() throws Exception
	{
		//given
		final RuleParameterData data = new RuleParameterData();
		data.setValue(newArrayList(FIRST_ELEMENT, SECOND_ELEMENT));
		//when
		final String value = listResolutionStrategy.getValue(data, promotionResults, locale);
		//then
		assertThat(value).isEqualTo(RESOLVED_FIRST_ELEMENT +", "+ RESOLVED_SECOND_ELEMENT);
	}

	@Test
	public void shouldCustomizeJoiningSeparatorForResolvedElements() throws Exception
	{
		//given
		final RuleParameterData data = new RuleParameterData();
		data.setValue(newArrayList(FIRST_ELEMENT, SECOND_ELEMENT));
		given(configurationService.getConfiguration().getString(DefaultListResolutionStrategy.LIST_ITEMS_SEPARATOR_KEY,",")).willReturn("####");
		//when
		final String value = listResolutionStrategy.getValue(data, promotionResults, locale);
		//then
		assertThat(value).isEqualTo(RESOLVED_FIRST_ELEMENT +"#### "+ RESOLVED_SECOND_ELEMENT);
	}

	@Test
	public void shouldValidateRuleParameter() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> listResolutionStrategy.getValue(null, promotionResults, locale));
		//then
		assertThat(throwable).hasMessage("parameter data must not be null");
	}

	@Test
	public void shouldValidatePromotionResultParameter() throws Exception
	{
		//given
		final RuleParameterData data = new RuleParameterData();
		//when
		final Throwable throwable = catchThrowable(() -> listResolutionStrategy.getValue(data, null, locale));
		//then
		assertThat(throwable).hasMessage("parameter promotionResult must not be null");
	}

	@Test
	public void shouldValidateLocaleParameter() throws Exception
	{
		//given
		final RuleParameterData data = new RuleParameterData();
		//when
		final Throwable throwable = catchThrowable(() -> listResolutionStrategy.getValue(data, promotionResults, null));
		//then
		assertThat(throwable).hasMessage("parameter locale must not be null");
	}
}
