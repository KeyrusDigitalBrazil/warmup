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
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class YSapSimplePocConfigMockImplTest
{
	private YSapSimplePocConfigMockImpl classUnderTest;
	private ConfigModel model;
	private InstanceModel instance;
	private CsticModel cstic;

	@Before
	public void setUp()
	{
		classUnderTest = (YSapSimplePocConfigMockImpl) new RunTimeConfigMockFactory().createConfigMockForProductCode("ysap");
		model = classUnderTest.createDefaultConfiguration();
		instance = model.getRootInstance();
		cstic = instance.getCstic(YSapSimplePocConfigMockImpl.NUM_NAME);
	}

	private List<CsticValueModel> setAssignedValue(final String value)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName(value);
		assignedValues.add(csticValue);

		return assignedValues;
	}

	@Test
	public void testCheckCsticPassNullValue()
	{
		final String assignedValue = null;
		final List<CsticValueModel> assignedValues = setAssignedValue(assignedValue);
		cstic.setAssignedValues(assignedValues);
		classUnderTest.checkCstic(model, instance, cstic);

		final CsticValueModel csticValue = classUnderTest.retrieveValue(instance, YSapSimplePocConfigMockImpl.NUM_NAME);
		assertEquals(assignedValue, csticValue.getName());
	}

	@Test
	public void testCheckCsticPassIntegerValue()
	{
		final String assignedValue = "12";
		final List<CsticValueModel> assignedValues = setAssignedValue(assignedValue);
		cstic.setAssignedValues(assignedValues);
		classUnderTest.checkCstic(model, instance, cstic);

		final CsticValueModel csticValue = classUnderTest.retrieveValue(instance, YSapSimplePocConfigMockImpl.NUM_NAME);
		assertEquals(assignedValue, csticValue.getName());
	}

	@Test
	public void testCheckCsticPassFloatValue()
	{
		final String assignedValue = "12.0";
		final List<CsticValueModel> assignedValues = setAssignedValue(assignedValue);
		cstic.setAssignedValues(assignedValues);
		classUnderTest.checkCstic(model, instance, cstic);

		final CsticValueModel csticValue = classUnderTest.retrieveValue(instance, YSapSimplePocConfigMockImpl.NUM_NAME);
		assertEquals(assignedValue, csticValue.getName());
	}

	@Test(expected = NumberFormatException.class)
	public void testCheckCsticPassStringValue()
	{
		final String assignedValue = "aaa";
		final List<CsticValueModel> assignedValues = setAssignedValue(assignedValue);
		cstic.setAssignedValues(assignedValues);
		classUnderTest.checkCstic(model, instance, cstic);
	}

	@Test
	public void testGeneralGroupCreated()
	{
		final List<CsticGroupModel> groups = instance.getCsticGroups();
		assertEquals(1, groups.size());

		final CsticGroupModel genGroup = groups.get(0);
		assertEquals("_GEN", genGroup.getName());
		assertEquals(4, genGroup.getCsticNames().size());
	}

}
