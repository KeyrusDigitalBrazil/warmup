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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSConfigurationChangeAdapter;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.GenericTestConfigModelImpl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSConfigurationChangeAdapterImplPrepareChangeTest
{

	GenericTestConfigModelImpl genericTestModel;
	CPSConfigurationChangeAdapter classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new CPSConfigurationChangeAdapterImpl();
	}

	protected Properties retrieveModelAsProperties(final String resourcePath)
	{

		final Properties properties = new Properties();

		final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
		if (null == resourceAsStream)
		{
			throw new IllegalArgumentException("Configuration file for geric model not found using respurePath: " + resourcePath);
		}
		try (BufferedInputStream stream = new BufferedInputStream(resourceAsStream))
		{
			properties.load(stream);
		}
		catch (final IOException e)
		{
			throw new IllegalArgumentException("not possible to load properties from resource: " + resourcePath, e);
		}
		return properties;
	}

	@Test
	public void testPrepareChangedConfiguration() throws Exception
	{
		final Properties properties = retrieveModelAsProperties("testproperties/testModel.properties");
		genericTestModel = new GenericTestConfigModelImpl(properties);
		final ConfigModel configModel = genericTestModel.createDefaultConfiguration();
		assertNotNull(configModel);

		// mark as changed cstics C1 and C3 from root instance
		final CsticModel c1 = configModel.getRootInstance().getCstic("C1");
		c1.setChangedByFrontend(true);
		final CsticModel c3 = configModel.getRootInstance().getCstic("C3");
		c3.setChangedByFrontend(true);

		// mark as changed cstics C1 and C3 from root.SI2.SI22 sub-instance
		final CsticModel si22C1 = configModel.getRootInstance().getSubInstances().get(1).getSubInstances().get(1).getCstic("C1");
		si22C1.setChangedByFrontend(true);
		final CsticModel si22C3 = configModel.getRootInstance().getSubInstances().get(1).getSubInstances().get(1).getCstic("C3");
		si22C3.setChangedByFrontend(true);

		final CPSConfiguration changedConfiguration = classUnderTest.prepareChangedConfiguration(configModel);
		assertNotNull(changedConfiguration);
		assertEquals(GenericTestConfigModelImpl.CONFIG_ID, changedConfiguration.getId());
		assertEquals(GenericTestConfigModelImpl.VERSION, changedConfiguration.getETag());

		// check root
		final CPSItem root = changedConfiguration.getRootItem();
		final List<CPSCharacteristic> rootCstics = root.getCharacteristics();
		assertFalse(rootCstics.isEmpty());
		checkChangedCstics(rootCstics);

		// check root.si1
		final CPSItem si1 = root.getSubItems().get(0);
		final List<CPSCharacteristic> si1Cstics = si1.getCharacteristics();
		assertTrue(si1Cstics.isEmpty());

		// check root.si2.si22
		final CPSItem si22 = root.getSubItems().get(1).getSubItems().get(1);
		final List<CPSCharacteristic> si22Cstics = si22.getCharacteristics();
		assertFalse(si22Cstics.isEmpty());
		checkChangedCstics(si22Cstics);
	}

	private void checkChangedCstics(final List<CPSCharacteristic> cstics)
	{
		assertEquals(2, cstics.size());

		final CPSCharacteristic c1 = cstics.get(0);
		assertEquals("C1", c1.getId());
		final List<CPSValue> c1Values = c1.getValues();
		assertEquals(3, c1Values.size());

		final CPSValue c1Value1 = c1Values.get(0);
		assertEquals("V1", c1Value1.getValue());
		assertTrue(c1Value1.isSelected());

		final CPSValue c1Value2 = c1Values.get(1);
		assertEquals("V2", c1Value2.getValue());
		assertFalse(c1Value2.isSelected());

		final CPSValue c1Value3 = c1Values.get(2);
		assertEquals("V3", c1Value3.getValue());
		assertTrue(c1Value3.isSelected());

		final CPSCharacteristic c3 = cstics.get(1);
		assertEquals("C3", c3.getId());
		final List<CPSValue> c3Values = c3.getValues();
		assertEquals(1, c3Values.size());
		final CPSValue c3Value = c3Values.get(0);
		assertNotNull(c3Value.getValue());
		assertEquals("1.111", c3Value.getValue());
		assertTrue(c3Value.isSelected());
	}


}
