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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


@UnitTest
public class ConfigModelImplTest extends AbstractBaseModelTest
{
	private final ConfigModelImpl classUnderTest = new ConfigModelImpl();

	@Test
	public void testToStringSolvableConflicts()
	{
		final List<SolvableConflictModel> solvableConflicts = new ArrayList<>();
		final SolvableConflictModel solvableConflict = new SolvableConflictModelImpl();
		final String description = "This is a description";
		solvableConflict.setDescription(description);
		solvableConflicts.add(solvableConflict);
		classUnderTest.setSolvableConflicts(solvableConflicts);
		assertTrue("We expect the description of the conflict to appear in toString",
				classUnderTest.toString().indexOf(description) > -1);
	}


	@Test
	public void testSetGetMessageList()
	{
		final Set<ProductConfigMessage> messages = new HashSet();
		classUnderTest.setMessages(messages);
		assertEquals(messages, classUnderTest.getMessages());
	}

	@Test
	public void testGetMessageListNotNull()
	{
		assertNotNull(classUnderTest.getMessages());
	}

	@Test
	public void testEquals() throws Exception
	{
		final ConfigModel testConfigModel = new ConfigModelImpl();
		testGenericEqualPart(classUnderTest, testConfigModel);

		equalCheck(classUnderTest, testConfigModel, "setComplete", true, null);
		equalCheck(classUnderTest, testConfigModel, "setConsistent", true, null);
		equalCheck(classUnderTest, testConfigModel, "setSingleLevel", true, null);
		equalCheck(classUnderTest, testConfigModel, "setPricingError", true, null);
		equalCheck(classUnderTest, testConfigModel, "setRootInstance", new InstanceModelImpl(), null);

		equalCheck(classUnderTest, testConfigModel, "setBasePrice", new PriceModelImpl(), null);
		equalCheck(classUnderTest, testConfigModel, "setSelectedOptionsPrice", new PriceModelImpl(), null);
		equalCheck(classUnderTest, testConfigModel, "setCurrentTotalPrice", new PriceModelImpl(), null);

		equalCheck(classUnderTest, testConfigModel, "setId", "Test", "Test1");
		equalCheck(classUnderTest, testConfigModel, "setVersion", "Test", "Test1");
		equalCheck(classUnderTest, testConfigModel, "setName", "Test", "Test1");
		equalCheck(classUnderTest, testConfigModel, "setKbId", "Test", "Test1");
		equalCheck(classUnderTest, testConfigModel, "setKbKey",
				new KBKeyImpl("productCode", "kbName", "kbLogsys", "kbVersion", new Date()), null);
	}
}
