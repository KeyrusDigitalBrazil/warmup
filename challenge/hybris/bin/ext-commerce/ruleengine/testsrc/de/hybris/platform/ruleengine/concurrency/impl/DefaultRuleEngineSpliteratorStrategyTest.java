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
package de.hybris.platform.ruleengine.concurrency.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultRuleEngineSpliteratorStrategyTest
{
	@InjectMocks
	private DefaultRuleEngineSpliteratorStrategy strategy;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;

	@Test
	public void shouldCalculateNumberOfThreadsUsingExpression() throws Exception
	{
		//given
		given(configurationService.getConfiguration().getString(DefaultRuleEngineSpliteratorStrategy.FIXED_NO_OF_THREADS)).willReturn("#cores + 17 ");
		//when
		final int numberOfThreads = strategy.getNumberOfThreads();
		//then
		assertThat(numberOfThreads).isEqualTo(numberOfProcessors() + 17);
	}

	@Test
	public void shouldUseDefaultNumberOfThreadsIfThreadNumberIsNotConfigured() throws Exception
	{
		//given
		given(configurationService.getConfiguration().getString(DefaultRuleEngineSpliteratorStrategy.FIXED_NO_OF_THREADS)).willReturn(null);
		//when
		final int numberOfThreads = strategy.getNumberOfThreads();
		//then
		assertThat(numberOfThreads).isEqualTo(numberOfProcessors() + 1);
	}

	protected int numberOfProcessors()
	{
		return Runtime.getRuntime().availableProcessors();
	}
}
