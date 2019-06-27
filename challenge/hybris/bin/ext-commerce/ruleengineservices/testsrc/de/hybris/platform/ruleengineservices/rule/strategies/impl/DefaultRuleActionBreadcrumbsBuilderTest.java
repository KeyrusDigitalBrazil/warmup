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
import de.hybris.platform.ruleengineservices.rule.data.RuleActionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionData;
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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultRuleActionBreadcrumbsBuilderTest
{
	private static final String ACTION_DEF1_ID = "action1";
	private static final String ACTION_DEF1_BREADCRUMB = "action 1";

	private static final String ACTION_DEF2_ID = "action2";
	private static final String ACTION_DEF2_PARAMETER_ID = "param";
	private static final String ACTION_DEF2_PARAMETER_TYPE = String.class.getName();
	private static final String ACTION_DEF2_BREADCRUMB = "action 2 {" + ACTION_DEF2_PARAMETER_ID + "}";

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

	private Map<String, RuleActionDefinitionData> actionDefinitions;

	private DefaultRuleActionBreadcrumbsBuilder ruleActionBreadcrumbsBuilder;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final RuleActionDefinitionData actionDefinition1 = new RuleActionDefinitionData();
		actionDefinition1.setId(ACTION_DEF1_ID);
		actionDefinition1.setBreadcrumb(ACTION_DEF1_BREADCRUMB);

		final RuleParameterDefinitionData actionDefinition2Param = new RuleParameterDefinitionData();
		actionDefinition2Param.setType(ACTION_DEF2_PARAMETER_TYPE);

		final RuleActionDefinitionData actionDefinition2 = new RuleActionDefinitionData();
		actionDefinition2.setId(ACTION_DEF2_ID);
		actionDefinition2.setBreadcrumb(ACTION_DEF2_BREADCRUMB);
		actionDefinition2.setParameters(Collections.singletonMap(ACTION_DEF2_PARAMETER_ID, actionDefinition2Param));

		actionDefinitions = new HashMap<String, RuleActionDefinitionData>();
		actionDefinitions.put(actionDefinition1.getId(), actionDefinition1);
		actionDefinitions.put(actionDefinition2.getId(), actionDefinition2);

		final DefaultRuleMessageFormatStrategy ruleMessageFormatStrategy = new DefaultRuleMessageFormatStrategy();
		ruleMessageFormatStrategy.setL10NService(l10NService);
		ruleMessageFormatStrategy.setEnumerationService(enumerationService);
		ruleMessageFormatStrategy.setRuleParameterValueNormalizerStrategy(ruleParameterValueNormalizerStrategy);
		
		when(ruleParameterValueNormalizerStrategy.normalize(any(), anyString())).then(returnsFirstArg());

		ruleActionBreadcrumbsBuilder = new DefaultRuleActionBreadcrumbsBuilder();
		ruleActionBreadcrumbsBuilder.setRuleMessageFormatStrategy(ruleMessageFormatStrategy);
		ruleActionBreadcrumbsBuilder.setXssEncodeService(new DefaultXssEncodeService());
		ruleActionBreadcrumbsBuilder.setI18NService(i18NService);
	}

	@Test
	public void buildBreadcrumbsNullActions() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		ruleActionBreadcrumbsBuilder.buildActionBreadcrumbs(null, null);
	}

	@Test
	public void buildBreadcrumbsEmptyActions() throws Exception
	{
		// when
		final String breadcrumb = ruleActionBreadcrumbsBuilder.buildActionBreadcrumbs(Collections.emptyList(), actionDefinitions);

		// then
		Assert.assertTrue(StringUtils.isEmpty(breadcrumb));
	}

	@Test
	public void buildBreadcrumbsNoDefinitionFound() throws Exception
	{
		// given
		final RuleActionData action = new RuleActionData();
		action.setDefinitionId("blabla");

		final List<RuleActionData> actions = Collections.singletonList(action);

		// expect
		expectedException.expect(RuleEngineServiceException.class);

		// when
		ruleActionBreadcrumbsBuilder.buildActionBreadcrumbs(actions, actionDefinitions);
	}

	@Test
	public void buildSingleActionBreadcrumb() throws Exception
	{
		// given
		final RuleActionData action = new RuleActionData();
		action.setDefinitionId(ACTION_DEF1_ID);

		final List<RuleActionData> actions = Collections.singletonList(action);
		final String expectedBreadcrumbs = "action 1";

		// when
		final String breadcrumbs = ruleActionBreadcrumbsBuilder.buildActionBreadcrumbs(actions, actionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildSingleActionBreadcrumbWithParameter() throws Exception
	{
		// given
		final RuleParameterData actionParameter = new RuleParameterData();
		actionParameter.setValue("param");

		final RuleActionData action = new RuleActionData();
		action.setDefinitionId(ACTION_DEF2_ID);
		action.setParameters(Collections.singletonMap(ACTION_DEF2_PARAMETER_ID, actionParameter));

		final List<RuleActionData> actions = Collections.singletonList(action);
		final String expectedBreadcrumbs = "action 2 param";

		// when
		final String breadcrumbs = ruleActionBreadcrumbsBuilder.buildActionBreadcrumbs(actions, actionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildMultipleActionBreadcrumbs() throws Exception
	{
		// given
		final RuleActionData action1 = new RuleActionData();
		action1.setDefinitionId(ACTION_DEF1_ID);

		final RuleParameterData action2Parameter = new RuleParameterData();
		action2Parameter.setValue("testparam");

		final RuleActionData action2 = new RuleActionData();
		action2.setDefinitionId(ACTION_DEF2_ID);
		action2.setParameters(Collections.singletonMap(ACTION_DEF2_PARAMETER_ID, action2Parameter));

		final List<RuleActionData> actions = Arrays.asList(action1, action2);
		final String expectedBreadcrumbs = "action 1, action 2 testparam";

		// when
		final String breadcrumbs = ruleActionBreadcrumbsBuilder.buildActionBreadcrumbs(actions, actionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}

	@Test
	public void buildStyledMultipleActionBreadcrumbs() throws Exception
	{
		// given
		final RuleActionData action1 = new RuleActionData();
		action1.setDefinitionId(ACTION_DEF1_ID);

		final RuleParameterData action2Parameter = new RuleParameterData();
		action2Parameter.setValue("testmultiple");

		final RuleActionData action2 = new RuleActionData();
		action2.setDefinitionId(ACTION_DEF2_ID);
		action2.setParameters(Collections.singletonMap(ACTION_DEF2_PARAMETER_ID, action2Parameter));

		final List<RuleActionData> actions = Arrays.asList(action1, action2);
		final String expectedBreadcrumbs = "action 1<span class=\"rule-separator\">, </span>action 2 <span class=\"rule-parameter\">testmultiple</span>";

		// when
		final String breadcrumbs = ruleActionBreadcrumbsBuilder.buildStyledActionBreadcrumbs(actions, actionDefinitions);

		// then
		assertEquals(expectedBreadcrumbs, breadcrumbs);
	}
}
