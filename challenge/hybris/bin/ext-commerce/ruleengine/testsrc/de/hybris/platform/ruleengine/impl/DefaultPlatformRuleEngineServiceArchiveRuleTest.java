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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.InitializationFuture;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newLinkedList;
import static de.hybris.platform.ruleengine.EngineRulesBuilder.newDroolsRule;
import static de.hybris.platform.ruleengine.EngineRulesBuilder.newKieBase;
import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.SWAPPING_IS_BLOCKING;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_CONCURRENCY_LEVEL;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_INITIAL_CAPACITY;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_LOAD_FACTOR;
import static de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper.WORKER_PRE_DESTROY_TIMEOUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @deprecated since 1811
 */
@Deprecated
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformRuleEngineServiceArchiveRuleTest
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String RULE_NAME1 = "rule1";
	private static final String RULE_NAME2 = "rule2";

	private AbstractRuleEngineRuleModel rule = null;
	@Mock
	private DroolsKIEModuleModel rulesModule;

	@Mock
	private AbstractRuleModel sourceRule;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private EventService eventService;
	@Mock
	private ModelService modelService;
	@Mock
	private KieServices kieServices;
	@InjectMocks
	private DefaultPlatformRuleEngineService service;
	@Mock
	private KieFileSystem kieFileSystem;
	@Mock
	private KieModuleModel kieModuleModel;
	@Mock
	private KieRepository kieRepository;
	@Mock
	private KieContainer kieContainer;
	@Mock
	private ReleaseId releaseId;
	@Mock
	private KieBuilder kieBuilder;
	@Mock
	private KieModule kieModule;
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
	private RulesModuleDao rulesModuleDao;

	private RuleEngineActionResult actionResult;

	@Before
	public void setUp()
	{
		ruleEngineKieModuleSwapper.setConcurrentMapFactory(concurrentMapFactory);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(eq(WORKER_PRE_DESTROY_TIMEOUT), anyLong())).thenReturn(5000L);
		when(configuration.getInt(eq(WORKER_MAP_INITIAL_CAPACITY), anyInt())).thenReturn(3);
		when(configuration.getFloat(eq(WORKER_MAP_LOAD_FACTOR), anyFloat())).thenReturn(0.75F);
		when(configuration.getInt(eq(WORKER_MAP_CONCURRENCY_LEVEL), anyInt())).thenReturn(2);
		when(configuration.getBoolean(eq(SWAPPING_IS_BLOCKING), anyBoolean())).thenReturn(false);

		ruleEngineKieModuleSwapper.setup();

		actionResult = new RuleEngineActionResult();
		service.setEventService(eventService);
		service.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);

		when(currentTenant.createAndRegisterBackgroundThread(Mockito.any(Runnable.class), Mockito.any(ThreadFactory.class)))
				.thenReturn(createTenantAwareThread(true));
		when(kieBuilder.getResults()).thenReturn(results);
		when(kieBuilder.getKieModule()).thenReturn(kieModule);
		when(kieModule.getReleaseId()).thenReturn(releaseId);
		when(kieServices.newKieModuleModel()).thenReturn(kieModuleModel);
		when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
		when(kieServices.getRepository()).thenReturn(kieRepository);
		when(kieContainer.getReleaseId()).thenReturn(releaseId);
		when(kieServices.newKieContainer(releaseId)).thenReturn(kieContainer);
		when(kieServices.newReleaseId(anyString(), anyString(), anyString())).thenReturn(releaseId);
		when(kieServices.newKieBuilder(any(KieFileSystem.class))).thenReturn(kieBuilder);
		when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(rulesModule);
	}

	private Thread createTenantAwareThread(final boolean propagateToOtherNodes)
	{
		final LinkedList<Supplier<Object>> postTasks = newLinkedList();
		postTasks.add(() -> Optional.of(releaseId).map(r -> ruleEngineKieModuleSwapper.removeKieModuleIfPresent(r, actionResult))
				.orElse(false));

		return new Thread(() -> ruleEngineKieModuleSwapper.switchKieModule(rulesModule,
				new KieContainerListener()
				{
					@Override
					public void onSuccess(final KieContainer kieContainer, final KIEModuleCacheBuilder cache)
					{
						service.doSwapKieContainers(kieContainer, cache, actionResult, rulesModule, null, propagateToOtherNodes);
					}

					@Override
					public void onFailure(final RuleEngineActionResult result)
					{
						// Empty
					}
				},
				postTasks, false, actionResult));
	}

	@Test
	public void testArchiveRuleAbstractRuleEngineRuleIsNull()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Cannot update engine rule, given rule is null");
		service.archiveRule(null);
	}

	@Test
	public void testArchiveRuleAbstractRuleEngineRuleWrongType()
	{
		final AbstractRuleEngineRuleModel ruleEngineRule = mock(AbstractRuleEngineRuleModel.class);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Cannot update engine rule, given rule with code: null is not DroolsRuleModel, but null.");
		service.archiveRule(ruleEngineRule);
	}

	@Test
	public void testArchiveRuleAbstractRuleEngineRuleWrongKieBase()
	{
		final DroolsRuleModel droolEngineRule = mock(DroolsRuleModel.class);
		final RuleEngineActionResult ruleEngineActionResult = service.archiveRule(droolEngineRule);
		assertTrue(ruleEngineActionResult.isActionFailed());
		assertThat(ruleEngineActionResult.getMessagesAsString(MessageLevel.ERROR), is(not(nullValue())));
	}

	@Test
	public void testArchiveRuleAbstractRuleEngineRuleModelSuccessScenario()
	{
		final DroolsRuleModel droolsRule = mock(DroolsRuleModel.class);

		final DroolsKIEModuleModel droolsKieModule = mock(DroolsKIEModuleModel.class);
		when(droolsKieModule.getName()).thenReturn(MODULE_NAME);

		when(droolsKieModule.getPk()).thenReturn(PK.fromLong(2345L));
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		when(kieBase.getKieModule()).thenReturn(droolsKieModule);
		when(droolsRule.getKieBase()).thenReturn(kieBase);
		when(droolsRule.getActive()).thenReturn(Boolean.TRUE);
		when(droolsRule.getSourceRule()).thenReturn(sourceRule);

		service.archiveRule(droolsRule);

		verify(droolsRule).setActive(Boolean.FALSE);
		verify(sourceRule, times(1)).setStatus(RuleStatus.ARCHIVED);
		verify(modelService).saveAll(droolsRule, sourceRule);
	}

	@Test
	public void testArchiveRuleRuleAndModuleValidationNullRule()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Cannot update engine rule, given rule is null");
		service.archiveRule(rule, rulesModule);
	}

	@Test
	public void testArchiveRuleRuleAndModuleValidationWrongTypeOfRule()
	{
		rule = mock(AbstractRuleEngineRuleModel.class);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Cannot update engine rule, given rule with code: null is not DroolsRuleModel, but null.");
		service.archiveRule(rule, rulesModule);
	}

	@Test
	public void testArchiveRuleRuleAndModuleValidationNullRulesModule()
	{
		rule = mock(DroolsRuleModel.class);
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Cannot update engine rule, given module is null");
		service.archiveRule(rule, null);
	}

	@Test
	public void testArchiveRuleRuleAndModuleValidationWrongTypeOfModule()
	{

		rule = mock(DroolsRuleModel.class);
		rulesModule = mock(DroolsKIEModuleModel.class);
		final RuleEngineActionResult archiveRule = service.archiveRule(rule, rulesModule);
		assertTrue(archiveRule.isActionFailed());
		assertThat(archiveRule.getMessagesAsString(MessageLevel.ERROR), is(not(nullValue())));
	}

	@Test
	public void testArchiveRuleRuleAndModuleValidationRuleIsInactive()
	{
		rule = mock(DroolsRuleModel.class);
		final DroolsRuleModel droolsRule = (DroolsRuleModel) rule;
		when(droolsRule.getActive()).thenReturn(Boolean.FALSE);
		rulesModule = mock(DroolsKIEModuleModel.class);
		final RuleEngineActionResult archiveRule = service.archiveRule(rule, rulesModule);
		assertTrue(archiveRule.isActionFailed());
		assertThat(archiveRule.getMessagesAsString(MessageLevel.ERROR), is(not(nullValue())));
	}

	@Test
	public void testArchiveRulesEmptyInput()
	{
		final Optional<InitializationFuture> initializationFuture = service.archiveRules(Collections.emptyList());
		assertThat(initializationFuture).isNotPresent();
	}

	@Test
	public void testArchiveRulesRulesHaveDifferentTypes()
	{
		final DroolsRuleModel rule1 = mock(DroolsRuleModel.class);
		final DroolsRuleModel rule2 = mock(DroolsRuleModel.class);
		when(rule1.getRuleType()).thenReturn(RuleType.DEFAULT);

		assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> service.archiveRules(Arrays.asList(rule1, rule2)))
				.withMessage("One or more rules in the collection are having different rule types");
	}

	@Test
	public void testArchiveRulesNoKieBase()
	{
		final DroolsKIEBaseModel kieBaseModel = newKieBase(MODULE_NAME);

		final DroolsRuleModel rule1 = newDroolsRule(kieBaseModel, RULE_NAME1);
		final DroolsRuleModel rule2 = newDroolsRule(kieBaseModel, RULE_NAME2);
		when(rule2.getKieBase()).thenReturn(null);

		assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> service.archiveRules(Arrays.asList(rule1, rule2)))
				.withMessage("Rule [rule2] has no KieBase assigned to it");
	}

	@Test
	public void testArchiveRulesRulesInactive()
	{
		final DroolsKIEBaseModel kieBaseModel = newKieBase(MODULE_NAME);

		final DroolsRuleModel rule1 = newDroolsRule(kieBaseModel, RULE_NAME1);
		final DroolsRuleModel rule2 = newDroolsRule(kieBaseModel, RULE_NAME2);
		when(rule1.getActive()).thenReturn(Boolean.FALSE);
		when(rule2.getActive()).thenReturn(Boolean.FALSE);

		final Optional<InitializationFuture> initializationFuture = service.archiveRules(Arrays.asList(rule1, rule2));
		assertThat(initializationFuture).isNotPresent();
	}

	@Test
	public void testArchiveRulesRulesNonCurrentVersion()
	{
		final DroolsKIEBaseModel kieBaseModel = newKieBase(MODULE_NAME);

		final DroolsRuleModel rule1 = newDroolsRule(kieBaseModel, RULE_NAME1);
		final DroolsRuleModel rule2 = newDroolsRule(kieBaseModel, RULE_NAME2);
		when(rule1.getCurrentVersion()).thenReturn(Boolean.FALSE);
		when(rule2.getCurrentVersion()).thenReturn(Boolean.FALSE);

		final Optional<InitializationFuture> initializationFuture = service.archiveRules(Arrays.asList(rule1, rule2));
		assertThat(initializationFuture).isNotPresent();
	}

	@Test
	public void testArchiveRulesRulesOK()
	{
		final DroolsKIEBaseModel kieBaseModel = newKieBase(MODULE_NAME);

		final DroolsRuleModel rule1 = newDroolsRule(kieBaseModel, RULE_NAME1);
		final DroolsRuleModel rule2 = newDroolsRule(kieBaseModel, RULE_NAME2);

		final Optional<InitializationFuture> initializationFuture = service.archiveRules(Arrays.asList(rule1, rule2));
		assertThat(initializationFuture).isPresent();
		assertThat(initializationFuture.get().getResults()).isNotEmpty().hasSize(1);
	}

}
