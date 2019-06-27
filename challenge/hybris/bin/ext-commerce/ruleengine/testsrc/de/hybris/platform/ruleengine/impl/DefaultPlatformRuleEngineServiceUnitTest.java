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
package de.hybris.platform.ruleengine.impl;

import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.SWAPPING_IS_BLOCKING;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_CONCURRENCY_LEVEL;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_INITIAL_CAPACITY;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_LOAD_FACTOR;
import static de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper.WORKER_PRE_DESTROY_TIMEOUT;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.cache.RuleEngineCacheService;
import de.hybris.platform.ruleengine.constants.RuleEngineConstants;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.RulePublishingSpliterator;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.init.tasks.PostRulesModuleSwappingTasksProvider;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.strategies.DroolsKIEBaseFinderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformRuleEngineServiceUnitTest
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private DroolsKIEBaseFinderStrategy droolsKIEBaseFinderStrategy;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private ModelService modelService;
	@Mock
	private KieServices kieServices;
	@InjectMocks
	private DefaultPlatformRuleEngineService service;
	private Configuration configurationMock;
	@Mock
	private RuleEngineActionResult result;
	@Mock
	private KieFileSystem kieFileSystem;
	@Mock
	private KieModuleModel kieModuleModel;
	@Mock
	private KieRepository kieRepository;
	@Mock
	private ReleaseId releaseId;
	@Mock
	private KieBuilder kieBuilder;
	@Mock
	private Results results;
	@InjectMocks
	private DefaultRuleEngineKieModuleSwapper ruleEngineKieModuleSwapper;
	@InjectMocks
	private ConcurrentMapFactory concurrentMapFactory;
	@Mock
	private Tenant currentTenant;
	@Mock
	private Configuration configuration;
	@Mock
	private AbstractRulesModuleModel ruleModule;
	@Mock
	private RuleEngineCacheService ruleEngineCacheService;
	@Mock
	private KIEModuleCacheBuilder cache;
	@Mock
	private MemoryKieModule kieModule;
	@Mock
	private MemoryFileSystem memoryFileSystem;
	@Mock
	private RulePublishingSpliterator rulePublishingSpliterator;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private PostRulesModuleSwappingTasksProvider postRulesModuleSwappingTasksProvider;

	@Before
	public void setUp()
	{
		ruleEngineKieModuleSwapper.setConcurrentMapFactory(concurrentMapFactory);
		service.setConcurrentMapFactory(concurrentMapFactory);
		service.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(eq(WORKER_PRE_DESTROY_TIMEOUT), anyLong())).thenReturn(5000L);
		when(configuration.getInt(eq(WORKER_MAP_INITIAL_CAPACITY), anyInt())).thenReturn(3);
		when(configuration.getFloat(eq(WORKER_MAP_LOAD_FACTOR), anyFloat())).thenReturn(0.75F);
		when(configuration.getInt(eq(WORKER_MAP_CONCURRENCY_LEVEL), anyInt())).thenReturn(2);
		when(configuration.getBoolean(eq(SWAPPING_IS_BLOCKING), anyBoolean())).thenReturn(false);
		when(ruleEngineCacheService.createKIEModuleCacheBuilder(any())).thenReturn(cache);

		ruleEngineKieModuleSwapper.setup();
		service.setup();

		//set up base configuration:
		configurationMock = new BaseConfiguration();
		when(kieBuilder.getResults()).thenReturn(results);
		when(kieServices.newKieModuleModel()).thenReturn(kieModuleModel);
		when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
		when(kieServices.getRepository()).thenReturn(kieRepository);
		when(kieServices.newReleaseId(anyString(), anyString(), anyString())).thenReturn(releaseId);
		when(kieServices.newKieBuilder(any(KieFileSystem.class))).thenReturn(kieBuilder);
		when(configurationService.getConfiguration()).thenReturn(configurationMock);

		when(kieBuilder.getKieModule()).thenReturn(kieModule);
		when(kieModule.getMemoryFileSystem()).thenReturn(memoryFileSystem);

		when(postRulesModuleSwappingTasksProvider.getTasks(any())).thenReturn(singletonList(Object::new));
	}

	@Test
	public void testUpdateEngineRuleAbstractRuleEngineRuleModelWhenRuleIsNull()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("ruleEngineRule");
		service.updateEngineRule(null, ruleModule);
	}

	@Test
	public void testUpdateEngineRuleAbstractRuleEngineRuleModelWhenRuleoModuleIsNull()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("rulesModule");
		service.updateEngineRule(mock(DroolsRuleModel.class), null);
	}

	@Test
	public void testUpdateEngineRuleAbstractRuleEngineRuleModelForSuccessCondition()
	{
		final DroolsRuleModel ruleEngineRule = mock(DroolsRuleModel.class);
		final DroolsKIEModuleModel droolsKIEModuleModel = mock(DroolsKIEModuleModel.class);
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		when(droolsKIEBaseFinderStrategy.getKIEBaseForKIEModule(droolsKIEModuleModel)).thenReturn(kieBase);
		final RuleEngineActionResult actionResult = service.updateEngineRule(ruleEngineRule, droolsKIEModuleModel);
		verify(ruleEngineRule).setKieBase(kieBase);
		verify(modelService).save(ruleEngineRule);
		assertFalse(actionResult.isActionFailed());
		assertThat(actionResult.getMessagesAsString(MessageLevel.INFO), is(not(nullValue())));
	}

	@Test
	public void testUpdateEngineRuleAbstractRuleEngineRuleEngineRuleIsNull()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("ruleEngineRule");
		service.updateEngineRule(null, null);
	}

	@Test
	public void testUpdateEngineRuleAbstractRuleEngineRuleModelisNull()
	{
		final AbstractRuleEngineRuleModel rule = mock(AbstractRuleEngineRuleModel.class);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("rulesModule");
		service.updateEngineRule(rule, null);
	}

	@Test
	public void testGetRuleByCodeAndModule()
	{
		service.getRuleForCodeAndModule("random string code", "module_name");
		verify(engineRuleDao).getRuleByCode(anyString(), anyString());
	}

	protected DroolsRuleModel createDroolsRule(final String ruleCode, final String kieModuleName)
	{
		final DroolsRuleModel rule = new DroolsRuleModel();
		rule.setCode(ruleCode);
		final DroolsKIEBaseModel kieBase = new DroolsKIEBaseModel();
		final DroolsKIEModuleModel kieModule = new DroolsKIEModuleModel();
		kieModule.setName(kieModuleName);
		kieBase.setKieModule(kieModule);
		rule.setKieBase(kieBase);
		return rule;
	}

	@Test
	public void testResetFlagMapOnAnyException()
	{
		// given (when switching causes an unrelated exception)
		final DroolsKIEModuleModel module = mock(DroolsKIEModuleModel.class);
		when(module.getName()).thenReturn("myModuleName");
		doThrow(new ModelSavingException("testResetFlagMapOnAnyException")).when(modelService).save(module);
		final KieContainerListener listener = mock(KieContainerListener.class);

		// when (switching modules)
		service.switchKieModule(module, listener, false, false, result, singletonList(() -> new Object()));

		// then (the multi-flag needs to be reset and the listener.onFailure has been called)
		assertTrue("should be able to set the flag for the module again",
				service.getInitializationMultiFlag().compareAndSet("myModuleName", false, true));
		verify(listener).onFailure(result);
	}

	@Test
	public void testCreateRuleEngineActionResult()
	{

		assertTestCreateRuleEngineActionResult(null, null, false, null);
		assertTestCreateRuleEngineActionResult("Message", null, true, MessageLevel.INFO);
		assertTestCreateRuleEngineActionResult("Message", "moduleName", true, MessageLevel.INFO);
		assertTestCreateRuleEngineActionResult("Message", "moduleName", true, MessageLevel.WARNING);
		assertTestCreateRuleEngineActionResult("Message", "moduleName", true, MessageLevel.ERROR);
		assertTestCreateRuleEngineActionResult("Message", "moduleName", false, MessageLevel.INFO);
		assertTestCreateRuleEngineActionResult("Message", "moduleName", false, MessageLevel.WARNING);
		assertTestCreateRuleEngineActionResult("Message", "moduleName", false, MessageLevel.ERROR);
	}

	private void assertTestCreateRuleEngineActionResult(final String message, final String moduleName, final boolean success,
			final MessageLevel level)
	{
		final RuleEngineActionResult rear = service.createRuleEngineActionResult(message, moduleName, success, level);
		assertThat(rear.getModuleName(), is(moduleName));
		assertThat(Boolean.valueOf(rear.isActionFailed()), is(not(Boolean.valueOf(success))));

		if (MessageLevel.INFO.equals(level))
		{
			assertThat(rear.getMessagesAsString(level), containsString(message));
		}
		else
		{
			assertThat(rear.getMessagesAsString(MessageLevel.INFO), is(not(message)));
		}
		if (MessageLevel.WARNING.equals(level))
		{
			assertThat(rear.getMessagesAsString(level), containsString(message));
		}
		else
		{
			assertThat(rear.getMessagesAsString(MessageLevel.WARNING), is(not(message)));
		}
		if (MessageLevel.ERROR.equals(level))
		{
			assertThat(rear.getMessagesAsString(level), containsString(message));
		}
		else
		{
			assertThat(rear.getMessagesAsString(MessageLevel.ERROR), is(not(message)));
		}
	}

	@Test
	public void testSetup()
	{
		//second scenario
		configurationMock.setProperty(RuleEngineConstants.DROOLS_DATE_FORMAT_KEY, "dateFormat");
		service.setup();
		assertThat(System.getProperty(RuleEngineConstants.DROOLS_DATE_FORMAT_KEY), is("dateFormat"));
	}

}
