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
package de.hybris.platform.sap.productconfig.rules.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerProblem;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult.Result;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.ruleengineservices.model.RuleGroupModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.rao.populator.ProductConfigCartRAOPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;
import de.hybris.platform.sap.productconfig.services.integrationtests.CPQServiceLayerTest;
import de.hybris.platform.store.BaseStoreModel;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;


/**
 * Base test class for rules related integration tests
 */
@SuppressWarnings("javadoc")
public abstract class ProductConfigRulesTest extends CPQServiceLayerTest
{
	private final Logger LOG = Logger.getLogger(ProductConfigRulesTest.class.getName());

	@Resource(name = "sapProductConfigDefaultRuleAwareConfigurationService")
	protected ProductConfigurationServiceImpl ruleAwareService;
	@Resource(name = "ruleMaintenanceService")
	protected RuleMaintenanceService ruleMaintenanceService;
	@Resource(name = "sapProductConfigCartRaoPopulator")
	protected ProductConfigCartRAOPopulator raoPopulator;
	@Resource(name = "productService")
	protected ProductService productService;
	@Resource(name = "commerceCartService")
	protected CommerceCartService commerceCartService;
	protected String expectedMessage;

	protected static final String EXPECTED_MESSAGE = "expected one message, but was ";
	protected static final String YM_NS_F160 = "YM_NS_F160";
	protected static final String CPQ_HT_SPK_COLOR = "CPQ_HT_SPK_COLOR";
	protected static final String CPQ_HT_SPK_MODEL = "CPQ_HT_SPK_MODEL";
	protected static final String EXP_NUMBER = "EXP_NUMBER";
	protected static final String YSAP_POC_SIMPLE_FLAG = "YSAP_POC_SIMPLE_FLAG";
	protected static final String STEREO = "STEREO";
	protected static final String SURROUND = "SURROUND";
	protected static final String CPQ_HT_SURROUND_MODE = "CPQ_HT_SURROUND_MODE";
	protected static final String CPQ_SOFTWARE = "CPQ_SOFTWARE";
	protected static final String PAINTER = "PAINTER";
	protected static final String CPQ_SECURITY = "CPQ_SECURITY";
	protected static final String NORTON = "NORTON";
	protected static final String CPQ_DISPLAY = "CPQ_DISPLAY";
	protected static final String CPQ_RAM = "CPQ_RAM";
	protected static final String CPQ_DISPLAY_17 = "17";
	protected static final String CPQ_DISPLAY_13 = "13";
	protected static final String CPQ_DISPLAY_15 = "15";
	protected static final String CPQ_MONITOR = "CPQ_MONITOR";
	protected static final String CPQ_MONITOR_21 = "21";
	protected static final String CPQ_MONITOR_24 = "24";
	protected static final String CPQ_MONITOR_24HD = "24HD";
	protected static final String CPQ_MONITOR_27 = "27";
	protected static final String CPQ_CPU = "CPQ_CPU";
	protected static final String INTELI7_40 = "INTELI7_40";
	protected static final String INTELI5_35 = "INTELI5_35";
	protected static final String CPQ_OS = "CPQ_OS";
	protected static final String LINUSDEBIAN = "LINUSDEBIAN";
	protected static final String MS10 = "MS10";

