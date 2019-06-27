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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleUtilImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigAbstractRuleActionStrategyTest
{

	private ProductConfigAbstractRuleActionStrategy classUnderTest;
	private AbstractRuleActionRAO action;
	private ConfigModel model;
	private Map<String, CsticModel> csticMap;
	private AbstractRuleEngineRuleModel rule;
	private ProductConfigRuleUtil ruleUtil;

	@Mock
	private RuleParametersService mockedRuleParamService;
	@Mock
	private RuleEngineService mockedRuleEngineService;
	@Mock
	private I18NService mockedI18nService;
	private List<RuleParameterData> paramList;

	@Before
	public void setUp() throws RuleConverterException
	{
		classUnderTest = new ProductConfigAbstractRuleActionStrategy()
		{
			@Override
			protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
					final Map<String, CsticModel> csticMap)
			{
				return false;
			}

			@Override
			protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action,
					final Map<String, CsticModel> csticMap)
			{
				return false;
			}
		};
		MockitoAnnotations.initMocks(this);
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest, mockedRuleEngineService, mockedI18nService,
				mockedRuleParamService);

		model = ConfigurationRulesTestData.createEmptyConfigModel();
		ruleUtil = new ProductConfigRuleUtilImpl();
		csticMap = ruleUtil.getCsticMap(model);
		action = new AbstractRuleActionRAO();
		action.setFiredRuleCode("rule123");
		action.setModuleName(SapproductconfigrulesConstants.RULES_MODULE_NAME);

		rule = new AbstractRuleEngineRuleModel();
		rule.setCode("rule123");

		given(mockedRuleEngineService.getRuleForCodeAndModule("rule123", SapproductconfigrulesConstants.RULES_MODULE_NAME))
				.willReturn(rule);
		given(mockedI18nService.getCurrentLocale()).willReturn(Locale.ENGLISH);

		paramList = new ArrayList<>();
		prepareRuleParam("0226a9c4-f20c-4111-b578-d6a318e33100",
				"Enum(de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleMessageSeverity)", "WARNING");
		given(mockedRuleParamService.convertParametersFromString(any(String.class))).willReturn(paramList);
	}

	@Test
	public void testHandleMessage_noMessage()
	{
		classUnderTest.handleMessage(model, action, csticMap);
		assertTrue(model.getMessages().isEmpty());
	}


	@Test
	public void testHandleMessage_newMessage()
	{
		rule.setMessageFired("a test message", Locale.ENGLISH);
		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessage("a test message", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_newMessage_de()
	{
		given(mockedI18nService.getCurrentLocale()).willReturn(Locale.GERMAN);
		rule.setMessageFired("eine test meldung", Locale.GERMAN);
		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessage("eine test meldung", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_sameRuleWith2Actions()
	{
		rule.setMessageFired("a test message", Locale.ENGLISH);
		classUnderTest.handleMessage(model, action, csticMap);
		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessage("a test message", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);
	}


	protected void assertOneMessage(final String message, final String key, final ProductConfigMessageSeverity severity,
			final ProductConfigMessageSource source)
	{
		assertTrue(model.getMessages().size() == 1);
		final ProductConfigMessage firstMessage = model.getMessages().iterator().next();
		assertEquals(message, firstMessage.getMessage());
		assertEquals(key, firstMessage.getKey());
		assertEquals(severity, firstMessage.getSeverity());
		assertEquals(source, firstMessage.getSource());
		assertEquals(ProductConfigMessageSourceSubType.DEFAULT, firstMessage.getSourceSubType());
	}

	@Test
	public void testMessageContainsParameters_validParam()
	{
		final String messageWithValidParam = "a message with a param {1dc12f4e-c48a-4a48-bc5b-43f606b61bbc}";
		assertTrue(classUnderTest.messageContainsParameters(messageWithValidParam));
	}

	@Test
	public void testMessageContainsParameters_noParam()
	{
		final String messageWithoutParam = "a message without a param";
		assertFalse(classUnderTest.messageContainsParameters(messageWithoutParam));
	}

	@Test
	public void testMessageContainsParameters_invalidParam()
	{
		final String messageWithInvalidParam = "a message with invalid param. params mut be in format '{hex|-}'";
		assertFalse(classUnderTest.messageContainsParameters(messageWithInvalidParam));
	}

	@Test
	public void testMessageReplace_noParam()
	{
		final String message = "message without any param";
		final String replacedMessage = classUnderTest.replaceMessageParameters(message, paramList);
		assertEquals(message, replacedMessage);
	}

	@Test
	public void testMessageReplace_replaceSingleParam()
	{
		prepareRuleParam("aa-111-bb", "java.lang.String", "WCEM_RELEASE");
		final String message = "Characteristic '{aa-111-bb}' has been prefilled, based on your current cart contents.";

		final String replacedMessage = classUnderTest.replaceMessageParameters(message, paramList);

		final String expectedMessage = "Characteristic 'WCEM_RELEASE' has been prefilled, based on your current cart contents.";
		assertEquals(expectedMessage, replacedMessage);
	}

	@Test
	public void testMessageReplace_replaceSingleParam_nullValue()
	{
		prepareRuleParam("aa-111-bb", "java.lang.String", null);
		final String message = "Characteristic '{aa-111-bb}' has been prefilled, based on your current cart contents.";

		final String replacedMessage = classUnderTest.replaceMessageParameters(message, paramList);

		final String expectedMessage = "Characteristic '' has been prefilled, based on your current cart contents.";
		assertEquals(expectedMessage, replacedMessage);
	}

	protected void prepareRuleParam(final String uuid, final String type, final String value)
	{
		final RuleParameterData ruleParam = new RuleParameterData();
		ruleParam.setUuid(uuid);
		ruleParam.setType(type);
		ruleParam.setValue(value);
		paramList.add(ruleParam);
	}

	@Test
	public void testHandleMessage_withParams()
	{

		prepareRuleParam("111-ff-22", "java.lang.String", "p1");
		prepareRuleParam("222-aa-11", "java.lang.String", "p2");
		rule.setMessageFired("a test message withe params {111-ff-22} and {222-aa-11}", Locale.ENGLISH);

		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessage("a test message withe params p1 and p2", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_withParamsIncomplete()
	{

		prepareRuleParam("aaaa-ff-22", "java.lang.String", "p1");
		prepareRuleParam("222-aa-11", "java.lang.String", "p2");
		rule.setMessageFired("a test message withe params {111-ff-22} and {222-aa-11}", Locale.ENGLISH);

		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessage("a test message withe params {111-ff-22} and p2", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testMapSeverity_warning()
	{
		final ProductConfigMessageSeverity severity = classUnderTest.mapSeverity(ProductConfigRuleMessageSeverity.WARNING);
		assertEquals(ProductConfigMessageSeverity.WARNING, severity);
	}

	@Test
	public void testMapSeverity_info()
	{
		final ProductConfigMessageSeverity severity = classUnderTest.mapSeverity(ProductConfigRuleMessageSeverity.INFO);
		assertEquals(ProductConfigMessageSeverity.INFO, severity);
	}

	@Test
	public void testMapSeverity_null()
	{
		final ProductConfigMessageSeverity severity = classUnderTest.mapSeverity(null);
		assertEquals(ProductConfigMessageSeverity.INFO, severity);
	}

	@Test
	public void testHandleMessage_assigneOneMessageToOneCstic()
	{
		final String csticName = "CSTIC_1";

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);
		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		rule.setMessageFired("a test message", Locale.ENGLISH);
		rule.setMessageForCstic(csticName);

		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessageForCstic(csticName, "a test message", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_assigneOneMessageToOneCstic_ignoreCase()
	{
		final String csticName = "CSTIC_1";

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);
		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		rule.setMessageFired("a test message", Locale.ENGLISH);
		rule.setMessageForCstic(csticName.toLowerCase(Locale.ENGLISH));

		classUnderTest.handleMessage(model, action, csticMap);

		assertOneMessageForCstic(csticName, "a test message", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testHandleMessage_assigneMessageToCsticWrongName()
	{
		final String csticName = "CSTIC_1";

		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);
		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		rule.setMessageFired("a test message", Locale.ENGLISH);
		rule.setMessageForCstic("XXX");

		classUnderTest.handleMessage(model, action, csticMap);

		assertTrue(model.getMessages().size() == 0);

		model.getRootInstance().getCstic(csticName);
		assertTrue(cstic.getMessages().size() == 0);

		final CsticModel csticNotExist = model.getRootInstance().getCstic("XXX");
		assertNull(csticNotExist);
	}

	@Test
	public void testHandleMessage_assigneMessagesToProductAndTwoCstics()
	{

		// Message for product
		rule.setMessageFired("a test message", Locale.ENGLISH);
		classUnderTest.handleMessage(model, action, csticMap);

		// Message for cstic 1
		final String csticName1 = "CSTIC_1";
		final CsticModel cstic1 = new CsticModelImpl();
		cstic1.setName(csticName1);
		model.getRootInstance().addCstic(cstic1);
		csticMap = ruleUtil.getCsticMap(model);
		rule.setMessageFired("a test message 1", Locale.ENGLISH);
		rule.setMessageForCstic(csticName1);

		classUnderTest.handleMessage(model, action, csticMap);


		// Message for cstic 2
		final String csticName2 = "CSTIC_2";
		final CsticModel cstic2 = new CsticModelImpl();
		cstic2.setName(csticName2);
		model.getRootInstance().addCstic(cstic2);
		csticMap = ruleUtil.getCsticMap(model);
		rule.setMessageFired("a test message 2", Locale.ENGLISH);
		rule.setMessageForCstic(csticName2);

		classUnderTest.handleMessage(model, action, csticMap);


		assertOneMessage("a test message", "rule123", ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.RULE);

		assertOneMessageForCstic(csticName1, "a test message 1", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);

		assertOneMessageForCstic(csticName2, "a test message 2", "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	protected void assertOneMessageForCstic(final String csticName, final String message, final String key,
			final ProductConfigMessageSeverity severity, final ProductConfigMessageSource source)
	{
		final CsticModel cstic = model.getRootInstance().getCstic(csticName);

		assertTrue(cstic.getMessages().size() == 1);
		final ProductConfigMessage firstMessage = cstic.getMessages().iterator().next();

		assertEquals(message, firstMessage.getMessage());
		assertEquals(key, firstMessage.getKey());
		assertEquals(severity, firstMessage.getSeverity());
		assertEquals(source, firstMessage.getSource());
	}

	protected void assertOneMessageForCsticValue(final String csticName, final String csticValueName, final String message,
			final String key, final ProductConfigMessageSeverity severity, final ProductConfigMessageSource source)
	{
		final CsticModel cstic = model.getRootInstance().getCstic(csticName);

		CsticValueModel csticValue = null;
		for (final CsticValueModel assignableValue : cstic.getAssignableValues())
		{
			if (assignableValue.getName().equals(csticValueName))
			{
				csticValue = assignableValue;
				break;
			}
		}

		assertNotNull(csticValue);
		assertTrue(csticValue.getMessages().size() == 1);
		final ProductConfigMessage firstMessage = csticValue.getMessages().iterator().next();
		assertEquals(message, firstMessage.getMessage());
		assertEquals(key, firstMessage.getKey());
		assertEquals(severity, firstMessage.getSeverity());
		assertEquals(source, firstMessage.getSource());
	}

	protected void assertNoMessages()
	{
		assertTrue(model.getMessages().size() == 0);

		for (final CsticModel cstic : model.getRootInstance().getCstics())
		{
			assertTrue(cstic.getMessages().size() == 0);
			for (final CsticValueModel assignableValue : cstic.getAssignableValues())
			{
				assertTrue(assignableValue.getMessages().size() == 0);
			}
		}
	}

	@Test
	public void testGetRule()
	{
		final AbstractRuleEngineRuleModel rule = classUnderTest.getRule(action);
		assertEquals("rule123", rule.getCode());
	}

	@Test
	public void testResolveMessage()
	{
		prepareRuleParam("aa-111-bb", "java.lang.String", "WCEM_RELEASE");
		final String message = "xxx {aa-111-bb} xxx";
		final String expectedResolvedMessage = "xxx WCEM_RELEASE xxx";

		final String resolvedMessage = classUnderTest.resolveMessage(message, "rule123", "dummy");
		assertEquals(expectedResolvedMessage, resolvedMessage);
	}

	@Test
	public void testResolveMessage_invalidParam()
	{
		prepareRuleParam("hex|-", "java.lang.String", "WCEM_RELEASE");
		final String messageWithInvalidParam = "a message with invalid param. params mut be in format '{hex|-}'";
		final String resolvedMessage = classUnderTest.resolveMessage(messageWithInvalidParam, "rule123", "dummy");
		assertEquals(messageWithInvalidParam, resolvedMessage);
	}

	@Test
	public void testShowMessageAssignedMessagesExistingCstic()
	{
		// Message for cstic
		final String csticName = "CSTIC";
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);
		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		final String messageFired = "a test message for cstic";

		final ProductConfigMessage productConfigMessage = classUnderTest.createMessage(rule.getCode(), messageFired,
				ProductConfigMessageSeverity.INFO);
		classUnderTest.showMessage(model, csticMap, rule, csticName, null, productConfigMessage);

		assertOneMessageForCstic(csticName, messageFired, "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testShowMessageAssignedMessagesExistingCsticValue()
	{
		// Message for cstic value
		final String csticName = "CSTIC";
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue_1 = new CsticValueModelImpl();
		assignableValue_1.setName("VALUE_1");
		assignableValues.add(assignableValue_1);
		final CsticValueModel assignableValue_2 = new CsticValueModelImpl();
		assignableValue_2.setName("VALUE_2");
		assignableValues.add(assignableValue_2);
		cstic.setAssignableValues(assignableValues);

		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		final String messageFired = "a test message for cstic value";

		final ProductConfigMessage productConfigMessage = classUnderTest.createMessage(rule.getCode(), messageFired,
				ProductConfigMessageSeverity.INFO);
		classUnderTest.showMessage(model, csticMap, rule, csticName, "VALUE_1", productConfigMessage);

		assertOneMessageForCsticValue(csticName, "VALUE_1", messageFired, "rule123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.RULE);
	}

	@Test
	public void testShowMessageAssignedMessagesNotExistingCstic()
	{
		// Message for cstic
		final String csticName = "CSTIC";
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);
		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		final String messageFired = "a test message for cstic";

		final ProductConfigMessage productConfigMessage = classUnderTest.createMessage(rule.getCode(), messageFired,
				ProductConfigMessageSeverity.INFO);
		classUnderTest.showMessage(model, csticMap, rule, "NOT_EXISTING_CSTIC", null, productConfigMessage);

		assertNoMessages();
	}

	@Test
	public void testShowMessageAssignedMessagesNotExistingCsticValue()
	{
		// Message for cstic value
		final String csticName = "CSTIC";
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue_1 = new CsticValueModelImpl();
		assignableValue_1.setName("VALUE_1");
		assignableValues.add(assignableValue_1);
		final CsticValueModel assignableValue_2 = new CsticValueModelImpl();
		assignableValue_2.setName("VALUE_2");
		assignableValues.add(assignableValue_2);
		cstic.setAssignableValues(assignableValues);

		model.getRootInstance().addCstic(cstic);
		csticMap = ruleUtil.getCsticMap(model);
		final String messageFired = "a test message for cstic value";

		final ProductConfigMessage productConfigMessage = classUnderTest.createMessage(rule.getCode(), messageFired,
				ProductConfigMessageSeverity.INFO);
		classUnderTest.showMessage(model, csticMap, rule, csticName, "NOT_EXISTING_VALUE", productConfigMessage);

		assertNoMessages();
	}

	@Test
	public void testAddMessageToCsticValue()
	{

		final ProductConfigMessage message = new ProductConfigMessageBuilder().appendMessage("Message").build();

		final CsticModel cstic = new CsticModelImpl();
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = new CsticValueModelImpl();
		assignedValue.setName("VALUE");
		assignedValues.add(assignedValue);
		cstic.setAssignedValues(assignedValues);

		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName("VALUE");

		classUnderTest.addMessageToCsticValue(message, cstic, csticValue);

		assertEquals(1, csticValue.getMessages().size());
		assertEquals(1, cstic.getAssignedValues().get(0).getMessages().size());

		final ProductConfigMessage msg1 = csticValue.getMessages().iterator().next();
		final ProductConfigMessage msg2 = cstic.getAssignedValues().get(0).getMessages().iterator().next();
		assertEquals(msg1, msg2);
	}

	@Test
	public void testAddMessageToCsticValueOtherAssignedValue()
	{

		final ProductConfigMessage message = new ProductConfigMessageBuilder().appendMessage("Message").build();

		final CsticModel cstic = new CsticModelImpl();
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = new CsticValueModelImpl();
		assignedValue.setName("VALUE_2");
		assignedValues.add(assignedValue);
		cstic.setAssignedValues(assignedValues);

		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName("VALUE_1");

		classUnderTest.addMessageToCsticValue(message, cstic, csticValue);

		assertEquals(1, csticValue.getMessages().size());
		assertEquals(0, cstic.getAssignedValues().get(0).getMessages().size());
	}

	@Test
	public void testAddMessageToCsticValueNoAssignedValue()
	{

		final ProductConfigMessage message = new ProductConfigMessageBuilder().appendMessage("Message").build();

		final CsticModel cstic = new CsticModelImpl();

		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName("VALUE");

		classUnderTest.addMessageToCsticValue(message, cstic, csticValue);

		assertEquals(1, csticValue.getMessages().size());
		assertEquals(0, cstic.getAssignedValues().size());
	}
}
