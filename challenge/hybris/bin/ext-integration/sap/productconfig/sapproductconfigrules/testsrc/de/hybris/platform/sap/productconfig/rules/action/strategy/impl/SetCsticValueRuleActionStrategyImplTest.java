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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.SetCsticValueRAO;
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
public class SetCsticValueRuleActionStrategyImplTest
{

	public static final String VALUE_NAME_TO_SET = "Value_Name_To_Set";
	public static final String CSTIC_NAME = "Cstic_Name";

	private SetCsticValueRuleActionStrategyImpl classUnderTest;

	private SetCsticValueRAO action;
	private ConfigModel model;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new SetCsticValueRuleActionStrategyImpl();

		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);
		assertNotNull(classUnderTest.getConfigModelFactory());
		action = new SetCsticValueRAO();
		final CsticValueRAO valueNameToSet = ConfigurationRulesTestData.createCsticValueRAO(VALUE_NAME_TO_SET);
		action.setValueNameToSet(valueNameToSet);
		ConfigurationRulesTestData.setCsticAsActionTarget(action, CSTIC_NAME);

		model = ConfigurationRulesTestData.createEmptyConfigModel();
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setName(CSTIC_NAME);
		cstic.setMultivalued(false);
		model.getRootInstance().addCstic(cstic);

	}


	@Test
	public void testApplySingleValue()
	{
		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertTrue(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		final CsticValueModel value = cstic.getAssignedValues().get(0);
		assertEquals(VALUE_NAME_TO_SET, value.getName());
	}

	@Test
	public void testApplySingleValue_noChange()
	{
		model.getRootInstance().getCstics().get(0).setSingleValue(VALUE_NAME_TO_SET);
		model.getRootInstance().getCstics().get(0).setChangedByFrontend(false);
		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		final CsticValueModel value = cstic.getAssignedValues().get(0);
		assertEquals(VALUE_NAME_TO_SET, value.getName());
	}


	@Test
	public void testApplySingleValueNumeric()
	{
		model.getRootInstance().getCstics().get(0).setValueType(CsticModel.TYPE_FLOAT);
		action.getValueNameToSet().setCsticValueName("1,500");
		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertTrue(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		final CsticValueModel value = cstic.getAssignedValues().get(0);
		assertEquals("1500.0", value.getName());
	}

	@Test
	public void testApplySingleValueNumericEmpty()
	{
		model.getRootInstance().getCstics().get(0).setSingleValue("123");
		model.getRootInstance().getCstics().get(0).setChangedByFrontend(false);
		model.getRootInstance().getCstics().get(0).setValueType(CsticModel.TYPE_FLOAT);
		action.getValueNameToSet().setCsticValueName("");
		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertTrue(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		assertEquals(0, cstic.getAssignedValues().size());
	}

	@Test
	public void testApplySingleValueNumericNotParseable()
	{
		model.getRootInstance().getCstics().get(0).setValueType(CsticModel.TYPE_FLOAT);
		action.getValueNameToSet().setCsticValueName("aaaa");
		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		assertEquals(0, cstic.getAssignedValues().size());
	}

	@Test
	public void testApplyMultiValue()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.setMultivalued(true);
		adjustCstic.addValue("AAA_OldValue");
		adjustCstic.setChangedByFrontend(false);


		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertTrue(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		assertEquals(2, cstic.getAssignedValues().size());
		final CsticValueModel value = cstic.getAssignedValues().get(1);
		assertEquals(VALUE_NAME_TO_SET, value.getName());
	}

	@Test
	public void testApplyMultiValue_noChange()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.setMultivalued(true);
		adjustCstic.addValue("AAA_OldValue");
		adjustCstic.addValue(VALUE_NAME_TO_SET);
		adjustCstic.setChangedByFrontend(false);

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);
		assertEquals(2, cstic.getAssignedValues().size());
		final CsticValueModel value = cstic.getAssignedValues().get(1);
		assertEquals(VALUE_NAME_TO_SET, value.getName());
	}

	@Test
	public void testApplyCsticNotExists()
	{
		final CsticRAO adjustAppliedToObject = (CsticRAO) action.getAppliedToObject();
		adjustAppliedToObject.setCsticName("Another_Cstic_Name");

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel anotherCstic = model.getRootInstance().getCstic("Another_Cstic_Name");
		assertNull(anotherCstic);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		assertEquals(0, cstic.getAssignedValues().size());
	}

	@Test
	public void testApplyCsticValueNotInDomain()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.setConstrained(true);
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName("NAME1");
		assignableValues.add(value1);
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName("NAME2");
		assignableValues.add(value2);
		adjustCstic.setAssignableValues(assignableValues);

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		assertEquals(0, cstic.getAssignedValues().size());
	}

	@Test
	public void testApplyCsticValueInDomain()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.setConstrained(true);
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		final CsticValueModel value1 = new CsticValueModelImpl();
		value1.setName("NAME1");
		assignableValues.add(value1);
		final CsticValueModel value2 = new CsticValueModelImpl();
		value2.setName("NAME2");
		assignableValues.add(value2);
		final CsticValueModel value3 = new CsticValueModelImpl();
		value3.setName(VALUE_NAME_TO_SET);
		assignableValues.add(value3);
		adjustCstic.setAssignableValues(assignableValues);

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertTrue(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		final CsticValueModel value = cstic.getAssignedValues().get(0);
		assertEquals(VALUE_NAME_TO_SET, value.getName());
	}

	@Test
	public void testApplyCsticIsReadOnly()
	{
		final CsticModel adjustCstic = model.getRootInstance().getCstic(CSTIC_NAME);
		adjustCstic.setReadonly(true);

		final boolean modelAdjusted = classUnderTest.apply(model, action);

		assertFalse(modelAdjusted);

		final CsticModel cstic = model.getRootInstance().getCstic(CSTIC_NAME);
		assertNotNull(cstic);

		assertEquals(0, cstic.getAssignedValues().size());
	}
}
