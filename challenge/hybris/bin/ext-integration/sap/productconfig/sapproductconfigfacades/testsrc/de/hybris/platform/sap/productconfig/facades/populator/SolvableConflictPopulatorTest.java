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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;



@UnitTest
public class SolvableConflictPopulatorTest
{
	SolvableConflictPopulator classUnderTest = new SolvableConflictPopulator();
	private ConfigurationData configurationDataNoGroups;
	private ConfigurationData configurationData;
	private ConflictingAssumptionModel conflictingAssumption;
	private static final String csticName = "name";
	private static final String instanceId = "1";
	private static String csticName2 = "name2";
	private UiGroupData group;
	private UiGroupData group2;
	private List<CsticData> csticList;
	private List<CsticData> csticList2;
	private CsticData cstic;
	private CsticData cstic2;
	private CsticData cstic2a;
	private List<UiGroupData> groups;
	private final SolvableConflictModel solvableConflict = new SolvableConflictModelImpl();
	private final SolvableConflictModel solvableConflict2 = new SolvableConflictModelImpl();
	private final SolvableConflictModel solvableConflict3 = new SolvableConflictModelImpl();
	private final List<SolvableConflictModel> solvableConflicts = new ArrayList<>();
	private final ConfigurationData configurationDataEmpty = new ConfigurationData();
	private ConfigModel configModel;
	private ConflictingAssumptionModelImpl conflictingAssumption2;
	private static String conflictId = "12";

	private static final String conflictDescription = "Conflict Description";

	@Before
	public void setUp()
	{
		configurationDataNoGroups = new ConfigurationData();
		configurationDataNoGroups.setGroups(new ArrayList<>());
		configurationData = new ConfigurationData();
		groups = new ArrayList<>();
		group = new UiGroupData();
		groups.add(group);
		group2 = new UiGroupData();
		groups.add(group2);
		configurationData.setGroups(groups);
		conflictingAssumption = new ConflictingAssumptionModelImpl();
		conflictingAssumption.setCsticName(csticName);
		conflictingAssumption.setInstanceId(instanceId);
		conflictingAssumption2 = new ConflictingAssumptionModelImpl();
		conflictingAssumption2.setCsticName(csticName2);
		conflictingAssumption2.setInstanceId(instanceId);

		csticList = new ArrayList<>();
		cstic = new CsticData();
		cstic.setName(csticName);
		cstic.setInstanceId(instanceId);
		cstic.setConflicts(Collections.emptyList());
		cstic2 = new CsticData();
		cstic2.setName(csticName2);
		cstic2.setInstanceId(instanceId);
		csticList.add(cstic);
		csticList.add(cstic2);
		group.setCstics(csticList);
		cstic2a = new CsticData();
		cstic2a.setName(csticName2);
		cstic2a.setInstanceId(instanceId);


		csticList2 = new ArrayList<>();
		csticList2.add(cstic2a);
		group2.setCstics(csticList2);

		final List<UiGroupData> csticGroupsFlat = new ArrayList();
		csticGroupsFlat.add(group);
		csticGroupsFlat.add(group2);
		configurationData.setCsticGroupsFlat(csticGroupsFlat);

		solvableConflict.setConflictingAssumptions(Arrays.asList(conflictingAssumption));
		solvableConflict.setId(conflictId);
		solvableConflict.setDescription(conflictDescription);
		solvableConflict2.setConflictingAssumptions(Arrays.asList(conflictingAssumption2));

	}

	@Test
	public void testFindCsticInConfigurationNoGroups()
	{
		assertTrue(classUnderTest.findCsticsInConfiguration(configurationDataNoGroups, conflictingAssumption).isEmpty());
	}

	@Test
	public void testFindCsticInConfigurationNoCstics()
	{
		group.setCstics(null);
		assertTrue("Group w/o cstics: We expect no result",
				classUnderTest.findCsticsInConfiguration(configurationData, conflictingAssumption).isEmpty());
	}

	@Test
	public void testFindCsticInConfigurationEmptyCstics()
	{
		group.setCstics(Collections.EMPTY_LIST);
		assertTrue("Group with empty cstics list: We expect no result",
				classUnderTest.findCsticsInConfiguration(configurationData, conflictingAssumption).isEmpty());
	}

	@Test
	public void testFindCsticInConfiguration()
	{
		assertNotNull("We expect a result", classUnderTest.findCsticsInConfiguration(configurationData, conflictingAssumption));
	}

	@Test
	public void testFindCsticInCsticList()
	{
		assertNotNull(classUnderTest.findCsticInCsticList(csticList, csticName, instanceId));
	}

	@Test
	public void testFindCsticInCsticListNameDoesNotMatch()
	{
		assertNull(classUnderTest.findCsticInCsticList(csticList, "X", instanceId));
	}

