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
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anySet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.builder.PromotionResultMockBuilder;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderEntryLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResults;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NonDiscountPromotionEngineResultsClassifyingPopulatorTest
{
	@InjectMocks
	private NonDiscountPromotionEngineResultsClassifyingPopulator populator;
	@Mock
	private Converter<PromotionResultModel, PromotionEngineResult> promotionResultConverter;
	@Mock
	private OrderModel order;
	@Mock
	private AbstractOrderEntryModel orderEntry;
	private PromotionEngineResults target;
	private PromotionResultMockBuilder builder = new PromotionResultMockBuilder();
	private OrderLevelPromotionEngineResults orderLevelPromotionEngineResult;
	private OrderEntryLevelPromotionEngineResults orderEntryLevelPromotionEngineResult;

	@Before
	public void setup() throws Exception
	{
		target = new PromotionEngineResults();
		target.setOrderEntryLevelPromotionEngineResults(newArrayList());
		target.setOrderLevelPromotionEngineResults(new OrderLevelPromotionEngineResults());

		orderEntryLevelPromotionEngineResult = new OrderEntryLevelPromotionEngineResults();
		orderEntryLevelPromotionEngineResult.setOrderEntry(orderEntry);

		orderLevelPromotionEngineResult = new OrderLevelPromotionEngineResults();
		orderLevelPromotionEngineResult.setOrder(order);
	}

	@Test
	public void cannotClassifyPromotionEngineResultsWhenNoPromotionAppliedToOrder() throws Exception
	{
		//given
		given(promotionResultConverter.convertAll(anySet())).willReturn(newArrayList());
		//when
		populator.populate(order,target);
		//then
		assertThat(target.getOrderLevelPromotionEngineResults().getPromotionEngineResults()).isNull();
		assertThat(target.getOrderEntryLevelPromotionEngineResults()).isEmpty();
	}

	@Test
	public void shouldClassifyPromotionAsOrderLevelPromotion() throws Exception
	{
		//given
		final PromotionEngineResult promotionEngineResult = buildOrderLevelPromotions();

		target.setOrderLevelPromotionEngineResults(orderLevelPromotionEngineResult);
		assertThat(orderLevelPromotionEngineResult.getPromotionEngineResults()).isNullOrEmpty();
		//when
		populator.populate(order,target);
		//then
		assertThat(orderLevelPromotionEngineResult.getPromotionEngineResults()).containsExactly(promotionEngineResult);
	}

	protected PromotionEngineResult buildOrderLevelPromotions()
	{
		final PromotionEngineResult promotionEngineResult = createOrderLevelPromotionEngineResult();
		promotionEngineResult.setFired(true);
		given(order.getAllPromotionResults()).willReturn(newHashSet(promotionEngineResult.getPromotionResult()));
		given(promotionResultConverter.convertAll(anySet())).willReturn(newArrayList(promotionEngineResult));
		return promotionEngineResult;
	}


	@Test
	public void shouldSkipClassifyingIfPromotionIsNotAllowedByFilter() throws Exception
	{
		//given
		buildOrderLevelEntryPotentialPromotions();
		target.setOrderLevelPromotionEngineResults(orderLevelPromotionEngineResult);
		target.setOrderEntryLevelPromotionEngineResults(newArrayList(orderEntryLevelPromotionEngineResult));
		assertThat(orderEntryLevelPromotionEngineResult.getPromotionEngineResults()).isNullOrEmpty();
		//when
		populator.populate(order,target);
		//then
		assertThat(orderEntryLevelPromotionEngineResult.getPromotionEngineResults()).isNullOrEmpty();
	}

	@Test
	public void shouldClassifyPromotionAsOrderEntryLevelPromotion() throws Exception
	{
		//given
		final PromotionEngineResult promotionEngineResult = buildOrderLevelEntryFiredPromotions();

		target.setOrderLevelPromotionEngineResults(orderLevelPromotionEngineResult);
		target.setOrderEntryLevelPromotionEngineResults(newArrayList(orderEntryLevelPromotionEngineResult));
		assertThat(orderEntryLevelPromotionEngineResult.getPromotionEngineResults()).isNullOrEmpty();
		//when
		populator.populate(order,target);
		//then
		assertThat(orderEntryLevelPromotionEngineResult.getPromotionEngineResults()).containsExactly(promotionEngineResult);
	}

	protected PromotionEngineResult buildOrderLevelEntryFiredPromotions()
	{
		given(order.getEntries()).willReturn(newArrayList(orderEntry));
		final PromotionEngineResult promotionEngineResult = createOrderEntryLevelFiredPromotionEngineResult(orderEntry);
		given(order.getAllPromotionResults()).willReturn(newHashSet(promotionEngineResult.getPromotionResult()));
		given(promotionResultConverter.convertAll(anySet())).willReturn(newArrayList(promotionEngineResult));
		return promotionEngineResult;
	}

	protected PromotionEngineResult buildOrderLevelEntryPotentialPromotions()
	{
		given(order.getEntries()).willReturn(newArrayList(orderEntry));
		final PromotionEngineResult promotionEngineResult = createOrderEntryLevelPotentialPromotionEngineResult(orderEntry);
		given(order.getAllPromotionResults()).willReturn(newHashSet(promotionEngineResult.getPromotionResult()));
		given(promotionResultConverter.convertAll(anySet())).willReturn(newArrayList(promotionEngineResult));
		return promotionEngineResult;
	}

	protected PromotionEngineResult createOrderEntryLevelPotentialPromotionEngineResult(AbstractOrderEntryModel orderEntry)
	{
		final PromotionEngineResult promotionEngineResult = new PromotionEngineResult();
		promotionEngineResult.setPromotionResult( builder.createSamplePromotionResultForOrderEntry(orderEntry) );
		promotionEngineResult.setFired(false);
		return promotionEngineResult;
	}

	protected PromotionEngineResult createOrderEntryLevelFiredPromotionEngineResult(AbstractOrderEntryModel orderEntry)
	{
		final PromotionEngineResult promotionEngineResult = new PromotionEngineResult();
		promotionEngineResult.setPromotionResult( builder.createSamplePromotionResultForOrderEntry(orderEntry) );
		promotionEngineResult.setFired(true);
		return promotionEngineResult;
	}

	protected PromotionEngineResult createOrderLevelPromotionEngineResult()
	{
		final PromotionEngineResult promotionEngineResult = new PromotionEngineResult();
		promotionEngineResult.setPromotionResult( builder.createSamplePromotionResultForOrder() );
		return promotionEngineResult;
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
