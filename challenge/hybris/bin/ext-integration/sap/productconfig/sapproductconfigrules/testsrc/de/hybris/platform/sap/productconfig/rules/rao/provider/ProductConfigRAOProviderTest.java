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
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigRAOProviderTest
{

	private ProductConfigRAOProvider classUnderTest;

	@Mock
	private ConfigModel configModel;

	@Mock
	private Converter<ConfigModel, ProductConfigRAO> productConfigRaoConverter;

	private ProductConfigRAO productConfigRAO;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigRAOProvider();
		productConfigRAO = new ProductConfigRAO();
		given(productConfigRaoConverter.convert(configModel)).willReturn(productConfigRAO);
		classUnderTest.setProductConfigRaoConverter(productConfigRaoConverter);
	}

	@Test
	public void testCreateRAO()
	{
		final ProductConfigRAO productConfigRao = classUnderTest.createRAO(configModel);
		assertNotNull(productConfigRao);
	}

	@Test
	public void testExpandFactModel()
	{
		fillProductConfigRAO(productConfigRAO);
		final Set<Object> expandFactModel = classUnderTest.expandFactModel(configModel);
		assertNotNull(expandFactModel);
		assertEquals(5, expandFactModel.size());
	}

	private void fillProductConfigRAO(final ProductConfigRAO productConfigRAO)
	{
		final CsticValueRAO csticValueRAO111 = new CsticValueRAO();
		csticValueRAO111.setCsticValueName("Value111");
		final CsticValueRAO csticValueRAO112 = new CsticValueRAO();
		csticValueRAO112.setCsticValueName("Value112");
		final CsticRAO csticRAO11 = new CsticRAO();
		csticRAO11.setCsticName("Cstic11");
		final List<CsticValueRAO> csticValues11 = new ArrayList<CsticValueRAO>();
		csticValues11.add(csticValueRAO111);
		csticValues11.add(csticValueRAO112);
		csticRAO11.setAssignedValues(csticValues11);

		final CsticRAO csticRAO12 = new CsticRAO();
		csticRAO12.setCsticName("Cstic12");

		final List<CsticRAO> cstics1 = new ArrayList<CsticRAO>();
		cstics1.add(csticRAO11);
		cstics1.add(csticRAO12);
		productConfigRAO.setCstics(cstics1);

		return;
	}
}
