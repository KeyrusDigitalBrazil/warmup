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
import static de.hybris.platform.promotionengineservices.assertions.PromotionEngineServicesAssertions.assertThat;
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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderEntryLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionEngineResultsPopulatorTest
{
	@InjectMocks
	private PromotionEngineResultsPopulator populator;
	@Mock
	private Converter<AbstractOrderModel, OrderLevelPromotionEngineResults> orderDiscountPromotionsConverter;
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryLevelPromotionEngineResults> orderEntryDiscountPromotionConverter;
	private PromotionEngineResults target;
	@Mock
	private OrderModel order;
	@Mock
	private OrderEntryModel orderEntry;
	private OrderLevelPromotionEngineResults orderLevelPromotionEngineResults;
	private OrderEntryLevelPromotionEngineResults orderEntryLevelPromotionsResult;

	@Before
	public void setUp() throws Exception
	{
		target = new PromotionEngineResults();
		orderLevelPromotionEngineResults = new OrderLevelPromotionEngineResults();
		orderEntryLevelPromotionsResult = new OrderEntryLevelPromotionEngineResults();
		given(order.getEntries()).willReturn(newArrayList(orderEntry));

		given(orderDiscountPromotionsConverter.convert(order)).willReturn(orderLevelPromotionEngineResults);
		given(orderEntryDiscountPromotionConverter.convertAll(order.getEntries())).willReturn(newArrayList(orderEntryLevelPromotionsResult));
	}

	@Test
	public void shouldPopulateOrderLevelDiscountRelatedPromotions() throws Exception
	{
		//when
		populator.populate(order,target);
		//test
		verify(orderDiscountPromotionsConverter, times(1)).convert(order);
	}


	@Test
	public void shouldPopulateOrderEntryLevelDiscountRelatedPromotions() throws Exception
	{
		//when
		populator.populate(order,target);
		//test
		verify(orderEntryDiscountPromotionConverter, times(1)).convertAll(order.getEntries());
	}
	@Test
	public void shouldRaiseExceptionWhenSourceToPopulateIsNull() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> populator.populate(null, target));
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
