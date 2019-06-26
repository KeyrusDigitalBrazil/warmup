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
package de.hybris.platform.ruleengineservices.maintenance.systemsetup.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.event.RuleEngineInitializedEvent;
import de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.versioning.ModuleVersionResolver;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.Optional;

import javax.annotation.Resource;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;


@IntegrationTest
public class DefaultRuleEngineSystemSetupIntegrationTest extends ServicelayerTest
{
	@Resource
	private EventService eventService;

	@Resource
	private DefaultPlatformRuleEngineService platformRuleEngineService;

	@Resource
	private RulesModuleDao rulesModuleDao;

	@Resource
	private ModuleVersionResolver<DroolsKIEModuleModel> moduleVersionResolver;

	@Mock
	private EventService mockedEventService;

	@Captor
	private ArgumentCaptor<AbstractEvent> eventArgumentCaptor;

	@Before
	public void setUp()
	{
		initMocks(this);
		platformRuleEngineService.setEventService(mockedEventService);
	}

	@After
	public void restore()
	{
		platformRuleEngineService.setEventService(eventService);
	}

	@Test
	public void shouldInitializeModuleFromImpexScript() throws ImpExException
	{
		importCsv("/ruleengineservices/test/rule/maintenance/systemsetup/defaultRuleEngineSystemSetup.impex", "utf-8");

		verify(mockedEventService, atLeastOnce()).publishEvent(eventArgumentCaptor.capture());

		assertThat(eventArgumentCaptor.getAllValues(),
				hasItem(new RuleEngineInitializedEventMatcher("ruleEngineSystemSetupTest-module-junit")));

		final DroolsKIEModuleModel rulesModule = rulesModuleDao.findByName("ruleEngineSystemSetupTest-module-junit");

		assertNotNull(rulesModule);

		final Optional<Long> deployedModuleVersion = moduleVersionResolver.getDeployedModuleVersion(rulesModule);
		assertTrue(deployedModuleVersion.isPresent());
		assertThat(deployedModuleVersion.get(), is(0L));
	}

	private static class RuleEngineInitializedEventMatcher extends BaseMatcher
	{
		private final String name;

		public RuleEngineInitializedEventMatcher(final String name)
		{
			this.name = name;
		}

		@Override
		public boolean matches(final Object compareTo)
		{
			if (compareTo instanceof RuleEngineInitializedEvent)
			{
				final RuleEngineInitializedEvent event = (RuleEngineInitializedEvent) compareTo;

				assertThat(event.getRulesModuleName(), is(name));

				return true;
			}

			return false;
		}

		@Override
		public void describeTo(final Description description)
		{
			description.appendText(String.format("RulesModuleName of RuleEngineInitializedEvent should be '%s'", name));
		}
	}
}
