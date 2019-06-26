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
package de.hybris.platform.promotionengineservices.promotionengine.report.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReportPromotionServiceTest
{
	@InjectMocks
	private DefaultReportPromotionService promotionReportService;
	@Mock
	private Converter<AbstractOrderModel, PromotionEngineResults> promotionEngineResultsConverter;
	@Mock
	private OrderModel order;

	@Test
	public void shouldUseConverterToProvideResults() throws Exception
	{
		//when
		promotionReportService.report(order);
		//then
		verify(promotionEngineResultsConverter).convert(order);
	}

	@Test
	public void shouldRaiseExceptionWhenGettingReportForNonExistingOrder() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> promotionReportService.report(null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Order cannot be null");
	}
}
