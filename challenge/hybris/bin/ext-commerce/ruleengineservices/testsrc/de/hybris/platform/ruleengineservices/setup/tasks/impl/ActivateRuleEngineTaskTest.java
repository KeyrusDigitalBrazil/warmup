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
package de.hybris.platform.ruleengineservices.setup.tasks.impl;

import static org.mockito.Mockito.verify;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengine.constants.RuleEngineConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ActivateRuleEngineTaskTest
{
	@InjectMocks
	private ActivateRuleEngineTask task;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;
	@Mock
	private SystemSetupContext context;

	@Test
	public void shouldUpdateRuleEngineActivationProperty() throws Exception
	{
		//when
		task.execute(context);
		//then
		verify(configurationService.getConfiguration()).setProperty(RuleEngineConstants.RULE_ENGINE_ACTIVE, "true");
	}
}
