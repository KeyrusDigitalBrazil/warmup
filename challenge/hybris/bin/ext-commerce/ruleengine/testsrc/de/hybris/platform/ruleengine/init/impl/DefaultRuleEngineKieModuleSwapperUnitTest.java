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
package de.hybris.platform.ruleengine.init.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.ExecutionContext;
import de.hybris.platform.ruleengine.InitializeMode;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.cache.RuleEngineCacheService;
import de.hybris.platform.ruleengine.concurrency.SuspendResumeTaskManager;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.drools.impl.DefaultKieModuleService;
import de.hybris.platform.ruleengine.enums.DroolsEqualityBehavior;
import de.hybris.platform.ruleengine.enums.DroolsEventProcessingMode;
import de.hybris.platform.ruleengine.enums.DroolsSessionType;
import de.hybris.platform.ruleengine.impl.KieContainerListener;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.IncrementalRuleEngineUpdateStrategy;
import de.hybris.platform.ruleengine.init.RuleEngineBootstrap;
import de.hybris.platform.ruleengine.init.RuleEngineContainerRegistry;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.util.Lists;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KieBuilderSet;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.SWAPPING_IS_BLOCKING;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_CONCURRENCY_LEVEL;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_INITIAL_CAPACITY;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_LOAD_FACTOR;
import static de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper.WORKER_PRE_DESTROY_TIMEOUT;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineKieModuleSwapperUnitTest
{
	private static final String EXTERNAL_FORM = "EXTERNAL_FORM";
	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String DEPLOYED_VERSION = "DEPLOYED_VERSION";
	private static final String SUSPEND_MESSAGE = "Rule engine module deployment is in progress";

	@InjectMocks
	private DefaultRuleEngineKieModuleSwapper moduleSwapper;
	@InjectMocks
	private ConcurrentMapFactory concurrentMapFactory;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private KieServices kieServices;
	@Mock
	private KieFileSystem kfs;
	@Mock
	private KieBaseModel baseKieSessionModel;
	@Mock
	private KieModuleModel kieModuleModel;
	@Mock
	private InternalKieBuilder kieBuilder;
	@Mock
	private Results kieBuilderResults;
	@Mock
	private MemoryKieModule kieModule;
	@Mock
	private KieRepository kieRepository;
	@Mock
	private Tenant currentTenant;
	@Mock
	private KieContainer kieContainer;
	@Mock
	private RuleEngineActionResult ruleEngineActionResult;
	@Mock
	private KIEModuleCacheBuilder cache;
	@Mock
	private KieContainerListener kieContainerListener;
	@Mock
	private DroolsKIEModuleModel droolsModule;
	@Mock
	private ReleaseId releaseId;
	@Mock
	private Configuration configuration;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private RuleEngineCacheService ruleEngineCacheService;
	@Mock
	private MemoryFileSystem memoryFileSystem;
	@Mock
	private DefaultContentMatchRulesFilter contentMatchRulesFilter;
	@Mock
	private IncrementalRuleEngineUpdateStrategy incrementalRuleEngineUpdateStrategy;
	@Mock
	private MemoryKieModule incrementalKieModule;
	@Mock
	private RuleEngineBootstrap ruleEngineBootstrap;
	@Mock
	private RuleEngineContainerRegistry ruleEngineContainerRegistry;
	@Mock
	private SuspendResumeTaskManager suspendResumeTaskManager;
	@Mock
	private DefaultKieModuleService kieModuleService;
	@Mock
	private EngineRuleDao engineRuleDao;

	@Before
	public void setUp()
	{
		moduleSwapper.setConcurrentMapFactory(concurrentMapFactory);
		moduleSwapper.setRuleEngineCacheService(ruleEngineCacheService);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(eq(WORKER_PRE_DESTROY_TIMEOUT), anyLong())).thenReturn(5000L);
		when(configuration.getInt(eq(WORKER_MAP_INITIAL_CAPACITY), anyInt())).thenReturn(3);
		when(configuration.getFloat(eq(WORKER_MAP_LOAD_FACTOR), anyFloat())).thenReturn(0.75F);
		when(configuration.getInt(eq(WORKER_MAP_CONCURRENCY_LEVEL), anyInt())).thenReturn(2);
		when(configuration.getBoolean(eq(SWAPPING_IS_BLOCKING), anyBoolean())).thenReturn(false);

		moduleSwapper.setup();
		when(kieServices.newReleaseId(anyString(), anyString(), anyString())).thenReturn(releaseId);
		when(kieServices.newKieFileSystem()).thenReturn(kfs);
		when(kieServices.newKieModuleModel()).thenReturn(kieModuleModel);
		when(kieModule.getKieModuleModel()).thenReturn(kieModuleModel);
		when(kieServices.newKieBuilder(kfs)).thenReturn(kieBuilder);
		when(kieServices.getRepository()).thenReturn(kieRepository);
		when(kieServices.newKieContainer(releaseId)).thenReturn(kieContainer);
		when(kieBuilder.getKieModule()).thenReturn(kieModule);
		when(kieBuilder.getResults()).thenReturn(kieBuilderResults);
		when(kieModule.getReleaseId()).thenReturn(releaseId);
		when(kieContainer.getReleaseId()).thenReturn(releaseId);
		when(releaseId.getVersion()).thenReturn(DEPLOYED_VERSION);
		when(droolsModule.getName()).thenReturn(MODULE_NAME);
		when(releaseId.toExternalForm()).thenReturn(EXTERNAL_FORM);
		when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(droolsModule);
		when(ruleEngineCacheService.createKIEModuleCacheBuilder(any())).thenReturn(cache);
		when(kieModule.getMemoryFileSystem()).thenReturn(memoryFileSystem);
		when(suspendResumeTaskManager.isSystemRunning()).thenReturn(Boolean.TRUE);
		when(kieModuleService.loadKieModule(anyString(), anyString())).thenReturn(Optional.empty());
	}

	@Test
	public void testSwitchKieModuleExecutePostTasks()
	{
		final LinkedList<Supplier<Object>> postTaskList = newLinkedList();
		postTaskList.addAll(asList(() -> "task1", () -> "task2")); // NOSONAR
		final List<Object> resultsAccumulator = moduleSwapper.switchKieModule(droolsModule, kieContainerListener, postTaskList,
				false, ruleEngineActionResult);
		verify(kieContainerListener).onSuccess(kieContainer, cache);
		assertThat(resultsAccumulator).isNotNull().hasSize(2).containsSequence("task1", "task2");
	}

	@Test
	public void testRestoredKieModuleOnSecondaryAction()
	{
		final ExecutionContext executionContext = new ExecutionContext();
		executionContext.setInitializeMode(InitializeMode.RESTORE);
		when(ruleEngineActionResult.getExecutionContext()).thenReturn(executionContext);
		when(droolsModule.getKieBases()).thenReturn(Collections.emptyList());
		when(kieModuleModel.newKieBaseModel(anyString())).thenReturn(baseKieSessionModel);
		when(kieModuleService.loadKieModule(MODULE_NAME, EXTERNAL_FORM)).thenReturn(Optional.of(kieModule)); // KieModule's found
		moduleSwapper.switchKieModule(droolsModule, kieContainerListener, newLinkedList(), false, ruleEngineActionResult);
		verify(kieContainerListener, times(1)).onSuccess(kieContainer, cache);
		verify(kieRepository, times(1)).addKieModule(kieModule); // a restored KieModule instance added to kieRepository
		verify(kieModuleService, times(0)).saveKieModule(anyString(), anyString(), any(KieModule.class)); // KieModule wasn't saved
	}

	@Test
	public void testNotRestoredNotSavedKieModuleOnSecondaryAction()
	{
		final ExecutionContext executionContext = new ExecutionContext();
		executionContext.setInitializeMode(InitializeMode.RESTORE);
		when(ruleEngineActionResult.getExecutionContext()).thenReturn(executionContext);
		when(droolsModule.getKieBases()).thenReturn(Collections.emptyList());
		when(kieModuleModel.newKieBaseModel(anyString())).thenReturn(baseKieSessionModel);
		moduleSwapper.switchKieModule(droolsModule, kieContainerListener, newLinkedList(), false, ruleEngineActionResult);
		verify(kieContainerListener, times(1)).onSuccess(kieContainer, cache);
		verify(kieRepository, times(1)).addKieModule(any(MemoryKieModule.class)); // not restored but newly created instance of MemoryKieModule added to kieRepository
		verify(kieModuleService, times(0)).saveKieModule(anyString(), anyString(), any(KieModule.class)); // KieModule wasn't saved
	}

	@Test
	public void testCreateAndSaveKieModuleOnNotSecondaryAction()
	{
		final ExecutionContext executionContext = new ExecutionContext();
		executionContext.setInitializeMode(InitializeMode.NEW);
		when(ruleEngineActionResult.getExecutionContext()).thenReturn(executionContext);
		when(droolsModule.getKieBases()).thenReturn(Collections.emptyList());
		when(kieModuleModel.newKieBaseModel(anyString())).thenReturn(baseKieSessionModel);
		moduleSwapper.switchKieModule(droolsModule, kieContainerListener, newLinkedList(), false, ruleEngineActionResult);
		verify(kieContainerListener, times(1)).onSuccess(kieContainer, cache);
		verify(kieRepository, times(1)).addKieModule(any(MemoryKieModule.class)); // not restored but newly created instance of MemoryKieModule added to kieRepository
		verify(kieModuleService, times(1)).saveKieModule(eq(MODULE_NAME), eq(EXTERNAL_FORM), any(MemoryKieModule.class)); // KieModule is saved
	}

	@Test
	public void testSwitchKieModuleAsyncExecutePostTasksWhenCompleted() throws InterruptedException
	{
		final List<Object> resultsAccumulator = newArrayList();

		final LinkedList<Supplier<Object>> postTaskList = newLinkedList();
		postTaskList.addAll(asList(() -> "task1", () -> "task2"));
		when(currentTenant.createAndRegisterBackgroundThread(any(Runnable.class), any(ThreadFactory.class))).thenReturn(
				createTenantAwareThread(resultsAccumulator, postTaskList));
		moduleSwapper.switchKieModuleAsync(MODULE_NAME, kieContainerListener, resultsAccumulator, () -> "task2", postTaskList,
				false, ruleEngineActionResult);
		Thread.sleep(500);
		verify(kieContainerListener).onSuccess(kieContainer, cache);
		verify(suspendResumeTaskManager).registerAsNonSuspendableTask(any(Thread.class), eq(SUSPEND_MESSAGE));
		verify(suspendResumeTaskManager).isSystemRunning();
		assertThat(resultsAccumulator).isNotNull().hasSize(2).containsSequence("task1", "task2");
	}

	@Test
	public void testAddKieSessionModel()
	{
		final DroolsKIESessionModel session = mock(DroolsKIESessionModel.class);
		final KieBaseModel base = mock(KieBaseModel.class);
		final KieSessionModel kieSessionModel = mock(KieSessionModel.class);
		final DroolsKIEBaseModel droolsKIEBaseModel = mock(DroolsKIEBaseModel.class);

		when(session.getPk()).thenReturn(PK.fromLong(1235L));
		when(session.getSessionType()).thenReturn(DroolsSessionType.STATEFUL);
		when(droolsKIEBaseModel.getDefaultKIESession()).thenReturn(session);
		when(base.newKieSessionModel(any(String.class))).thenReturn(kieSessionModel);
		when(session.getKieBase()).thenReturn(droolsKIEBaseModel);
		moduleSwapper.addKieSession(base, session);

		verify(kieSessionModel).setDefault(true);
		verify(kieSessionModel).setType(KieSessionModel.KieSessionType.STATEFUL);
	}

	@Test
	public void testAddRulesRuleInactive()
	{
		final KieFileSystem kieFileSystem = mock(KieFileSystem.class);

		final Set<DroolsRuleModel> rules = newHashSet();
		final DroolsRuleModel rule = mock(DroolsRuleModel.class);
		when(rule.getRuleContent()).thenReturn("rule content");
		when(rule.getActive()).thenReturn(Boolean.FALSE);
		rules.add(rule);

		moduleSwapper.writeRulesToKieFileSystem(kieFileSystem, rules);

		verifyZeroInteractions(kieFileSystem);
	}

	@Test
	public void testAddRulesRuleWithoutContent()
	{
		final KieFileSystem kieFileSystem = mock(KieFileSystem.class);
		final Set<DroolsRuleModel> rules = newHashSet();
		final DroolsRuleModel rule = mock(DroolsRuleModel.class);
		rules.add(rule);

		moduleSwapper.writeRulesToKieFileSystem(kieFileSystem, rules);

		verifyZeroInteractions(kieFileSystem);
	}

	@Test
	public void testAddRulesEmptyRules()
	{
		final KieFileSystem kieFileSystem = mock(KieFileSystem.class);

		moduleSwapper.writeRulesToKieFileSystem(kieFileSystem, Collections.emptySet());
		verifyZeroInteractions(kieFileSystem);
	}

	@Test
	public void testAddKieBaseModel()
	{
		final DroolsKIEBaseModel base = mock(DroolsKIEBaseModel.class);
		when(base.getEqualityBehavior()).thenReturn(DroolsEqualityBehavior.EQUALITY);
		when(base.getName()).thenReturn("Mock Base"); // NOSONAR
		when(base.getItemtype()).thenReturn(DroolsKIEBaseModel._TYPECODE);
		when(base.getEventProcessingMode()).thenReturn(DroolsEventProcessingMode.STREAM);
		when(kieModuleModel.newKieBaseModel("Mock Base")).thenReturn(baseKieSessionModel);

		moduleSwapper.addKieBase(kieModuleModel, base);

		verify(kieModuleModel).newKieBaseModel("Mock Base");
		verify(baseKieSessionModel).setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
		verify(baseKieSessionModel).setEventProcessingMode(EventProcessingOption.STREAM);
		verify(base).getKieSessions();//test addKieSessionModel separately.
	}

	@Test
	public void testWriteKModuleXML()
	{
		final KieModuleModel module = mock(KieModuleModel.class);
		final KieFileSystem kieFileSystem = mock(KieFileSystem.class);
		moduleSwapper.writeKModuleXML(module, kieFileSystem);
		verify(kieFileSystem).writeKModuleXML(anyString());
		verify(module).toXML();
	}

	@Test
	public void testWritePomXML()
	{
		final DroolsKIEModuleModel module = mock(DroolsKIEModuleModel.class);
		final KieFileSystem kieFileSystem = mock(KieFileSystem.class);

		moduleSwapper.writePomXML(module, kieFileSystem);

		verify(kieFileSystem).generateAndWritePomXML(any(ReleaseId.class));
	}

	@Test
	public void testGetReleaseId()
	{
		final Long moduleVersion = 0L;
		final String groupId = "groupId";
		final String artifactId = "artifactId";
		final String version = "version";
		final DroolsKIEModuleModel module = mock(DroolsKIEModuleModel.class);

		when(module.getMvnGroupId()).thenReturn(groupId);
		when(module.getMvnArtifactId()).thenReturn(artifactId);
		when(module.getMvnVersion()).thenReturn(version);

		moduleSwapper.getReleaseId(module);

		final ArgumentCaptor<String> argArtifactId = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> argGroupId = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> argVersion = ArgumentCaptor.forClass(String.class);
		verify(kieServices).newReleaseId(argGroupId.capture(), argArtifactId.capture(), argVersion.capture());
		Assert.assertThat(argArtifactId.getValue(), is(artifactId));
		Assert.assertThat(argGroupId.getValue(), is(groupId));
		Assert.assertThat(argVersion.getValue(), is(version + "." + moduleVersion));
	}

	@Test
	public void testSwitchKieModuleAsyncIncrementalUpdate() throws InterruptedException
	{

		setUpForIncrementalRuleEngineUpdate();

		final List<Object> resultsAccumulator = newArrayList();

		final LinkedList<Supplier<Object>> postTaskList = newLinkedList();
		postTaskList.addAll(asList(() -> "task1", () -> "task2"));
		final Thread tenantAwareThread = createTenantAwareThread(resultsAccumulator, postTaskList);
		when(currentTenant.createAndRegisterBackgroundThread(any(Runnable.class), any(ThreadFactory.class))).thenReturn(
				tenantAwareThread);


		moduleSwapper.switchKieModuleAsync(MODULE_NAME, kieContainerListener, resultsAccumulator, () -> "task2", postTaskList,
				true, ruleEngineActionResult);
		tenantAwareThread.join();
		verify(kieModule).getMemoryFileSystem();
		verify(incrementalKieModule).getMemoryFileSystem();
		verify(kieContainerListener).onSuccess(kieContainer, cache);
		verify(suspendResumeTaskManager).registerAsNonSuspendableTask(any(Thread.class), eq(SUSPEND_MESSAGE));
		verify(suspendResumeTaskManager).isSystemRunning();
		assertThat(resultsAccumulator).isNotNull().hasSize(2).containsSequence("task1", "task2");
	}

	private Thread createTenantAwareThread(final List<Object> resultsAccumulator, final LinkedList<Supplier<Object>> postTaskList)
	{
		return new Thread(moduleSwapper.switchKieModuleRunnableTask(MODULE_NAME, kieContainerListener, resultsAccumulator,
				() -> "resetFlag", postTaskList, true, ruleEngineActionResult));
	}

	private void setUpForIncrementalRuleEngineUpdate()
	{
		when(droolsModule.getMvnVersion()).thenReturn("NEW_MVN_VERSION");
		when(droolsModule.getVersion()).thenReturn(1L);
		when(droolsModule.getDeployedMvnVersion()).thenReturn(DEPLOYED_VERSION);
		when(kieRepository.getKieModule(releaseId)).thenReturn(kieModule);
		final ReleaseId newReleaseId = mock(ReleaseId.class);
		when(kieServices.newReleaseId(anyString(), anyString(), eq("NEW_MVN_VERSION.1"))).thenReturn(newReleaseId);
		when(newReleaseId.getVersion()).thenReturn("NEW_MVN_VERSION.1");
		when(newReleaseId.toExternalForm()).thenReturn(EXTERNAL_FORM);
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		when(kieBase.getKieModule()).thenReturn(droolsModule);
		when(kieBase.getEqualityBehavior()).thenReturn(DroolsEqualityBehavior.EQUALITY);
		when(kieBase.getEventProcessingMode()).thenReturn(DroolsEventProcessingMode.STREAM);
		when(kieBase.getName()).thenReturn("KIE_BASE");
		final KieBaseModel kieBaseModel = mock(KieBaseModel.class);
		when(kieModuleModel.newKieBaseModel("KIE_BASE")).thenReturn(kieBaseModel);
		when(droolsModule.getKieBases()).thenReturn(Collections.singletonList(kieBase));
		final DroolsRuleModel rule = mock(DroolsRuleModel.class);
		when(rule.getActive()).thenReturn(Boolean.TRUE);
		when(rule.getCurrentVersion()).thenReturn(Boolean.TRUE);
		when(rule.getRuleContent()).thenReturn("RULE_CONTENT");
		when(kieBase.getRules()).thenReturn(Collections.singleton(rule));
		when(engineRuleDao.getActiveRules(droolsModule)).thenReturn(Arrays.asList(rule));
		when(rule.getUuid()).thenReturn("RULE_UUID");
		when(rule.getCode()).thenReturn("RULE_CODE");
		when(contentMatchRulesFilter.apply(anyCollectionOf(String.class), eq(1L))).thenReturn(
				ImmutablePair.of(Collections.singletonList(rule), Lists.emptyList()));
		when(
				incrementalRuleEngineUpdateStrategy.shouldUpdateIncrementally(eq(releaseId), eq(MODULE_NAME), anyCollection(),
						anyCollection())).thenReturn(Boolean.TRUE);
		when(kieModuleModel.toXML()).thenReturn("<xml/>");
		final InternalKieBuilder incrKieBuilder = mock(InternalKieBuilder.class);
		final KieBuilderSet kieBuilderSet = mock(KieBuilderSet.class);
		final IncrementalResults incrementalResults = mock(IncrementalResults.class);
		when(incrKieBuilder.createFileSet(any())).thenReturn(kieBuilderSet);
		when(incrKieBuilder.getKieModule()).thenReturn(incrementalKieModule);
		final Results incrBuilderResults = mock(Results.class);
		when(incrKieBuilder.getResults()).thenReturn(incrBuilderResults);
		when(kieBuilderSet.build()).thenReturn(incrementalResults);
		when(kieServices.newKieBuilder(any(KieFileSystem.class))).thenReturn(incrKieBuilder);
		final MemoryFileSystem incrMemoryFileSystem = mock(MemoryFileSystem.class);
		when(incrementalKieModule.getMemoryFileSystem()).thenReturn(incrMemoryFileSystem);
		when(kieServices.newKieContainer(newReleaseId)).thenReturn(kieContainer);
	}

}
