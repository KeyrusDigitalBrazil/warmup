/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.ProductConfigAbstractRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.ProductConfigRuleActionStrategyCheckerImpl;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleUtilImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.VariantConditionModelImpl;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import org.mockito.Mockito;


public class ConfigurationRulesTestData
{

	public static final String CONFIG_ID = "1";
	public static final String CONFIG_NAME = "Config Name";

	public static final String ROOT_INSTANCE_ID = "1";
	public static final String ROOT_INSTANCE_NAME = "SIMPLE_PRODUCT";
	public static final String ROOT_INSTANCE_LANG_DEP_NAME = "simple product for";

	public static final String AUTHOR_EXTERNAL_DEFAULT = "8";
	public static final String AUTHOR_EXTERNAL_CONSTRAINT = "4";
	public static final String STRING_CSTIC = "CSTIC_STRING";
	public static final String STRING_CSTIC_VALUE = "Simple string";

	public static final String ASSIGNABLE_VALUE_1 = "ASSIGNABLE_VALUE_1";
	public static final String ASSIGNABLE_VALUE_2 = "ASSIGNABLE_VALUE_2";
	public static final String CSTIC_WITH_ASSIGNABLE_VALUES = "CSTIC_WITH_ASSIGNABLE_VALUES";
	public static final String NUMERIC_CSTIC_VALUE = "1500.0";
	public static final String NUMERIC_CSTIC = "CSTIC_NUMERIC";
	private static final String INSTANCE_ID_2 = "2";
	private static final String VAR_COND_KEY_PREFIX = "VARCOND_";
	private static final String INSTANCE_ID_6 = "6";
	private static final String INSTANCE_ID_4 = "4";

