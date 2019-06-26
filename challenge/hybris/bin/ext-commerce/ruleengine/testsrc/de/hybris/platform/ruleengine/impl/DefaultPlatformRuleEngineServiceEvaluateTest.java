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

import static com.google.common.collect.Lists.newLinkedList;
import static de.hybris.platform.ruleengine.constants.RuleEngineConstants.RULE_ENGINE_ACTIVE;
import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.SWAPPING_IS_BLOCKING;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_CONCURRENCY_LEVEL;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_INITIAL_CAPACITY;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_LOAD_FACTOR;
import static de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper.WORKER_PRE_DESTROY_TIMEOUT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.cache.RuleEngineCacheService;
import de.hybris.platform.ruleengine.concurrency.SuspendResumeTaskManager;
import de.hybris.platform.ruleengine.dao.DroolsKIEModuleMediaDao;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.drools.impl.DefaultKieModuleService;
import de.hybris.platform.ruleengine.drools.impl.DefaultKieSessionHelper;
import de.hybris.platform.ruleengine.enums.DroolsSessionType;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineBootstrap;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineContainerRegistry;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.init.tasks.PostRulesModuleSwappingTasksProvider;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleMediaModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.DroolsKIEBaseFinderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import org.apache.commons.configuration.Configuration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
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
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformRuleEngineServiceEvaluateTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String MODULE_MVN_VERSION = "MODULE_MVN_VERSION";
	private static final String KIE_SESSION_NAME = "KIE_SESSION_NAME";

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private DroolsKIEBaseFinderStrategy droolsKIEBaseFinderStrategy;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private EventService eventService;
	@Mock
	private ModelService modelService;
	@Mock
	private KieServices kieServices;
	@InjectMocks
	private DefaultPlatformRuleEngineService service;
	@Mock
	private DroolsKIEModuleModel droolsModule;
	@Mock
	private RuleEngineActionResult result;
	@Mock
	private KieFileSystem kieFileSystem;
	@Mock
	private KieModuleModel kieModuleModel;
	@Mock
	private KieBaseModel baseKieSessionModel;
	@Mock
	private KieRepository kieRepository;
	@Mock
	private ReleaseId releaseId;
	@Mock
	private ReleaseId newReleaseId;
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
	private KieModule oldKieModule;
	@Mock
	private KieContainerImpl oldKieContainer;
	@Mock
	private KieContainerImpl newKieContainer;
	@Mock
	private RuleEvaluationContext ruleEvaluationContext;
	@Mock
	private DroolsRuleEngineContextModel ruleEngineContext;
	@Mock
	private DroolsKIESessionModel kieSessionModel;
	@Mock
	private DroolsKIEBaseModel kieBase;
	@Mock
	private KieSession session;
	@Mock
	private Configuration configuration;
	@Mock
	private RuleEngineCacheService ruleEngineCacheService;
	@Mock
	private KIEModuleCacheBuilder cache;
	@Mock
	private MemoryKieModule newKieModule;
	@Mock
	private AgendaFilter agendaFilter;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private PostRulesModuleSwappingTasksProvider postRulesModuleSwappingTasksProvider;
	@Mock
	private SuspendResumeTaskManager suspendResumeTaskManager;
	@InjectMocks
	private DefaultRuleEngineBootstrap ruleEngineBootstrap;
	private DefaultRuleEngineContainerRegistry ruleEngineContainerRegistry;
	@InjectMocks
	private DefaultKieSessionHelper kieSessionHelper;
	@InjectMocks
	private DefaultKieModuleService kieModuleService;
	@Mock
	private DroolsKIEModuleMediaDao droolsKIEModuleMediaDao;
	@Mock
	MediaService mediaService;

	@Mock
	private MemoryFileSystem memoryFileSystem;

	private RuleEngineActionResult actionResult;


	@Before
	public void setUp()
	{

		ruleEngineContainerRegistry = new DefaultRuleEngineContainerRegistry();
		ruleEngineContainerRegistry.setConcurrentMapFactory(concurrentMapFactory);
		service.setRuleEngineContainerRegistry(ruleEngineContainerRegistry);
		service.setRuleEngineBootstrap(ruleEngineBootstrap);
		ruleEngineBootstrap.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);
		ruleEngineBootstrap.setRuleEngineContainerRegistry(ruleEngineContainerRegistry);
		ruleEngineKieModuleSwapper.setConcurrentMapFactory(concurrentMapFactory);
		ruleEngineKieModuleSwapper.setRuleEngineBootstrap(ruleEngineBootstrap);
		ruleEngineKieModuleSwapper.setKieModuleService(kieModuleService);
		service.setConcurrentMapFactory(concurrentMapFactory);
		service.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);
		service.setKieSessionHelper(kieSessionHelper);

		kieSessionHelper.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getLong(eq(WORKER_PRE_DESTROY_TIMEOUT), anyLong())).thenReturn(5000L);
		when(configuration.getInt(eq(WORKER_MAP_INITIAL_CAPACITY), anyInt())).thenReturn(3);
		when(configuration.getFloat(eq(WORKER_MAP_LOAD_FACTOR), anyFloat())).thenReturn(0.75F);
		when(configuration.getInt(eq(WORKER_MAP_CONCURRENCY_LEVEL), anyInt())).thenReturn(2);
		when(configuration.getBoolean(eq(SWAPPING_IS_BLOCKING), anyBoolean())).thenReturn(false);
		when(ruleEngineCacheService.createKIEModuleCacheBuilder(droolsModule)).thenReturn(cache);
		activateRuleEngine();

		ruleEngineContainerRegistry.setup();
		ruleEngineKieModuleSwapper.setup();
		service.setup();

		actionResult = new RuleEngineActionResult();
		//set up base configuration:
		when(currentTenant.createAndRegisterBackgroundThread(Mockito.any(Runnable.class), Mockito.any(ThreadFactory.class)))
				.thenReturn(createTenantAwareThread());
		when(kieBuilder.getResults()).thenReturn(results);
		when(kieBuilder.getKieModule()).thenReturn(newKieModule);
		when(kieServices.newKieModuleModel()).thenReturn(kieModuleModel);
		when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
		when(kieRepository.getKieModule(any(ReleaseId.class))).thenReturn(oldKieModule);
		when(kieServices.getRepository()).thenReturn(kieRepository);
		when(kieServices.newReleaseId(anyString(), anyString(), eq(MODULE_MVN_VERSION + ".0"))).thenReturn(releaseId);
		when(kieServices.newReleaseId(anyString(), anyString(), eq(MODULE_MVN_VERSION + ".1"))).thenReturn(newReleaseId);
		when(kieServices.newKieBuilder(any(KieFileSystem.class))).thenReturn(kieBuilder);

		when(kieServices.newKieContainer(newReleaseId)).thenReturn(newKieContainer);

		when(newKieContainer.getContainerReleaseId()).thenReturn(newReleaseId);
		when(newKieContainer.getReleaseId()).thenReturn(newReleaseId);
		when(droolsModule.getName()).thenReturn(MODULE_NAME);
		when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(droolsModule);
		when(droolsModule.getDeployedMvnVersion()).thenReturn(MODULE_MVN_VERSION + ".0");
		when(newKieModule.getReleaseId()).thenReturn(newReleaseId);
		when(releaseId.getVersion()).thenReturn(MODULE_MVN_VERSION + ".0");
		when(releaseId.toExternalForm()).thenReturn(MODULE_MVN_VERSION + ".0");
		when(newReleaseId.getVersion()).thenReturn(MODULE_MVN_VERSION + ".1");
		when(newReleaseId.toExternalForm()).thenReturn(MODULE_MVN_VERSION + ".1");
		when(kieSessionModel.getName()).thenReturn(KIE_SESSION_NAME);
		when(ruleEvaluationContext.getRuleEngineContext()).thenReturn(ruleEngineContext);
		when(ruleEvaluationContext.getFilter()).thenReturn(agendaFilter);
		when(ruleEngineContext.getKieSession()).thenReturn(kieSessionModel);
		when(kieSessionModel.getSessionType()).thenReturn(DroolsSessionType.STATEFUL);
		when(kieSessionModel.getKieBase()).thenReturn(kieBase);
		when(kieBase.getKieModule()).thenReturn(droolsModule);
		when(newKieContainer.newKieSession(anyString())).thenReturn(session);
		when(newKieModule.getMemoryFileSystem()).thenReturn(memoryFileSystem);

		when(droolsModule.getMvnVersion()).thenReturn(MODULE_MVN_VERSION);

		when(suspendResumeTaskManager.isSystemRunning()).thenReturn(Boolean.TRUE);
		when(droolsKIEModuleMediaDao.findKIEModuleMedia(anyString(), anyString())).thenReturn(Optional.empty());
		when(modelService.create(DroolsKIEModuleMediaModel.class)).thenReturn(new DroolsKIEModuleMediaModel());
	}

	private void setUpOldContainer()
	{
		when(droolsModule.getVersion()).thenReturn(0L);
		when(droolsModule.getDeployedMvnVersion()).thenReturn(null);
		when(kieServices.newReleaseId(anyString(), anyString(), eq(null))).thenReturn(null);
		when(kieServices.newKieContainer(releaseId)).thenReturn(oldKieContainer);
		when(oldKieContainer.getContainerReleaseId()).thenReturn(releaseId);
		when(oldKieContainer.getReleaseId()).thenReturn(releaseId);

	}

	private void setUpInitializedContainer()
	{
		when(droolsModule.getVersion()).thenReturn(0L);
		when(droolsModule.getDeployedMvnVersion()).thenReturn(MODULE_MVN_VERSION + ".0");
		when(kieServices.newReleaseId(anyString(), anyString(), eq(MODULE_MVN_VERSION + ".0"))).thenReturn(releaseId);
		when(kieServices.newKieContainer(releaseId)).thenReturn(oldKieContainer);
		when(oldKieContainer.getContainerReleaseId()).thenReturn(releaseId);
		when(oldKieContainer.getReleaseId()).thenReturn(releaseId);
		when(oldKieContainer.newKieSession(anyString())).thenReturn(session);
	}

	private void setUpNewContainer()
	{
		when(kieServices.newReleaseId(anyString(), anyString(), eq(MODULE_MVN_VERSION + ".1"))).thenReturn(newReleaseId);
		when(kieServices.newKieContainer(newReleaseId)).thenReturn(newKieContainer);
		when(newKieContainer.getContainerReleaseId()).thenReturn(newReleaseId);
		when(newKieContainer.getReleaseId()).thenReturn(newReleaseId);
		when(newKieContainer.newKieSession(anyString())).thenReturn(session);
	}

	private Thread createTenantAwareThread()
	{
		final LinkedList<Supplier<Object>> postTasks = newLinkedList();
		postTasks.add(() -> Optional.of(releaseId).map(r -> ruleEngineKieModuleSwapper.removeKieModuleIfPresent(r, actionResult))
				.orElse(false));
		postTasks.addLast(() -> service.getInitializationMultiFlag().compareAndSet(MODULE_NAME, true, false));

		return new Thread(() -> ruleEngineKieModuleSwapper.switchKieModule(droolsModule, new KieContainerListener()
		{
			@Override
			public void onSuccess(final KieContainer kieContainer, final KIEModuleCacheBuilder cache)
			{
				service.doSwapKieContainers(kieContainer, cache, result, droolsModule, null, true);
			}

			@Override
			public void onFailure(final RuleEngineActionResult result)
			{
				// Empty
			}
		}, postTasks, false, actionResult));
	}

	@Test
	public void testEvaluateNoInitialization()
	{
		expectedException
				.expectMessage("Cannot complete the evaluation: rule engine was not initialized for releaseId [DUMMY_GROUP:DUMMY_ARTIFACT:DUMMY_VERSION]");
		setUpOldContainer();
		service.evaluate(ruleEvaluationContext);
	}

	@Test
	public void testEvaluateAfterInitialization()
	{
		setUpOldContainer();
		service.initialize(Collections.singletonList(droolsModule), false, false).waitForInitializationToFinish();
		setUpInitializedContainer();
		service.evaluate(ruleEvaluationContext);
		verify(oldKieContainer).newKieSession(KIE_SESSION_NAME);
	}

	@Test
	public void testEvaluateAfterSecondInitialization()
	{
		final Thread tenantAwareThread = createTenantAwareThread();
		when(currentTenant.createAndRegisterBackgroundThread(Mockito.any(Runnable.class), Mockito.any(ThreadFactory.class)))
				.thenReturn(tenantAwareThread);
		setUpOldContainer();
		service.initialize(Collections.singletonList(droolsModule), false, false).waitForInitializationToFinish();
		setUpInitializedContainer();
		final Thread tenantAwareThread1 = createTenantAwareThread();
		when(currentTenant.createAndRegisterBackgroundThread(Mockito.any(Runnable.class), Mockito.any(ThreadFactory.class)))
				.thenReturn(tenantAwareThread1);
		when(droolsModule.getVersion()).thenReturn(1L);
		service.initialize(Collections.singletonList(droolsModule), false, false).waitForInitializationToFinish();
		when(droolsModule.getDeployedMvnVersion()).thenReturn(MODULE_MVN_VERSION + ".1");
		setUpNewContainer();
		service.evaluate(ruleEvaluationContext);
		verify(newKieContainer).newKieSession(KIE_SESSION_NAME);
	}

	private void activateRuleEngine()
	{
		when(configuration.getBoolean(RULE_ENGINE_ACTIVE)).thenReturn(true);
		when(configuration.getBoolean(RULE_ENGINE_ACTIVE, true)).thenReturn(true);
	}

}
