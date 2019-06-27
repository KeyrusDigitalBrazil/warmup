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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.jalo.SourceRule;
import de.hybris.platform.ruleengineservices.model.RuleActionDefinitionModel;
import de.hybris.platform.ruleengineservices.model.RuleConditionDefinitionModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionCategoryData;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionCategoryData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.services.RuleActionDefinitionService;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionDefinitionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultRuleActionsServiceTest
{
	private static final String DEF_ID1 = "D1";
	private static final String DEF_NAME1 = "Def1";
	private static final String DEF_ID2 = "D2";
	private static final String DEF_NAME2 = "Def2";
	private static final String CAT_ID1 = "1";
	private static final String CAT_NAME1 = "CAT1";
	private static final String CAT_ID2 = "2";
	private static final String CAT_NAME2 = "CAT2";

	private static final Class<SourceRule> SOURCE_RULE_TYPE = SourceRule.class;

	private DefaultRuleActionsRegistry ruleActionsRegistry;

	@Mock
	private RuleConditionDefinitionService ruleConditionDefinitionService;

	@Mock
	private Converter<RuleConditionDefinitionModel, RuleConditionDefinitionData> ruleConditionDefinitionConverter;

	@Mock
	private RuleActionDefinitionService ruleActionDefinitionService;

	@Mock
	private Converter<RuleActionDefinitionModel, RuleActionDefinitionData> ruleActionDefinitionConverter;

	@Mock
	private RuleActionDefinitionModel mockDefAct1;

	@Mock
	private RuleActionDefinitionModel mockDefAct2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		ruleActionsRegistry = new DefaultRuleActionsRegistry();
		ruleActionsRegistry.setRuleActionDefinitionConverter(ruleActionDefinitionConverter);
		ruleActionsRegistry.setRuleActionDefinitionService(ruleActionDefinitionService);

		final RuleConditionDefinitionCategoryData catCon1 = new RuleConditionDefinitionCategoryData();
		catCon1.setId(CAT_ID1);
		catCon1.setName(CAT_NAME1);
		final RuleConditionDefinitionCategoryData catCon2 = new RuleConditionDefinitionCategoryData();
		catCon2.setId(CAT_ID2);
		catCon2.setName(CAT_NAME2);

		final RuleActionDefinitionCategoryData catAct1 = new RuleActionDefinitionCategoryData();
		catAct1.setId(CAT_ID1);
		catAct1.setName(CAT_NAME1);
		final RuleActionDefinitionCategoryData catAct2 = new RuleActionDefinitionCategoryData();
		catAct2.setId(CAT_ID2);
		catAct2.setName(CAT_NAME2);

		final RuleConditionDefinitionModel mockDefCon1 = mock(RuleConditionDefinitionModel.class);
		final RuleConditionDefinitionModel mockDefCon2 = mock(RuleConditionDefinitionModel.class);
		given(ruleConditionDefinitionService.getAllRuleConditionDefinitions()).willReturn(Arrays.asList(mockDefCon1, mockDefCon2));

		final RuleConditionDefinitionData defCon1 = new RuleConditionDefinitionData();
		defCon1.setId(DEF_ID1);
		defCon1.setName(DEF_NAME1);
		defCon1.setCategories(Arrays.asList(catCon1, catCon2));
		final RuleConditionDefinitionData defCon2 = new RuleConditionDefinitionData();
		defCon2.setId(DEF_ID2);
		defCon2.setName(DEF_NAME2);
		defCon2.setCategories(Collections.singletonList(catCon1));
		given(ruleConditionDefinitionConverter.convert(mockDefCon1)).willReturn(defCon1);
		given(ruleConditionDefinitionConverter.convert(mockDefCon2)).willReturn(defCon2);

		final RuleActionDefinitionData defAct1 = new RuleActionDefinitionData();
		defAct1.setId(DEF_ID1);
		defAct1.setName(DEF_NAME1);
		defAct1.setCategories(Arrays.asList(catAct1, catAct2));
		final RuleActionDefinitionData defAct2 = new RuleActionDefinitionData();
		defAct2.setId(DEF_ID2);
		defAct2.setName(DEF_NAME2);
		defAct2.setCategories(Collections.singletonList(catAct1));
		given(ruleActionDefinitionConverter.convert(mockDefAct1)).willReturn(defAct1);
		given(ruleActionDefinitionConverter.convert(mockDefAct2)).willReturn(defAct2);
	}

	@Test
	public void testGetAllRuleActionDefinitions()
	{
		//given
		given(ruleActionDefinitionService.getAllRuleActionDefinitions()).willReturn(Arrays.asList(mockDefAct1, mockDefAct2));

		// when
		final List<RuleActionDefinitionData> ruleActionDefinitions = ruleActionsRegistry.getAllActionDefinitions();

		// then
		assertNotNull(ruleActionDefinitions);
		assertEquals(2, ruleActionDefinitions.size());
		for (final RuleActionDefinitionData ruleActionDefinition : ruleActionDefinitions)
		{
			assertNotNull(ruleActionDefinition);
			assertTrue(ruleActionDefinition.getId().equals(DEF_ID1) || ruleActionDefinition.getId().equals(DEF_ID2));

			if (ruleActionDefinition.getId().equals(DEF_ID1))
			{
				assertEquals(2, ruleActionDefinition.getCategories().size());
			}
			else
			{
				assertEquals(1, ruleActionDefinition.getCategories().size());
			}
		}
	}

	@Test
	public void testGetAllRuleActionDefinitionsForRuleType()
	{
		//given
		given(ruleActionDefinitionService.getRuleActionDefinitionsForRuleType(SOURCE_RULE_TYPE)).willReturn(
				Arrays.asList(mockDefAct1));

		// when
		final List<RuleActionDefinitionData> ruleActionDefinitions = ruleActionsRegistry
				.getActionDefinitionsForRuleType(SOURCE_RULE_TYPE);

		// then
		assertNotNull(ruleActionDefinitions);
		assertEquals(1, ruleActionDefinitions.size());
		final RuleActionDefinitionData ruleActionDefinition = ruleActionDefinitions.get(0);
		assertNotNull(ruleActionDefinition);
		assertTrue(ruleActionDefinition.getId().equals(DEF_ID1));
		assertEquals(2, ruleActionDefinition.getCategories().size());
	}
}
