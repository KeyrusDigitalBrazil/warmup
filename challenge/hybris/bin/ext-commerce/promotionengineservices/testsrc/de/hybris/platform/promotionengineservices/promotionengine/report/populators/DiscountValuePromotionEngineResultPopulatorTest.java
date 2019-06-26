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
package de.hybris.platform.promotionengineservices.promotionengine.report.populators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.builder.PromotionResultMockBuilder;
import de.hybris.platform.promotionengineservices.promotionengine.report.dao.RuleBasedPromotionActionDao;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.util.DiscountValue;
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DiscountValuePromotionEngineResultPopulatorTest
{
	@InjectMocks
	private DiscountValuePromotionEngineResultPopulator populator;
	@Mock
	private RuleBasedPromotionActionDao ruleBasedPromotionActionDao;
	@Mock
	private PromotionResultService promotionResultService;
	@Mock
	private Populator<PromotionResultModel, PromotionEngineResult> promotionResultPopulator;

	@Mock
	private DiscountValue discount;
	@Mock
	private AbstractRuleBasedPromotionActionModel action;
	private PromotionEngineResult target;

	private PromotionResultMockBuilder builder = new PromotionResultMockBuilder();

	@Before
	public void setUp() throws Exception
	{
		target = new PromotionEngineResult();
		given(discount.getCode()).willReturn("guid");
	}

	@Test
	public void shouldPopulatePromotionResultByDelegatingJobToPromotionResultPopulator() throws Exception
	{
		//given
		final PromotionResultModel promotionResult = builder.createSamplePromotionResult(promotionResultService);
		given(ruleBasedPromotionActionDao.findRuleBasedPromotionByGuid(discount.getCode())).willReturn(action);
		given(action.getPromotionResult()).willReturn(promotionResult);
		//when
		populator.populate(discount,target);
		//then
		verify(promotionResultPopulator).populate(promotionResult,target);
	}

	@Test
	public void shouldRaiseExceptionWhenSourceToPopulateIsNull() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> populator.populate(null,target));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Source cannot be null");
	}


	@Test
	public void shouldRaiseExceptionWhenTargetIsNull() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> populator.populate(discount,null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Target cannot be null");
	}
}
