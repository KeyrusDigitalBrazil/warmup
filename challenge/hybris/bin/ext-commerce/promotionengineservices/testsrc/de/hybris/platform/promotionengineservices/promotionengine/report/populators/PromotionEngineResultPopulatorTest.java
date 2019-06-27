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

import static de.hybris.platform.promotionengineservices.assertions.PromotionEngineServicesAssertions.assertThat;
import static de.hybris.platform.promotionengineservices.promotionengine.report.builder.PromotionResultMockBuilder.CODE;
import static de.hybris.platform.promotionengineservices.promotionengine.report.builder.PromotionResultMockBuilder.DESCRIPTION;
import static de.hybris.platform.promotionengineservices.promotionengine.report.builder.PromotionResultMockBuilder.NAME;
import static org.assertj.core.api.Assertions.catchThrowable;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotionengineservices.promotionengine.report.builder.PromotionResultMockBuilder;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.PromotionResultModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionEngineResultPopulatorTest
{
	@InjectMocks
	private PromotionEngineResultPopulator populator;
	@Mock
	private PromotionResultService promotionResultService;
	private final PromotionResultMockBuilder builder = new PromotionResultMockBuilder();
	private PromotionEngineResult target;

	@Before
	public void setUp() throws Exception
	{
		target = new PromotionEngineResult();
	}

	@Test
	public void shouldPopulatePromotionResult() throws Exception
	{
		//given
		final PromotionResultModel source = builder.createSamplePromotionResult(promotionResultService);

		//when
		populator.populate(source, target);
		//then
		assertThat(target).hasCode(CODE).hasName(NAME).hasDescription(DESCRIPTION);

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
		final Throwable throwable = catchThrowable(() -> populator.populate(new PromotionResultModel(), null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Target cannot be null");
	}

	@Test
	public void shouldClassifyAsFiredPromotion() throws Exception
	{
		//given
		final PromotionResultModel source = builder.createSampleFiredPromotionResult(promotionResultService);
		//when
		populator.populate(source, target);
		//then
		assertThat(target).isFiredPromotion();
	}

	@Test
	public void shouldClassifyAsPotentialPromotion() throws Exception
	{
		//given
		final PromotionResultModel source = builder.createSamplePotentialPromotionResult(promotionResultService);
		//when
		populator.populate(source, target);
		//then
		assertThat(target).isPotentialPromotion();
	}
}
