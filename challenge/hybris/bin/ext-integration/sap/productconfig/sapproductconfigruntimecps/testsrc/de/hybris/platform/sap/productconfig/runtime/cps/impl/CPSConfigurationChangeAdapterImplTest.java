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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@UnitTest
public class CPSConfigurationChangeAdapterImplTest
{
	private static final String CSTIC_VALUE = "csticValue";
	private static final String INSTANCE_ID = "instanceId";
	private static final String CSTIC_NAME = "csticName";
	CPSConfigurationChangeAdapterImpl classUnderTest = new CPSConfigurationChangeAdapterImpl();

	protected CsticModel createCsticString(final String name)
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setName(name);
		csticModel.setAssignableValues(createValueList(0));
		csticModel.setMultivalued(false);
		csticModel.setReadonly(false);
		csticModel.setValueType(CsticModel.TYPE_STRING);
		return csticModel;
	}

	protected CsticModel createCsticNumeric(final String name, final int valueType)
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setName(name);
		csticModel.setAssignableValues(createValueList(0));
		csticModel.setMultivalued(false);
		csticModel.setReadonly(false);
		csticModel.setValueType(valueType);
		return csticModel;
	}

	protected InstanceModel createInstance()
	{
		final InstanceModel instModel = new InstanceModelImpl();
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createC1());
		cstics.add(createC2());
		cstics.add(createC3());
		cstics.add(createC4());
		instModel.setCstics(cstics);
		return instModel;
	}

	protected ConfigModel createConfigModel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setRootInstance(createInstance());
		configModel.setId("S1");
		return configModel;
	}


	protected CsticModel createC1()
	{
		final CsticModel c1 = createCsticString("C1");
		c1.setMultivalued(false);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		c1.setAssignedValuesWithoutCheckForChange(assignedValues);
		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createValue("V1"));
		assignableValues.add(createValue("V2"));
		assignableValues.add(createValue("V3"));
		assignableValues.add(createValue("V4"));
		assignableValues.add(createValue("V5"));
		c1.setAssignableValues(assignableValues);
		return c1;
	}

	protected CsticModel createC2()
	{
		final CsticModel c2 = createCsticString("C2");
		c2.setMultivalued(false);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		c2.setAssignedValuesWithoutCheckForChange(assignedValues);
		final List<CsticValueModel> assignableValues = new ArrayList<>();
		c2.setAssignableValues(assignableValues);
		return c2;
	}


	protected CsticModel createC3()
	{
		final CsticModel c3 = createCsticString("C3");
		c3.setMultivalued(true);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(createValue("V2"));
		assignedValues.add(createValue("V3"));
		c3.setAssignedValuesWithoutCheckForChange(assignedValues);
		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createValue("V1"));
		assignableValues.add(createValue("V2"));
		assignableValues.add(createValue("V3"));
		assignableValues.add(createValue("V4"));
		c3.setAssignableValues(assignableValues);
		return c3;
	}

	protected CsticModel createC4()
	{
		final CsticModel c4 = createCsticString("C4");
		c4.setMultivalued(false);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(createValue("V2"));
		c4.setAssignedValuesWithoutCheckForChange(assignedValues);
		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createValue("V1"));
		assignableValues.add(createValue("V2"));
		assignableValues.add(createValue("V3"));
		c4.setAssignableValues(assignableValues);
		return c4;
	}

	protected List<CsticValueModel> createValueList(final int size)
	{
		final List<CsticValueModel> values = new ArrayList<>(size);
		for (int ii = 0; ii < size; ii++)
		{
			values.add(createValue(String.valueOf(ii)));
		}
		return values;
	}

	protected CsticValueModel createValue(final String name)
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName(name);
		value.setAuthor(CsticValueModel.AUTHOR_USER);
		value.setNumeric(true);
		return value;
	}

	@Test
	public void testCollectChangedValuesStringSV()
	{
		final CsticModel csticModel = createCsticString("C1");
		final List<CsticValueModel> assignedValues = createValueList(1);
		final List<CsticValueModel> assignableValues = createValueList(5);
		csticModel.setAssignedValues(assignedValues);
		csticModel.setAssignableValues(assignableValues);
		final List<CPSValue> ceValues = classUnderTest.collectChangedValues(csticModel);
		assertEquals(1, ceValues.size());
		assertTrue(ceValues.get(0).isSelected());
		assertEquals("0", ceValues.get(0).getValue());
	}

	@Test
	public void testCollectChangedValuesStringSV_NoAssignedValues()
	{
		final CsticModel csticModel = createCsticString("C1");
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final List<CsticValueModel> assignableValues = createValueList(5);
		csticModel.setAssignedValues(assignedValues);
		csticModel.setAssignableValues(assignableValues);
		final List<CPSValue> ceValues = classUnderTest.collectChangedValues(csticModel);
		assertEquals(5, ceValues.size());
		assertFalse(ceValues.get(0).isSelected());
	}

	@Test
	public void testCollectChangedValuesStringMV_2assigned()
	{
		final CsticModel csticModel = createCsticString("C1");
		csticModel.setMultivalued(true);
		final List<CsticValueModel> assignedValues = createValueList(2);
		final List<CsticValueModel> assignableValues = createValueList(5);
		csticModel.setAssignedValues(assignedValues);
		csticModel.setAssignableValues(assignableValues);
		final List<CPSValue> ceValues = classUnderTest.collectChangedValues(csticModel);
		assertEquals(5, ceValues.size());
		assertTrue(ceValues.get(0).isSelected());
		assertFalse(ceValues.get(2).isSelected());
	}

	@Test
	public void testCollectChangedValuesStringMV_NoAssignedValues()
	{
		final CsticModel csticModel = createCsticString("C1");
		csticModel.setMultivalued(true);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final List<CsticValueModel> assignableValues = createValueList(5);
		csticModel.setAssignedValues(assignedValues);
		csticModel.setAssignableValues(assignableValues);
		final List<CPSValue> ceValues = classUnderTest.collectChangedValues(csticModel);
		assertEquals(5, ceValues.size());
		assertFalse(ceValues.get(0).isSelected());
		assertFalse(ceValues.get(2).isSelected());
	}


	@Test
	public void testCollectChangedValuesIntegerSV()
	{
		final CsticModel csticModel = createCsticNumeric("C1", CsticModel.TYPE_INTEGER);
		final List<CsticValueModel> assignedValues = createValueList(1);
		final List<CsticValueModel> assignableValues = createValueList(5);
		csticModel.setAssignedValues(assignedValues);
		csticModel.setAssignableValues(assignableValues);
		final List<CPSValue> ceValues = classUnderTest.collectChangedValues(csticModel);
		assertEquals(1, ceValues.size());
		assertTrue(ceValues.get(0).isSelected());
		assertEquals("0", ceValues.get(0).getValue());
	}

	@Test
	public void testCollectChangedValuesIntegerMV_2assigned()
	{
		final CsticModel csticModel = createCsticNumeric("C1", CsticModel.TYPE_INTEGER);
		csticModel.setMultivalued(true);
		final List<CsticValueModel> assignedValues = createValueList(1);
		final List<CsticValueModel> assignableValues = createValueList(5);
		csticModel.setAssignedValues(assignedValues);
		csticModel.setAssignableValues(assignableValues);
		final List<CPSValue> ceValues = classUnderTest.collectChangedValues(csticModel);
		assertEquals(5, ceValues.size());
		assertTrue(ceValues.get(0).isSelected());
		assertEquals("0", ceValues.get(0).getValue());
		assertFalse(ceValues.get(2).isSelected());
		assertEquals("2", ceValues.get(2).getValue());
	}

	@Test
	public void testCollectInstanceCsticChanges_1CsticChange()
	{
		final CPSCharacteristic changedCstic = prepareCsticChange();
		assertEquals("C4", changedCstic.getId());
		assertEquals(1, changedCstic.getValues().size());
		final CPSValue cloudEngineValue = changedCstic.getValues().get(0);
		assertEquals("V2", cloudEngineValue.getValue());
		assertTrue(cloudEngineValue.isSelected());
	}

	@Test
	public void testCollectInstanceCsticChanges_1CsticChangeIdPresent()
	{
		final CPSConfiguration config = new CPSConfiguration();
		//		config.setKbId("99");
		final CPSItem item = new CPSItem();
		item.setParentConfiguration(config);
		final CPSCharacteristic changedCstic = prepareCsticChange();
		changedCstic.setParentItem(item);

		final List<CPSValue> values = new ArrayList<>();
		final CPSValue value = new CPSValue();
		value.setParentCharacteristic(changedCstic);
		values.add(value);
		changedCstic.setValues(values);
		assertNotNull(changedCstic.getValues().get(0).getParentCharacteristic().getId());
		assertEquals("C4", changedCstic.getValues().get(0).getParentCharacteristic().getId());
	}

	protected CPSCharacteristic prepareCsticChange()
	{
		final InstanceModel instModel = createInstance();
		// only C4 flagged as changed
		instModel.getCstics().get(3).setChangedByFrontend(true);
		final List<CPSCharacteristic> changedCstics = classUnderTest.collectInstanceCsticChanges(instModel, null);
		assertEquals(1, changedCstics.size());
		final CPSCharacteristic changedCstic = changedCstics.get(0);
		return changedCstic;
	}


	@Test
	public void testCollectInstanceCsticChanges_NoCstics()
	{
		final InstanceModel instModel = new InstanceModelImpl();
		final List<CPSCharacteristic> changedCstics = classUnderTest.collectInstanceCsticChanges(instModel, null);
		assertEquals(0, changedCstics.size());
	}

	@Test
	public void testCollectInstanceCsticChanges_NoCsticChanges()
	{
		final InstanceModel instModel = createInstance();
		final List<CPSCharacteristic> changedCstics = classUnderTest.collectInstanceCsticChanges(instModel, null);
		assertEquals(0, changedCstics.size());
	}


	@Test
	public void testCollectInstanceCsticChanges_2CsticChanges()
	{
		final InstanceModel instModel = createInstance();
		// C3 and C4 flagged as changed
		instModel.getCstics().get(2).setChangedByFrontend(true);
		instModel.getCstics().get(3).setChangedByFrontend(true);
		final List<CPSCharacteristic> changedCstics = classUnderTest.collectInstanceCsticChanges(instModel, null);
		assertEquals(2, changedCstics.size());
		final CPSCharacteristic changedCstic = changedCstics.get(0);
		assertEquals("C3", changedCstic.getId());
		assertEquals(4, changedCstic.getValues().size());
		final CPSValue cloudEngineValue = changedCstic.getValues().get(0);
		assertEquals("V1", cloudEngineValue.getValue());
		assertFalse(cloudEngineValue.isSelected());
		assertEquals("V2", changedCstic.getValues().get(1).getValue());
		assertTrue(changedCstic.getValues().get(1).isSelected());
		final CPSCharacteristic changedCstic2 = changedCstics.get(1);
		assertEquals("C4", changedCstic2.getId());
		assertEquals(1, changedCstic2.getValues().size());
		final CPSValue cloudEngineValue2 = changedCstic2.getValues().get(0);
		assertEquals("V2", cloudEngineValue2.getValue());
		assertTrue(cloudEngineValue2.isSelected());
	}

	@Test
	public void testProcessInstance()
	{
		final InstanceModel instModel = createInstance();
		// C3 and C4 flagged as changed
		instModel.getCstics().get(2).setChangedByFrontend(true);
		instModel.getCstics().get(3).setChangedByFrontend(true);
		final CPSItem item = classUnderTest.processInstance(instModel, null);
		assertEquals(instModel.getId(), item.getId());
		assertEquals(2, item.getCharacteristics().size());
	}

	@Test
	public void testProcessInstance_NoChanges()
	{
		final InstanceModel instModel = createInstance();
		final CPSItem item = classUnderTest.processInstance(instModel, null);
		assertEquals(instModel.getId(), item.getId());
		assertEquals(0, item.getCharacteristics().size());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetRetractValueNameException()
	{
		classUnderTest.getRetractValueName(createCsticString(CSTIC_NAME), new ArrayList());
	}

	@Test
	public void testGetRetractValueName()
	{
		final CsticModel csticModel = createCsticString(CSTIC_NAME);
		csticModel.setInstanceId(INSTANCE_ID);

		assertEquals(CSTIC_VALUE,
				classUnderTest.getRetractValueName(csticModel, prepareConflicts(CSTIC_NAME, CSTIC_VALUE, INSTANCE_ID)));
	}

	private List<SolvableConflictModel> prepareConflicts(final String csticName, final String csticValue, final String instanceId)
	{
		final List<SolvableConflictModel> conflicts = new ArrayList<>();
		final SolvableConflictModel conflict = new SolvableConflictModelImpl();
		final ConflictingAssumptionModel assumption = new ConflictingAssumptionModelImpl();
		assumption.setCsticName(csticName);
		assumption.setInstanceId(instanceId);
		assumption.setValueName(csticValue);
		List<ConflictingAssumptionModel> assumptions = new ArrayList<>();
		assumptions.add(assumption);
		conflict.setConflictingAssumptions(assumptions);
		conflicts.add(conflict);
		return conflicts;
	}

	@Test
	public void testCollectRetractValue()
	{
		final CsticModel csticModel = createCsticString(CSTIC_NAME);
		csticModel.setInstanceId(INSTANCE_ID);
		final List<CPSValue> result = classUnderTest.collectRetractValue(csticModel,
				prepareConflicts(CSTIC_NAME, CSTIC_VALUE, INSTANCE_ID));
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(CSTIC_VALUE, result.get(0).getValue());
	}

	@Test
	public void testProcessCsticWithConflict()
	{
		final List<CPSCharacteristic> characteristics = new ArrayList<>();
		final CsticModel csticModel = createCsticString(CSTIC_NAME);
		csticModel.setInstanceId(INSTANCE_ID);
		csticModel.setRetractTriggered(true);
		classUnderTest.processCstic(characteristics, csticModel, prepareConflicts(CSTIC_NAME, CSTIC_VALUE, INSTANCE_ID));
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		assertEquals(CSTIC_VALUE, characteristics.get(0).getValues().get(0).getValue());
	}

}
