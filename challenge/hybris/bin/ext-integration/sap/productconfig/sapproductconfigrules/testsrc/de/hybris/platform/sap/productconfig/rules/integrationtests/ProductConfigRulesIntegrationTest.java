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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest
@SuppressWarnings("javadoc")
public class ProductConfigRulesIntegrationTest extends ProductConfigRulesTest
{
	private final Logger LOG = Logger.getLogger(ProductConfigRulesIntegrationTest.class.getName());

	@Test
	public void testModifyDefaultConfigOfVariantDefaultMessage()
			throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Set  CPQ_SOFTWARE = PAINTER for currently configured CPQ_LAPTOP
		prepareAndPublishRule("cpq_test_set_software_for_cpq_laptop");

		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		boolean softwareSelected = serviceConfigValueHelper.isValueAssigned(serviceConfigValueHelper.getCstic(config, CPQ_SOFTWARE),
				PAINTER);
		assertTrue("Expected 'CPQ_SOFTWARE' to be 'PAINTER', rule should match: " + softwareSelected, softwareSelected);
		//assertSingleMessage(config, ProductConfigMessageSeverity.INFO, expectedMessage);

		// make a modification ->  default should remain (as we did not change it), message should disappear
		config = changeValueAndUpdate(config, CPQ_SECURITY, NORTON);