	public static ConfigModel createConfigModelWithSubInstances()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();
		final List<InstanceModel> subInstances = createSubInstances();
		rootInstance.setSubInstances(subInstances);
		return configModel;
	}

	public static ConfigModel createConfigModelWithSubInstancesAndVarConds(final double startFactor)
	{
		final ConfigModel configModel = createConfigModelWithSubInstances();
		final InstanceModel subInstance2 = configModel.getRootInstance().getSubInstance(INSTANCE_ID_2);
		createVariantConditions(subInstance2, 3, startFactor);
		final InstanceModel subInstance6 = new InstanceModelImpl();
		subInstance6.setId(INSTANCE_ID_6);
		createVariantConditions(subInstance6, 1, startFactor);
		final List<InstanceModel> subInstances = new ArrayList<>();
		subInstances.add(subInstance6);
		configModel.getRootInstance().getSubInstance(INSTANCE_ID_4).setSubInstances(subInstances);
		return configModel;
	}


	protected static void createVariantConditions(final InstanceModel instance, final int numberOfVarConds,
			final double startFactor)
	{
		final List<VariantConditionModel> variantConditions = new ArrayList<>();
		for (int i = 0; i < numberOfVarConds; i++)
		{
			final VariantConditionModel varCond = new VariantConditionModelImpl();
			varCond.setKey(VAR_COND_KEY_PREFIX + instance.getId() + "_" + i);
			final BigDecimal factor = BigDecimal.valueOf(((i + 1) * 0.2 + startFactor));
			varCond.setFactor(factor);
			variantConditions.add(varCond);
		}
		instance.setVariantConditions(variantConditions);
	}

	public static ConfigModel createConfigModelWith2GroupAndAssignedValues()
	{
		final ConfigModel configModel = createEmptyConfigModel();

		final InstanceModel rootInstance = configModel.getRootInstance();

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		rootInstance.setCstics(cstics);

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		createGroup1WithCsticAndAssignedValues(rootInstance, csticGroups);
		createGroup2WithCsticAndAssignedValues(rootInstance, csticGroups);

		rootInstance.setCsticGroups(csticGroups);
		return configModel;
	}

	protected static List<InstanceModel> createSubInstances()
	{
		final List<InstanceModel> subInstances = new ArrayList<>();
		for (int i = 0; i <= 3; i++)
		{
			final InstanceModel subInstance = new InstanceModelImpl();
			final List<CsticModel> cstics = new ArrayList<CsticModel>();
			if (i == 0 || i == 2)
			{
				cstics.add(createStringCstic(STRING_CSTIC, STRING_CSTIC_VALUE));
			}
			else if (i == 1 || i == 3)
			{
				cstics.add(createNumericCstic(NUMERIC_CSTIC, NUMERIC_CSTIC_VALUE));
			}
			subInstance.setId(String.valueOf(i + 2));
			subInstance.setCstics(cstics);
			subInstance.setVariantConditions(new ArrayList<>());
			subInstances.add(subInstance);

		}
		return subInstances;
	}

	public static ConfigModel createEmptyConfigModel()
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId(CONFIG_ID);
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		final InstanceModel rootInstance;
		rootInstance = createInstance();
		model.setRootInstance(rootInstance);

		return model;
	}

	protected static void createGroup1WithCsticAndAssignedValues(final InstanceModel rootInstance,
			final List<CsticGroupModel> csticGroups)
	{
		rootInstance.addCstic(createRadioButtonCstic("CSTIC_1.1", null, null, true));
		rootInstance.addCstic(
				createRadioButtonCstic("CSTIC_1.2", CsticValueModel.AUTHOR_USER, CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(
				createRadioButtonCstic("CSTIC_1.3", CsticValueModel.AUTHOR_USER, CsticValueModel.AUTHOR_EXTERNAL_USER, false));
		rootInstance.addCstic(
				createCheckBoxListCstic("CSTIC_1.4", CsticValueModel.AUTHOR_USER, CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(createCheckBoxListCstic("CSTIC_1.5", null, null, true));

		final CsticGroupModel group1 = createCsticGroup("GROUP1", "Group 1", "CSTIC_1.1", "CSTIC_1.2", "CSTIC_1.3", "CSTIC_1.4",
				"CSTIC_1.5");
		csticGroups.add(group1);
	}

	protected static void createGroup2WithCsticAndAssignedValues(final InstanceModel rootInstance,
			final List<CsticGroupModel> csticGroups)
	{
		rootInstance.addCstic(
				createCheckBoxListCstic("CSTIC_2.1", CsticValueModel.AUTHOR_USER, CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(createCheckBoxListCstic("CSTIC_2.2", null, null, true));
		rootInstance.addCstic(
				createRadioButtonCstic("CSTIC_2.3", CsticValueModel.AUTHOR_USER, CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(createRadioButtonCstic("CSTIC_2.4", null, null, false));

		final CsticGroupModel group2 = createCsticGroup("GROUP2", "Group 2", "CSTIC_2.1", "CSTIC_2.2", "CSTIC_2.3", "CSTIC_2.4");
		csticGroups.add(group2);
	}

	public static CsticModel createRadioButtonCstic(final String name, final String author, final String authorExternal,
			final boolean visible)
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(name);
		cstic.setLanguageDependentName(name.replace("_", " "));
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(visible);

		final CsticValueModelImpl value1 = createCsticValue("VALUE_1");
		final CsticValueModelImpl value2 = createCsticValue("VALUE_2");
		final CsticValueModelImpl value3 = createCsticValue("VALUE_3");

		if (author != null)
		{
			final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
			value2.setAuthor(author);
			value2.setAuthorExternal(authorExternal);
			assignedValues.add(value2);
			cstic.setAssignedValuesWithoutCheckForChange(assignedValues);
		}
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	private static CsticValueModelImpl createCsticValue(final String value)
	{
		final CsticValueModelImpl valueModel = new CsticValueModelImpl();
		valueModel.setName(value);
		valueModel.setLanguageDependentName(value);
		return valueModel;
	}



	protected static InstanceModel createInstance()
	{
		final InstanceModel rootInstance;
		rootInstance = new InstanceModelImpl();
		rootInstance.setId(ROOT_INSTANCE_ID);
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setLanguageDependentName(ROOT_INSTANCE_LANG_DEP_NAME);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		rootInstance.setSubInstances(new ArrayList<>());
		rootInstance.setCstics(new ArrayList<>());
		rootInstance.setVariantConditions(new ArrayList<>());
		return rootInstance;
	}

	public static CsticModel createCheckBoxListCstic(final String name, final String author, final String authorExternal,
			final boolean visible)
	{

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(name);
		cstic.setLanguageDependentName(name.replace("_", " "));
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(30);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(visible);

		final CsticValueModelImpl value1 = createCsticValueModel(1);
		final CsticValueModelImpl value2 = createCsticValueModel(2);
		final CsticValueModelImpl value3 = createCsticValueModel(3);
		final CsticValueModelImpl value4 = createCsticValueModel(4);

		if (author != null)
		{
			final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
			value2.setAuthor(author);
			value2.setAuthorExternal(authorExternal);
			assignedValues.add(value2);
			value3.setAuthor(author);
			value3.setAuthorExternal(authorExternal);
			assignedValues.add(value3);
			cstic.setAssignedValuesWithoutCheckForChange(assignedValues);
		}

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		assignableValues.add(value4);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}

	private static CsticValueModelImpl createCsticValueModel(final int ii)
	{
		final CsticValueModelImpl value1 = new CsticValueModelImpl();
		value1.setName("VAL" + ii);
		value1.setLanguageDependentName("VALUE " + ii);
		value1.setDomainValue(true);
		return value1;
	}

	private static CsticGroupModel createCsticGroup(final String groupName, final String description, final String... csticNames)
	{
		final List<String> csticNamesInGroup = new ArrayList<>();
		for (final String csticName : csticNames)
		{
			csticNamesInGroup.add(csticName);
		}

		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(groupName);
		csticGroup.setDescription(description);
		csticGroup.setCsticNames(csticNamesInGroup);

		return csticGroup;
	}

	public static ProductConfigRAO createConfigRAOWithCstic(final String csticName)
	{
		final ProductConfigRAO configRAO = new ProductConfigRAO();

		final CsticRAO csticRAO = new CsticRAO();
		csticRAO.setCsticName(csticName);
		configRAO.setCstics(Collections.singletonList(csticRAO));

		return configRAO;
	}

	public static RuleEvaluationResult createEmptyRulesResult()
	{
		final RuleEvaluationResult ruleResult = new RuleEvaluationResult();
		final RuleEngineResultRAO resultRAO = new RuleEngineResultRAO();
		resultRAO.setActions(new LinkedHashSet<AbstractRuleActionRAO>());
		ruleResult.setResult(resultRAO);
		return ruleResult;
	}

	public static ConfigModel createConfigModelWithCstic()
	{
		final ConfigModel model = createEmptyConfigModel();

		final InstanceModel rootInstance = model.getRootInstance();
		// Characteristics and Values

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		cstics.add(createStringCstic(STRING_CSTIC, STRING_CSTIC_VALUE));
		rootInstance.setCstics(cstics);

		return model;
	}

	public static ConfigModel createConfigModelWithNumericCstic()
	{
		final ConfigModel model = createEmptyConfigModel();

		final InstanceModel rootInstance = model.getRootInstance();

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		cstics.add(createNumericCstic(NUMERIC_CSTIC, NUMERIC_CSTIC_VALUE));
		rootInstance.setCstics(cstics);

		return model;
	}

	private static CsticModel createNumericCstic(final String numericCstic, final String numericCsticValue)
	{
		final CsticModel cstic = createStringCstic(numericCstic, numericCsticValue);
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		cstic.setTypeLength(10);
		cstic.setNumberScale(0);
		return cstic;

	}

	private static CsticModel createStringCstic(final String name, final String value)
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(name);
		cstic.setLanguageDependentName(name);
		cstic.setValueType(CsticModel.TYPE_STRING);
		setDefaultProperties(cstic);
		cstic.setVisible(true);
		cstic.setLongText("Model long text");

		final CsticValueModel csticValue = createCsticValue(value);
		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		assignedValues.add(csticValue);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		return cstic;
	}

	private static void setDefaultProperties(final CsticModel cstic)
	{
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		cstic.setAssignableValues(assignableValues);
	}

	public static ConfigModel createConfigModelWithCsticWithAssignableValues()
	{
		final ConfigModel configModel = createEmptyConfigModel();

		final InstanceModel rootInstance = configModel.getRootInstance();

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CSTIC_WITH_ASSIGNABLE_VALUES);

		final List<CsticValueModel> csticValues = new ArrayList<CsticValueModel>();
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName(ASSIGNABLE_VALUE_1);
		csticValues.add(value1);
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName(ASSIGNABLE_VALUE_2);
		csticValues.add(value2);

		cstic.setAssignableValues(csticValues);

		cstics.add(cstic);
		rootInstance.setCstics(cstics);

		return configModel;
	}

	public static void addSubInstanceWithCsticWithAssignableValues(final ConfigModel configModel, final String subInstanceId)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CSTIC_WITH_ASSIGNABLE_VALUES);

		final List<CsticValueModel> csticValues = new ArrayList<CsticValueModel>();
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName(ASSIGNABLE_VALUE_1);
		csticValues.add(value1);
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName(ASSIGNABLE_VALUE_2);
		csticValues.add(value2);
		cstic.setAssignableValues(csticValues);
		cstics.add(cstic);

		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setId(subInstanceId);
		subInstance.setCstics(cstics);

		final List<InstanceModel> subinstances = rootInstance.getSubInstances();
		subinstances.add(subInstance);
		rootInstance.setSubInstances(subinstances);
	}


	public static ConfigModel createConfigModelWithNumericCsticAssignable()
	{
		final ConfigModel configModel = createEmptyConfigModel();

		final InstanceModel rootInstance = configModel.getRootInstance();

		final List<CsticModel> cstics = new ArrayList<CsticModel>();
		final CsticModel cstic = createNumericCstic(NUMERIC_CSTIC, "");

		final List<CsticValueModel> csticValues = new ArrayList<CsticValueModel>();
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName(NUMERIC_CSTIC_VALUE);
		csticValues.add(value1);

		cstic.setAssignableValues(csticValues);

		cstics.add(cstic);
		rootInstance.setCstics(cstics);

		return configModel;
	}

	public static void setCsticAsActionTarget(final AbstractRuleActionRAO action, final String csticName)
	{
		final CsticRAO appliedToObject = new CsticRAO();
		appliedToObject.setCsticName(csticName);
		action.setAppliedToObject(appliedToObject);
	}

	public static CsticValueRAO createCsticValueRAO(final String valueNameToSet)
	{
		final CsticValueRAO valueRao = new CsticValueRAO();
		valueRao.setCsticValueName(valueNameToSet);
		return valueRao;
	}

	public static void initDependenciesOfActionStrategy(final ProductConfigAbstractRuleActionStrategy classUnderTest)
	{
		initDependenciesOfActionStrategy(classUnderTest, null, null, null);

	}

	public static void initDependenciesOfActionStrategy(final ProductConfigAbstractRuleActionStrategy classUnderTest,
			RuleEngineService ruleEngineService, I18NService i18nService, RuleParametersService ruleParamService)
	{
		classUnderTest.setRulesFormator(new ProductConfigRuleFormatTranslatorImpl());
		final ProductConfigRuleActionStrategyCheckerImpl strategyChecker = new ProductConfigRuleActionStrategyCheckerImpl();
		final ConfigModelFactory configModelFactory = new ConfigModelFactoryImpl();
		strategyChecker.setConfigModelFactory(configModelFactory);
		classUnderTest.setRuleActionChecker(strategyChecker);
		classUnderTest.setRuleUtil(new ProductConfigRuleUtilImpl());
		classUnderTest.setConfigModelFactory(configModelFactory);

		if (i18nService == null)
		{
			i18nService = Mockito.mock(I18NService.class);
			given(i18nService.getCurrentLocale()).willReturn(Locale.ENGLISH);
		}
		if (ruleEngineService == null)
		{
			final AbstractRuleEngineRuleModel ruleModel = Mockito.mock(AbstractRuleEngineRuleModel.class);
			ruleEngineService = Mockito.mock(RuleEngineService.class);
			given(ruleModel.getMessageFired(any(Locale.class))).willReturn("");
			given(ruleEngineService.getRuleForCodeAndModule(any(String.class), any(String.class))).willReturn(ruleModel);

		}
		if (ruleParamService == null)
		{
			ruleParamService = Mockito.mock(RuleParametersService.class);
			try
			{
				given(ruleParamService.convertParametersFromString(any(String.class))).willReturn(Collections.emptyList());
			}
			catch (final RuleConverterException e)
			{
				throw new AssertionError("SetUp failed due to an exception", e);
			}
		}
		classUnderTest.setRuleParametersService(ruleParamService);
		classUnderTest.setRuleEngineService(ruleEngineService);
		classUnderTest.setI18NService(i18nService);

	}
}
