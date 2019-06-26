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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.util.EngineRulesRepository;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.ReleaseId;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIncrementalRuleEngineUpdateStrategyUnitTest
{
	private static final String MODULE_NAME = "MODULE_NAME";

	@InjectMocks
	private DefaultIncrementalRuleEngineUpdateStrategy incrementalRuleEngineUpdateStrategy;
	@Mock
	private EngineRulesRepository engineRulesRepository;
	@Mock
	private ReleaseId releaseId;

	@Before
	public void setUp()
	{
		incrementalRuleEngineUpdateStrategy.setTotalNumOfRulesThreshold(100);
		incrementalRuleEngineUpdateStrategy.setFractionOfRulesThreshold(0.5F);
	}

	@Test
	public void testShouldUpdateIncrementallyTotalIsLow()
	{
		when(engineRulesRepository.countDeployedEngineRulesForModule(MODULE_NAME)).thenReturn(99L);
		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> addRemoveRules = createAddRemoveRulesList(10,
				0);
		final boolean updateIncrementally = incrementalRuleEngineUpdateStrategy.shouldUpdateIncrementally(releaseId, MODULE_NAME, addRemoveRules.getLeft(), addRemoveRules.getRight());
		assertThat(updateIncrementally).isFalse();
	}

	@Test
	public void testShouldUpdateIncrementallyRulesToUpdateIsHigh()
	{
		when(engineRulesRepository.countDeployedEngineRulesForModule(MODULE_NAME)).thenReturn(99L);
		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> addRemoveRules = createAddRemoveRulesList(10,
				99);
		final boolean updateIncrementally = incrementalRuleEngineUpdateStrategy.shouldUpdateIncrementally(releaseId, MODULE_NAME, addRemoveRules.getLeft(), addRemoveRules.getRight());
		assertThat(updateIncrementally).isFalse();
	}

	@Test
	public void testShouldUpdateIncrementallyFractionIsHigh()
	{
		when(engineRulesRepository.countDeployedEngineRulesForModule(MODULE_NAME)).thenReturn(1000L);
		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> addRemoveRules = createAddRemoveRulesList(900,
				10);
		final boolean updateIncrementally = incrementalRuleEngineUpdateStrategy.shouldUpdateIncrementally(releaseId, MODULE_NAME, addRemoveRules.getLeft(), addRemoveRules.getRight());
		assertThat(updateIncrementally).isFalse();
	}

	@Test
	public void testShouldUpdateIncrementallyFractionIsLow()
	{
		when(engineRulesRepository.countDeployedEngineRulesForModule(MODULE_NAME)).thenReturn(1000L);
		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> addRemoveRules = createAddRemoveRulesList(90,
				10);
		final boolean updateIncrementally = incrementalRuleEngineUpdateStrategy.shouldUpdateIncrementally(releaseId, MODULE_NAME, addRemoveRules.getLeft(), addRemoveRules.getRight());
		assertThat(updateIncrementally).isTrue();
	}

	private Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> createAddRemoveRulesList(final int rulesToAdd,
			final int rulesToRemove)
	{
		final Collection<DroolsRuleModel> rulesToAddCollection = IntStream.range(0, rulesToAdd).boxed().map(i -> createEngineRule())
				.collect(
						Collectors.toList());
		final Collection<DroolsRuleModel> rulesToRemoveCollection = IntStream.range(0, rulesToRemove).boxed()
				.map(i -> createEngineRule()).collect(
						Collectors.toList());
		return ImmutablePair.of(rulesToAddCollection, rulesToRemoveCollection);
	}

	private DroolsRuleModel createEngineRule()
	{
		return mock(DroolsRuleModel.class);
	}
}
