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
package de.hybris.platform.ruleengine.init;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.enums.DroolsEqualityBehavior;
import de.hybris.platform.ruleengine.enums.DroolsEventProcessingMode;
import de.hybris.platform.ruleengine.enums.DroolsSessionType;

import org.junit.Test;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;


@UnitTest
public class DefaultPlatformRuleEngineServiceConverterMethodsTest
{

	@Test
	public void testGetSessionType()
	{
		assertThat(RuleEngineKieModuleSwapper.getSessionType(DroolsSessionType.STATEFUL), is(KieSessionType.STATEFUL));
		assertThat(RuleEngineKieModuleSwapper.getSessionType(DroolsSessionType.STATELESS), is(KieSessionType.STATELESS));
	}

	@Test
	public void testGetEqualityBehaviorOption()
	{
		final DroolsEqualityBehavior equality = DroolsEqualityBehavior.EQUALITY;
		assertThat(RuleEngineKieModuleSwapper.getEqualityBehaviorOption(equality), is(EqualityBehaviorOption.EQUALITY));
		final DroolsEqualityBehavior identity = DroolsEqualityBehavior.IDENTITY;
		assertThat(RuleEngineKieModuleSwapper.getEqualityBehaviorOption(identity), is(EqualityBehaviorOption.IDENTITY));
	}

	@Test
	public void testGetEventProcessingOption()
	{
		assertThat(RuleEngineKieModuleSwapper.getEventProcessingOption(DroolsEventProcessingMode.STREAM),
					 is(EventProcessingOption.STREAM));
		assertThat(RuleEngineKieModuleSwapper.getEventProcessingOption(DroolsEventProcessingMode.CLOUD),
					 is(EventProcessingOption.CLOUD));
	}

	@Test
	public void testConvertLevel()
	{
		assertThat(RuleEngineKieModuleSwapper.convertLevel(null), is(nullValue()));
		assertThat(RuleEngineKieModuleSwapper.convertLevel(Level.ERROR), is(MessageLevel.ERROR));
		assertThat(RuleEngineKieModuleSwapper.convertLevel(Level.INFO), is(MessageLevel.INFO));
		assertThat(RuleEngineKieModuleSwapper.convertLevel(Level.WARNING), is(MessageLevel.WARNING));
	}

}
