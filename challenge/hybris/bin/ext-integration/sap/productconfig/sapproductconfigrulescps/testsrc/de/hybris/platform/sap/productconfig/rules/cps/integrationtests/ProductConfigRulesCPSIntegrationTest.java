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
package de.hybris.platform.sap.productconfig.rules.cps.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.sap.productconfig.rules.integrationtests.ProductConfigRulesTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest
@SuppressWarnings("javadoc")
public class ProductConfigRulesCPSIntegrationTest extends ProductConfigRulesTest
{
	private final Logger LOG = Logger.getLogger(ProductConfigRulesCPSIntegrationTest.class.getName());

	@Override
	protected void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCsv("/impex/sapproductconfigrulescps_definitions.impex", "windows-1252");
	}

	@Test
	public void testShowMessagesOnProductAndCsticLevels() throws CommerceCartModificationException, RuleEngineServiceException
	{
		// Show promo messages for configured CPQ_LAPTOP cstic CPQ_OS and cstic / value CPQ_DISPLAY / CPQ_DISPLAY_17

		prepareAndPublishRule("cpq_test_promo_message_for_cpq_laptop_operating_system",
				"cpq_test_promo_message_for_cpq_laptop_display_17");

		final ConfigModel config = ruleAwareService.createDefaultConfiguration(KB_CPQ_LAPTOP);

		final CsticModel operatingSystemCstic = serviceConfigValueHelper.getCstic(config, CPQ_OS);
		final CsticModel displayCstic = serviceConfigValueHelper.getCstic(config, CPQ_DISPLAY);
		final CsticValueModel displayCsticValue17 = serviceConfigValueHelper.getCsticValue(config, CPQ_DISPLAY, CPQ_DISPLAY_17);
		final CsticValueModel displayCsticValue13 = serviceConfigValueHelper.getCsticValue(config, CPQ_DISPLAY, CPQ_DISPLAY_13);

		assertNoMessage(config);
		assertEquals(2, operatingSystemCstic.getMessages().size());
		assertEquals(0, displayCstic.getMessages().size());
		assertEquals(2, displayCsticValue17.getMessages().size());
		assertEquals(0, displayCsticValue13.getMessages().size());

		final Date endDate = prepareDate("2099/12/31");

		final ProductConfigMessageBuilder builder = new ProductConfigMessageBuilder();
		initBuilderWithDefaults(builder);
		builder.appendBasicFields("promo message", "cpq_test_promo_message_for_cpq_laptop_operating_system",
				ProductConfigMessageSeverity.INFO);
		builder.appendPromotionFields(ProductConfigMessagePromoType.PROMO_APPLIED, "promo message extended", endDate);
		assertContainsMessage(CPQ_OS, operatingSystemCstic.getMessages(), builder.build());

		initBuilderWithDefaults(builder);
		builder.appendBasicFields("opportunity message", "cpq_test_promo_message_for_cpq_laptop_operating_system",
				ProductConfigMessageSeverity.INFO);
		builder.appendPromotionFields(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, "opportunity message extended", endDate);
		assertContainsMessage(CPQ_OS, operatingSystemCstic.getMessages(), builder.build());

		initBuilderWithDefaults(builder);
		builder.appendBasicFields("value promo message", "cpq_test_promo_message_for_cpq_laptop_display_17",
				ProductConfigMessageSeverity.INFO);
		builder.appendPromotionFields(ProductConfigMessagePromoType.PROMO_APPLIED, "value promo message extended", endDate);
		assertContainsMessage(CPQ_DISPLAY_17, displayCsticValue17.getMessages(), builder.build());

		initBuilderWithDefaults(builder);
		builder.appendBasicFields("value opportunity message", "cpq_test_promo_message_for_cpq_laptop_display_17",
				ProductConfigMessageSeverity.INFO);
		builder.appendPromotionFields(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, "value opportunity message extended",
				endDate);
		assertContainsMessage(CPQ_DISPLAY_17, displayCsticValue17.getMessages(), builder.build());

	}

	protected void initBuilderWithDefaults(final ProductConfigMessageBuilder builder)
	{
		builder.reset();
		builder.appendSourceAndType(ProductConfigMessageSource.RULE, ProductConfigMessageSourceSubType.DISPLAY_PROMO_MESSAGE);
	}

	protected void assertContainsMessage(final String obj, final Set<ProductConfigMessage> productConfigMessages,
			final ProductConfigMessage expectedProductConfigMessage)
	{
		for (final ProductConfigMessage productConfigMessage : productConfigMessages)
		{
			if (verifyMessage(productConfigMessage, expectedProductConfigMessage))
			{
				return;
			}
		}

		final String error = obj + " does not contain expected message with: " + "key = " + expectedProductConfigMessage.getKey()
				+ ", message = " + expectedProductConfigMessage.getMessage() + ", extendedMessage = "
				+ expectedProductConfigMessage.getExtendedMessage() + ", source = " + expectedProductConfigMessage.getSource()
				+ ", sourceSubType = " + expectedProductConfigMessage.getSourceSubType() + " promoType = "
				+ expectedProductConfigMessage.getPromoType() + ", severity = " + expectedProductConfigMessage.getSeverity()
				+ ", endDate = " + expectedProductConfigMessage.getEndDate();

		assertTrue(error, false);
	}

	protected boolean verifyMessage(final ProductConfigMessage productConfigMessage,
			final ProductConfigMessage expectedProductConfigMessage)
	{
		if (!expectedProductConfigMessage.getKey().equals(productConfigMessage.getKey())
				|| !expectedProductConfigMessage.getMessage().equals(productConfigMessage.getMessage())
				|| !expectedProductConfigMessage.getExtendedMessage().equals(productConfigMessage.getExtendedMessage())
				|| !expectedProductConfigMessage.getSource().equals(productConfigMessage.getSource())
				|| !expectedProductConfigMessage.getSourceSubType().equals(productConfigMessage.getSourceSubType())
				|| !expectedProductConfigMessage.getPromoType().equals(productConfigMessage.getPromoType())
				|| !expectedProductConfigMessage.getSeverity().equals(productConfigMessage.getSeverity())
				|| !expectedProductConfigMessage.getEndDate().equals(productConfigMessage.getEndDate()))
		{
			return false;
		}
		return true;
	}

}