	@Override
	protected void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCsv("/sapproductconfigrules/test/sapProductConfig_cpqRules_testData.impex", "utf-8");
		importCsv("/impex/sapproductconfigrules_definitions.impex", "windows-1252");
		useLocale_EN();
	}

	@Before
	public void setUp() throws ImpExException, Exception
	{
		MockitoAnnotations.initMocks(this);
		raoPopulator.setProductConfigService(ruleAwareService);
		prepareCPQData();

		final AbstractRulesModuleModel pcm = getFromPersistence(
				"select {pk} from {AbstractRulesModule} where {name} = 'productconfig-module' and {active} = true");
		assertNotNull(pcm);
	}

	@Before
	public void enforeRuleAwareService()
	{
		raoPopulator.setProductConfigService(ruleAwareService);
	}

	@Override
	public void initProviders()
	{
		ensureMockProvider();
	}

	@After
	public void restoreDefaultProductConfigService()
	{
		raoPopulator.setProductConfigService(cpqService);
	}


	protected void assertSingleMessage(final ConfigModel config, final ProductConfigMessageSeverity expectedSeverity,
			final String expectedMessage)
	{
		final Set<ProductConfigMessage> messages = config.getMessages();
		assertEquals(EXPECTED_MESSAGE + messages.size() + " messages=" + getLogString(messages), 1, messages.size());
		assertEquals(expectedSeverity, messages.iterator().next().getSeverity());
		final String actualMessage = messages.iterator().next().getMessage();
		assertEquals("expected encoded message: " + expectedMessage + ", but was " + actualMessage, expectedMessage, actualMessage);
	}

	protected void assertSingleMessageForCstic(final CsticModel cstic, final ProductConfigMessageSeverity expectedSeverity,
			final String expectedMessage)
	{
		final Set<ProductConfigMessage> messages = cstic.getMessages();
		assertEquals(EXPECTED_MESSAGE + messages.size() + " messages=" + getLogString(messages), 1, messages.size());
		assertEquals(expectedSeverity, messages.iterator().next().getSeverity());
		final String actualMessage = messages.iterator().next().getMessage();
		assertEquals("expected encoded message: " + expectedMessage + ", but was " + actualMessage, expectedMessage, actualMessage);
	}


	protected void assertNoMessage(final ConfigModel config)
	{
		final Set<ProductConfigMessage> messages = config.getMessages();
		assertTrue("expected zero messages, but was " + messages.size() + " messages=" + getLogString(messages),
				messages.isEmpty());
	}

	protected void assertNoMessageForCstic(final CsticModel cstic)
	{
		final Set<ProductConfigMessage> messages = cstic.getMessages();
		assertTrue("expected zero messages, but was " + messages.size() + " messages=" + getLogString(messages),
				messages.isEmpty());
	}

	protected String getLogString(final Set<ProductConfigMessage> messages)
	{
		final StringBuilder builder = new StringBuilder("[");
		final int counter = 0;
		for (final ProductConfigMessage message : messages)
		{
			builder.append(counter);
			builder.append(": (");
			builder.append(message.getSeverity().toString());
			builder.append(") ");
			builder.append(message.getMessage());
			builder.append(";  ");
		}
		builder.append("]");
		return builder.toString();
	}

	protected Map<String, String> prepareAndPublishRule(final String... ruleNames) throws RuleEngineServiceException
	{
		final Map<String, String> nameToMessage = new HashMap<>();
		final List<SourceRuleModel> sourceRules = new ArrayList<>();
		for (final String ruleName : ruleNames)
		{
			// Get source rule data from property
			final SourceRuleModel sourceRuleModel = prepareSourceRule(nameToMessage, ruleName);
			sourceRules.add(sourceRuleModel);
		}

		final RuleCompilerPublisherResult compileAndPublishRules = ruleMaintenanceService.compileAndPublishRules(sourceRules,
				SapproductconfigrulesConstants.RULES_MODULE_NAME, false);

		checkCompilerAndPublishResult(sourceRules, compileAndPublishRules);
		return nameToMessage;
	}

	protected SourceRuleModel prepareSourceRule(final Map<String, String> nameToMessage, final String ruleName)
	{
		final String propertyFilePath = "/sourcerules/" + ruleName + ".properties";
		final Properties testProps = new Properties();
		try
		{
			testProps.load(this.getClass().getResourceAsStream(propertyFilePath));
		}
		catch (final IOException ex)
		{
			LOG.warn("Unable to retrieve test properties " + propertyFilePath, ex);
			fail("Unable to retrieve test properties " + propertyFilePath + " - " + ex.getMessage());
		}

		final String code = testProps.getProperty("code");
		final String priority = testProps.getProperty("priority");
		final String maxAllowedRuns = testProps.getProperty("maxAllowedRuns");
		final String ruleGroup = testProps.getProperty("ruleGroup");
		final String messageSeverityString = testProps.getProperty("messageSeverity");
		String conditions = testProps.getProperty("conditions");
		final String actions = testProps.getProperty("actions");
		expectedMessage = testProps.getProperty("messageFired");
		nameToMessage.put(ruleName, expectedMessage);
		final String messageForCstic = testProps.getProperty("messageForCstic");

		final String userMappingForConditions = testProps.getProperty("userMappingForConditions");
		if (userMappingForConditions != null && !userMappingForConditions.trim().isEmpty())
		{
			conditions = mapUsersInConditions(conditions, userMappingForConditions);
		}

		ProductConfigRuleMessageSeverity messageSeverity = ProductConfigRuleMessageSeverity.INFO;
		if (messageSeverityString != null && messageSeverityString.trim().equalsIgnoreCase("WARNING"))
		{
			messageSeverity = ProductConfigRuleMessageSeverity.WARNING;
		}

		final Set<BaseStoreModel> baseStores = prepareBaseStores(testProps);

		final String startDateString = testProps.getProperty("startDate");
		final String endDateString = testProps.getProperty("endDate");

		final Date startDate = prepareDate(startDateString);
		final Date endDate = prepareDate(endDateString);

		// Prepare and Compile Rule
		final RuleGroupModel ruleGroupModel = getFromPersistence("Select {pk} from {rulegroup} where {code}='" + ruleGroup + "'");

		final ProductConfigSourceRuleModel sourceRuleModel = modelService.create(ProductConfigSourceRuleModel.class);
		sourceRuleModel.setCode(code);
		sourceRuleModel.setPriority(Integer.valueOf(priority));
		sourceRuleModel.setMaxAllowedRuns(Integer.valueOf(maxAllowedRuns));
		sourceRuleModel.setRuleGroup(ruleGroupModel);
		sourceRuleModel.setConditions(conditions);
		sourceRuleModel.setActions(actions);
		sourceRuleModel.setMessageFired(expectedMessage, Locale.ENGLISH);
		sourceRuleModel.setMessageSeverity(messageSeverity);
		sourceRuleModel.setMessageForCstic(messageForCstic);
		sourceRuleModel.setBaseStores(baseStores);

		if (startDate != null)
		{
			sourceRuleModel.setStartDate(startDate);
		}

		if (endDate != null)
		{
			sourceRuleModel.setEndDate(endDate);
		}

		modelService.save(sourceRuleModel);

		assertNotNull(sourceRuleModel.getConditions());
		assertNotNull(sourceRuleModel.getActions());

		return sourceRuleModel;

	}

	protected Date prepareDate(final String dateAsString)
	{
		Date date = null;
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

		if (dateAsString != null && !dateAsString.isEmpty())
		{
			try
			{
				date = formatter.parse(dateAsString);
			}
			catch (final ParseException e)
			{
				LOG.error("Wrong date format: " + dateAsString, e);
			}
		}
		return date;
	}

	private String mapUsersInConditions(final String conditions, final String userMappingForConditions)
	{
		// User id mapping for conditions:
		// Starting with hybris 6.6 "customers" condition contains userPK and not user Id any more
		// PK is generated first during the test run. We have to replace "stable" user Id by PK during the test execution
		// Example:
		// userMappingForConditions=cpq01;cpq02;userXYZ

		String resultConditions = conditions;

		final String[] userIds = userMappingForConditions.split(";");
		for (String userId : userIds)
		{
			userId = userId.trim();
			final UserModel userModel = realUserService.getUserForUID(userId);
			if (userModel != null)
			{
				final String userPK = userModel.getPk().toString();
				resultConditions = resultConditions.replaceAll(userId, userPK);
			}
		}

		return resultConditions;
	}

	protected void checkCompilerAndPublishResult(final List<SourceRuleModel> sourceRules,
			final RuleCompilerPublisherResult compileAndPublishRules)
	{
		if (Result.COMPILER_ERROR.equals(compileAndPublishRules.getResult()))
		{
			for (final RuleCompilerResult result : compileAndPublishRules.getCompilerResults())
			{
				final String code = result.getRuleCode();
				for (final RuleCompilerProblem problem : result.getProblems())
				{
					LOG.error(problem.getSeverity() + ": " + code + ": " + problem.getMessage());
				}
			}
		}

		if (Result.PUBLISHER_ERROR.equals(compileAndPublishRules.getResult()))
		{
			for (final RuleEngineActionResult result : compileAndPublishRules.getPublisherResults())
			{
				final String publishMessage = result.getMessagesAsString(null);
				{
					LOG.error("PublishMessage: " + publishMessage);
				}
			}
		}

		assertEquals("CompileAndPublish rule was not succesfull: " + compileAndPublishRules.getResult() + " - see log for details",
				Result.SUCCESS, compileAndPublishRules.getResult());

		assertEquals(
				"compiled and published " + sourceRules.size() + " source rules, but there were "
						+ compileAndPublishRules.getCompilerResults().size() + " rule engine rules created.",
				sourceRules.size(), compileAndPublishRules.getCompilerResults().size());
	}

	protected Set<BaseStoreModel> prepareBaseStores(final Properties testProps)
	{
		final Set<BaseStoreModel> baseStores = new HashSet<BaseStoreModel>();
		final String baseStoresString = testProps.getProperty("baseStores");
		if (baseStoresString != null)
		{
			final String[] baseStoresArray = baseStoresString.split(";");
			for (int i = 0; i < baseStoresArray.length; i++)
			{
				final String baseStore = baseStoresArray[i];
				final BaseStoreModel baseStoreModel = getFromPersistence(
						"Select {pk} from {BaseStore} where {uid}='" + baseStore + "'");
				baseStores.add(baseStoreModel);
			}
		}
		return baseStores;
	}

	protected ConfigModel changeValueAndUpdate(final ConfigModel config, final String cstic, final String value)
	{
		serviceConfigValueHelper.setSingleCsticValue(config, cstic, value);
		ruleAwareService.updateConfiguration(config);
		return ruleAwareService.retrieveConfigurationModel(config.getId());
	}


	protected void addConfigurationToCart(final ConfigModel configModel, final KBKey kbKey)
			throws CommerceCartModificationException
	{
		final CartModel cart = cartService.getSessionCart();
		final String productCode = kbKey.getProductCode();
		final ProductModel product = productService.getProductForCode(productCode);

		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setEnableHooks(true);
		commerceCartParameter.setCart(cart);
		commerceCartParameter.setProduct(product);
		commerceCartParameter.setQuantity(1);
		commerceCartParameter.setUnit(product.getUnit());
		commerceCartParameter.setCreateNewEntry(true);
		commerceCartParameter.setConfigId(configModel.getId());

		try
		{
			commerceCartService.addToCart(commerceCartParameter);
			LOG.debug("Configuration with product code '" + productCode + "' war added to the cart");

		}
		catch (final CommerceCartModificationException e)
		{
			LOG.debug("Configuration war not added to the cart");
		}
	}

}