	@Test
	public void testFindCsticInGroupsNameDoesNotMatch()
	{

		assertNotNull(classUnderTest.findCsticsInGroups(groups, "X", instanceId));
		assertEquals("We expect list with size 0 if name does not match", 0,
				classUnderTest.findCsticsInGroups(groups, "X", instanceId).size());
	}

	@Test
	public void testFindCsticInGroups()
	{
		assertNotNull(classUnderTest.findCsticsInGroups(groups, csticName, instanceId));
	}


	@Test
	public void testFindCsticInGroupsInstanceId()
	{
		final List<CsticData> csticsWithAssumption = classUnderTest.findCsticsInGroups(groups, csticName, instanceId);
		assertEquals("We expect an instance ID", instanceId, csticsWithAssumption.get(0).getInstanceId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindCsticInGroupsNoInstanceIDProvided()
	{
		cstic.setInstanceId(null);
		assertNotNull(classUnderTest.findCsticsInGroups(groups, csticName, instanceId));
	}

	@Test
	public void testCreateConflictUiGroupEmptyConflict()
	{
		solvableConflict.setConflictingAssumptions(Collections.EMPTY_LIST);
		checkEmptyConflict();
	}

	@Test
	public void testCreateConflictUiGroupNullAssumptions()
	{
		solvableConflict.setConflictingAssumptions(null);
		checkEmptyConflict();
	}


	@Test
	public void testCreateConflictUiGroup()
	{
		final UiGroupData conflictUiGroup = classUnderTest.createConflictUiGroup(solvableConflict, configurationData);
		assertNotNull("We expect one group", conflictUiGroup);
		assertTrue(conflictUiGroup.isConfigurable());
		assertFalse(conflictUiGroup.isCollapsed());
		assertEquals(GroupType.CONFLICT, conflictUiGroup.getGroupType());
		final List<CsticData> csticsAtUiGroup = conflictUiGroup.getCstics();
		assertNotNull("We expect a list of cstics", csticsAtUiGroup);
		assertEquals("We expect cstics as the conflict contains assumptions", 1, csticsAtUiGroup.size());
		assertEquals("We expect that the status of the conflict to be 'Conflict Status'", CsticStatusType.CONFLICT,
				csticsAtUiGroup.get(0).getCsticStatus());
	}

	@Test
	public void testCreateConflictHeaderUiGroup()
	{
		final UiGroupData conflictUiGroup = classUnderTest.createConflictUiGroup(solvableConflict, configurationData);
		final ArrayList<UiGroupData> conflictGroups = new ArrayList();
		conflictGroups.add(conflictUiGroup);
		final List<UiGroupData> headerUiGroups = classUnderTest.createConflictHeader(conflictGroups);
		assertEquals("We expect only one header", 1, headerUiGroups.size());
		final UiGroupData headerUigroup = headerUiGroups.get(0);
		assertTrue(headerUigroup.isConfigurable());
		assertFalse(headerUigroup.isCollapsed());
		assertEquals(GroupType.CONFLICT_HEADER, headerUigroup.getGroupType());
		assertEquals("We expect one confilict group", 1, headerUigroup.getSubGroups().size());
		assertEquals("As it is only one conflict the number of errors one", 1, headerUigroup.getNumberErrorCstics());
		final List<CsticData> csticsAtUiGroup = headerUigroup.getCstics();
		assertNotNull("We expect a list of cstics", csticsAtUiGroup);
	}

	@Test
	public void testCreateConflictUiGroupGroupId()
	{
		final UiGroupData conflictUiGroup = classUnderTest.createConflictUiGroup(solvableConflict, configurationData);
		assertNotNull("We expect one group", conflictUiGroup);
		assertTrue(conflictUiGroup.getId().indexOf(GroupType.CONFLICT.toString()) > -1);
	}

	@Test
	public void testCreateConflictUiGroupGroupIdContainsCID()
	{
		final UiGroupData conflictUiGroup = classUnderTest.createConflictUiGroup(solvableConflict, configurationData);
		assertNotNull("We expect one group", conflictUiGroup);
		assertTrue(conflictUiGroup.getId().indexOf(conflictId) > -1);
	}

	@Test
	public void testPopulate()
	{
		preparePopulateTestData();
		solvableConflicts.add(solvableConflict);
		configModel.setSolvableConflicts(solvableConflicts);
		classUnderTest.populate(configModel, configurationDataEmpty);
		final List<UiGroupData> groupsAfterPopulate = configurationDataEmpty.getGroups();
		final List<UiGroupData> flatGroupsAfterPopulate = configurationDataEmpty.getCsticGroupsFlat();
		assertNotNull(groupsAfterPopulate);
		assertEquals("We expect one ui group now", 1, groupsAfterPopulate.size());
		assertNotNull(flatGroupsAfterPopulate);
		assertEquals("We expect one ui group in the flat list now", 1, flatGroupsAfterPopulate.size());
		assertEquals("We expect 1 as number of conflicts", 1, groupsAfterPopulate.get(0).getNumberErrorCstics());
	}

	@Test
	public void testPopulateNoConflict()
	{
		preparePopulateTestData();
		classUnderTest.populate(configModel, configurationDataEmpty);
		final List<UiGroupData> groupsAfterPopulate = configurationDataEmpty.getGroups();
		assertNotNull(groupsAfterPopulate);
		assertEquals("We expect no groups now", 0, groupsAfterPopulate.size());
	}

	@Test
	public void testPopulate2Conflicts()
	{
		preparePopulateTestData();
		solvableConflicts.add(solvableConflict2);
		solvableConflicts.add(solvableConflict);
		configModel.setSolvableConflicts(solvableConflicts);
		classUnderTest.populate(configModel, configurationData);
		final List<UiGroupData> groupsAfterPopulate = configurationData.getGroups();
		final List<UiGroupData> flatGroupsAfterPopulate = configurationData.getCsticGroupsFlat();
		assertNotNull(groupsAfterPopulate);
		assertEquals("We expect two ui group now", 3, groupsAfterPopulate.size());
		assertNotNull(flatGroupsAfterPopulate);
		assertEquals("We expect three ui groups in the flat list now", 4, flatGroupsAfterPopulate.size());
		assertEquals("We expect 2 as number of conflicts", 2, groupsAfterPopulate.get(0).getNumberErrorCstics());
		assertEquals("We expect that the cstic of a conflict group has 'Conflict' as cstic-status", CsticStatusType.CONFLICT,
				flatGroupsAfterPopulate.get(0).getCstics().get(0).getCsticStatus());
		assertEquals("We expect that first cstic in first normal group has 'Conflict' as cstic-status", CsticStatusType.CONFLICT,
				groupsAfterPopulate.get(1).getCstics().get(0).getCsticStatus());
		assertEquals("We expect that second cstic in first normal group has 'Conflict' as cstic-status", CsticStatusType.CONFLICT,
				groupsAfterPopulate.get(1).getCstics().get(1).getCsticStatus());
		assertEquals("We expect that first cstic in second normal group has 'Conflict' as cstic-status", CsticStatusType.CONFLICT,
				groupsAfterPopulate.get(2).getCstics().get(0).getCsticStatus());
	}


	@Test
	public void testCreateConflictList2Conflicts()
	{
		preparePopulateTestData();
		solvableConflicts.add(solvableConflict2);
		solvableConflicts.add(solvableConflict);
		final List<ComparableConflictGroup> conflictList = classUnderTest.createConflictList(configurationData, solvableConflicts);
		assertNotNull("We expect conflict list", conflictList);
		assertEquals("We expect 2 conflicts", 2, conflictList.size());
		assertEquals("Sorting: First conflict is the one where cstic matches the cstic group list on configuration level",
				conflictDescription, conflictList.get(0).getDescription());
	}

	@Test
	public void testCreateConflictList()
	{
		preparePopulateTestData();
		solvableConflicts.add(solvableConflict);
		final List<ComparableConflictGroup> conflictList = classUnderTest.createConflictList(configurationData, solvableConflicts);
		assertNotNull("We expect conflict list", conflictList);
		assertEquals("We expect 1 conflict", 1, conflictList.size());
		assertEquals("Sorting: First conflict is the one where cstic matches the cstic group list on configuration level",
				conflictDescription, conflictList.get(0).getDescription());
	}

	@Test
	public void testCreateConflictListCsticNotAvailable()
	{
		preparePopulateTestData();
		//Those conflicts do not contain cstics which are part of the group
		//The rank will be determined equally
		solvableConflicts.add(solvableConflict3);
		solvableConflicts.add(solvableConflict3);
		final List<ComparableConflictGroup> conflictList = classUnderTest.createConflictList(configurationData, solvableConflicts);
		assertEquals(2, conflictList.size());
		assertTrue(conflictList.get(0).getCstics().isEmpty());
	}

	private void preparePopulateTestData()
	{
		configModel = new ConfigModelImpl();
		configModel.setSolvableConflicts(solvableConflicts);
		configurationDataEmpty.setGroups(new ArrayList<>());
		configurationDataEmpty.setCsticGroupsFlat(new ArrayList<>());
	}

	private void checkEmptyConflict()
	{
		final UiGroupData conflictUiGroup = classUnderTest.createConflictUiGroup(solvableConflict, configurationData);
		assertNotNull("We expect one group", conflictUiGroup);
		final List<CsticData> csticsAtUiGroup = conflictUiGroup.getCstics();
		assertNotNull("We expect a list of cstics", csticsAtUiGroup);
		assertEquals("We don't expect cstics as the conflict does not contain assumptions", 0, csticsAtUiGroup.size());
	}

}
