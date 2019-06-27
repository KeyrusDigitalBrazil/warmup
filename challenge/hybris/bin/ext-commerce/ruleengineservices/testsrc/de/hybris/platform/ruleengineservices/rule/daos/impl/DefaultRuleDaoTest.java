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
package de.hybris.platform.ruleengineservices.rule.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleTemplateModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@IntegrationTest
public class DefaultRuleDaoTest extends ServicelayerTransactionalTest
{
	private static final String RULE_CODE = "rule1";
	private static final String RULE_DESCRIPTION = "description1";
	private static final String DEFAULT_RULE_TYPE = "DEFAULT";
	public static final String MULTI_VERSION_RULE_CODE = "rule6";

	@Resource
	private RuleDao ruleDao;

	@Resource
	private ModelService modelService;

	@Before
	public void setUp() throws ImpExException
	{
		final List<AbstractRuleModel> rules = ruleDao.findAllRules();
		rules.forEach(modelService::remove);

		importCsv("/ruleengineservices/test/rule/defaultRuleDaoTest.impex", "utf-8");
	}

	@Test
	public void testFindAll()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRules();

		// then
		assertNotNull(rules);
		assertEquals(6, rules.size());
	}

	@Test
	public void testFindAllActiveRules()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllActiveRules();

		// then
		assertNotNull(rules);
		assertEquals(3, rules.size());
	}

	@Test
	public void testFindRuleByCode()
	{
		// when
		final AbstractRuleModel rule = ruleDao.findRuleByCode(RULE_CODE);

		// then
		assertNotNull(rule);
		assertEquals(RULE_DESCRIPTION, rule.getDescription());
	}

	@Test
	public void testFindAllRuleVersionsByCode()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRuleVersionsByCode(MULTI_VERSION_RULE_CODE);

		// then
		assertThat(rules).hasSize(5);
	}

	@Test
	public void testFindAllRuleVersionsByCodeAndStatus()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRuleVersionsByCodeAndStatus(MULTI_VERSION_RULE_CODE,
				RuleStatus.PUBLISHED);

		// then
		assertThat(rules).hasSize(2);
	}

	@Test
	public void testFindAllRulesWithStatuses()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRulesWithStatuses(RuleStatus.ARCHIVED, RuleStatus.PUBLISHED);

		// then
		assertThat(rules).hasSize(3);
	}

	@Test
	public void testFindAllByType()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRulesByType(AbstractRuleModel.class);

		// then
		assertNotNull(rules);
		assertEquals(6, rules.size());
	}

	@Test
	public void testFindAllActiveRulesByType()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllActiveRulesByType(AbstractRuleModel.class);

		// then
		assertThat(rules).hasSize(3);
	}

	@Test
	public void testFindRuleByCodeByType()
	{
		// when
		final AbstractRuleModel rule = ruleDao.findRuleByCodeAndType(RULE_CODE, AbstractRuleModel.class);

		// then
		assertNotNull(rule);
		assertEquals(RULE_DESCRIPTION, rule.getDescription());
	}

	@Test
	public void testFindEngineRuleTypeForRuleType()
	{
		final RuleType ruleType = ruleDao.findEngineRuleTypeByRuleType(SourceRuleModel.class);
		assertEquals(DEFAULT_RULE_TYPE, ruleType.getCode());
	}

	@Test
	public void testFindEngineRuleTypeForRuleTypeNotFound()
	{
		final RuleType ruleType = ruleDao.findEngineRuleTypeByRuleType(SourceRuleTemplateModel.class);
		assertNull(ruleType);
	}

	@Test
	public void shouldFindTheLatestRuleVersion() throws Exception
	{
		//given
		assertNotNull(ruleDao.findRuleByCodeAndVersion(MULTI_VERSION_RULE_CODE, 1L).get());
		assertNotNull(ruleDao.findRuleByCodeAndVersion(MULTI_VERSION_RULE_CODE, 2L).get());
		assertNotNull(ruleDao.findRuleByCodeAndVersion(MULTI_VERSION_RULE_CODE, 3L).get());
		//when
		final AbstractRuleModel latestRule = ruleDao.findRuleByCode(MULTI_VERSION_RULE_CODE);
		//then
		assertEquals(5L, latestRule.getVersion().longValue());
	}

	@Test
	public void testFindAllRuleVersionsByCodeAndStatuses()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRuleVersionsByCodeAndStatuses(MULTI_VERSION_RULE_CODE,
				RuleStatus.PUBLISHED, RuleStatus.INACTIVE);

		assertThat(rules.stream().filter(r -> r.getStatus().equals(RuleStatus.PUBLISHED)).collect(Collectors.toList())).hasSize(2);
		assertThat(rules.stream().filter(r -> r.getStatus().equals(RuleStatus.INACTIVE)).collect(Collectors.toList())).hasSize(1);

		// then
		assertThat(rules).hasSize(3);
	}

	@Test
	public void testFindAllRuleVersionsByCodeAndStatusesNoStatusesGiven()
	{
		// when
		final List<AbstractRuleModel> rules = ruleDao.findAllRuleVersionsByCodeAndStatuses(MULTI_VERSION_RULE_CODE);

		assertThat(rules.stream().filter(r -> r.getStatus().equals(RuleStatus.PUBLISHED)).collect(Collectors.toList())).hasSize(2);
		assertThat(rules.stream().filter(r -> r.getStatus().equals(RuleStatus.INACTIVE)).collect(Collectors.toList())).hasSize(1);
		assertThat(rules.stream().filter(r -> r.getStatus().equals(RuleStatus.ARCHIVED)).collect(Collectors.toList())).hasSize(1);

		// then
		assertThat(rules).hasSize(5);
	}
}
