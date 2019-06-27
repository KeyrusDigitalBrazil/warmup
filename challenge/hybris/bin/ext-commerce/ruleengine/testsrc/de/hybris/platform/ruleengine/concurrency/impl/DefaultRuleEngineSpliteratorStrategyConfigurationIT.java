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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.concurrency.RuleEngineSpliteratorStrategy;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;


@IntegrationTest
public class DefaultRuleEngineSpliteratorStrategyConfigurationIT extends ServicelayerTest
{
	@Resource
	private RuleEngineSpliteratorStrategy ruleEngineSpliteratorStrategy;

	@Test
	public void shouldHaveConfiguredDefaultNumberOfWorkerThreads() throws Exception
	{
		//when
		final int defaultNumberOfThreads = ruleEngineSpliteratorStrategy.getNumberOfThreads();
		//then
		assertThat(defaultNumberOfThreads).isEqualTo(numberOfProcessors() + 1);
	}

	protected int numberOfProcessors()
	{
		return Runtime.getRuntime().availableProcessors();
	}
}
