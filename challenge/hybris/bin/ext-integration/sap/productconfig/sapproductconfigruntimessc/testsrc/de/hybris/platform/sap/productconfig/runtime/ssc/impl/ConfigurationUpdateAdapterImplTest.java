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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ValueChangeType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


@UnitTest
public class ConfigurationUpdateAdapterImplTest
{
	private static final String VAL1 = "VAL1";
	private static final String VAL2 = "VAL2";
	private static final String VAL3 = "VAL3";
	private static final String VAL4 = "VAL4";
	private static final String CSTICNAME = "CSTIC1";
	private static final String CSTICNAME2 = "CSTIC2";
	private static final String INSTANCENAME = "INSTANCENAME";
	private static final String INSTANCEID = "INSTANCEID";
	private static final String VALUE = "Value";

	ConfigurationUpdateAdapterImpl classUnderTest = new ConfigurationUpdateAdapterImpl();

	private CsticModel csticModel;
	private ConfigModel configModel;
	@Mock
	private IConfigSessionClient session;
	@Mock
	private IInstanceData instanceData;
	private String configId;
	private SolvableConflictModel solvableConflict;
	private String name;
	private ConflictingAssumptionModel conflictingAssumption;
	private final List<String> newValues = new ArrayList<>();
	private final List<String> oldValues = new ArrayList<>();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		csticModel = new CsticModelImpl();
		name = "CsticName";
		csticModel.setName(name);
		configModel = new ConfigModelImpl();
		configId = "1";
		classUnderTest.setConflictAdapter(new SolvableConflictAdapterImpl());
		solvableConflict = new SolvableConflictModelImpl();
		conflictingAssumption = new ConflictingAssumptionModelImpl();
	}

	@Test
	public void testHasBeenRetractedNoRetraction() throws IpcCommandException
	{
		classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId);
	}

	@Test(expected = IllegalStateException.class)
	public void testHasBeenRetractedNoAssumptions() throws IpcCommandException
	{
		csticModel.setRetractTriggered(true);
		classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId);
	}

	@Test(expected = IllegalStateException.class)
	public void testHasBeenRetractedAssumptionsNoMatch() throws IpcCommandException
	{
		csticModel.setRetractTriggered(true);
		configModel.setSolvableConflicts(Arrays.asList(solvableConflict));
		classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId);
	}

	@Test
	public void testHasBeenRetractedAssumptions() throws IpcCommandException
	{
		csticModel.setRetractTriggered(true);
		configModel.setSolvableConflicts(Arrays.asList(solvableConflict));
		solvableConflict.setConflictingAssumptions(Arrays.asList(conflictingAssumption));
		conflictingAssumption.setCsticName(name);
		conflictingAssumption.setId("A");
		assertTrue("Retraction expected", classUnderTest.hasBeenRetracted(csticModel, configModel, session, configId));
	}

	@Test
	public void testDetermineValuesToDeleteEmptyLists()
	{
		final ICsticValueData[] determineValuesToDelete = classUnderTest.determineValuesToDelete(newValues, oldValues);
		assertTrue(determineValuesToDelete.length == 0);
	}

	@Test
	public void testDetermineValuesToDeleteOnlyNew()
	{
		newValues.add(VALUE);
		final ICsticValueData[] determineValuesToDelete = classUnderTest.determineValuesToDelete(newValues, oldValues);
		assertTrue(determineValuesToDelete.length == 0);
	}

	@Test
	public void testDetermineValuesToDeleteOld()
	{
		final String oldValue = VALUE;
		oldValues.add(oldValue);
		final ICsticValueData[] determineValuesToDelete = classUnderTest.determineValuesToDelete(newValues, oldValues);
		assertTrue(determineValuesToDelete.length == 1);
		assertEquals(oldValue, determineValuesToDelete[0].getValueName());
	}

	@Test
	public void testDetermineValuesToSetEmptyList()
	{
		final ICsticValueData[] valuesToSet = classUnderTest.determineValuesToSet(newValues, oldValues);
		assertTrue(valuesToSet.length == 0);

	}

	@Test
	public void testDetermineValuesToSetOnlyOldValue()
	{
		final String oldValue = VALUE;
		oldValues.add(oldValue);
		final ICsticValueData[] valuesToSet = classUnderTest.determineValuesToSet(newValues, oldValues);
		assertTrue(valuesToSet.length == 0);
	}

	@Test
	public void testDetermineValuesToSetNew()
	{
		final String newValue = VALUE;
		newValues.add(newValue);
		final ICsticValueData[] valuesToSet = classUnderTest.determineValuesToSet(newValues, oldValues);
		assertTrue(valuesToSet.length == 1);
		assertEquals(newValue, valuesToSet[0].getValueName());
	}


	@Test
	public void testmapICsticDataToCsticValueDeltaEmptyValues()
	{
		final ICsticData icd = new CsticData();
		final List<CsticValueDelta> csticValueDeltas = new ArrayList();
		classUnderTest.mapICsticDataToCsticValueDelta(csticValueDeltas, null, null, null, icd);

		assertTrue(csticValueDeltas.isEmpty());
	}

	@Test
	public void testmapICsticDataToCsticValueDelta()
	{

		final ICsticData icd = createCsticData(CSTICNAME, VAL1, VAL2);

		final List<CsticValueDelta> csticValueDeltas = new ArrayList();
		classUnderTest.mapICsticDataToCsticValueDelta(csticValueDeltas, INSTANCENAME, INSTANCEID, ValueChangeType.DELETE, icd);

		assertEquals(1, csticValueDeltas.size());
		final CsticValueDelta result = csticValueDeltas.get(0);
		assertEquals(INSTANCENAME, result.getInstanceName());
		assertEquals(INSTANCEID, result.getInstanceId());
		assertEquals(CSTICNAME, result.getCsticName());
		assertEquals(ValueChangeType.DELETE, result.getChangeType());
		assertEquals(2, result.getValueNames().size());
		switch (result.getValueNames().get(0))
		{
			case VAL1:
				assertEquals(VAL2, result.getValueNames().get(1));
				break;
			case VAL2:
				assertEquals(VAL1, result.getValueNames().get(1));
				break;
			default:
				fail();
		}
	}

	private ICsticData createCsticData(final String csticName, final String... values)
	{
		final ICsticData icd = new CsticData();
		createCsticHeader(icd, csticName);
		createCsticValueData(icd, values);
		return icd;
	}

	private void createCsticHeader(final ICsticData icd, final String csticName)
	{
		final ICsticHeader ichd = new CsticHeader();

		ichd.setCsticName(csticName);
		icd.setCsticHeader(ichd);
	}

	private void createCsticValueData(final ICsticData icd, final String... values)
	{
		final ICsticValueData[] values2 = new ICsticValueData[values.length];
		for (int i = 0; i < values.length; i++)
		{
			final ICsticValueData icvd = new CsticValueData();
			icvd.setValueName(values[i]);
			values2[i] = icvd;

		}
		icd.setCsticValues(values2);
	}

	@Test
	public void fillCsticValueDeltasTest() throws IpcCommandException
	{

		final ICsticData icd = createCsticData(CSTICNAME, VAL1, VAL2);
		final ICsticData icd2 = createCsticData(CSTICNAME2, VAL4, VAL3);
		final List<ICsticData> csticDataListToClear = new ArrayList<>();
		csticDataListToClear.add(icd);
		final List<ICsticData> csticDataList = new ArrayList<>();
		csticDataList.add(icd2);
		configModel = new ConfigModelImpl();

		Mockito.when(session.getInstance("123", "S1")).thenReturn(instanceData);
		Mockito.when(instanceData.getInstName()).thenReturn(INSTANCENAME);

		classUnderTest.fillCsticValueDeltas(session, "123", configModel, "S1", csticDataListToClear, csticDataList);

		final List<CsticValueDelta> resultValueDeltas = configModel.getCsticValueDeltas();
		assertEquals(2, resultValueDeltas.size());
		assertEquals(ValueChangeType.DELETE, resultValueDeltas.get(0).getChangeType());
		assertEquals(ValueChangeType.SET, resultValueDeltas.get(1).getChangeType());

		final CsticValueDelta result = resultValueDeltas.get(1);
		assertEquals(INSTANCENAME, result.getInstanceName());
		assertEquals(CSTICNAME2, result.getCsticName());
		assertEquals(ValueChangeType.SET, result.getChangeType());
		assertEquals(2, result.getValueNames().size());
		switch (result.getValueNames().get(0))
		{
			case VAL3:
				assertEquals(VAL4, result.getValueNames().get(1));
				break;
			case VAL4:
				assertEquals(VAL3, result.getValueNames().get(1));
				break;
			default:
				fail();
		}
	}

	@Test
	public void testInitCsticValueDeltaList()
	{
		classUnderTest.initCsticValueDeltaList(configModel);
		assertEquals(ArrayList.class, configModel.getCsticValueDeltas().getClass());
	}

	@Test
	public void testInitCsticValueDeltaListDisbaled()
	{
		classUnderTest.setTrackingEnabled(false);
		classUnderTest.initCsticValueDeltaList(configModel);
		assertEquals(Collections.emptyList(), configModel.getCsticValueDeltas());
	}
}
