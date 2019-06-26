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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.SetCsticValueRAO;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SetCsticValueRAOActionTest extends AbstractProductConfigRAOActionTest
{
	private SetCsticValueRAOAction action;
	private ProductConfigProcessStepRAO processStep;
	private Map<String, Object> parameters;

	@Before
	public void setUp()
	{
		action = new SetCsticValueRAOAction();
		action.setConfigurationService(getConfigurationService());

		final Configuration configuration = mock(Configuration.class);
		when(Boolean.valueOf(configuration.getBoolean("droolsruleengineservices.validate.droolsrule.rulecode", true)))
				.thenReturn(Boolean.TRUE);
		when(getConfigurationService().getConfiguration()).thenReturn(configuration);
		processStep = new ProductConfigProcessStepRAO();
		processStep.setProcessStep(ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		when(getContext().getValue(ProductConfigProcessStepRAO.class)).thenReturn(processStep);

		parameters = new HashMap<>();
		parameters.put(SetCsticValueRAOAction.CSTIC_NAME, "CSTIC_NAME");
		parameters.put(SetCsticValueRAOAction.CSTIC_VALUE, "CSTIC_VALUE");
		when(getContext().getParameters()).thenReturn(parameters);
	}

	@Test
	public void testSetCsicValue()
	{
		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(1, actionList.size());

		final SetCsticValueRAO setCsticValueRAO = (SetCsticValueRAO) actionList.iterator().next();

		assertNotNull(setCsticValueRAO);

		final CsticRAO targetCstic = (CsticRAO) setCsticValueRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("CSTIC_VALUE", setCsticValueRAO.getValueNameToSet().getCsticValueName());
	}

	@Test
	public void testSetCsicValueNull()
	{
		parameters.put(SetCsticValueRAOAction.CSTIC_VALUE, null);

		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(1, actionList.size());

		final SetCsticValueRAO setCsticValueRAO = (SetCsticValueRAO) actionList.iterator().next();

		assertNotNull(setCsticValueRAO);
		assertTrue(getResult().getActions().contains(setCsticValueRAO));

		final CsticRAO targetCstic = (CsticRAO) setCsticValueRAO.getAppliedToObject();
		assertEquals("CSTIC_NAME", targetCstic.getCsticName());
		assertEquals("", setCsticValueRAO.getValueNameToSet().getCsticValueName());
	}

	@Test
	public void testSetCsicValue_wrongProcessStep()
	{
		processStep.setProcessStep(ProcessStep.RETRIEVE_CONFIGURATION);

		action.performAction(getContext());

		assertNotNull(getResult().getActions());
		final LinkedHashSet<AbstractRuleActionRAO> actionList = getResult().getActions();
		assertEquals(0, actionList.size());
	}
}
