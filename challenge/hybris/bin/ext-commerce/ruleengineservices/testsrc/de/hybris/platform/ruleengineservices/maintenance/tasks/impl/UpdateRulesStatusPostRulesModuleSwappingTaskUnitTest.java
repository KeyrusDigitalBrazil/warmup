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
package de.hybris.platform.ruleengineservices.maintenance.tasks.impl;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.ExecutionContext;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rule.services.RuleService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang.ArrayUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateRulesStatusPostRulesModuleSwappingTaskUnitTest
{
	private static final String MVN_VERSION = "0.0";
	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String OLD_DEPLOYED_MVN_VERSION = MVN_VERSION + ".5";
	private static final String DEPLOYED_MVN_VERSION = MVN_VERSION + ".10";

	@InjectMocks
	private UpdateRulesStatusPostRulesModuleSwappingTask postTask;
	@Mock
	private RuleService ruleService;
	@Mock
	private ModelService modelService;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private RuleEngineActionResult ruleEngineActionResult;
	private ExecutionContext executionContext;

	@Before
	public void setUp()
	{
		executionContext = new ExecutionContext();
		when(ruleEngineActionResult.getExecutionContext()).thenReturn(executionContext);
		executionContext.setModifiedRuleCodes(emptySet());
	}

	@Test
	public void testExecuteNoDeployedRules()
	{
		initializeRuleEngineActionResult(DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		verify(modelService, times(2)).saveAll(emptySet());
	}

	@Test
	public void testExecuteOldAndNewVersionsOk()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
	}

	@Test
	public void testExecutePublishAlreadyPublished()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.TRUE);
		final List<AbstractRuleEngineRuleModel> newEngineRules = rulesForVersion("ruleCode", 5, 5, Boolean.TRUE);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.PUBLISHED,
						oldEngineRules.get(i), newEngineRules.get(i))).collect(toList());

		IntStream.range(0, 5).boxed().forEach(i -> mockRuleService(i, sourceRulesList.get(i)));
		IntStream.range(0, 5).boxed().forEach(i -> when(engineRuleDao.getRuleByCodeAndMaxVersion("ruleCode" + i, MODULE_NAME, 10L))
				.thenReturn(newEngineRules.get(i)));

		initializeExecutionContextWithModifiedRuleCodes(sourceRulesList);

		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		sourceRulesList.forEach(r -> verify(r, times(2)).getStatus());
		verify(modelService).saveAll(emptySet());
	}

	@Test
	public void testExecuteInactiveToPublished()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.FALSE);
		final List<AbstractRuleEngineRuleModel> newEngineRules = rulesForVersion("ruleCode", 5, 5, Boolean.TRUE);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.INACTIVE,
						oldEngineRules.get(i), newEngineRules.get(i))).collect(toList());

		IntStream.range(0, 5).boxed().forEach(i -> mockRuleService(i, sourceRulesList.get(i)));
		IntStream.range(0, 5).boxed().forEach(i -> when(engineRuleDao.getRuleByCodeAndMaxVersion("ruleCode" + i, MODULE_NAME, 10L))
				.thenReturn(newEngineRules.get(i)));

		initializeExecutionContextWithModifiedRuleCodes(sourceRulesList);

		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		sourceRulesList.forEach(r -> verify(r, times(1)).getStatus());
		sourceRulesList.forEach(r -> verify(r, atMost(2)).setStatus(RuleStatus.PUBLISHED));
		verify(modelService, times(2)).saveAll(Mockito.argThat(new SourceRulesMatcher(sourceRulesList)));
	}

	protected void initializeExecutionContextWithModifiedRuleCodes(final List<AbstractRuleModel> sourceRulesList)
	{
		executionContext.setModifiedRuleCodes(sourceRulesList.stream().map(AbstractRuleModel::getCode).collect(toList()));
	}

	@Test
	public void testExecutePublishedToInactive()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.TRUE);
		final List<AbstractRuleEngineRuleModel> newEngineRules = rulesForVersion("ruleCode", 5, 5, Boolean.FALSE);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.PUBLISHED,
						oldEngineRules.get(i), newEngineRules.get(i))).collect(toList());

		IntStream.range(0, 5).boxed().forEach(i -> mockRuleService(i, sourceRulesList.get(i)));
		IntStream.range(0, 5).boxed().forEach(i -> when(engineRuleDao.getRuleByCodeAndMaxVersion("ruleCode" + i, MODULE_NAME, 10L))
				.thenReturn(newEngineRules.get(i)));

		initializeExecutionContextWithModifiedRuleCodes(sourceRulesList);

		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		sourceRulesList.forEach(r -> verify(r, times(0)).getStatus());
		verify(modelService, times(2)).saveAll(Mockito.argThat(new SourceRulesMatcher(sourceRulesList)));
		sourceRulesList.forEach(r -> verify(r).setStatus(RuleStatus.INACTIVE));
	}

	@Test
	public void testExecuteRuleEngineActionFailed()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, OLD_DEPLOYED_MVN_VERSION);
		when(ruleEngineActionResult.isActionFailed()).thenReturn(true);

		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.TRUE);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.UNPUBLISHED, oldEngineRules.get(i))).collect(toList());

		final boolean result = postTask.execute(ruleEngineActionResult);

		assertThat(result).isFalse();
		sourceRulesList.forEach(r -> verify(r, times(0)).setStatus(any()));
	}

	private void initializeRuleEngineActionResult(final String oldMvnVersion, final String newMvnVersion)
	{
		when(ruleEngineActionResult.getModuleName()).thenReturn(MODULE_NAME);
		when(ruleEngineActionResult.getOldVersion()).thenReturn(oldMvnVersion);
		when(ruleEngineActionResult.getDeployedVersion()).thenReturn(newMvnVersion);
	}

	private List<AbstractRuleEngineRuleModel> rulesForVersion(final String codePrefix, final int numOfRules,
			final int startVersion, final Boolean active)
	{
		return IntStream.range(startVersion, startVersion + numOfRules).boxed()
				.map(i -> createEngineRule(codePrefix + (i - startVersion), active, Long.valueOf(i))).collect(toList());
	}

	private AbstractRuleEngineRuleModel createEngineRule(final String code, final Boolean active, final Long version)
	{
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		final DroolsKIEModuleModel kieModule = mock(DroolsKIEModuleModel.class);
		when(kieModule.getName()).thenReturn(MODULE_NAME);
		when(kieModule.getVersion()).thenReturn(10L);
		when(kieModule.getDeployedMvnVersion()).thenReturn(DEPLOYED_MVN_VERSION);
		when(kieModule.getMvnVersion()).thenReturn(MVN_VERSION);
		when(kieBase.getKieModule()).thenReturn(kieModule);

		final DroolsRuleModel engineRule = mock(DroolsRuleModel.class);
		when(engineRule.getActive()).thenReturn(active);
		when(engineRule.getCode()).thenReturn(code);
		when(engineRule.getVersion()).thenReturn(version);
		when(engineRule.getKieBase()).thenReturn(kieBase);
		return engineRule;
	}

	private AbstractRuleModel createSourceRule(final String code, final RuleStatus ruleStatus,
			final AbstractRuleEngineRuleModel... engineRules)
	{
		final AbstractRuleModel rule = mock(AbstractRuleModel.class);
		if (ArrayUtils.isNotEmpty(engineRules))
		{
			final Set<AbstractRuleEngineRuleModel> engineRuleSet = Sets.newHashSet(engineRules);
			when(rule.getEngineRules()).thenReturn(engineRuleSet);
			engineRuleSet.forEach(r -> when(r.getSourceRule()).thenReturn(rule));
		}
		when(rule.getCode()).thenReturn(code);
		when(rule.getStatus()).thenReturn(ruleStatus);
		when(ruleService.getRuleForCode(code)).thenReturn(rule);
		return rule;
	}

	private void mockRuleService(final int codeIdx, final AbstractRuleModel sourceRule)
	{
		final List<AbstractRuleModel> rules = Collections.singletonList(sourceRule);
		when(ruleService.getAllRulesForCodeAndStatus("ruleCode" + codeIdx, RuleStatus.PUBLISHED, RuleStatus.INACTIVE)).thenReturn(
				rules);
	}

	private class SourceRulesMatcher extends BaseMatcher<Set<AbstractRuleModel>>
	{
		private final Collection<AbstractRuleModel> sourceRulesToMatch;

		private SourceRulesMatcher(final Collection<AbstractRuleModel> sourceRulesToMatch)
		{
			this.sourceRulesToMatch = sourceRulesToMatch;
		}

		@Override
		public boolean matches(final Object item)
		{
			boolean match = item instanceof Set;
			if (match)
			{
				match = ((Set<AbstractRuleModel>) item).containsAll(sourceRulesToMatch);
			}

			return match;
		}

		@Override
		public void describeTo(final Description description)
		{
			description.appendText("This set of source rules must contain " + sourceRulesToMatch);
		}
	}
}
