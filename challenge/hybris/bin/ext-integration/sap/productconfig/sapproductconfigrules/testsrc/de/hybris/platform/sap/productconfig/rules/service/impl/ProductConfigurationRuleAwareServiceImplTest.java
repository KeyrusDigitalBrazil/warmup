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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.impl.DefaultRuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.RemoveAssignableValueRuleActionStrategyImpl;
import de.hybris.platform.sap.productconfig.rules.action.strategy.impl.SetCsticValueRuleActionStrategyImpl;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.SetCsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ProductConfigurationRuleAwareServiceImplTest
{

	private static final String CSTIC_VALUE = "VALUE_1";
	private static final String CSTIC_VALUE_1 = "VAL1";
	private static final String CSTIC_VALUE_2 = "VAL2";
	private static final String CSTIC_WITHOUT_VALUE = "CSTIC_1.1";
	private static final String CSTIC_WITH_VALUE = "CSTIC_1.2";
	private static final String CSTIC_WITH_MULTI_VALUE = "CSTIC_1.4";
	private static final String CONFIG_ID = "123";

	private ProductConfigurationRuleAwareServiceImpl classUnderTest;
	private ConfigModelFactory configModelFactory;
	@Mock
	private FactContext factContext;
	@Mock
	private RAOProvider provider;

	@Mock
	private DefaultRuleEngineContextDao ruleEngineContextDao;

	@Mock
	private TrackingRecorder mockedRecorder;

	@Mock
	private RuleEngineContextFinderStrategy ruleEngineContextFinderStrategy;

	@Mock
	private ConfigurationModelCacheStrategy configModelCacheStrategy;

	@Mock
	private ConfigurationProductUtil mockConfigProductUtil;

	@Mock
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;

	@Mock
	private ConfigurationLifecycleStrategy configLifecycleStrategy;

	@Mock
	private ProductConfigRulesResultUtil rulesResultUtil;

	private ConfigModel config;
	private RuleEvaluationResult rulesResult;
	private LinkedHashSet<AbstractRuleActionRAO> actions;

	private AbstractRuleEngineContextModel engineContext1;
	private AbstractRuleEngineContextModel engineContext2;
	private ProductModel product;

	@Before
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationRuleAwareServiceImpl();
		configModelFactory = new ConfigModelFactoryImpl();

		classUnderTest.setRuleEngineContextDao(ruleEngineContextDao);
		classUnderTest.setDefaultRuleEngineContextName("productconfig-context");

		classUnderTest.setRuleEngineContextFinderStrategy(ruleEngineContextFinderStrategy);
		classUnderTest.setConfigurationProductUtil(mockConfigProductUtil);

		classUnderTest.setConfigModelCacheStrategy(configModelCacheStrategy);
		classUnderTest.setConfigLifecycleStrategy(configLifecycleStrategy);
		classUnderTest.setRulesResultUtil(rulesResultUtil);

		final Map<String, ProductConfigRuleActionStrategy> actionStrategiesMapping = new HashMap<String, ProductConfigRuleActionStrategy>();
		final SetCsticValueRuleActionStrategyImpl strategy = new SetCsticValueRuleActionStrategyImpl();
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(strategy);
		actionStrategiesMapping.put("sapProductConfigDefaultSetCsticValueRAOAction", strategy);
		final RemoveAssignableValueRuleActionStrategyImpl strategy2 = new RemoveAssignableValueRuleActionStrategyImpl();
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(strategy2);
		actionStrategiesMapping.put("sapProductConfigDefaultRemoveAssignableValueRAOAction", strategy2);

		classUnderTest.setActionStrategiesMapping(actionStrategiesMapping);

		final List<ConfigModelImpl> factList = new ArrayList<>();
		factList.add(new ConfigModelImpl());
		doReturn(factList).when(factContext).getFacts();

		final Set<RAOProvider> providers = new HashSet<>();
		providers.add(provider);
		doReturn(providers).when(factContext).getProviders(Mockito.any(ConfigModelImpl.class));

		final ProductConfigRAO productConfigRao = new ProductConfigRAO();
		when(provider.expandFactModel(Mockito.any(Object.class))).thenReturn(Collections.singleton(productConfigRao));

		engineContext1 = new DroolsRuleEngineContextModel();
		when(ruleEngineContextDao.findRuleEngineContextByName("productconfig-context")).thenReturn(engineContext1);

		product = new ProductModel();
		when(mockConfigProductUtil.getProductForCurrentCatalog(anyString())).thenReturn(product);
		engineContext2 = new DroolsRuleEngineContextModel();
		final Optional<AbstractRuleEngineContextModel> optionalOfEngineContext2 = Optional.of(engineContext2);
		when(ruleEngineContextFinderStrategy.findRuleEngineContext(product, RuleType.PRODUCTCONFIG))
				.thenReturn(optionalOfEngineContext2);

		rulesResult = ConfigurationRulesTestData.createEmptyRulesResult();
		actions = rulesResult.getResult().getActions();

		config = new ConfigModelImpl();
		config.setId(CONFIG_ID);

		//classUnderTest.setSessionAccessService(mockedSessionAccessService);
		classUnderTest.setRecorder(mockedRecorder);

		classUnderTest.setAssignmentResolverStrategy(assignmentResolverStrategy);
	}

	@Test
	public void testProvideRAOsWithNullFactContext()
	{
		factContext = null;
		final Set<Object> raos = classUnderTest.provideRAOs(factContext);
		assertTrue("Set of 'raos' not empty: ", raos.isEmpty());
	}

	@Test
	public void testProvideRAOsWithNotNullFactContext()
	{
		final Set<Object> raos = classUnderTest.provideRAOs(factContext);
		assertTrue("Set of 'raos' empty:", !raos.isEmpty());
	}

	@Test
	public void testPrepareRuleEvaluationContext()
	{
		final RuleEvaluationContext ruleEvaluationContext = classUnderTest.prepareRuleEvaluationContext(factContext,
				engineContext1);

		assertTrue("Rule engine context is missing", ruleEvaluationContext.getRuleEngineContext() != null);
		assertTrue("Facts (RAOs)are missing", ruleEvaluationContext.getFacts() != null);
		assertTrue("Wrong number of facts (RAOs)", ruleEvaluationContext.getFacts().size() == 1);
	}

	@Test
	public void testAdjustConfigurationRuleBasedFalseWhenRulesAreDeactivated()
	{
		final ConfigModel currentConfigModel = new ConfigModelImpl();
		when(ruleEngineContextDao.findRuleEngineContextByName("productconfig-context")).thenReturn(null);
		final boolean adjusted = classUnderTest.adjustConfigurationRuleBased(currentConfigModel, null, null);
		assertFalse(adjusted);
	}

	@Test
	public void testDetermineRuleEngineContextNotAvailable()
	{
		final ConfigModel currentConfigModel = new ConfigModelImpl();
		when(ruleEngineContextDao.findRuleEngineContextByName("productconfig-context")).thenReturn(null);
		final AbstractRuleEngineContextModel engineContext = classUnderTest.determineRuleEngineContext(currentConfigModel);
		assertNull("Wrong engine context retrieved", engineContext);
	}

	@Test
	public void testDetermineRuleEngineContextDefaultEngineContextNameAvailable()
	{

		final ConfigModel currentConfigModel = new ConfigModelImpl();
		final AbstractRuleEngineContextModel engineContext = classUnderTest.determineRuleEngineContext(currentConfigModel);
		assertSame("Wrong engine context retrieved", engineContext1, engineContext);
	}

	@Test
	public void testDetermineRuleEngineContextDefaultEngineContextCanBeDetermined()
	{
		classUnderTest.setDefaultRuleEngineContextName("");
		final ConfigModel currentConfigModel = createConfigModel();
		final AbstractRuleEngineContextModel engineContext = classUnderTest.determineRuleEngineContext(currentConfigModel);
		assertSame("Wrong engine context retrieved", engineContext2, engineContext);
	}

	@Test
	public void testDetermineRuleEngineContextDefaultEngineContextCanBeDeterminedReturnsNull()
	{
		classUnderTest.setDefaultRuleEngineContextName("");
		final ConfigModel currentConfigModel = createConfigModel();
		when(ruleEngineContextFinderStrategy.findRuleEngineContext(any(ProductModel.class), eq(RuleType.PRODUCTCONFIG)))
				.thenReturn(Optional.ofNullable(null));
		final AbstractRuleEngineContextModel engineContext = classUnderTest.determineRuleEngineContext(currentConfigModel);
		assertNull("Wrong engine context retrieved", engineContext);
	}

	protected ConfigModel createConfigModel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setName("PRODUCT_CODE");
		configModel.setRootInstance(rootInstance);
		return configModel;
	}

	@Test
	public void testApplyRulesEmptyResult()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createEmptyConfigModel();
		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);
		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesSetDefaultValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		assertTrue(!CSTIC_VALUE.equals(cstic.getSingleValue()));
		createSetCsticValueAction(CSTIC_WITHOUT_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		assertTrue(CSTIC_VALUE.equals(cstic.getSingleValue()));
		assertTrue(isChanged);
	}

	@Test
	public void testApplyRulesChangeDefaultValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(!CSTIC_VALUE.equals(cstic.getSingleValue()));
		createSetCsticValueAction(CSTIC_WITH_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(CSTIC_VALUE.equals(cstic.getSingleValue()));
		assertTrue(isChanged);
	}


	@Test
	public void testApplyRulesTryToSetNonAssignableValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		final String oldValue = cstic.getSingleValue();

		createSetCsticValueAction(CSTIC_WITH_VALUE, "valueNotExisting");

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(oldValue.equals(cstic.getSingleValue()));
		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesTryToSetValueForReadOnlyCstic()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		cstic.setReadonly(true);
		final String oldValue = cstic.getSingleValue();

		createSetCsticValueAction(CSTIC_WITH_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_VALUE);
		assertTrue(oldValue.equals(cstic.getSingleValue()));
		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesTryToSetValueUnconstrainedCstic()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		cstic.setConstrained(false);
		cstic.setAssignableValues(Collections.EMPTY_LIST);
		assertFalse(CSTIC_VALUE.equals(cstic.getSingleValue()));

		createSetCsticValueAction(CSTIC_WITHOUT_VALUE, CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITHOUT_VALUE);
		assertTrue(CSTIC_VALUE.equals(cstic.getSingleValue()));
		assertTrue(isChanged);
	}

	@Test
	public void testApplyRulesTryToSetNonExistingCstic()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		createSetCsticValueAction("doesNotExist", CSTIC_VALUE);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		assertFalse(isChanged);
	}

	@Test
	public void testApplyRulesAddMultiValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);

		boolean valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_1);
		assertFalse(valueAssigned);
		final int beforeListSize = cstic.getAssignedValues().size();
		createSetCsticValueAction(CSTIC_WITH_MULTI_VALUE, CSTIC_VALUE_1);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);
		assertEquals(beforeListSize + 1, cstic.getAssignedValues().size());
		valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_1);
		assertTrue(CSTIC_VALUE_1 + " value not assigned to cstic " + CSTIC_WITH_MULTI_VALUE, valueAssigned);
		assertTrue(isChanged);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyRulesOtherActionRAO()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		final AbstractRuleActionRAO otherAction = new AbstractRuleActionRAO();
		actions.add(otherAction);

		/* final boolean isChanged = */classUnderTest.applyRulesResult(rulesResult, configModel);
	}

	private boolean isValueAssigned(final CsticModel cstic, final String valueToCheck)
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName(valueToCheck);
		final boolean valueAssigned = cstic.getAssignedValues().contains(value);
		return valueAssigned;
	}

	@Test
	public void testApplyRulesAddExistingMultiValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWith2GroupAndAssignedValues();
		CsticModel cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);

		boolean valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_2);
		assertTrue(CSTIC_VALUE_2 + " value not assigned to cstic " + CSTIC_WITH_MULTI_VALUE, valueAssigned);
		final int beforeListSize = cstic.getAssignedValues().size();
		createSetCsticValueAction(CSTIC_WITH_MULTI_VALUE, CSTIC_VALUE_2);

		classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(CSTIC_WITH_MULTI_VALUE);
		assertEquals(beforeListSize, cstic.getAssignedValues().size());
		valueAssigned = isValueAssigned(cstic, CSTIC_VALUE_2);
		assertTrue(CSTIC_VALUE_2 + " value not assigned to cstic " + CSTIC_WITH_MULTI_VALUE, valueAssigned);

	}

	private void createSetCsticValueAction(final String csticName, final String valueName)
	{
		final SetCsticValueRAO csticSetAction = new SetCsticValueRAO();
		csticSetAction.setActionStrategyKey("sapProductConfigDefaultSetCsticValueRAOAction");
		final CsticValueRAO valueRao = new CsticValueRAO();
		valueRao.setCsticValueName(valueName);
		csticSetAction.setValueNameToSet(valueRao);
		final CsticRAO csticRao = new CsticRAO();
		csticRao.setCsticName(csticName);
		csticSetAction.setAppliedToObject(csticRao);
		actions.add(csticSetAction);
	}

	private void createRemoveAssignableValueAction(final String csticName, final String valueName)
	{
		final RemoveAssignableValueRAO csticRemoveAssignableValueAction = new RemoveAssignableValueRAO();
		csticRemoveAssignableValueAction.setActionStrategyKey("sapProductConfigDefaultRemoveAssignableValueRAOAction");
		final CsticValueRAO valueRao = new CsticValueRAO();
		valueRao.setCsticValueName(valueName);
		csticRemoveAssignableValueAction.setValueNameToRemoveFromAssignable(valueRao);
		final CsticRAO csticRao = new CsticRAO();
		csticRao.setCsticName(csticName);
		csticRemoveAssignableValueAction.setAppliedToObject(csticRao);
		actions.add(csticRemoveAssignableValueAction);
	}

	@Test
	public void testApplyRulesRemoveAssignableValue()
	{
		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();

		CsticModel cstic = configModel.getRootInstance().getCstic(ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES);
		assertEquals(2, cstic.getAssignableValues().size());

		createRemoveAssignableValueAction(ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_2);

		final boolean isChanged = classUnderTest.applyRulesResult(rulesResult, configModel);

		cstic = configModel.getRootInstance().getCstic(ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES);

		assertFalse(isChanged);
		assertEquals(1, cstic.getAssignableValues().size());
	}

	@Test
	public void testGetRuleActionStrategy()
	{
		final ProductConfigRuleActionStrategy strategy = classUnderTest
				.getRuleActionStrategy("sapProductConfigDefaultRemoveAssignableValueRAOAction");
		assertNotNull(strategy);
	}



	@Test(expected = IllegalStateException.class)
	public void testGetRuleActionStrategyEmptyMap()
	{
		classUnderTest.setActionStrategiesMapping(new HashMap<String, ProductConfigRuleActionStrategy>());
		classUnderTest.getRuleActionStrategy("sapProductConfigDefaultRemoveAssignableValueRAOAction");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRuleActionStrategyWrongStrategy()
	{
		classUnderTest.getRuleActionStrategy("wrongStrategy");
	}

	@Test
	public void testRemoveMessageBeforeNextStep_true()
	{
		final ProductConfigMessage msg = createMessage(ProductConfigMessageSourceSubType.DISPLAY_MESSAGE, "123");
		final boolean removeMsg = classUnderTest.removeMessageBeforeNextStep(ProcessStep.RETRIEVE_CONFIGURATION, msg);
		assertTrue("message should be removed", removeMsg);
	}

	@Test
	public void testRemoveMessageBeforeNextStep_wrongSubType()
	{
		final ProductConfigMessage msg = createMessage(ProductConfigMessageSourceSubType.DEFAULT, "123");
		final boolean removeMsg = classUnderTest.removeMessageBeforeNextStep(ProcessStep.RETRIEVE_CONFIGURATION, msg);
		assertFalse("message should NOT be removed", removeMsg);
	}

	@Test
	public void testRemoveMessageBeforeNextStep_wrongStep()
	{
		final ProductConfigMessage msg = createMessage(ProductConfigMessageSourceSubType.DISPLAY_MESSAGE, "123");
		final boolean removeMsg = classUnderTest.removeMessageBeforeNextStep(ProcessStep.CREATE_DEFAULT_CONFIGURATION, msg);
		assertFalse("message should NOT be removed", removeMsg);
	}

	protected ProductConfigMessage createMessage(final ProductConfigMessageSourceSubType mesgSubType, final String key)
	{
		return configModelFactory.createInstanceOfProductConfigMessage("text", key, ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE, mesgSubType);
	}

	@Test
	public void testRemoveMessagesRecomputedOnNextStep()
	{
		final Set<ProductConfigMessage> messages = new HashSet<>();
		messages.add(createMessage(ProductConfigMessageSourceSubType.DISPLAY_MESSAGE, "1"));
		messages.add(createMessage(ProductConfigMessageSourceSubType.DEFAULT, "2"));
		messages.add(createMessage(ProductConfigMessageSourceSubType.DISPLAY_MESSAGE, "3"));

		classUnderTest.removeMessagesRecomputedOnNextStep(messages, ProcessStep.RETRIEVE_CONFIGURATION);
		assertEquals(1, messages.size());
		assertEquals("2", messages.iterator().next().getKey());
	}


	@Test
	public void testAfterConfigCreated_cacheOnce()
	{
		classUnderTest = spy(classUnderTest);
		willReturn(false).given(classUnderTest).adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION, null);
		classUnderTest.afterConfigCreated(config, null);
		verify(configModelCacheStrategy).setConfigurationModelEngineState(CONFIG_ID, config);
	}

	@Test
	public void testAfterConfigCreated_cacheTwice()
	{
		classUnderTest = spy(classUnderTest);
		willReturn(true).given(classUnderTest).adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION, null);
		Mockito.doNothing().when(classUnderTest).adjustVariantConditionsUsingOptions(Mockito.any());
		classUnderTest.afterConfigCreated(config, null);
		verify(configModelCacheStrategy, times(2)).setConfigurationModelEngineState(CONFIG_ID, config);
	}

	@Test
	public void testAfterDefaultConfigCreated_cacheTwice()
	{
		classUnderTest = spy(classUnderTest);
		willReturn(false).given(classUnderTest).adjustConfigurationRuleBased(config, ProcessStep.CREATE_DEFAULT_CONFIGURATION,
				null);
		willReturn(false).given(classUnderTest).adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION, null);
		classUnderTest.afterDefaultConfigCreated(config);
		verify(configModelCacheStrategy, times(2)).setConfigurationModelEngineState(CONFIG_ID, config);
	}

	@Test
	public void testAfterDefaultConfigCreated_cacheOnceOnMethodLevel()
	{
		classUnderTest = spy(classUnderTest);
		willReturn(true).given(classUnderTest).adjustConfigurationRuleBased(config, ProcessStep.CREATE_DEFAULT_CONFIGURATION, null);
		willDoNothing().given(classUnderTest).updateConfiguration(config);
		final ConfigModel newConfig = new ConfigModelImpl();
		newConfig.setId("456");
		willReturn(newConfig).given(classUnderTest).retrieveConfigurationModelAfterUpdate(CONFIG_ID, null);
		config.setKbKey(new KBKeyImpl(null));
		classUnderTest.afterDefaultConfigCreated(config);
		verify(configModelCacheStrategy).setConfigurationModelEngineState(CONFIG_ID, config);
	}

	@Test
	public void testTransferVariantConditions()
	{
		final ConfigModel config = ConfigurationRulesTestData.createConfigModelWithSubInstancesAndVarConds(0.25);
		final ConfigModel newConfig = ConfigurationRulesTestData.createConfigModelWithSubInstancesAndVarConds(0.3);
		classUnderTest.transferVariantConditions(newConfig.getRootInstance(), config.getRootInstance());
		assertEquals(0, config.getRootInstance().getSubInstance("2").getVariantConditions().get(2).getFactor()
				.compareTo(newConfig.getRootInstance().getSubInstance("2").getVariantConditions().get(2).getFactor()));
		assertEquals(0,
				config.getRootInstance().getSubInstance("4").getSubInstance("6").getVariantConditions().get(0).getFactor().compareTo(
						newConfig.getRootInstance().getSubInstance("4").getSubInstance("6").getVariantConditions().get(0).getFactor()));
	}

	@Test
	public void testProvideConfigurationModelNoRuleEngine() throws ConfigurationEngineException
	{
		when(configLifecycleStrategy.retrieveConfigurationModel(Mockito.eq(CONFIG_ID),
				Mockito.notNull(ConfigurationRetrievalOptions.class))).thenReturn(config);
		when(assignmentResolverStrategy.retrieveRelatedObjectType(Mockito.anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.ORDER_ENTRY);
		when(assignmentResolverStrategy.retrieveCreationDateForRelatedEntry(CONFIG_ID)).thenReturn(new Date());
		final ConfigModel result = classUnderTest.provideConfigurationModel(CONFIG_ID, false, null);
		assertNotNull(result);
		verify(configLifecycleStrategy).retrieveConfigurationModel(Mockito.eq(CONFIG_ID),
				Mockito.notNull(ConfigurationRetrievalOptions.class));
	}

	@Test
	public void testAdjustVariantConditionsUsingOptions() throws ConfigurationEngineException
	{
		when(configLifecycleStrategy.retrieveConfigurationModel(Mockito.eq(CONFIG_ID),
				Mockito.notNull(ConfigurationRetrievalOptions.class)))
						.thenReturn(ConfigurationRulesTestData.createConfigModelWithSubInstancesAndVarConds(0.3));
		when(assignmentResolverStrategy.retrieveRelatedObjectType(Mockito.anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.ORDER_ENTRY);
		when(assignmentResolverStrategy.retrieveCreationDateForRelatedEntry(CONFIG_ID)).thenReturn(new Date());

		final List<ProductConfigurationDiscount> discountList = new ArrayList<>();
		discountList.add(new ProductConfigurationDiscount());
		when(rulesResultUtil.retrieveRulesBasedVariantConditionModifications(Mockito.eq(CONFIG_ID))).thenReturn(discountList);

		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithSubInstancesAndVarConds(0.25);
		configModel.setId(CONFIG_ID);
		classUnderTest.adjustVariantConditionsUsingOptions(configModel);
		verify(configLifecycleStrategy).retrieveConfigurationModel(Mockito.eq(CONFIG_ID),
				Mockito.notNull(ConfigurationRetrievalOptions.class));
	}

	@Test
	public void testAdjustVariantConditionsUsingOptionsCurrentPriceDate() throws ConfigurationEngineException
	{
		final ConfigModel configWithVariantCond = ConfigurationRulesTestData.createConfigModelWithSubInstancesAndVarConds(0.3);
		when(configLifecycleStrategy.retrieveConfigurationModel(Mockito.eq(CONFIG_ID),
				Mockito.isNotNull(ConfigurationRetrievalOptions.class))).thenReturn(configWithVariantCond);
		when(assignmentResolverStrategy.retrieveRelatedObjectType(Mockito.anyString()))
				.thenReturn(ProductConfigurationRelatedObjectType.CART_ENTRY);

		final List<ProductConfigurationDiscount> discountList = new ArrayList<>();
		discountList.add(new ProductConfigurationDiscount());
		when(rulesResultUtil.retrieveRulesBasedVariantConditionModifications(Mockito.eq(CONFIG_ID))).thenReturn(discountList);

		final ConfigModel configModel = ConfigurationRulesTestData.createConfigModelWithSubInstancesAndVarConds(0.25);
		configModel.setId(CONFIG_ID);
		classUnderTest.adjustVariantConditionsUsingOptions(configModel);
		verify(configLifecycleStrategy).retrieveConfigurationModel(Mockito.eq(CONFIG_ID),
				Mockito.notNull(ConfigurationRetrievalOptions.class));
	}
}
