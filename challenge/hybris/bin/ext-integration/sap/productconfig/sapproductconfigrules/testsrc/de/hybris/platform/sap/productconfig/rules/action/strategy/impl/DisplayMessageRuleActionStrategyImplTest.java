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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.ProductConfigDisplayMessageRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleUtilImpl;
import de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl.ProductConfigMessageRuleParameterValueMapper;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DisplayMessageRuleActionStrategyImplTest
{
	private DisplayMessageRuleActionStrategyImpl classUnderTest;

	private ProductConfigDisplayMessageRAO action;
	private ConfigModel model;
	private Map<String, CsticModel> csticMap;
	private CsticModel cstic;
	private ProductConfigRuleUtil ruleUtil;

	private final String message = "{\"de\":\"german text\",\"en\":\"english text\"}";
	private final String localizedMessage = "english text";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DisplayMessageRuleActionStrategyImpl();
		classUnderTest.setMessageValueMapper(new ProductConfigMessageRuleParameterValueMapper());
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);

		model = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();
		ruleUtil = new ProductConfigRuleUtilImpl();
		csticMap = ruleUtil.getCsticMap(model);
		cstic = model.getRootInstance().getCstics().get(0);
		cstic.setVisible(true);

		action = new ProductConfigDisplayMessageRAO();
		ConfigurationRulesTestData.setCsticAsActionTarget(action, cstic.getName());
		action.setFiredRuleCode("rule123");
		action.setModuleName(SapproductconfigrulesConstants.RULES_MODULE_NAME);
		action.setMessage(message);
		action.setMessageSeverity(ProductConfigRuleMessageSeverity.WARNING);
	}

	@Test
	public void testEsxecuteAction_MessageForCstic()
	{
		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		assertFalse(configChanged);
		assertEquals(1, cstic.getMessages().size());
		final ProductConfigMessage firstMessage = cstic.getMessages().iterator().next();
		assertNotNull(firstMessage);
		assertEquals(localizedMessage, firstMessage.getMessage());
		assertEquals(ProductConfigMessageSeverity.WARNING, firstMessage.getSeverity());
	}

	@Test
	public void testEsxecuteAction_MessageForCsticValue()
	{
		final String valueName = cstic.getAssignableValues().get(0).getName();
		final CsticValueRAO valueNameForMessage = new CsticValueRAO();
		valueNameForMessage.setCsticValueName(valueName);
		action.setValueNameForMessage(valueNameForMessage);

		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		assertFalse(configChanged);
		assertEquals(0, cstic.getMessages().size());

		final CsticValueModel csticValueModel = cstic.getAssignableValues().get(0);
		assertEquals(1, csticValueModel.getMessages().size());

		final ProductConfigMessage firstMessage = csticValueModel.getMessages().iterator().next();
		assertNotNull(firstMessage);
		assertEquals(localizedMessage, firstMessage.getMessage());
		assertEquals(ProductConfigMessageSeverity.WARNING, firstMessage.getSeverity());
	}

	@Test
	public void testExtractLocalizedString()
	{
		assertEquals(localizedMessage, classUnderTest.extractLocalizedMessageText(message));
	}

	@Test
	public void testIsActionPossible()
	{
		final boolean actionPossible = classUnderTest.isActionPossible(null, null, null);
		assertTrue(actionPossible);
	}

	@Test
	public void testCreateMessage()
	{
		classUnderTest.setConfigModelFactory(new ConfigModelFactoryImpl());
		final ProductConfigMessage message = classUnderTest.createMessage("123", localizedMessage,
				ProductConfigMessageSeverity.WARNING);

		assertSame("123", message.getKey());
		assertSame(localizedMessage, message.getMessage());
		assertSame(ProductConfigMessageSource.RULE, message.getSource());
		assertSame(ProductConfigMessageSourceSubType.DISPLAY_MESSAGE, message.getSourceSubType());
		assertSame(ProductConfigMessageSeverity.WARNING, message.getSeverity());
	}
}
