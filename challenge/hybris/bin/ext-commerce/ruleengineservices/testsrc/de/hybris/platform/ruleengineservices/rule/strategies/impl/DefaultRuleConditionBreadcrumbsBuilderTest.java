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
package de.hybris.platform.ruleengineservices.rule.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.ruleengineservices.definitions.conditions.RuleGroupOperator;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterDefinitionData;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueNormalizerStrategy;
import de.hybris.platform.security.impl.DefaultXssEncodeService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultRuleConditionBreadcrumbsBuilderTest
{
	private static final String CONDITION_DEF1_ID = "condition1";
	private static final String CONDITION_DEF1_BREADCRUMB = "condition 1";

	private static final String CONDITION_DEF2_ID = "condition2";
	private static final String CONDITION_DEF2_PARAMETER_ID = "param";
	private static final String CONDITION_DEF2_PARAMETER_TYPE = String.class.getName();
	private static final String CONDITION_DEF2_BREADCRUMB = "condition 2 {" + CONDITION_DEF2_PARAMETER_ID + "}";

	private static final String GROUP_CONDITION_DEF_ID = "y_group";
	private static final String GROUP_CONDITION_DEF_PARAM_ID = "operator";
	private static final String GROUP_CONDITION_DEF_PARAM_TYPE = RuleGroupOperator.class.getName();
	private static final String GROUP_CONDITION_DEF_BREADCRUMB = "{" + GROUP_CONDITION_DEF_PARAM_ID + "}";

	private static final String CONTAINER_CONDITION_DEF_ID = "y_container";
	private static final String CONTAINER_CONDITION_DEF_PARAM_ID = "id";
	private static final String CONTAINER_CONDITION_DEF_PARAM_TYPE = String.class.getName();
	private static final String CONTAINER_CONDITION_DEF_BREADCRUMB = "{" + CONTAINER_CONDITION_DEF_PARAM_ID + "}";

	@Rule
	@SuppressWarnings("PMD")
	public final ExpectedException expectedException = ExpectedException.none();

	@Mock
	private I18NService i18NService;

	@Mock
	private L10NService l10NService;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private RuleParameterValueNormalizerStrategy ruleParameterValueNormalizerStrategy;

	private Map<String, RuleConditionDefinitionData> conditionDefinitions;

	private DefaultRuleConditionBreadcrumbsBuilder ruleConditionBreadcrumbsBuilder;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final RuleConditionDefinitionData conditionDefinition1 = new RuleConditionDefinitionData();
		conditionDefinition1.setId(CONDITION_DEF1_ID);
		conditionDefinition1.setBreadcrumb(CONDITION_DEF1_BREADCRUMB);

		final RuleParameterDefinitionData conditionDefinition2Parameter = new RuleParameterDefinitionData();
		conditionDefinition2Parameter.setType(CONDITION_DEF2_PARAMETER_TYPE);

		final RuleConditionDefinitionData conditionDefinition2 = new RuleConditionDefinitionData();
		conditionDefinition2.setId(CONDITION_DEF2_ID);
		conditionDefinition2.setBreadcrumb(CONDITION_DEF2_BREADCRUMB);
		conditionDefinition2.setParameters(Collections.singletonMap(CONDITION_DEF2_PARAMETER_ID, conditionDefinition2Parameter));

		final RuleParameterDefinitionData groupConditionDefinitionParam = new RuleParameterDefinitionData();
		groupConditionDefinitionParam.setType(GROUP_CONDITION_DEF_PARAM_TYPE);

		final RuleConditionDefinitionData groupConditionDefinition = new RuleConditionDefinitionData();
		groupConditionDefinition.setId(GROUP_CONDITION_DEF_ID);
		groupConditionDefinition.setBreadcrumb(GROUP_CONDITION_DEF_BREADCRUMB);
		groupConditionDefinition
				.setParameters(Collections.singletonMap(GROUP_CONDITION_DEF_PARAM_ID, groupConditionDefinitionParam));
		groupConditionDefinition.setAllowsChildren(Boolean.TRUE);

		final RuleParameterDefinitionData containerConditionDefinitionParam = new RuleParameterDefinitionData();
		containerConditionDefinitionParam.setType(CONTAINER_CONDITION_DEF_PARAM_TYPE);

		final RuleConditionDefinitionData containerConditionDefinition = new RuleConditionDefinitionData();
		containerConditionDefinition.setId(CONTAINER_CONDITION_DEF_ID);
		containerConditionDefinition.setBreadcrumb(CONTAINER_CONDITION_DEF_BREADCRUMB);
		containerConditionDefinition
				.setParameters(Collections.singletonMap(CONTAINER_CONDITION_DEF_PARAM_ID, containerConditionDefinitionParam));
		containerConditionDefinition.setAllowsChildren(Boolean.TRUE);

		conditionDefinitions = new HashMap<>();
		conditionDefinitions.put(conditionDefinition1.getId(), conditionDefinition1);
		conditionDefinitions.put(conditionDefinition2.getId(), conditionDefinition2);
		conditionDefinitions.put(groupConditionDefinition.getId(), groupConditionDefinition);
		conditionDefinitions.put(containerConditionDefinition.getId(), containerConditionDefinition);

		when(l10NService.getLocalizedString(buildLocalizationKey(RuleGroupOperator.AND)))
				.thenReturn(RuleGroupOperator.AND.toString());
		when(l10NService.getLocalizedString(buildLocalizationKey(RuleGroupOperator.OR)))
				.thenReturn(RuleGroupOperator.OR.toString());
		when(ruleParameterValueNormalizerStrategy.normalize(any(), anyString())).then(returnsFirstArg());

		final DefaultRuleMessageFormatStrategy ruleMessageFormatStrategy = new DefaultRuleMessageFormatStrategy();
		ruleMessageFormatStrategy.setL10NService(l10NService);
		ruleMessageFormatStrategy.setEnumerationService(enumerationService);
		ruleMessageFormatStrategy.setRuleParameterValueNormalizerStrategy(ruleParameterValueNormalizerStrategy);

		ruleConditionBreadcrumbsBuilder = new DefaultRuleConditionBreadcrumbsBuilder();
		ruleConditionBreadcrumbsBuilder.setRuleMessageFormatStrategy(ruleMessageFormatStrategy);
		ruleConditionBreadcrumbsBuilder.setXssEncodeService(new DefaultXssEncodeService());
		ruleConditionBreadcrumbsBuilder.setI18NService(i18NService);
	}

	protected String buildLocalizationKey(final Object value)
	{
		final String localizationKey = value.getClass().getName() + "." + value + ".name";
		return localizationKey.toLowerCase(Locale.ENGLISH);
	}

	@Test
	public void buildBreadcrumbsNullConditions() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		ruleConditionBreadcrumbsBuilder.buildConditionBreadcrumbs(null, null);
	}

	@Test
	public void buildBreadcrumbsEmptyConditions() throws Exception
	{
		// when
		final String breadcrumb = ruleConditionBreadcrumbsBuilder.buildConditionBreadcrumbs(Collections.emptyList(),
				conditionDefinitions);

		// then
		Assert.assertTrue(StringUtils.isEmpty(breadcrumb));
	}

	@Test
	public void buildBreadcrumbsNoDefinitionFound() throws Exception
	{
		// given
		final RuleConditionData condition = new RuleConditionData();
		condition.setDefinitionId("blabla");

		final List<RuleConditionData> conditions = Collections.singletonList(condition);

		// expect
		expectedException.expect(RuleEngineServiceException.class);

		// when
		ruleConditionBreadcrumbsBuilder.buildConditionBreadcrumbs(conditions, conditionDefinitions);
	}

	@Test
	public void buildSingleConditionsBreadcrumb() throws Exception
	{
		// given
		final RuleConditionData condition = new RuleConditionData();
		condition.setDefinitionId(CONDITION_DEF1_ID);

		final List<RuleConditionData> conditions = Collections.singletonList(condition);
		final String expectedBreadcrumbs = "condition 1";

		// when
		final String breadcrumbs = ruleConditionBreadcrumbsBuilder.buildConditionBreadcrumbs(conditions, conditionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildSingleConditionsBreadcrumbWithParameter() throws Exception
	{
		// given
		final RuleParameterData conditionParameter = new RuleParameterData();
		conditionParameter.setValue("param");

		final RuleConditionData condition = new RuleConditionData();
		condition.setDefinitionId(CONDITION_DEF2_ID);
		condition.setParameters(Collections.singletonMap(CONDITION_DEF2_PARAMETER_ID, conditionParameter));

		final List<RuleConditionData> conditions = Collections.singletonList(condition);
		final String expectedBreadcrumbs = "condition 2 param";

		// when
		final String breadcrumbs = ruleConditionBreadcrumbsBuilder.buildConditionBreadcrumbs(conditions, conditionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildMultipleConditionBreadcrumbs() throws Exception
	{
		// given
		final RuleConditionData condition1 = new RuleConditionData();
		condition1.setDefinitionId(CONDITION_DEF1_ID);

		final RuleParameterData condition2Parameter = new RuleParameterData();
		condition2Parameter.setValue("testparam");

		final RuleConditionData condition2 = new RuleConditionData();
		condition2.setDefinitionId(CONDITION_DEF2_ID);
		condition2.setParameters(Collections.singletonMap(CONDITION_DEF2_PARAMETER_ID, condition2Parameter));

		final List<RuleConditionData> conditions = Arrays.asList(condition1, condition2);
		final String expectedBreadcrumbs = "condition 1 AND condition 2 testparam";

		// when
		final String breadcrumbs = ruleConditionBreadcrumbsBuilder.buildConditionBreadcrumbs(conditions, conditionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildStyledMultipleConditionBreadcrumbs() throws Exception
	{
		// given
		final RuleConditionData condition1 = new RuleConditionData();
		condition1.setDefinitionId(CONDITION_DEF1_ID);

		final RuleParameterData condition2Parameter = new RuleParameterData();
		condition2Parameter.setValue("testmultiple");

		final RuleConditionData condition2 = new RuleConditionData();
		condition2.setDefinitionId(CONDITION_DEF2_ID);
		condition2.setParameters(Collections.singletonMap(CONDITION_DEF2_PARAMETER_ID, condition2Parameter));

		final List<RuleConditionData> conditions = Arrays.asList(condition1, condition2);
		final String expectedBreadcrumbs = "condition 1<span class=\"rule-parent-condition rule-parent-condition-y_group\"> AND </span>condition 2 testmultiple";

		// when
		final String breadcrumbs = ruleConditionBreadcrumbsBuilder.buildStyledConditionBreadcrumbs(conditions,
				conditionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildStyledMultipleConditionBreadcrumbsWithOR() throws Exception
	{
		// given
		final RuleConditionData condition1 = new RuleConditionData();
		condition1.setDefinitionId(CONDITION_DEF1_ID);

		final RuleConditionData condition2 = new RuleConditionData();
		condition2.setDefinitionId(CONDITION_DEF1_ID);

		final RuleConditionData condition3 = new RuleConditionData();
		condition3.setDefinitionId(CONDITION_DEF1_ID);

		final RuleParameterData operatorParameter = new RuleParameterData();
		operatorParameter.setValue(RuleGroupOperator.OR);

		final RuleConditionData groupCondition = new RuleConditionData();
		groupCondition.setDefinitionId(GROUP_CONDITION_DEF_ID);
		groupCondition.setParameters(Collections.singletonMap(GROUP_CONDITION_DEF_PARAM_ID, operatorParameter));
		groupCondition.setChildren(Arrays.asList(condition2, condition3));

		final List<RuleConditionData> conditions = Arrays.asList(condition1, groupCondition);
		final String expectedBreadcrumbs = "condition 1<span class=\"rule-parent-condition rule-parent-condition-y_group\"> AND </span><span class=\"rule-parent-condition rule-parent-condition-y_group\">(</span>condition 1<span class=\"rule-parent-condition rule-parent-condition-y_group\"> OR </span>condition 1<span class=\"rule-parent-condition rule-parent-condition-y_group\">)</span>";

		// when
		final String breadcrumbs = ruleConditionBreadcrumbsBuilder.buildStyledConditionBreadcrumbs(conditions,
				conditionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildStyledMultipleConditionBreadcrumbsWithContainer() throws Exception
	{
		// given
		final RuleConditionData condition1 = new RuleConditionData();
		condition1.setDefinitionId(CONDITION_DEF1_ID);

		final RuleConditionData condition2 = new RuleConditionData();
		condition2.setDefinitionId(CONDITION_DEF1_ID);

		final RuleParameterData idParameter = new RuleParameterData();
		idParameter.setValue("CONTAINER");

		final RuleConditionData containerCondition = new RuleConditionData();
		containerCondition.setDefinitionId(CONTAINER_CONDITION_DEF_ID);
		containerCondition.setParameters(Collections.singletonMap(CONTAINER_CONDITION_DEF_PARAM_ID, idParameter));
		containerCondition.setChildren(Arrays.asList(condition1, condition2));

		final List<RuleConditionData> conditions = Arrays.asList(containerCondition);
		final String expectedBreadcrumbs = "<span class=\"rule-parent-condition rule-parent-condition-y_container\">CONTAINER (</span>condition 1<span class=\"rule-parent-condition rule-parent-condition-y_group\"> AND </span>condition 1<span class=\"rule-parent-condition rule-parent-condition-y_container\">)</span>";

		// when
		final String breadcrumbs = ruleConditionBreadcrumbsBuilder.buildStyledConditionBreadcrumbs(conditions,
				conditionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}
}
