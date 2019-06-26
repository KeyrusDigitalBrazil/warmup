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
package de.hybris.platform.sap.productconfig.facades.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultOverviewGroupFilterTest
{
	public DefaultOverviewGroupFilter classUnderTest;
	public InstanceModel instanceModel;


	@Before
	public void setUp()
	{
		classUnderTest = new DefaultOverviewGroupFilter();
		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroupsAndSubInstancesAllVisible();
		instanceModel = config.getRootInstance();
	}


	@Test
	public void testgetGroupsToBeDisplayedGroupsEmpty()
	{
		final Set<String> groupsToBeDisplayed = classUnderTest.getGroupsToBeDisplayed(instanceModel, new HashSet<>());
		assertTrue(groupsToBeDisplayed.contains("GROUP1"));
		assertTrue(groupsToBeDisplayed.contains("GROUP2"));
		assertTrue(groupsToBeDisplayed.contains("SUBINSTANCE1LEVEL1"));
		assertTrue(groupsToBeDisplayed.contains("SUBINSTANCE2LEVEL1"));
	}

	@Test
	public void testgetGroupsToBeDisplayedGroupsNotEmpty()
	{
		final Set<String> groups = new HashSet<>();
		groups.add("GROUP2");
		final Set<String> groupsToBeDisplayed = classUnderTest.getGroupsToBeDisplayed(instanceModel, groups);
		assertFalse(groupsToBeDisplayed.contains("GROUP1"));
		assertTrue(groupsToBeDisplayed.contains("GROUP2"));
		assertFalse(groupsToBeDisplayed.contains("SUBINSTANCE1LEVEL1"));
		assertFalse(groupsToBeDisplayed.contains("SUBINSTANCE2LEVEL1"));
	}

	@Test
	public void testgetGroupsToBeDisplayedGroupsContainsTwo()
	{
		final Set<String> groups = new HashSet<>();
		groups.add("GROUP1");
		groups.add("SUBINSTANCE1LEVEL1");
		final Set<String> groupsToBeDisplayed = classUnderTest.getGroupsToBeDisplayed(instanceModel, groups);
		assertTrue(groupsToBeDisplayed.contains("GROUP1"));
		assertFalse(groupsToBeDisplayed.contains("GROUP2"));
		assertTrue(groupsToBeDisplayed.contains("SUBINSTANCE1LEVEL1"));
		assertFalse(groupsToBeDisplayed.contains("SUBINSTANCE2LEVEL1"));
	}

}
