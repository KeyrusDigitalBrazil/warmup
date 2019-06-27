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
package de.hybris.platform.droolsruleengineservices.agendafilter.impl;

import static java.lang.Integer.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.kie.api.runtime.rule.Match;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rrd.RuleConfigurationRRD;


@UnitTest
public class ActionTriggeringLimitAgendaFilterTest
{
	private ActionTriggeringLimitAgendaFilter actionTriggeringLimitAgendaFilter = new ActionTriggeringLimitAgendaFilter();
	private final Match match = null;

	@Test
	public void shouldAcceptWhenMaxExecutionThresholdIsNotReached() throws Exception
	{
		//given
		final RuleConfigurationRRD config = createRuleConfigurationRRD(1, 2);
		//when
		final boolean result = actionTriggeringLimitAgendaFilter.accept(match, config);
		//then
		assertThat(result).isTrue();
	}

	protected RuleConfigurationRRD createRuleConfigurationRRD(final int currentRuns, final int maxAllowedRuns)
	{
		final RuleConfigurationRRD config = new RuleConfigurationRRD();
		config.setMaxAllowedRuns(valueOf(maxAllowedRuns));
		config.setCurrentRuns(valueOf(currentRuns));
		return config;
	}

	@Test
	public void shouldRejectWhenMaxExecutionThresholdIsEqualCurrentRuns() throws Exception
	{
		//given
		final RuleConfigurationRRD config = createRuleConfigurationRRD(2, 2);
		//when
		final boolean result = actionTriggeringLimitAgendaFilter.accept(match, config);
		//then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldRejectWhenMaxExecutionThresholdIsReached() throws Exception
	{
		//given
		final RuleConfigurationRRD config = createRuleConfigurationRRD(3, 2);
		//when
		final boolean result = actionTriggeringLimitAgendaFilter.accept(match, config);
		//then
		assertThat(result).isFalse();
	}
}
