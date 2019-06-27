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
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleDisplayMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.ProductConfigDisplayMessageRAO;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayMessageRAOActionTest extends AbstractProductConfigRAOActionTest
{
	private DisplayMessageRAOAction action;
	private ProductConfigProcessStepRAO processStep;

	private final Map<String, Object> parameters = new HashMap<>();

	@Before
	public void setUp()
	{
		action = new DisplayMessageRAOAction();
		action.setConfigurationService(getConfigurationService());

		final Configuration configuration = mock(Configuration.class);
		when(Boolean.valueOf(configuration.getBoolean("droolsruleengineservices.validate.droolsrule.rulecode", true)))
				.thenReturn(Boolean.TRUE);
		when(getConfigurationService().getConfiguration()).thenReturn(configuration);

		processStep = new ProductConfigProcessStepRAO();
		processStep.setProcessStep(ProcessStep.RETRIEVE_CONFIGURATION);
		when(getContext().getValue(ProductConfigProcessStepRAO.class)).thenReturn(processStep);

		parameters.put(DisplayMessageRAOAction.CSTIC_NAME, "CSTIC_NAME");
		parameters.put(DisplayMessageRAOAction.MESSAGE, "Message");
		parameters.put(DisplayMessageRAOAction.MESSAGE_SEVERITY, ProductConfigRuleDisplayMessageSeverity.WARNING);
		when(getContext().getParameters()).thenReturn(parameters);
	}

	@Test
	public void testDisplayMessageForCstic()
	{
		final ProductConfigDisplayMessageRAO productConfigDisplayMessageRAO = performActionAndRetrieveMessageRAO();

		final CsticRAO targetCstic = (CsticRAO) productConfigDisplayMessageRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("Message", productConfigDisplayMessageRAO.getMessage());
		assertEquals(ProductConfigRuleMessageSeverity.WARNING, productConfigDisplayMessageRAO.getMessageSeverity());
		assertEquals("", productConfigDisplayMessageRAO.getValueNameForMessage().getCsticValueName());
	}

	@Test
	public void testDisplayMessageForCsticValue()
	{
		parameters.put(DisplayMessageRAOAction.CSTIC_VALUE, "CSTIC_VALUE");
		final ProductConfigDisplayMessageRAO productConfigDisplayMessageRAO = performActionAndRetrieveMessageRAO();

		final CsticRAO targetCstic = (CsticRAO) productConfigDisplayMessageRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("Message", productConfigDisplayMessageRAO.getMessage());
		assertEquals(ProductConfigRuleMessageSeverity.WARNING, productConfigDisplayMessageRAO.getMessageSeverity());
		assertEquals("CSTIC_VALUE", productConfigDisplayMessageRAO.getValueNameForMessage().getCsticValueName());
	}

	@Test
	public void testDisplayMessage_anotherProcessStep()
	{
		processStep.setProcessStep(ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		final ProductConfigDisplayMessageRAO productConfigDisplayMessageRAO = performActionAndRetrieveMessageRAO();
	}

	@Test
	public void testDisplayMessageForCstic_prepareLog()
	{
		final String logMessage = action.prepareActionLogText(getContext(), getContext().getParameters());
		assertNotNull(logMessage);
		assertTrue(logMessage.contains("\"Message\""));
		assertTrue(logMessage.contains("WARNING"));
		assertTrue(logMessage.contains("CSTIC_NAME"));
		assertFalse(logMessage.contains("CSTIC_VALUE"));
	}

	@Test
	public void testDisplayMessageForCsticValue_prepareLog()
	{
		parameters.put(DisplayMessageRAOAction.CSTIC_VALUE, "CSTIC_VALUE");
		final String logMessage = action.prepareActionLogText(getContext(), getContext().getParameters());
		assertNotNull(logMessage);
		assertTrue(logMessage.contains("\"Message\""));
		assertTrue(logMessage.contains("WARNING"));
		assertTrue(logMessage.contains("CSTIC_NAME"));
		assertTrue(logMessage.contains("CSTIC_VALUE"));
	}

	@Test
	public void testDisplayMessageInvalidProcessStep()
	{
		processStep.setProcessStep(null);
		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}

	@Test
	public void testConvertMessageSeverityWarning()
	{
		assertEquals(ProductConfigRuleMessageSeverity.WARNING,
				action.convertMessageSeverity(ProductConfigRuleDisplayMessageSeverity.WARNING));
	}

	@Test
	public void testConvertMessageSeverityInfo()
	{
		assertEquals(ProductConfigRuleMessageSeverity.INFO,
				action.convertMessageSeverity(ProductConfigRuleDisplayMessageSeverity.INFO));
	}

	@Test
	public void testConvertMessageSeverityNull()
	{
		assertEquals(ProductConfigRuleMessageSeverity.INFO, action.convertMessageSeverity(null));
	}

	protected ProductConfigDisplayMessageRAO performActionAndRetrieveMessageRAO()
	{
		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(1, actionList.size());

		final ProductConfigDisplayMessageRAO productConfigDisplayMessageRAO = (ProductConfigDisplayMessageRAO) actionList.iterator()
				.next();

		assertNotNull(productConfigDisplayMessageRAO);
		return productConfigDisplayMessageRAO;
	}

}
