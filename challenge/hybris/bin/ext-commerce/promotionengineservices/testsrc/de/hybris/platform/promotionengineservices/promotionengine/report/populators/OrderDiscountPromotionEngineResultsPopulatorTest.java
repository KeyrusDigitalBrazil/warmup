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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.dao.RuleBasedPromotionActionDao;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.util.DiscountValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderDiscountPromotionEngineResultsPopulatorTest
{
	@InjectMocks
	private OrderDiscountPromotionEngineResultsPopulator populator;
	@Mock
	private RuleBasedPromotionActionDao ruleBasedPromotionActionDao;
	@Mock
	private Populator<PromotionResultModel, PromotionEngineResult> promotionResultPopulator;
	@Mock
	private OrderModel order;
	private OrderLevelPromotionEngineResults target;

	@Before
	public void setUp() throws Exception
	{
		target = new OrderLevelPromotionEngineResults();
		populator.setActionUtils(new ActionUtils());
	}

	@Test
	public void shouldPopulatePromotionEngineDiscounts() throws Exception
	{
		//given
		final DiscountValue manualDiscountValue = mock(DiscountValue.class);
		when(manualDiscountValue.getCode()).thenReturn("code");
		final DiscountValue promotionEngineDiscountValue = mock(DiscountValue.class);
		when(promotionEngineDiscountValue.getCode()).thenReturn("Action[859fe7dc-5eb0-4757-9701-7a9ef3929cec]");
		final PromotionResultModel promotionResult = mock(PromotionResultModel.class);
		final AbstractRuleBasedPromotionActionModel ruleBasedPromotionAction = mock(AbstractRuleBasedPromotionActionModel.class);
		when(ruleBasedPromotionAction.getGuid()).thenReturn("Action[859fe7dc-5eb0-4757-9701-7a9ef3929cec]");
		when(ruleBasedPromotionAction.getPromotionResult()).thenReturn(promotionResult);
		when(ruleBasedPromotionActionDao.findRuleBasedPromotions(any(), anyCollectionOf(DiscountValue.class))).thenReturn(
				Lists.newArrayList(ruleBasedPromotionAction));
		when(order.getGlobalDiscountValues()).thenReturn(Lists.newArrayList(manualDiscountValue, promotionEngineDiscountValue));

		//when
		populator.populate(order, target);
		//then
		verify(promotionResultPopulator, times(1))
				.populate(promotionResult, target.getPromotionEngineResults().iterator().next());
	}

	@Test
	public void shouldPopulateWithEmptyListIfNoDiscountValues() throws Exception
	{
		//given
		given(order.getGlobalDiscountValues()).willReturn(newArrayList());
		//when
		populator.populate(order, target);
		//then
		Assert.assertNotNull(target.getPromotionEngineResults());
		Assert.assertEquals(0, target.getPromotionEngineResults().size());
	}

	@Test
	public void shouldPopulateOrderReference() throws Exception
	{
		//when
		populator.populate(order, target);
		//then
		assertThat(target.getOrder()).isEqualTo(order);
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
		final Throwable throwable = catchThrowable(() -> populator.populate(order,null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Target cannot be null");
	}
}
