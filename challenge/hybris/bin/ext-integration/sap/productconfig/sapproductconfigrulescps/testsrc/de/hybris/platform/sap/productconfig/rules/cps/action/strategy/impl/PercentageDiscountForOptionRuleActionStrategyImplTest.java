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
package de.hybris.platform.sap.productconfig.rules.cps.action.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;
import de.hybris.platform.sap.productconfig.rules.cps.handler.impl.CharacteristicValueRulesResultHandlerImpl;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.PercentageDiscountForOptionWithMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleUtilImpl;
import de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl.ProductConfigMessageRuleParameterValueMapper;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PercentageDiscountForOptionRuleActionStrategyImplTest
{
	private static final String CSTIC_VALUE1 = "csticValue1";
	private static final String CSTIC1 = "cstic1";
	private static final String MESSAGE = "{\"de\":\"german text\",\"en\":\"english text\"}";
	private static final String LOCALIZED_MESSAGE = "english text";
	private static final String EXTENDED_MESSAGE = "{\"de\":\"extended german text\",\"en\":\"extended english text\"}";
	private static final String LOCALIZED_EXTENDED_MESSAGE = "extended english text";
	private static final String CONFIG_ID = "c123";

	private PercentageDiscountForOptionRuleActionStrategyImpl classUnderTest;

	private PercentageDiscountForOptionWithMessageRAO action;
	private ConfigModel model;
	private Map<String, CsticModel> csticMap;
	private CsticModel cstic;
	private ProductConfigRuleUtil ruleUtil;
	private CharacteristicValueRulesResultModel ruleResult;
	private ProductConfigMessage productConfigMessage;


	@Mock
	private CharacteristicValueRulesResultHandlerImpl rulesResultHandler;
	private DiscountMessageRulesResultModel resultMessage;
	private String valueName;
	private String valueName2;



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PercentageDiscountForOptionRuleActionStrategyImpl();
		classUnderTest.setRulesResultHandler(rulesResultHandler);
		classUnderTest.setMessageValueMapper(new ProductConfigMessageRuleParameterValueMapper());
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);

		// configId = "1"
		model = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();
		ruleUtil = new ProductConfigRuleUtilImpl();
		csticMap = ruleUtil.getCsticMap(model);

		// csticName = "CSTIC_WITH_ASSIGNABLE_VALUES"
		cstic = model.getRootInstance().getCstics().get(0);
		cstic.setVisible(true);
		action = new PercentageDiscountForOptionWithMessageRAO();
		ConfigurationRulesTestData.setCsticAsActionTarget(action, cstic.getName());
		action.setFiredRuleCode("rule123");
		action.setModuleName(SapproductconfigrulesConstants.RULES_MODULE_NAME);

		valueName = cstic.getAssignableValues().get(0).getName();
		valueName2 = cstic.getAssignableValues().get(1).getName();
		final CsticValueRAO valueRAO = new CsticValueRAO();
		valueRAO.setCsticValueName(valueName);
		action.setValueName(valueRAO);
		action.setDiscountValue(BigDecimal.TEN);

		action.setMessage(MESSAGE);
		action.setExtendedMessage(EXTENDED_MESSAGE);

		ruleResult = new CharacteristicValueRulesResultModel();
		resultMessage = new DiscountMessageRulesResultModel();
		given(rulesResultHandler.createInstance()).willReturn(ruleResult);
		given(rulesResultHandler.createMessageInstance()).willReturn(resultMessage);

		productConfigMessage = new ProductConfigMessageBuilder()
				.appendBasicFields(LOCALIZED_MESSAGE, "123", ProductConfigMessageSeverity.INFO)
				.appendPromotionFields(ProductConfigMessagePromoType.PROMO_APPLIED, EXTENDED_MESSAGE, new Date()).build();
	}

	@Test
	public void testExecuteActionPercentageDiscountForOption()
	{
		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		Mockito.verify(rulesResultHandler).mergeDiscountAndPersistResults(ruleResult, "1");
		assertTrue(configChanged);

		assertTrue(cstic.getMessages().isEmpty());

		final CsticValueModel csticValueModel = cstic.getAssignableValues().get(0);
		assertEquals(1, csticValueModel.getMessages().size());

		final ProductConfigMessage firstMessage = csticValueModel.getMessages().iterator().next();
		assertNotNull(firstMessage);
		assertEquals(LOCALIZED_MESSAGE, firstMessage.getMessage());
		assertEquals(LOCALIZED_EXTENDED_MESSAGE, firstMessage.getExtendedMessage());
	}

	@Test
	public void testExecuteActionPercentageDiscountForAllOptions()
	{
		action.getValueName().setCsticValueName("");
		final CharacteristicValueRulesResultModel rulesResult1 = new CharacteristicValueRulesResultModel();
		final CharacteristicValueRulesResultModel rulesResult2 = new CharacteristicValueRulesResultModel();
		given(rulesResultHandler.createInstance()).willReturn(rulesResult1, rulesResult2);

		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		Mockito.verify(rulesResultHandler).mergeDiscountAndPersistResults(rulesResult1, "1");
		Mockito.verify(rulesResultHandler).mergeDiscountAndPersistResults(rulesResult2, "1");
		assertTrue(configChanged);

		assertFalse(cstic.getMessages().isEmpty());
		final ProductConfigMessage firstMessage = cstic.getMessages().iterator().next();
		assertNotNull(firstMessage);
		assertEquals(LOCALIZED_MESSAGE, firstMessage.getMessage());
		assertEquals(LOCALIZED_EXTENDED_MESSAGE, firstMessage.getExtendedMessage());

		for (final CsticValueModel csticValueModel : cstic.getAssignableValues())
		{
			assertTrue(csticValueModel.getMessages().isEmpty());
		}
	}

	@Test
	public void testIsActionPossible()
	{
		final boolean actionPossible = classUnderTest.isActionPossible(model, action, csticMap);
		assertTrue(actionPossible);
	}

	@Test
	public void testIsActionPossibleWrongCstic()
	{
		ConfigurationRulesTestData.setCsticAsActionTarget(action, "WRONG_CSTIC_NAME");

		final boolean actionPossible = classUnderTest.isActionPossible(model, action, csticMap);
		assertFalse(actionPossible);
	}

	@Test
	public void testIsActionPossibleWrongValue()
	{
		final String valueName = "WRONG_VALUE_NAME";
		final CsticValueRAO valueNameForDiscount = new CsticValueRAO();
		valueNameForDiscount.setCsticValueName(valueName);
		action.setValueName(valueNameForDiscount);

		final boolean actionPossible = classUnderTest.isActionPossible(model, action, csticMap);
		assertFalse(actionPossible);
	}

	@Test
	public void testGetValueTopSet()
	{
		assertEquals("ASSIGNABLE_VALUE_1", classUnderTest.getValueToSet(action, cstic));
	}

	@Test
	public void testAfterMessageCreated()
	{
		classUnderTest.afterMessageCreated(CONFIG_ID, CSTIC1, CSTIC_VALUE1, productConfigMessage);
		verify(rulesResultHandler).addMessageToRulesResult(resultMessage, CONFIG_ID, CSTIC1, CSTIC_VALUE1);
	}


	@Test
	public void testMapMessage()
	{
		final DiscountMessageRulesResultModel resultMessage = classUnderTest.mapMessage(productConfigMessage);
		assertEquals(productConfigMessage.getMessage(), resultMessage.getMessage());
		assertEquals(productConfigMessage.getEndDate(), resultMessage.getEndDate());
	}

	@Test
	public void testMapResult()
	{
		final CharacteristicValueRulesResultModel result = classUnderTest.mapResult(action, null).get(0);
		assertEquals(cstic.getName(), result.getCharacteristic());
		assertEquals(valueName, result.getValue());
		assertEquals(BigDecimal.TEN, result.getDiscountValue());
	}

	@Test
	public void testMapResultNoValueNameMappedToAll()
	{
		given(rulesResultHandler.createInstance()).willReturn(new CharacteristicValueRulesResultModel(),
				new CharacteristicValueRulesResultModel());
		action.getValueName().setCsticValueName("");
		final List<CharacteristicValueRulesResultModel> resultList = classUnderTest.mapResult(action, csticMap);
		assertEquals(2, resultList.size());
		CharacteristicValueRulesResultModel result = resultList.get(0);
		assertEquals(cstic.getName(), result.getCharacteristic());
		assertEquals(valueName, result.getValue());
		assertEquals(BigDecimal.TEN, result.getDiscountValue());
		result = resultList.get(1);
		assertEquals(cstic.getName(), result.getCharacteristic());
		assertEquals(valueName2, result.getValue());
		assertEquals(BigDecimal.TEN, result.getDiscountValue());
	}

}
