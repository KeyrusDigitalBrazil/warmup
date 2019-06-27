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
package de.hybris.platform.sap.productconfig.rules.rao.populator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigProcessStepRAOPopulatorTest
{
	private static final ProcessStep PROCESS_STEP = ProcessStep.CREATE_DEFAULT_CONFIGURATION;

	private ProductConfigProcessStepRAOPopulator classUnderTset;

	@Before
	public void setUp()
	{
		classUnderTset = new ProductConfigProcessStepRAOPopulator();
	}

	@Test
	public void testPopulate()
	{
		final ProductConfigProcessStepModel processStepModel = new ProductConfigProcessStepModel();
		processStepModel.setProcessStep(PROCESS_STEP);
		final ProductConfigProcessStepRAO processStepRAO = new ProductConfigProcessStepRAO();

		classUnderTset.populate(processStepModel, processStepRAO);
		assertEquals(PROCESS_STEP, processStepRAO.getProcessStep());
	}
}
