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
package de.hybris.platform.ruleengineservices.rule.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengine.versioning.ModuleVersionResolver;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleServiceUnitTest
{
	private static final String DEFAULT_RULE_TYPE = "DEFAULT";

	private static final String MODULE_NAME = "moduleName";

	@InjectMocks
	private DefaultRuleService defaultRuleService;

	@Mock
	private RuleDao ruleDao;

	@Mock
	private EngineRuleDao engineRuleDao;

	@Mock
	private RuleEngineContextFinderStrategy ruleEngineContextFinderStrategy;

	@Mock
	private CatalogVersionModel catalogVersionModel;

	@Mock
	private DroolsRuleEngineContextModel droolsRuleEngineContextModel;

	@Mock
	private DroolsKIEModuleModel droolsKIEModuleModel;

	@Mock
	private DroolsKIESessionModel droolsKIESessionModel;

	@Mock
	private DroolsKIEBaseModel droolsKIEBaseModel;

	@Mock
	private ModuleVersionResolver<DroolsKIEModuleModel> moduleVersionResolver;

	@Before
	public void setup()
	{
		when(droolsRuleEngineContextModel.getKieSession()).thenReturn(droolsKIESessionModel);
		when(droolsKIESessionModel.getKieBase()).thenReturn(droolsKIEBaseModel);
		when(droolsKIEBaseModel.getKieModule()).thenReturn(droolsKIEModuleModel);
		when(droolsKIEModuleModel.getName()).thenReturn(MODULE_NAME);
	}

	@Test
	public void testFindEngineRuleTypeForRuleType()
	{
		when(ruleDao.findEngineRuleTypeByRuleType(SourceRuleModel.class)).thenReturn(RuleType.DEFAULT);

		final RuleType ruleType = defaultRuleService.getEngineRuleTypeForRuleType(SourceRuleModel.class);
		assertEquals(DEFAULT_RULE_TYPE, ruleType.getCode());
	}

	@Test
	public void testFindEngineRuleTypeForRuleTypeNotFound()
	{
		when(ruleDao.findEngineRuleTypeByRuleType(SourceRuleModel.class)).thenReturn(null);

		final RuleType ruleType = defaultRuleService.getEngineRuleTypeForRuleType(SourceRuleModel.class);
		assertEquals(DEFAULT_RULE_TYPE, ruleType.getCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testActiveRulesShouldThrowIllegalArgumentExceptionOnMissingKiBase()
	{
		when(droolsKIESessionModel.getKieBase()).thenReturn(null);
		defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(null, RuleType.DEFAULT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testActiveRulesShouldThrowIllegalArgumentExceptionOnMissingCatalogVersion()
	{
		defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(null, RuleType.DEFAULT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testActiveRulesShouldThrowIllegalArgumentExceptionOnMissingRuleType()
	{
		defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(catalogVersionModel, null);

		verifyZeroInteractions(ruleEngineContextFinderStrategy);
	}

	@Test(expected = IllegalStateException.class)
	public void testActiveRulesShouldThrowIllegalStateExceptionWhenMultipleRuleEngineContextsFound()
	{
		when(ruleEngineContextFinderStrategy.getRuleEngineContextForCatalogVersions(singletonList(catalogVersionModel),
				RuleType.DEFAULT)).thenThrow(IllegalStateException.class);

		defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(catalogVersionModel, RuleType.DEFAULT);

		verify(ruleEngineContextFinderStrategy).getRuleEngineContextForCatalogVersions(eq(singletonList(catalogVersionModel)),
				eq(RuleType.DEFAULT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllRulesForStatusShouldThrowIllegalStateExceptionOnMissingStatus()
	{
		defaultRuleService.getAllRulesForStatus(null);

		verifyZeroInteractions(ruleDao);
	}

	@Test
	public void testAllRulesForStatus()
	{
		final RuleStatus[] statuses = new RuleStatus[]{RuleStatus.PUBLISHED};

		final List<AbstractRuleModel> daoResult = mock(List.class);

		when(ruleDao.findAllRulesWithStatuses(statuses)).thenReturn(daoResult);
		final List<AbstractRuleModel> result = defaultRuleService.getAllRulesForStatus(statuses);

		assertSame(daoResult, result);
		verify(ruleDao).findAllRulesWithStatuses(statuses);
	}

	@Test
	public void testActiveRulesShouldReturnEmptyListWhenNoRuleContextIsFound()
	{
		when(ruleEngineContextFinderStrategy.getRuleEngineContextForCatalogVersions(singletonList(catalogVersionModel),
				RuleType.DEFAULT)).thenReturn(Optional.empty());

		final List<AbstractRuleModel> result = defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(catalogVersionModel,
				RuleType.DEFAULT);

		assertThat(result, is(emptyList()));

		verify(ruleEngineContextFinderStrategy).getRuleEngineContextForCatalogVersions(eq(singletonList(catalogVersionModel)),
				eq(RuleType.DEFAULT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testActiveRulesShouldPropagateIllegalArgumentException()
	{
		when(ruleEngineContextFinderStrategy.getRuleEngineContextForCatalogVersions(singletonList(catalogVersionModel),
				RuleType.DEFAULT)).thenReturn(Optional.of(droolsRuleEngineContextModel));
		when(moduleVersionResolver.getDeployedModuleVersion(droolsKIEModuleModel)).thenThrow(IllegalArgumentException.class);

		defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(catalogVersionModel, RuleType.DEFAULT);

		verify(ruleEngineContextFinderStrategy).getRuleEngineContextForCatalogVersions(eq(singletonList(catalogVersionModel)),
				eq(RuleType.DEFAULT));
		verify(moduleVersionResolver).getDeployedModuleVersion(droolsKIEModuleModel);
		verifyZeroInteractions(engineRuleDao);
	}

	@Test
	public void testActiveRulesShouldReturnEmptyListWhenActiveRulesAreFound()
	{
		when(ruleEngineContextFinderStrategy.getRuleEngineContextForCatalogVersions(singletonList(catalogVersionModel),
				RuleType.DEFAULT)).thenReturn(Optional.of(droolsRuleEngineContextModel));
		when(engineRuleDao.getActiveRulesForVersion(MODULE_NAME, 1)).thenReturn(emptyList());
		when(moduleVersionResolver.getDeployedModuleVersion(droolsKIEModuleModel)).thenReturn(Optional.of(Long.valueOf(1)));

		final List<AbstractRuleModel> result = defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(catalogVersionModel,
				RuleType.DEFAULT);

		assertThat(result, is(emptyList()));

		verify(ruleEngineContextFinderStrategy).getRuleEngineContextForCatalogVersions(eq(singletonList(catalogVersionModel)),
				eq(RuleType.DEFAULT));
		verify(engineRuleDao).getActiveRulesForVersion(MODULE_NAME, 1);
	}

	@Test
	public void testActiveRulesShouldReturnSourceRules()
	{
		final AbstractRuleEngineRuleModel engineRule1 = mock(AbstractRuleEngineRuleModel.class);
		final AbstractRuleEngineRuleModel engineRule2 = mock(AbstractRuleEngineRuleModel.class);

		final AbstractRuleModel rule1 = mock(AbstractRuleModel.class);
		final AbstractRuleModel rule2 = mock(AbstractRuleModel.class);

		when(engineRule1.getSourceRule()).thenReturn(rule1);
		when(engineRule2.getSourceRule()).thenReturn(rule2);

		when(ruleEngineContextFinderStrategy.getRuleEngineContextForCatalogVersions(singletonList(catalogVersionModel),
				RuleType.DEFAULT)).thenReturn(Optional.of(droolsRuleEngineContextModel));
		when(engineRuleDao.getActiveRulesForVersion(MODULE_NAME, 1)).thenReturn(asList(engineRule1, engineRule2));
		when(moduleVersionResolver.getDeployedModuleVersion(droolsKIEModuleModel)).thenReturn(Optional.of(Long.valueOf(1)));

		final List<AbstractRuleModel> result = defaultRuleService.getActiveRulesForCatalogVersionAndRuleType(catalogVersionModel,
				RuleType.DEFAULT);

		assertTrue(isNotEmpty(result));
		assertThat(result, hasSize(2));
		assertThat(result, containsInAnyOrder(rule1, rule2));

		verify(ruleEngineContextFinderStrategy).getRuleEngineContextForCatalogVersions(eq(singletonList(catalogVersionModel)),
				eq(RuleType.DEFAULT));
		verify(engineRuleDao).getActiveRulesForVersion(MODULE_NAME, 1);
	}
}
