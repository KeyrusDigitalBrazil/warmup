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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class RemoveAssignableValueRuleActionStrategyImplTest
{
	public static final String ASSIGNABLE_VALUE_NAME_TO_REMOVE = "Value_Name_To_Remove";
	public static final String CSTIC_NAME = "Cstic_Name";
	public static final String ASSIGNABLE_VALUE_1 = "ASSIGNABLE_VALUE_1";
	public static final String ASSIGNABLE_VALUE_2 = "ASSIGNABLE_VALUE_2";

	private RemoveAssignableValueRuleActionStrategyImpl classUnderTest;

	private RemoveAssignableValueRAO action;
	private ConfigModel model;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new RemoveAssignableValueRuleActionStrategyImpl();

		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);

		action = new RemoveAssignableValueRAO();
		final CsticValueRAO valueToRemveRao = ConfigurationRulesTestData.createCsticValueRAO(ASSIGNABLE_VALUE_NAME_TO_REMOVE);
		action.setValueNameToRemoveFromAssignable(valueToRemveRao);
		ConfigurationRulesTestData.setCsticAsActionTarget(action, CSTIC_NAME);

		model = ConfigurationRulesTestData.createEmptyConfigModel();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(CSTIC_NAME);
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		final CsticValueModel assignableValue1 = new CsticValueModelImpl();
		assignableValue1.setName(ASSIGNABLE_VALUE_1);
		assignableValues.add(assignableValue1);
		final CsticValueModel assignableValue2 = new CsticValueModelImpl();
		assignableValue2.setName(ASSIGNABLE_VALUE_2);
		assignableValues.add(assignableValue2);
		final CsticValueModel assignableValue3 = new CsticValueModelImpl();
		assignableValue3.setName(ASSIGNABLE_VALUE_NAME_TO_REMOVE);
		assignableValues.add(assignableValue3);
		cstic.setAssignableValues(assignableValues);
		model.getRootInstance().addCstic(cstic);

	}

	@Test
	public void testApply()
	{
		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		assertEquals(2, cstic.getAssignableValues().size());

		final CsticValueModel value1 = cstic.getAssignableValues().get(0);
		final CsticValueModel value2 = cstic.getAssignableValues().get(1);
		assertTrue(!value1.getName().equals(ASSIGNABLE_VALUE_NAME_TO_REMOVE)
				&& !value2.getName().equals(ASSIGNABLE_VALUE_NAME_TO_REMOVE));
	}

	@Test
	public void testApplyValueNotInAssignable()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.removeAssignableValue(ASSIGNABLE_VALUE_NAME_TO_REMOVE);

		assertEquals(2, adjustCstic.getAssignableValues().size());

		final CsticValueModel value1 = adjustCstic.getAssignableValues().get(0);
		final CsticValueModel value2 = adjustCstic.getAssignableValues().get(1);
		assertTrue(!value1.getName().equals(ASSIGNABLE_VALUE_NAME_TO_REMOVE)
				&& !value2.getName().equals(ASSIGNABLE_VALUE_NAME_TO_REMOVE));

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		assertEquals(2, cstic.getAssignableValues().size());
	}

	@Test
	public void testApplyNotPossibleToRemoveAssignedValue()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.setSingleValue(ASSIGNABLE_VALUE_NAME_TO_REMOVE);

		assertEquals(3, adjustCstic.getAssignableValues().size());

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		assertEquals(3, cstic.getAssignableValues().size());

		final CsticValueModel value = cstic.getAssignableValues().get(2);
		assertEquals(ASSIGNABLE_VALUE_NAME_TO_REMOVE, value.getName());
	}

	@Test
	public void testApplyWrondCstic()
	{
		ConfigurationRulesTestData.setCsticAsActionTarget(action, "WRONG_CSTIC_NAME");
		final boolean modelAdjusted = classUnderTest.apply(model, action);
		assertFalse(modelAdjusted);
	}
}