		softwareSelected = serviceConfigValueHelper.isValueAssigned(serviceConfigValueHelper.getCstic(config, CPQ_SOFTWARE),
				PAINTER);
		assertTrue("Expected 'CPQ_SOFTWARE' to be 'PAINTER', rule should match: " + softwareSelected, softwareSelected);
		//assertNoMessage(config);
	}

	@Test
	public void testRemoveAssignableValueNoMessage() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Remove  CPQ_DISPLAY = "17" for currently configured CPQ_LAPTOP, if CPQ_DISPLAY == "13"
		prepareAndPublishRule("cpq_test_remove_cpq_display_for_cpq_laptop");

		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected only two value available: ", 3, displayCstic.getAssignableValues().size());
		assertEquals("Expected 'CPQ_DISPLAY' value '13': ", CPQ_DISPLAY_13, displayCstic.getAssignableValues().get(0).getName());
		assertEquals("Expected 'CPQ_DISPLAY' value '15': ", CPQ_DISPLAY_15, displayCstic.getAssignableValues().get(1).getName());
		assertEquals("Expected 'CPQ_DISPLAY' value '17': ", CPQ_DISPLAY_17, displayCstic.getAssignableValues().get(2).getName());

		final Set<ProductConfigMessage> messages = config.getMessages();
		assertTrue("expected zero messages, but was " + messages.size(), messages.isEmpty());

		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected only two value available: ", 2, displayCstic.getAssignableValues().size());
		assertEquals("Expected 'CPQ_DISPLAY' value '13': ", CPQ_DISPLAY_13, displayCstic.getAssignableValues().get(0).getName());
		assertEquals("Expected 'CPQ_DISPLAY' value '15': ", CPQ_DISPLAY_15, displayCstic.getAssignableValues().get(1).getName());

		// make a modification ->  rule should still apply
		config = changeValueAndUpdate(config, CPQ_SECURITY, NORTON);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected only two value available: ", 2, displayCstic.getAssignableValues().size());
		assertEquals("Expected 'CPQ_DISPLAY' value '13': ", CPQ_DISPLAY_13, displayCstic.getAssignableValues().get(0).getName());
		assertEquals("Expected 'CPQ_DISPLAY' value '15': ", CPQ_DISPLAY_15, displayCstic.getAssignableValues().get(1).getName());

		assertNoMessage(config);
	}

	@Test
	public void testModifyDefaultConfigAndRemoveAssignableValueBasedOnCartNoMessage()
			throws CommerceCartModificationException, RuleEngineServiceException
	{
		// For CPQ_LAPTOP
		// Set CPQ_DISPLAY = 17,
		// Remove assignable value INTELI7_40 for CPQ_CPU,
		// when YSAP_POC_SIMPLE_FLAG = ' ' for the YSAP_SIMPLE_POC in the cart

		prepareAndPublishRule("cpq_test_set_display_for_cpq_laptop");

		// no product in cart, no modification to default config expected
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		boolean display17Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_17);
		boolean display13Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_13);
		boolean display15Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_15);
		assertFalse("Expected 'CPQ_DISPLAY' NOT to be '17', as rule should not match: ", display17Selected);
		assertFalse("Expected 'CPQ_DISPLAY' to be '13' by default: ", display13Selected);
		assertFalse("Expected 'CPQ_DISPLAY' NOT to be '15', as rule should not match: ", display15Selected);
		final Set<ProductConfigMessage> messages = config.getMessages();
		assertTrue(messages.isEmpty());
		CsticValueModel valueInteli7_40 = serviceConfigValueHelper.getCsticValue(config, CPQ_CPU, INTELI7_40);
		assertNotNull("Expected 'CPQ_CPU' contains assignable value 'INTELI7_40', as rule should not match: ", valueInteli7_40);

		ruleAwareService.releaseSession(config.getId());

		// create prerequisite for rule to macth
		LOG.debug("Trying to fullfill condition for rule");
		config = ruleAwareService.createDefaultConfiguration(KB_Y_SAP_SIMPLE_POC);

		serviceConfigValueHelper.selectUnselectCsticValue(config, YSAP_POC_SIMPLE_FLAG, "X", false);
		ruleAwareService.updateConfiguration(config);
		ruleAwareService.retrieveConfigurationModel(config.getId());
		addConfigurationToCart(config, KB_Y_SAP_SIMPLE_POC);
		config = ruleAwareService.retrieveConfigurationModel(config.getId());

		// now we expect rule to match
		LOG.debug("create defualt configuration, so for rule to set value '17' by 'CPQ_DISPLAY' should match");
		config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		display17Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_17);
		display13Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_13);
		display15Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_15);
		assertTrue("Expected 'CPQ_DISPLAY' to be '17', as rule should match: ", display17Selected);
		assertFalse("Expected 'CPQ_DISPLAY' NOT to be '13', as rule should not match: ", display13Selected);
		assertFalse("Expected 'CPQ_DISPLAY' NOT to be '15', as rule should not match: ", display15Selected);
		assertNoMessage(config);

		valueInteli7_40 = serviceConfigValueHelper.getCsticValue(config, CPQ_CPU, INTELI7_40);
		assertNull("Expected 'CPQ_CPU' does not contain assignable value anymore 'INTELI7_40', rule should match: ",
				valueInteli7_40);

		// now try to override the default as user
		LOG.debug("trying to override rule set value 17->15");
		serviceConfigValueHelper.selectUnselectCsticValue(config, CPQ_DISPLAY, CPQ_DISPLAY_17, false);
		serviceConfigValueHelper.selectUnselectCsticValue(config, CPQ_DISPLAY, CPQ_DISPLAY_15, true);

		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_15);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		display17Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_17);
		display15Selected = serviceConfigValueHelper.isValueAssigned(displayCstic, CPQ_DISPLAY_15);
		assertFalse("Expected 'CPQ_DISPLAY' NOT to be '17', rule overridden by user: ", display17Selected);
		assertTrue("Expected 'CPQ_DISPLAY' to be '15', rule overriden by user: ", display15Selected);
		assertNoMessage(config);

		valueInteli7_40 = serviceConfigValueHelper.getCsticValue(config, CPQ_CPU, INTELI7_40);
		assertNull("Expected 'CPQ_CPU' does not contain assignable value anymore 'INTELI7_40', rule should match: ",
				valueInteli7_40);
	}

	/**
	 * Condition in rule checks for 500 (which is also displayed on UI, if you enter 500.0), however the RAO contains 500
	 * Fix: use ValueFormatTranslator for numeric CStics to get formatted string to be set into RAO.
	 */
	@Test
	public void testNumericConditionUnderThousand() throws RuleEngineServiceException, CommerceCartModificationException
	{
		prepareAndPublishRule("cpq_test_numeric_value_under_thousand_condition_rule");

		// no product in cart, no modification to default config expected
		//create default configuration 'CPQ_LAPTOP'
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		CsticModel processorCstic = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		CsticModel memoryCstic = serviceConfigValueHelper.getCstic(config, CPQ_RAM);
		assertEquals("There should be 3 assignable values for 'CPQ_DISPLAY': ", 3, displayCstic.getAssignableValues().size());
		assertTrue("'CPQ_CPU' should be vissible: ", processorCstic.isVisible());
		assertFalse("'CPQ_RAM' should not be readdOnly: ", memoryCstic.isReadonly());

		// create prerequisite for rule to macth
		LOG.debug("Trying to fullfill condition for rule");
		// change so that rule applies
		config = changeValueAndUpdate(config, EXP_NUMBER, "500.0");
		final CsticModel expNoUsers = serviceConfigValueHelper.getCstic(config, EXP_NUMBER);
		LOG.debug("Entered value of 'EXP_NO_USERS': " + expNoUsers.getAssignedValues().get(0).getName());

		// now we expect rule to match
		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		processorCstic = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		memoryCstic = serviceConfigValueHelper.getCstic(config, CPQ_RAM);
		//check whether rule works properly
		assertEquals("There should be 2 assignable values for 'CPQ_DISPLAY': ", 2, displayCstic.getAssignableValues().size());
		assertFalse("'CPQ_CPU' should not be vissible: ", processorCstic.isVisible());
		assertTrue("'CPQ_RAM' should be readdOnly: ", memoryCstic.isReadonly());

	}

	/**
	 * Condition in rule checks for 1,500 (which is also displayed on UI, if you enter 1500.0), however the RAO contains
	 * 1500.0 Fix: use ValueFormatTranslator for numeric CStics to get formatted string to be set into RAO.
	 */
	@Test
	public void testNumericConditionOverThousand() throws RuleEngineServiceException, CommerceCartModificationException
	{
		prepareAndPublishRule("cpq_test_numeric_value_over_thousand_condition_rule");

		// no product in cart, no modification to default config expected
		//create default configuration 'CPQ_LAPTOP'
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		CsticModel processorCstic = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		CsticModel memoryCstic = serviceConfigValueHelper.getCstic(config, CPQ_RAM);
		assertEquals("There should be 3 assignable values for 'CPQ_DISPLAY': ", 3, displayCstic.getAssignableValues().size());
		assertTrue("'CPQ_CPU' should be vissible: ", processorCstic.isVisible());
		assertFalse("'CPQ_RAM' should not be readdOnly: ", memoryCstic.isReadonly());

		// create prerequisite for rule to macth
		LOG.debug("Trying to fullfill condition for rule");
		// change so that rule applies
		config = changeValueAndUpdate(config, EXP_NUMBER, "1500.0");
		final CsticModel expNoUsers = serviceConfigValueHelper.getCstic(config, EXP_NUMBER);
		LOG.debug("Entered value of 'EXP_NO_USERS': " + expNoUsers.getAssignedValues().get(0).getName());

		// now we expect rule to match
		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		processorCstic = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		memoryCstic = serviceConfigValueHelper.getCstic(config, CPQ_RAM);
		//check whether rule works properly
		assertEquals("There should be 2 assignable values for 'CPQ_DISPLAY': ", 2, displayCstic.getAssignableValues().size());
		assertFalse("'CPQ_CPU' should not be vissible: ", processorCstic.isVisible());
		assertTrue("'CPQ_RAM' should be readdOnly: ", memoryCstic.isReadonly());

	}



	@Test
	public void testSetDisplayOnlyHideAnotherWarnMessage() throws RuleEngineServiceException
	{
		prepareAndPublishRule("cpq_test_readonly_hide_action_rule");

		// check default
		LOG.debug("Create default MOCKED config of 'CPQ_LAPTOP'");
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel security = serviceConfigValueHelper.getCstic(config, CPQ_SECURITY);
		CsticModel processor = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		assertNoMessage(config);
		LOG.debug("Default: 'CPQ_SECURITY' is visible: " + security.isVisible());
		assertTrue("Default config: Estimated 'CPQ_SECURITY' Date should be visible", security.isVisible());
		LOG.debug("Default: 'CPQ_CPU' is readOnly: " + processor.isReadonly());
		assertFalse("Default config: Expected 'CPQ_CPU' cstic should be editable: " + processor.isReadonly(),
				processor.isReadonly());

		// change so that rule applies
		LOG.debug("Set value of 'CPQ_OS' to 'LINUSDEBIAN' so that rule applies");
		config = changeValueAndUpdate(config, CPQ_OS, LINUSDEBIAN);
		CsticModel operatingSystemCstic = serviceConfigValueHelper.getCstic(config, CPQ_OS);
		LOG.debug("Entered value of 'CPQ_OS': " + operatingSystemCstic.getAssignedValues().get(0).getName());

		security = serviceConfigValueHelper.getCstic(config, CPQ_SECURITY);
		processor = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		assertSingleMessage(config, ProductConfigMessageSeverity.WARNING, expectedMessage);
		LOG.debug("Rule matches: 'CPQ_SECURITY' is visible: " + security.isVisible());
		assertFalse("Rule matches: Estimated 'CPQ_SECURITY' Date should be hidden", security.isVisible());
		LOG.debug("Rule matches: 'CPQ_CPU' is readOnly: " + processor.isReadonly());
		assertTrue("Rule matches: 'CPQ_CPU' cstic should be readonly: " + processor.isReadonly(), processor.isReadonly());

		// change so that rules does not apply anymore
		LOG.debug("Set value of 'CPQ_OS' to 'MS10' so thatt rule does not aplly anymore");
		config = changeValueAndUpdate(config, CPQ_OS, MS10);
		operatingSystemCstic = serviceConfigValueHelper.getCstic(config, CPQ_OS);
		LOG.debug("Entered value of 'CPQ_OS': " + operatingSystemCstic.getAssignedValues().get(0).getName());

		security = serviceConfigValueHelper.getCstic(config, CPQ_SECURITY);
		processor = serviceConfigValueHelper.getCstic(config, CPQ_CPU);
		assertNoMessage(config);
		LOG.debug("Rule doesn't match anymore: 'CPQ_SECURITY' is visible: " + security.isVisible());
		assertTrue("Rule doesn't match anymore: Estimated 'CPQ_SECURITY' Date should be visible again", security.isVisible());
		LOG.debug("Rule doesn't match anymore: 'CPQ_CPU' is readOnly: " + processor.isReadonly());
		assertFalse("Rule doesn't match anymore: 'CPQ_CPU' cstic should be editable again: " + processor.isReadonly(),
				processor.isReadonly());
	}


	@Test
	public void testDoesNotHaveValueConditionAndDisplayMessageOnly()
			throws RuleEngineServiceException, CommerceCartModificationException
	{
		prepareAndPublishRule("cpq_test_doesNotHaveValue_condition_rule");

		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_17);
		assertSingleMessage(config, ProductConfigMessageSeverity.INFO, expectedMessage);

		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_15);
		assertSingleMessage(config, ProductConfigMessageSeverity.INFO, expectedMessage);

		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);
		assertNoMessage(config);
	}

	@Test
	public void testShowMessagesOnProductAndCsticLevels() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Show messages for configured CPQ_LAPTOP, if CPQ_DISPLAY == "13":
		// Warning message for product
		// Info message for cstic CPQ_OS
		// Warning message for cstic CPQ_SECURITY

		final Map<String, String> expectedMessagesByName = prepareAndPublishRule("cpq_test_message_for_cpq_laptop",
				"cpq_test_message_for_cpq_laptop_operating_system", "cpq_test_message_for_cpq_laptop_security");
		final String messageForProduct = expectedMessagesByName.get("cpq_test_message_for_cpq_laptop");
		final String messageForCsticOperatingSystem = expectedMessagesByName
				.get("cpq_test_message_for_cpq_laptop_operating_system");
		final String messageForCsticSecurity = expectedMessagesByName.get("cpq_test_message_for_cpq_laptop_security");

		// Default configuration
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		CsticModel operatingSystemCstic = serviceConfigValueHelper.getCstic(config, CPQ_OS);
		CsticModel securityCstic = serviceConfigValueHelper.getCstic(config, CPQ_SECURITY);
		assertNoMessage(config);
		assertNoMessageForCstic(operatingSystemCstic);
		assertNoMessageForCstic(securityCstic);

		// Set CPQ_DISPLAY = 13
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		operatingSystemCstic = serviceConfigValueHelper.getCstic(config, CPQ_OS);
		securityCstic = serviceConfigValueHelper.getCstic(config, CPQ_SECURITY);
		assertEquals("Expected 'CPQ_DISPLAY' value '13'", CPQ_DISPLAY_13, displayCstic.getAssignedValues().get(0).getName());
		assertSingleMessage(config, ProductConfigMessageSeverity.WARNING, messageForProduct);
		assertSingleMessageForCstic(operatingSystemCstic, ProductConfigMessageSeverity.INFO, messageForCsticOperatingSystem);
		assertSingleMessageForCstic(securityCstic, ProductConfigMessageSeverity.WARNING, messageForCsticSecurity);
	}

	@Test
	public void testRuleForBaseStore_testConfigureStore() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Show messages for configured CPQ_LAPTOP, if CPQ_DISPLAY == "13":
		// only for testConfigureStore base store

		prepareAndPublishRule("cpq_test_message_for_cpq_laptop_baseStore_testConfigureStore");

		// Default configuration
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertNoMessage(config);

		// Set CPQ_DISPLAY = 13
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected 'CPQ_DISPLAY' value '13'", CPQ_DISPLAY_13, displayCstic.getAssignedValues().get(0).getName());
		assertSingleMessage(config, ProductConfigMessageSeverity.INFO, expectedMessage);
	}

	@Test
	public void testRuleForBaseStore_allBaseStores() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Show messages for configured CPQ_LAPTOP, if CPQ_DISPLAY == "13":
		// all base stores

		prepareAndPublishRule("cpq_test_message_for_cpq_laptop_baseStore_all");

		// Default configuration
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertNoMessage(config);

		// Set CPQ_DISPLAY = 13
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected 'CPQ_DISPLAY' value '13'", CPQ_DISPLAY_13, displayCstic.getAssignedValues().get(0).getName());
		assertSingleMessage(config, ProductConfigMessageSeverity.INFO, expectedMessage);
	}

	@Test
	public void testRuleForBaseStore_dummyStore() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Show messages for configured CPQ_LAPTOP, if CPQ_DISPLAY == "13":
		// only for dummyStore base store
		// we use the testConfigureStore base store -> therefore expect no message

		prepareAndPublishRule("cpq_test_message_for_cpq_laptop_baseStore_dummyStore");

		// Default configuration
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertNoMessage(config);

		// Set CPQ_DISPLAY = 13
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected 'CPQ_DISPLAY' value '13'", CPQ_DISPLAY_13, displayCstic.getAssignedValues().get(0).getName());
		assertNoMessage(config);
	}

	@Test
	public void testSupportMultilevelConfiguration() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Support multilevel configuration CPQ_HOME_THEATER when CPQ_HT_SURROUND_MODE = SURROUND
		// Remove assignable value YM_NS_F160 from CPQ_HT_SPK_MODEL
		// Hide characteristic CPQ_HT_SPK_COLOR
		prepareAndPublishRule("cpq_test_support_multilevel_configuration_for_cpq_home_theater"); // Default configuration
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_HOME_THEATER);

		CsticModel surroundMode = serviceConfigValueHelper.getCstic(config, CPQ_HT_SURROUND_MODE);
		assertNotNull("surroundMode should be not null: ", surroundMode);

		//Set CPQ_HT_SURROUND_MODE = SURROUND
		// Remove assignable value YM_NS_F160 from CPQ_HT_SPK_MODEL in in 3-CPQ_FRONT_SPEAKERS._GEN subGroup
		// Hide characteristic CPQ_HT_SPK_COLOR in 3-CPQ_FRONT_SPEAKERS._GEN subGroup
		config = changeValueAndUpdate(config, CPQ_HT_SURROUND_MODE, SURROUND);

		//Get front speaker model
		CsticModel frontSpeakerModel = serviceConfigValueHelper.getCstic(config, "3", CPQ_HT_SPK_MODEL);
		assertNotNull("frontSpeakerModel should be not null: ", frontSpeakerModel);
		//Get front speaker model cstic values
		List<CsticValueModel> frontSpeakerModelValues = frontSpeakerModel.getAssignableValues();
		for (final CsticValueModel csticValue : frontSpeakerModelValues)
		{
			assertFalse("cstic value \'YM_NS_F160\' should not be included: ", csticValue.getName().equalsIgnoreCase(YM_NS_F160));
		}
		assertEquals("number of values in frontSpeakerModelValues should be 3: ", 3, frontSpeakerModelValues.size());

		//Get front speaker color
		CsticModel frontSpeakerColor = serviceConfigValueHelper.getCstic(config, "3", CPQ_HT_SPK_COLOR);
		assertFalse("frontSpeakerColor should not be visible: ", frontSpeakerColor.isVisible());

		//Get rear speaker model
		CsticModel rearSpeakerModel = serviceConfigValueHelper.getCstic(config, "4", CPQ_HT_SPK_MODEL);
		assertNotNull("rearSpeakerModel should not be null: ", rearSpeakerModel);
		//Get rear speaker model cstic values
		final List<CsticValueModel> rearSpeakerModelValues = rearSpeakerModel.getAssignableValues();
		assertEquals("number of values in rearSpeakerModelValues should be 4: ", 4, rearSpeakerModelValues.size());
		//Get rear speaker color
		CsticModel rearSpeakerColor = serviceConfigValueHelper.getCstic(config, "4", CPQ_HT_SPK_COLOR);
		assertTrue("rearSpeakerColor should be visible: ", rearSpeakerColor.isVisible());

		//Set CPQ_HT_SURROUND_MODE = STEREO
		//Display CPQ_HT_SPK_COLOR in 3-CPQ_FRONT_SPEAKERS._GEN subGroup
		//4-CPQ_REAR_SPEAKERS group does not exist
		config = ruleAwareService.createDefaultConfiguration(KB_CPQ_HOME_THEATER);
		surroundMode = serviceConfigValueHelper.getCstic(config, CPQ_HT_SURROUND_MODE);
		assertNotNull("surroundMode should be not null: ", surroundMode);
		config = changeValueAndUpdate(config, CPQ_HT_SURROUND_MODE, STEREO);

		//Get front speaker model
		frontSpeakerModel = serviceConfigValueHelper.getCstic(config, "3", CPQ_HT_SPK_MODEL);
		frontSpeakerModelValues = frontSpeakerModel.getAssignableValues();
		assertEquals("number of values in frontSpeakerModelValues should be 4: ", 4, frontSpeakerModelValues.size());
		//Get front speaker color
		frontSpeakerColor = serviceConfigValueHelper.getCstic(config, "3", CPQ_HT_SPK_COLOR);
		assertTrue("frontSpeakerColor should be visible: ", frontSpeakerColor.isVisible());

		//Get rear speaker model
		rearSpeakerModel = serviceConfigValueHelper.getCstic(config, "4", CPQ_HT_SPK_MODEL);
		assertNull("rearSpeakerModel should be null: ", rearSpeakerModel);

		//Get rear speaker color
		rearSpeakerColor = serviceConfigValueHelper.getCstic(config, "4", CPQ_HT_SPK_COLOR);
		assertNull("rearSpeakerColor should be null: ", rearSpeakerColor);
	}

	/**
	 * see also TIGER-2285
	 */
	@Test
	public void testDefaultMessageNotShownWhenDefaultValueModified()
			throws CommerceCartModificationException, RuleEngineServiceException
	{
		prepareAndPublishRule("cpq_test_display_message_for_default_value");
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertNoMessage(config);

		// Set CPQ_DISPLAY = 13
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected 'CPQ_DISPLAY' value '13': ", CPQ_DISPLAY_13, displayCstic.getAssignedValues().get(0).getName());
		assertSingleMessage(config, ProductConfigMessageSeverity.WARNING, expectedMessage);

		prepareAndPublishRule("cpq_test_change_default_value");
		config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("'CPQ_DISPLAY' should changed from '13' -> '17': ", CPQ_DISPLAY_17,
				displayCstic.getAssignedValues().get(0).getName());
		assertNoMessage(config);
	}

	/**
	 * Rules should be also executed, when configuration is created from external source. This happens for exmaple, when
	 * a cart is restored from cookie and users clicks on the change config link afterwards<br>
	 * see also TIGER-2332
	 *
	 * @throws CommerceCartModificationException
	 */
	@Test
	public void testRulesAreAppliedOnCreateFromExternal() throws CommerceCartModificationException
	{
		// prepare
		prepareAndPublishRule("cpq_test_display_message_for_default_value");
		final String expectedMessage = this.expectedMessage;
		ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);
		CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertNoMessage(config);

		// Set CPQ_DISPLAY = 13
		config = changeValueAndUpdate(config, CPQ_DISPLAY, CPQ_DISPLAY_13);
		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Expected 'CPQ_DISPLAY' value '13': ", CPQ_DISPLAY_13, displayCstic.getAssignedValues().get(0).getName());
		assertSingleMessage(config, ProductConfigMessageSeverity.WARNING, expectedMessage);
		final String extConfig = ruleAwareService.retrieveExternalConfiguration(config.getId());

		// release session
		ruleAwareService.releaseSession(config.getId());
		config = new ConfigModelImpl();

		// activate another rule
		prepareAndPublishRule("cpq_test_change_default_value");

		// get from external
		final KBKey kbKey = new KBKeyImpl(KB_CPQ_LAPTOP.getProductCode());
		final String newConfigId = ruleAwareService.createConfigurationFromExternal(kbKey, extConfig).getId();

		// read config
		config.setId(newConfigId);
		config.setKbId(PRODUCT_CODE_CPQ_LAPTOP);
		config = ruleAwareService.retrieveConfigurationModel(config.getId());

		// check
		displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		assertEquals("Rules changing default values, should not be executed on create from external", CPQ_DISPLAY_13,
				displayCstic.getAssignedValues().get(0).getName());
		assertSingleMessage(config, ProductConfigMessageSeverity.WARNING, expectedMessage);
	}
}
