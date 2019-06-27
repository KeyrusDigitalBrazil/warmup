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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CPQConfigurationsPopulatorTest
{
	private static final String VALUE_NAME = "valueName";
	private static final String CSTIC_NAME = "csticName";
	private CPQConfigurationsPopulator<CPQOrderEntryProductInfoModel> classUnderTest;
	private CPQOrderEntryProductInfoModel source;
	private List<ConfigurationInfoData> target;

	@Before
	public void setup()
	{
		classUnderTest = new CPQConfigurationsPopulator<>();
		source = new CPQOrderEntryProductInfoModel();
		target = new ArrayList<>();
		source.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		source.setProductInfoStatus(ProductInfoStatus.INFO);
		source.setCpqCharacteristicName(CSTIC_NAME);
		source.setCpqCharacteristicAssignedValues(VALUE_NAME);
	}

	@Test
	public void testPopulate()
	{
		classUnderTest.populate(source, target);
		assertFalse(target.isEmpty());
		assertEquals(1, target.size());
		assertEquals(CSTIC_NAME, target.get(0).getConfigurationLabel());
		assertEquals(VALUE_NAME, target.get(0).getConfigurationValue());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, target.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.INFO, target.get(0).getStatus());
	}

	@Test
	public void testPopulateDifferentConfigurator()
	{
		source.setConfiguratorType(ConfiguratorType.valueOf("YET_ANOTHER_CONFIGURATOR"));
		classUnderTest.populate(source, target);
		assertTrue(target.isEmpty());
	}
}
