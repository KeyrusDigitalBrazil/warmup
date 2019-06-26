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
package de.hybris.platform.droolsruleengineservices.compiler.impl;

import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.ruleengine.constants.RuleEngineConstants.DEFAULT_DROOLS_DATE_FORMAT;
import static de.hybris.platform.ruleengine.constants.RuleEngineConstants.DROOLS_DATE_FORMAT_KEY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleActionsGenerator;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleConditionsGenerator;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleGeneratorContext;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleMetadataGenerator;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.strategies.DroolsKIEBaseFinderStrategy;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAction;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariablesContainer;
import de.hybris.platform.ruleengineservices.model.RuleGroupModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDroolsRuleTargetCodeGeneratorTest
{
	public static final String RULE_UUID = "7be85ae9-8f69-4d51-a3b4-a4b08b457798";
	public static final String RULE_CODE = "rule_code";
	public static final String RULE_NAME = "rule_name";
	public static final String RULE_GROUP = "rule_group";
	public static final String MODULE_NAME = "MODULE_NAME";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private CatalogUnawareMediaModel mediaModel;

	@Mock
	private SourceRuleModel sourceRule;
	@Mock
	private DroolsRuleModel droolsRule;
	@Mock
	private RuleGroupModel ruleGroup;
	@Mock
	private RuleCompilerContext compilerContext;
	@Mock
	private RuleIrAction action;
	@Mock
	private RuleIrCondition condition;
	@Mock
	private ModelService modelService;
	@Mock
	private RuleEngineService platformRuleEngineService;
	@Mock
	private DroolsRuleConditionsGenerator droolsRuleConditionsGenerator;
	@Mock
	private DroolsRuleActionsGenerator droolsRuleActionsGenerator;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private RuleParametersService ruleParametersService;
	@Mock
	private DroolsRuleMetadataGenerator droolsRuleMetadataGenerator;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private Configuration configuration;
	@Mock
	private DroolsKIEModuleModel rulesModule;
	@Mock
	private DroolsKIEBaseModel kieBase;
	@Mock
	private RulesModuleDao rulesModuleDao;

	@InjectMocks
	private DefaultDroolsRuleTargetCodeGenerator droolsRuleTargetCodeGenerator;
	@Mock
	private DroolsKIEBaseFinderStrategy droolsKIEBaseFinderStrategy;

	@Before
	public void setUp()
	{
		given(droolsRule.getUuid()).willReturn(RULE_UUID);
		given(droolsRule.getVersion()).willReturn(0L);

		final RuleEngineActionResult result = new RuleEngineActionResult();
		result.setActionFailed(false);

		given(modelService.create(DroolsRuleModel.class)).willReturn(droolsRule);
		given(modelService.create(CatalogUnawareMediaModel.class)).willReturn(mediaModel);

		given(commonI18NService.getAllLanguages()).willReturn(newArrayList());

		given(sourceRule.getUuid()).willReturn(RULE_UUID);
		given(sourceRule.getCode()).willReturn(RULE_CODE);
		given(sourceRule.getName()).willReturn(RULE_NAME);
		given(sourceRule.getStartDate()).willReturn(new Date());
		given(sourceRule.getEndDate()).willReturn(new Date());
		given(sourceRule.getRuleGroup()).willReturn(ruleGroup);

		given(ruleGroup.getCode()).willReturn(RULE_GROUP);
		given(rulesModule.getName()).willReturn(MODULE_NAME);

		given(compilerContext.getRule()).willReturn(sourceRule);
		given(compilerContext.getModuleName()).willReturn(MODULE_NAME);
		given(platformRuleEngineService.getRuleForCodeAndModule(RULE_CODE, MODULE_NAME)).willReturn(droolsRule);
		given(platformRuleEngineService.updateEngineRule(droolsRule, rulesModule)).willReturn(result);

		given(configurationService.getConfiguration()).willReturn(configuration);

		given(configuration.getString(DROOLS_DATE_FORMAT_KEY, DEFAULT_DROOLS_DATE_FORMAT)).willReturn("dd-MM-yyyy");
		given(droolsKIEBaseFinderStrategy.getKIEBaseForKIEModule(rulesModule)).willReturn(kieBase);

		given(rulesModuleDao.findByName(MODULE_NAME)).willReturn(rulesModule);

	}

	@Test
	public void nullTest()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, null);
	}

	@Test
	public void emptyActionsTest() throws Exception
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(Collections.singletonList(condition));
		ruleIr.setActions(emptyList());

		// expect
		expectedException.expect(UnknownIdentifierException.class);

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);
	}

	@Test
	public void validRuleIrTest()
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(Collections.singletonList(condition));
		ruleIr.setActions(Collections.singletonList(action));
		given(droolsRuleConditionsGenerator
					 .generateConditions(new DefaultDroolsGeneratorContext(compilerContext, ruleIr, droolsRule),
								  StringUtils.EMPTY)).willReturn("");

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		//then
		verify(droolsRule, times(1)).setKieBase(kieBase);
		//then
		verify(modelService, times(1)).save(droolsRule);
	}

	@Test
	public void validCreateNewRule()
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(Collections.singletonList(condition));
		ruleIr.setActions(Collections.singletonList(action));

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		//then
		verify(droolsRule).setKieBase(kieBase);
	}

	@Test
	public void testGetFormattedDateString()
	{
		when(configuration.getString(DROOLS_DATE_FORMAT_KEY, DEFAULT_DROOLS_DATE_FORMAT)).thenReturn("dd-MMM-yyyy HH:mm:ss");

		final Calendar calendar = getCalendarForLocale(Locale.getDefault());

		String formattedDateString = droolsRuleTargetCodeGenerator.getFormattedDateString(calendar.getTime());
		assertThat(formattedDateString).isEqualTo("01-Jan-2016 00:00:00");

		Locale.setDefault(new Locale("pl", "PL"));
		formattedDateString = droolsRuleTargetCodeGenerator.getFormattedDateString(calendar.getTime());
		assertThat(formattedDateString).isEqualTo("01-Jan-2016 00:00:00");
	}

	private Calendar getCalendarForLocale(final Locale locale)
	{
		final Calendar calendar = Calendar.getInstance(locale);
		calendar.set(Calendar.YEAR, 2016);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}

	@Test
	public void testCreateNewRuleEstablishReferenceToSourceRule()
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(singletonList(condition));
		ruleIr.setActions(singletonList(action));

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		verify(droolsRule).setSourceRule(sourceRule);
	}

	@Test
	public void testCreateNewRuleAssignRuleGroup()
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(singletonList(condition));
		ruleIr.setActions(singletonList(action));

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		verify(droolsRule).setRuleGroupCode(RULE_GROUP);
	}

	@Test
	public void testGenerateRequiredFactsCheckIsCalled()
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(singletonList(condition));
		ruleIr.setActions(singletonList(action));

		droolsRuleTargetCodeGenerator = spy(droolsRuleTargetCodeGenerator);

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		verify(droolsRuleTargetCodeGenerator).generateRequiredFactsCheck(any(DroolsRuleGeneratorContext.class),anyString());
	}

	@Test
	public void testSettingRuleGroupToNullShouldSetItToNullInDroolsRule()
	{
		// given
		final RuleIr ruleIr = new RuleIr();
		ruleIr.setVariablesContainer(new RuleIrVariablesContainer());
		ruleIr.setConditions(singletonList(condition));
		ruleIr.setActions(singletonList(action));

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		verify(droolsRule).setRuleGroupCode(RULE_GROUP);

		// now group is set to null
		when(sourceRule.getRuleGroup()).thenReturn(null);

		// when
		droolsRuleTargetCodeGenerator.generate(compilerContext, ruleIr);

		// droolsRule group should be set to null as well
		verify(droolsRule).setRuleGroupCode(null);

	}

}
