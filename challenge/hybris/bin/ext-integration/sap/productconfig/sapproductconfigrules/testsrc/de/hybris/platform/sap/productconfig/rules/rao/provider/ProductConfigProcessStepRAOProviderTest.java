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
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigProcessStepRAOProviderTest
{
	private ProductConfigProcessStepRAOProvider classUnderTest;

	@Mock
	private Converter<ProductConfigProcessStepModel, ProductConfigProcessStepRAO> productConfigProcessStepRaoConverter;

	private ProductConfigProcessStepRAO productConfigProcessStepRAO;
	private ProductConfigProcessStepModel productConfigProcessStepModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigProcessStepRAOProvider();
		productConfigProcessStepModel = new ProductConfigProcessStepModel();
		classUnderTest.setProductConfigProcessStepRaoConverter(productConfigProcessStepRaoConverter);
		productConfigProcessStepRAO = new ProductConfigProcessStepRAO();
		given(productConfigProcessStepRaoConverter.convert(productConfigProcessStepModel)).willReturn(productConfigProcessStepRAO);
	}

	@Test
	public void testCreateRAO()
	{
		final ProductConfigProcessStepRAO productConfigProcessStepRao = classUnderTest.createRAO(productConfigProcessStepModel);
		assertNotNull(productConfigProcessStepRao);
	}

	@Test
	public void testExpandFactModel()
	{
		productConfigProcessStepRAO.setProcessStep(ProcessStep.RETRIEVE_CONFIGURATION);
		final Set<Object> expandFactModel = classUnderTest.expandFactModel(productConfigProcessStepModel);
		assertNotNull(expandFactModel);
		assertEquals(1, expandFactModel.size());

		final List<Object> list = new ArrayList<Object>(expandFactModel);
		final ProductConfigProcessStepRAO processStepRao = (ProductConfigProcessStepRAO) list.get(0);
		assertEquals(ProcessStep.RETRIEVE_CONFIGURATION, processStepRao.getProcessStep());
	}

}
