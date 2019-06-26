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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.ProductConfigPromoMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleUtilImpl;
import de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl.ProductConfigMessageRuleParameterValueMapper;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hsqldb.lib.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DisplayPromoMessageRuleActionStrategyImplTest
{
	private DisplayPromoMessageRuleActionStrategyImpl classUnderTest;

	private ProductConfigPromoMessageRAO action;
	private ConfigModel model;
	private Map<String, CsticModel> csticMap;
	private CsticModel cstic;
	private ProductConfigRuleUtil ruleUtil;

	private final String message = "{\"de\":\"german text\",\"en\":\"english text\"}";
	private final String localizedMessage = "english text";

	private final String extendedMessage = "{\"de\":\"extended german text\",\"en\":\"extended english text\"}";
	private final String localizedExtendedMessage = "extended english text";

	private final Date endDate = new Date();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DisplayPromoMessageRuleActionStrategyImpl();
		classUnderTest.setMessageValueMapper(new ProductConfigMessageRuleParameterValueMapper());
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);

		model = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();
		ruleUtil = new ProductConfigRuleUtilImpl();
		csticMap = ruleUtil.getCsticMap(model);
		cstic = model.getRootInstance().getCstics().get(0);
		cstic.setVisible(true);

		action = new ProductConfigPromoMessageRAO();
		ConfigurationRulesTestData.setCsticAsActionTarget(action, cstic.getName());
		action.setFiredRuleCode("rule123");
		action.setModuleName(SapproductconfigrulesConstants.RULES_MODULE_NAME);
		action.setMessage(message);
		action.setExtendedMessage(extendedMessage);
		final CsticValueRAO valueRAO = new CsticValueRAO();
		valueRAO.setCsticValueName("");
		action.setValueName(valueRAO);
	}

	@Test
	public void testEsxecuteActionMessageForCstic()
	{
		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		assertFalse(configChanged);
		assertEquals(1, cstic.getMessages().size());
		final ProductConfigMessage firstMessage = cstic.getMessages().iterator().next();
		assertNotNull(firstMessage);
		assertEquals(localizedMessage, firstMessage.getMessage());
		assertEquals(localizedExtendedMessage, firstMessage.getExtendedMessage());
		assertEquals(ProductConfigMessageSeverity.INFO, firstMessage.getSeverity());
	}

	@Test
	public void testEsxecuteActionMessageForCsticValue()
	{
		final String valueName = cstic.getAssignableValues().get(0).getName();
		final CsticValueRAO valueNameForMessage = new CsticValueRAO();
		valueNameForMessage.setCsticValueName(valueName);
		action.setValueName(valueNameForMessage);

		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		assertFalse(configChanged);
		assertEquals(0, cstic.getMessages().size());

		final CsticValueModel csticValueModel = cstic.getAssignableValues().get(0);
		assertEquals(1, csticValueModel.getMessages().size());

		final ProductConfigMessage firstMessage = csticValueModel.getMessages().iterator().next();
		assertNotNull(firstMessage);
		assertEquals(localizedMessage, firstMessage.getMessage());
		assertEquals(localizedExtendedMessage, firstMessage.getExtendedMessage());
		assertEquals(ProductConfigMessageSeverity.INFO, firstMessage.getSeverity());
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

		final ProductConfigMessage message = classUnderTest.createPromoMessageBuilder().appendKey("123")
				.appendMessage(localizedMessage)
				.appendPromotionFields(ProductConfigMessagePromoType.PROMO_APPLIED, localizedExtendedMessage, endDate).build();

		verifyMessage(message);
	}

	@Test
	public void testShowMessageForAllOccurrencesOfCsticValue()
	{
		final String code = "123";
		final AbstractRuleEngineRuleModel rule = new AbstractRuleEngineRuleModel();
		rule.setCode(code);

		final ConfigModel model = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();
		ConfigurationRulesTestData.addSubInstanceWithCsticWithAssignableValues(model, "2");

		final ProductConfigMessage message = classUnderTest.createPromoMessageBuilder().appendKey(code)
				.appendMessage(localizedMessage)
				.appendPromotionFields(ProductConfigMessagePromoType.PROMO_APPLIED, localizedExtendedMessage, endDate).build();

		classUnderTest.showMessageForAllOccurrences(model, rule, ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_1, message);

		Set<ProductConfigMessage> messages = retrieveMessagesAssignedTo(model, "1",
				ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES, ConfigurationRulesTestData.ASSIGNABLE_VALUE_1);
		assertEquals(1, messages.size());
		verifyMessage(messages.iterator().next());

		messages = retrieveMessagesAssignedTo(model, "1", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_2);
		assertTrue(messages.isEmpty());

		messages = retrieveMessagesAssignedTo(model, "1", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES, "");
		assertTrue(messages.isEmpty());

		messages = retrieveMessagesAssignedTo(model, "2", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_1);
		assertEquals(1, messages.size());
		verifyMessage(messages.iterator().next());

		messages = retrieveMessagesAssignedTo(model, "2", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_2);
		assertTrue(messages.isEmpty());

		messages = retrieveMessagesAssignedTo(model, "2", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES, "");
		assertTrue(messages.isEmpty());
	}

	@Test
	public void testShowMessageForAllOccurrencesOfCstic()
	{
		final String code = "123";
		final AbstractRuleEngineRuleModel rule = new AbstractRuleEngineRuleModel();
		rule.setCode(code);

		final ConfigModel model = ConfigurationRulesTestData.createConfigModelWithCsticWithAssignableValues();
		ConfigurationRulesTestData.addSubInstanceWithCsticWithAssignableValues(model, "2");

		final ProductConfigMessage message = classUnderTest.createPromoMessageBuilder().appendKey(code)
				.appendMessage(localizedMessage)
				.appendPromotionFields(ProductConfigMessagePromoType.PROMO_APPLIED, localizedExtendedMessage, endDate).build();

		classUnderTest.showMessageForAllOccurrences(model, rule, ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES, null,
				message);

		Set<ProductConfigMessage> messages = retrieveMessagesAssignedTo(model, "1",
				ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES, null);
		assertEquals(1, messages.size());
		verifyMessage(messages.iterator().next());

		messages = retrieveMessagesAssignedTo(model, "1", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_1);
		assertTrue(messages.isEmpty());

		messages = retrieveMessagesAssignedTo(model, "1", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_2);
		assertTrue(messages.isEmpty());

		messages = retrieveMessagesAssignedTo(model, "2", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES, null);
		assertEquals(1, messages.size());
		verifyMessage(messages.iterator().next());

		messages = retrieveMessagesAssignedTo(model, "2", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_1);
		assertTrue(messages.isEmpty());

		messages = retrieveMessagesAssignedTo(model, "2", ConfigurationRulesTestData.CSTIC_WITH_ASSIGNABLE_VALUES,
				ConfigurationRulesTestData.ASSIGNABLE_VALUE_2);
		assertTrue(messages.isEmpty());
	}

	private Set<ProductConfigMessage> retrieveMessagesAssignedTo(final ConfigModel model, final String instanceId,
			final String csticName, final String csticValueName)
	{
		Set<ProductConfigMessage> messages = new HashSet<>();

		InstanceModel instance;
		if (instanceId.equals("1"))
		{
			instance = model.getRootInstance();
		}
		else
		{
			instance = model.getRootInstance().getSubInstance(instanceId);
		}

		final CsticModel cstic = instance.getCstic(csticName);

		if (cstic != null)
		{
			if (StringUtil.isEmpty(csticValueName))
			{
				messages = cstic.getMessages();
			}
			else
			{
				final List<CsticValueModel> values = cstic.getAssignableValues();
				for (final CsticValueModel value : values)
				{
					if (csticValueName.equals(value.getName()))
					{
						messages = value.getMessages();
						break;
					}
				}
			}
		}

		return messages;
	}

	private void verifyMessage(final ProductConfigMessage message)
	{
		assertSame("123", message.getKey());
		assertSame(localizedMessage, message.getMessage());
		assertSame(localizedExtendedMessage, message.getExtendedMessage());
		assertSame(ProductConfigMessageSource.RULE, message.getSource());
		assertSame(ProductConfigMessageSourceSubType.DISPLAY_PROMO_MESSAGE, message.getSourceSubType());
		assertSame(ProductConfigMessagePromoType.PROMO_APPLIED, message.getPromoType());
		assertSame(ProductConfigMessageSeverity.INFO, message.getSeverity());
		assertSame(endDate, message.getEndDate());
	}
}
