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
package de.hybris.platform.ruleengineservices.maintenance.impl;

import static de.hybris.platform.ruleengineservices.maintenance.impl.RulesCompilationGuardedSuspension.VERIFICATION_LATCH_TIMEOUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.concurrency.GuardStatus;
import de.hybris.platform.ruleengineservices.maintenance.RulesCompilationInProgressQueryEvent;
import de.hybris.platform.ruleengineservices.maintenance.RulesCompilationInProgressResponseEvent;
import de.hybris.platform.ruleengineservices.util.EventServiceStub;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationListener;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RulesCompilationGuardedSuspensionUnitTest
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private static final String MODULE_NAME = "MODULE_NAME";

	@InjectMocks
	private RulesCompilationGuardedSuspension guardedSuspension;
	private EventService eventService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Before
	public void setUp()
	{
		eventService = new EventServiceStub();
		guardedSuspension.setEventService(eventService);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(eq(VERIFICATION_LATCH_TIMEOUT), any())).thenReturn(3L);
	}

	@Test
	public void testCheckPreconditionsNoResponseListeners()
	{
		final GuardStatus guardStatus = guardedSuspension.checkPreconditions(MODULE_NAME);
		assertThat(guardStatus.getType()).isEqualTo(GuardStatus.Type.GO);
	}

	@Test
	public void testCheckPreconditionsOneResponseListener()
	{
		eventService.registerEventListener(new RulesCompilationInProgressApplicationListener(eventService, MODULE_NAME));
		final GuardStatus guardStatus = guardedSuspension.checkPreconditions(MODULE_NAME);
		assertThat(guardStatus.getType()).isEqualTo(GuardStatus.Type.NO_GO);
	}

	@Test
	public void testCheckPreconditionsResponseListenerOnOtherModuleName()
	{
		eventService.registerEventListener(new RulesCompilationInProgressApplicationListener(eventService, "OTHER_MODULE_NAME"));
		final GuardStatus guardStatus = guardedSuspension.checkPreconditions(MODULE_NAME);
		assertThat(guardStatus.getType()).isEqualTo(GuardStatus.Type.GO);
	}

	@Test
	public void testCheckPreconditionsMultipleResponseListeners()
	{
		eventService.registerEventListener(new RulesCompilationInProgressApplicationListener(eventService, MODULE_NAME));
		eventService.registerEventListener(new RulesCompilationInProgressApplicationListener(eventService, MODULE_NAME));

		final GuardStatus guardStatus = guardedSuspension.checkPreconditions(MODULE_NAME);
		assertThat(guardStatus.getType()).isEqualTo(GuardStatus.Type.NO_GO);
	}

	private static class RulesCompilationInProgressApplicationListener
				 implements ApplicationListener<RulesCompilationInProgressQueryEvent>
	{

		private final String moduleName;
		private final EventService eventService;

		public RulesCompilationInProgressApplicationListener(final EventService eventService, final String moduleName)
		{
			this.eventService = eventService;
			this.moduleName = moduleName;
		}

		@Override
		public void onApplicationEvent(final RulesCompilationInProgressQueryEvent queryEvent)
		{
			if (moduleName.equals(queryEvent.getModuleName()))
			{
				eventService.publishEvent(new RulesCompilationInProgressResponseEvent(moduleName));
			}
		}
	}

}
