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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class InstanceModelTest extends AbstractBaseModelTest
{
	private InstanceModelImpl instanceModel;

	@Before
	public void setup()
	{
		instanceModel = new InstanceModelImpl();
	}

	@Test
	public void testRetrieveCsticGroups()
	{
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createCstic("A"));
		cstics.add(createCstic("B"));
		cstics.add(createCstic("C"));
		cstics.add(createCstic("D"));

		instanceModel.setCstics(cstics);

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel grpA = createCsticGroup("1", "Grp A", "A", "B");
		final CsticGroupModel grpB = createCsticGroup("2", "Grp B", "C", "D");
		csticGroups.add(grpA);
		csticGroups.add(grpB);
		instanceModel.setCsticGroups(csticGroups);

		final List<CsticGroup> groups = instanceModel.retrieveCsticGroupsWithCstics();
		assertEquals(2, groups.size());

		final CsticGroup groupA = groups.get(0);
		final List<CsticModel> csticGroupA = groupA.getCstics();
		assertEquals(2, csticGroupA.size());
		assertEquals(grpA.getName(), groupA.getName());
		assertEquals(grpA.getDescription(), groupA.getDescription());

		assertEquals("A", csticGroupA.get(0).getName());
		assertEquals("B", csticGroupA.get(1).getName());


	}

	@Test
	public void testToString()
	{
		String name = "ThisIsALongName";
		String csticText = "cstics=";
		String csticGroupText = "csticGroups=";
		String subInstanceText = "subInstances=";

		instanceModel.setName(name);
		String toStringResult = instanceModel.toString();
		assertTrue(toStringResult.contains(name));
		assertFalse(toStringResult.contains(csticText));
		assertFalse(toStringResult.contains(csticGroupText));
		assertFalse(toStringResult.contains(subInstanceText));

		List<CsticModel> cstics = new ArrayList<>();
		cstics.add(new CsticModelImpl());
		instanceModel.setCstics(cstics);
		List<InstanceModel> subInstances = new ArrayList<>();
		subInstances.add(new InstanceModelImpl());
		instanceModel.setSubInstances(subInstances);
		List<CsticGroupModel> csticGroups = new ArrayList<>();
		csticGroups.add(new CsticGroupModelImpl());
		instanceModel.setCsticGroups(csticGroups);

		toStringResult = instanceModel.toString();
		assertTrue(toStringResult.contains(csticText));
		assertTrue(toStringResult.contains(csticGroupText));
		assertTrue(toStringResult.contains(subInstanceText));
	}

	@Test
	public void testEquals() throws Exception
	{
		InstanceModelImpl testInstanceModel = new InstanceModelImpl();

		testGenericEqualPart(instanceModel, testInstanceModel);

		instanceModel.setCstics(null);
		instanceModel.setCsticGroups(null);
		instanceModel.setSubInstances(null);

		testInstanceModel.setCstics(null);
		testInstanceModel.setCsticGroups(null);
		testInstanceModel.setSubInstances(null);

		equalCheck(instanceModel, testInstanceModel, "setComplete", true, null);
		equalCheck(instanceModel, testInstanceModel, "setConsistent", true, null);
		equalCheck(instanceModel, testInstanceModel, "setRootInstance", true, null);

		//		equalCheck(instanceModel, testInstanceModel, "setCstics", new ArrayList<CsticModel>(), null);
		List<CsticModel> cstics = new ArrayList<>();
		cstics.add(new CsticModelImpl());
		equalCheck(instanceModel, testInstanceModel, "setCstics", cstics, null);

		//		equalCheck(instanceModel, testInstanceModel, "setCsticGroups", new ArrayList<CsticGroupModel>(), null);
		List<CsticGroupModel> csticGroups = new ArrayList<>();
		csticGroups.add(new CsticGroupModelImpl());
		equalCheck(instanceModel, testInstanceModel, "setCsticGroups", csticGroups, null);

		//		equalCheck(instanceModel, testInstanceModel, "setSubInstances", new ArrayList<InstanceModel>(), null);
		List<InstanceModel> subInstances = new ArrayList<>();
		subInstances.add(new InstanceModelImpl());
		equalCheck(instanceModel, testInstanceModel, "setSubInstances", subInstances, null);

		equalCheck(instanceModel, testInstanceModel, "setId", "Test", "Test1");
		equalCheck(instanceModel, testInstanceModel, "setLanguageDependentName", "Test", "Test1");
		equalCheck(instanceModel, testInstanceModel, "setName", "Test", "Test1");
		equalCheck(instanceModel, testInstanceModel, "setPosition", "Test", "Test1");
	}

	@Test
	public void testGetDisplayName()
	{
		final String displayname = instanceModel.getDisplayName("langDepName", "name");
		assertEquals("langDepName", displayname);
	}

	@Test
	public void testGetDisplayNameEmpty()
	{
		final String displayname = instanceModel.getDisplayName("", "name");
		assertEquals("[name]", displayname);
	}

	@Test
	public void testGetDisplayNameNull()
	{
		final String displayname = instanceModel.getDisplayName(null, "name");
		assertEquals("[name]", displayname);
	}



	private CsticModel createCstic(final String csticName)
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticName);

		return cstic;
	}

	private CsticGroupModel createCsticGroup(final String groupName, final String description, final String... csticNames)
	{
		final List<String> csticNamesInGroup = new ArrayList<>();
		for (final String csticName : csticNames)
		{
			csticNamesInGroup.add(csticName);
		}

		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(groupName);
		csticGroup.setDescription(description);
		csticGroup.setCsticNames(csticNamesInGroup);

		return csticGroup;
	}
}
