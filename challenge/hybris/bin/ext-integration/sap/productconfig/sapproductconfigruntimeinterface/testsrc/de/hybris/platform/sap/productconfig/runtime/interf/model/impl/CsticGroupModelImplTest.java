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
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.testframework.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@UnitTest
public class CsticGroupModelImplTest extends AbstractBaseModelTest
{
	public static final String CSTIC_1 = "CSTIC_1";
	public static final String CSTIC_2 = "CSTIC_2";

	public static final String NAME = "1";
	public static final String DESCRIPTION = "Group Description";

	private final CsticGroupModel csticGroupModel = new CsticGroupModelImpl();

	@Test
	public void testCsticGroupTest()
	{
		fillCsticGroup();

		assertEquals(NAME, csticGroupModel.getName());
		assertEquals(DESCRIPTION, csticGroupModel.getDescription());
		assertEquals(CSTIC_1, csticGroupModel.getCsticNames().get(0));
		assertEquals(CSTIC_2, csticGroupModel.getCsticNames().get(1));
	}

	protected void fillCsticGroup()
	{

		final List<String> csticNames = new ArrayList<String>();
		csticNames.add(CSTIC_1);
		csticNames.add(CSTIC_2);

		csticGroupModel.setName(NAME);
		csticGroupModel.setDescription(DESCRIPTION);
		csticGroupModel.setCsticNames(csticNames);
	}

	@Test
	public void testToString()
	{
		String testName = "ThisIsATestName";
		csticGroupModel.setName(testName);
		assertTrue(csticGroupModel.toString().contains(testName));
	}

	@Test
	public void testEquals() throws Exception
	{
		CsticGroupModel testCsticGroupModel = new CsticGroupModelImpl();

		testGenericEqualPart(csticGroupModel, testCsticGroupModel);

		equalCheck(csticGroupModel, testCsticGroupModel, "setCsticNames", new ArrayList<String>(), null);
		List<String> cstics = new ArrayList<>();
		cstics.add("Cstic1");
		equalCheck(csticGroupModel, testCsticGroupModel, "setCsticNames", cstics, null);

		equalCheck(csticGroupModel, testCsticGroupModel, "setName", "Test", "Test1");
		equalCheck(csticGroupModel, testCsticGroupModel, "setDescription", "Test", "Test1");
	}
}
