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
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.dao.RuleBasedPromotionActionDao;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderEntryLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;
import de.hybris.platform.promotionengineservices.util.ActionUtils;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryDiscountPromotionEngineResultsPopulatorTest
{
	@InjectMocks
	private OrderEntryDiscountPromotionEngineResultsPopulator populator;
	@Mock
	private RuleBasedPromotionActionDao ruleBasedPromotionActionDao;
	@Mock
	private Populator<PromotionResultModel, PromotionEngineResult> promotionResultPopulator;
	@Mock
	private OrderEntryModel entry;
	private OrderEntryLevelPromotionEngineResults target;

	@Before
	public void setUp() throws Exception
	{
		target = new OrderEntryLevelPromotionEngineResults();
		populator.setActionUtils(new ActionUtils());

		given(entry.getQuantity()).willReturn(Long.valueOf(2l));
		given(entry.getBasePrice()).willReturn(Double.valueOf(10d));
		given(entry.getTotalPrice()).willReturn(Double.valueOf(17.05d));
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
		when(entry.getDiscountValues()).thenReturn(Lists.newArrayList(manualDiscountValue, promotionEngineDiscountValue));

		//when
		populator.populate(entry, target);
		//then
		verify(promotionResultPopulator, times(1))
				.populate(promotionResult, target.getPromotionEngineResults().iterator().next());
	}
	
	@Test
	public void shouldPopulateWithEmptyListIfNoDiscountValues() throws Exception
	{
		//given
		given(entry.getDiscountValues()).willReturn(newArrayList());
		//when
		populator.populate(entry, target);
		//then
		Assert.assertNotNull(target.getPromotionEngineResults());
		Assert.assertEquals(0, target.getPromotionEngineResults().size());
	}

	@Test
	public void shouldPopulateOrderEntryReference() throws Exception
	{
		//when
		populator.populate(entry, target);
		//then
		assertThat(target.getOrderEntry()).isEqualTo(entry);
	}

	@Test
	public void shouldCalculateOrderEntryTotalPrice() throws Exception
	{
		//when
		populator.populate(entry, target);
		//then
		assertThat(target.getTotalPrice()).isEqualByComparingTo("20.00");
	}

	@Test
	public void shouldCalculateEstimatedAdjustedBasePrice() throws Exception
	{
		//when
		populator.populate(entry, target);
		//then
		assertThat(target.getEstimatedAdjustedBasePrice()).isEqualByComparingTo("8.525");
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
		final Throwable throwable = catchThrowable(() -> populator.populate(entry,null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Target cannot be null");
	}

	@Test
	public void shouldReturnZeroWhenOrderEntryQuantityIsZero() throws Exception
	{
		// when quantity is zero (e.g. due to cancellation)
		given(entry.getQuantity()).willReturn(Long.valueOf(0L));

		// then populator should not fail with division by zero but return zero
		assertThat(populator.estimateAdjustedBasePriceTotalPrice(entry)).isEqualByComparingTo(BigDecimal.ZERO);
	}

}
