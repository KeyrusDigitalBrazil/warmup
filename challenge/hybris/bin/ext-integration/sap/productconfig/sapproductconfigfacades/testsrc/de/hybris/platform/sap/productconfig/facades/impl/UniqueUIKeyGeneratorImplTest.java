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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class UniqueUIKeyGeneratorImplTest
{
	private InstanceModel instance;

	private static final String INSTANCE_ID = "1";
	private static final String INSTANCE_NAME = "Instance Name";
	private static final String UI_GROUP_ID = "1-Instance Name";

	private UniqueUIKeyGeneratorImpl classUnderTest;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new UniqueUIKeyGeneratorImpl();
		instance = new InstanceModelImpl();
		instance.setId(INSTANCE_ID);
		instance.setName(INSTANCE_NAME);
	}

	@Test
	public void testGenerateGroupId()
	{
		final String groupId = classUnderTest.generateGroupIdForInstance(instance);
		assertEquals("Wrong ui group id", UI_GROUP_ID, groupId);
	}

	@Test
	public void testretrieveInstanceId()
	{
		final String groupId = classUnderTest.generateGroupIdForInstance(instance);
		final String instanceId = classUnderTest.retrieveInstanceId(groupId);
		assertEquals("Wrong instance id", INSTANCE_ID, instanceId);
	}

	@Test
	public void testSplitId()
	{
		final CsticQualifier csticQualifier = classUnderTest.splitId("instanceId-instanceName.groupName.csticName");
		assertEquals("instanceId", csticQualifier.getInstanceId());
		assertEquals("instanceName", csticQualifier.getInstanceName());
		assertEquals("groupName", csticQualifier.getGroupName());
		assertEquals("csticName", csticQualifier.getCsticName());
	}

	@Test
	public void testSplitMultipleMinusId()
	{
		final CsticQualifier csticQualifier = classUnderTest.splitId("instanceId-1-instanceName.groupName.csticName");
		assertEquals("instanceId", csticQualifier.getInstanceId());
		assertEquals("1-instanceName", csticQualifier.getInstanceName());
		assertEquals("groupName", csticQualifier.getGroupName());
		assertEquals("csticName", csticQualifier.getCsticName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSplitId_Invalid_noGroup()
	{
		final CsticQualifier csticQualifier = classUnderTest.splitId("instanceId-instanceName.csticName");
		assertEquals("instanceId", csticQualifier.getInstanceId());
		assertEquals("instanceName", csticQualifier.getInstanceName());
		assertEquals("csticName", csticQualifier.getCsticName());
	}

	@Test
	public void testGenerateId()
	{
		final CsticQualifier csticQualifier = new CsticQualifier();
		csticQualifier.setInstanceId("instanceId");
		csticQualifier.setInstanceName("instanceName");
		csticQualifier.setCsticName("csticName");
		csticQualifier.setGroupName("groupName");
		final String csticUiKey = classUnderTest.generateId(csticQualifier);
		assertEquals("instanceId-instanceName.groupName.csticName", csticUiKey);
	}

}
