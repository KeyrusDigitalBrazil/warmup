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
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class CPQLaptopPocConfigMockImplTest
{
	private CPQLaptopPocConfigMockImpl classUnderTest = new CPQLaptopPocConfigMockImpl();
	private ConfigModel model;

	@Before
	public void setUp()
	{
		model = classUnderTest.createDefaultConfiguration();
	}

	@Test
	public void testCheckModel4GHzSet()
	{
		removeGameBuilder(model);

		CsticModel cpuCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_CPU);
		CsticValueModel valueModel = cpuCsticModel.getAssignableValues().get(0);
		List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(valueModel);
		cpuCsticModel.setAssignedValues(assignedValues);

		classUnderTest.checkModel(model);

		CsticModel softwareCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_SOFTWARE);
		assertNotNull(softwareCsticModel);
		assertEquals(4, softwareCsticModel.getAssignableValues().size());
	}

	@Test
	public void testCheckModelOtherThan4GHzSet()
	{
		removeGameBuilder(model);

		CsticModel cpuCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_CPU);
		CsticValueModel valueModel = cpuCsticModel.getAssignableValues().get(1);
		List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(valueModel);
		cpuCsticModel.setAssignedValues(assignedValues);

		classUnderTest.checkModel(model);

		CsticModel softwareCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_SOFTWARE);
		assertNotNull(softwareCsticModel);
		assertEquals(5, softwareCsticModel.getAssignableValues().size());
	}

	private void removeGameBuilder(ConfigModel model)
	{
		CsticModel softwareCsticModel = model.getRootInstance().getCstic(CPQLaptopPocConfigMockImpl.CPQ_SOFTWARE);
		assertNotNull(softwareCsticModel);

		List<CsticValueModel> softwareValues = softwareCsticModel.getAssignableValues().stream().filter(value -> !(CPQLaptopPocConfigMockImpl.GAMEBUILDER.equalsIgnoreCase(value.getName()))).collect(Collectors.toList());

		softwareCsticModel.setAssignableValues(softwareValues);
	}
}
