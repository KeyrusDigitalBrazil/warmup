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
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.cps.rao.action.ProductConfigPromoMessageRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl.AbstractProductConfigRAOActionTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayPromoMessageRAOActionTest extends AbstractProductConfigRAOActionTest
{
	protected static final String CSTIC_NAME = "cstic";
	protected static final String CSTIC_VALUE = "cstic_value";
	protected static final String MESSAGE = "message";

	private DisplayPromoMessageRAOAction action;
	private ProductConfigProcessStepRAO processStep;

	private final Map<String, Object> parameters = new HashMap<>();

	@Before
	public void setUp()
	{
		action = new DisplayPromoMessageRAOAction();
		action.setConfigurationService(getConfigurationService());

		final Configuration configuration = mock(Configuration.class);
		when(Boolean.valueOf(configuration.getBoolean("droolsruleengineservices.validate.droolsrule.rulecode", true)))
				.thenReturn(Boolean.TRUE);
		when(getConfigurationService().getConfiguration()).thenReturn(configuration);

		processStep = new ProductConfigProcessStepRAO();
		processStep.setProcessStep(ProcessStep.RETRIEVE_CONFIGURATION);
		when(getContext().getValue(ProductConfigProcessStepRAO.class)).thenReturn(processStep);

		parameters.put(CSTIC_NAME, "CSTIC_NAME");
		parameters.put(MESSAGE, "Message");
		parameters.put(DisplayPromoMessageRAOAction.EXTENDED_MESSAGE, "Extended Message");

		when(getContext().getParameters()).thenReturn(parameters);
	}

	@Test
	public void testDisplayPromoMessageForCstic()
	{
		final ProductConfigPromoMessageRAO productConfigDisplayPromoMessageRAO = performActionAndRetrieveMessageRAO();

		final CsticRAO targetCstic = (CsticRAO) productConfigDisplayPromoMessageRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("Message", productConfigDisplayPromoMessageRAO.getMessage());
		assertEquals("Extended Message", productConfigDisplayPromoMessageRAO.getExtendedMessage());
		assertEquals("", productConfigDisplayPromoMessageRAO.getValueName().getCsticValueName());
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED, productConfigDisplayPromoMessageRAO.getPromoType());
	}

	@Test
	public void testDisplayMessageForCsticValue()
	{
		parameters.put(CSTIC_VALUE, "CSTIC_VALUE");
		final ProductConfigPromoMessageRAO productConfigDisplayPromoMessageRAO = performActionAndRetrieveMessageRAO();
		final CsticRAO targetCstic = (CsticRAO) productConfigDisplayPromoMessageRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("Message", productConfigDisplayPromoMessageRAO.getMessage());
		assertEquals("CSTIC_VALUE", productConfigDisplayPromoMessageRAO.getValueName().getCsticValueName());
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED, productConfigDisplayPromoMessageRAO.getPromoType());
	}

	@Test
	public void testDisplayPromoMessageAnotherProcessStep()
	{
		processStep.setProcessStep(ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		action.performAction(getContext());
		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}

	@Test
	public void testDisplayPromoMessageForCsticPrepareLog()
	{
		final String logMessage = action.prepareActionLogText(getContext(), getContext().getParameters());
		assertNotNull(logMessage);
		assertTrue(logMessage.contains("\"Message\""));
		assertTrue(logMessage.contains("\"Extended Message\""));
		assertTrue(logMessage.contains("CSTIC_NAME"));
		assertFalse(logMessage.contains("CSTIC_VALUE"));
		assertTrue(logMessage.contains(ProductConfigMessagePromoType.PROMO_APPLIED.toString()));
	}

	@Test
	public void testDisplayPromoMessageForCsticValuePrepareLog()
	{
		parameters.put(CSTIC_VALUE, "CSTIC_VALUE");
		final String logMessage = action.prepareActionLogText(getContext(), getContext().getParameters());
		assertNotNull(logMessage);
		assertTrue(logMessage.contains("\"Message\""));
		assertTrue(logMessage.contains("\"Extended Message\""));
		assertTrue(logMessage.contains("CSTIC_NAME"));
		assertTrue(logMessage.contains("CSTIC_VALUE"));
		assertTrue(logMessage.contains(ProductConfigMessagePromoType.PROMO_APPLIED.toString()));
	}

	@Test
	public void testDisplayPromoMessageInvalidProcessStep()
	{
		processStep.setProcessStep(null);
		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}

	@Test
	public void testDisplayPromoMessageGetgetPromoType()
	{
		assertEquals(ProductConfigMessagePromoType.PROMO_APPLIED, action.getPromoType());
	}

	protected ProductConfigPromoMessageRAO performActionAndRetrieveMessageRAO()
	{
		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(1, actionList.size());

		final ProductConfigPromoMessageRAO productConfigDisplayPromoMessageRAO = (ProductConfigPromoMessageRAO) actionList
				.iterator().next();

		assertNotNull(productConfigDisplayPromoMessageRAO);
		return productConfigDisplayPromoMessageRAO;
	}

}
