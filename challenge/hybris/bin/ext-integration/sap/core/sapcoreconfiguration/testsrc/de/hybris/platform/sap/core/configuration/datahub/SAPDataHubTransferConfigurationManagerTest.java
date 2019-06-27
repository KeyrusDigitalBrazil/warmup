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
package de.hybris.platform.sap.core.configuration.datahub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Tests the {@link DataHubTransferConfigurationManager}.
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "SAPDataHubTransferConfigurationManagerTest-spring.xml" })
public class SAPDataHubTransferConfigurationManagerTest extends ServicelayerTransactionalTest
{

	@Resource(name = "sapCoreDataHubTransferConfigurationManager")
	private DataHubTransferConfigurationManager classUnderTest;
	private static boolean customConfigAdded = false;

	@SuppressWarnings("javadoc")
	@Before
	public void setup()
	{
		if (!customConfigAdded)
		{
			// Add custom config for BaseStore
			final DataHubTransferConfiguration dataHubTransferConfiguration = new DataHubTransferConfiguration();
			dataHubTransferConfiguration.setItemCode("BaseStore");
			dataHubTransferConfiguration.setRawType("RawSAPBaseStoreInformation");
			classUnderTest.addToDataHubTransferConfigurations(dataHubTransferConfiguration);
			customConfigAdded = true;
		}
	}

	// test testContainsTypeCode removed. please see jira ticket GRIFFIN-2817

	@SuppressWarnings("javadoc")
	@Test
	public void testGetDefaultDataHubTransferConfigurations()
	{
		assertNotNull(classUnderTest.getDataHubTransferConfigurations("BaseStore"));
		assertEquals(2, classUnderTest.getDataHubTransferConfigurations("BaseStore").size());
		assertNotNull(classUnderTest.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_CONFIGURATION));
		assertEquals(1,
				classUnderTest.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_CONFIGURATION).size());
		assertNotNull(
				classUnderTest.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_HTTP_DESTINATION));
		assertEquals(1,
				classUnderTest.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_HTTP_DESTINATION).size());
		assertNotNull(
				classUnderTest.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_GLOBAL_CONFIGURATION));
		assertEquals(1, classUnderTest
				.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_GLOBAL_CONFIGURATION).size());
		assertNull(classUnderTest.getDataHubTransferConfigurations(SapcoreconfigurationConstants.ITEM_CODE_SAP_ADMINISTRATION));
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testGetConverter()
	{
		assertNotNull(classUnderTest.getDataHubTransferConfigurations("SAPConfiguration").get(0).getConverter());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testGetRawType()
	{
		assertNotNull(classUnderTest.getDataHubTransferConfigurations("SAPConfiguration").get(0).getRawType());
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testGetMultipleDataHubTransferConfigurationsForSameTypeCode()
	{
		final List<DataHubTransferConfiguration> dataHubTransferConfigurations = classUnderTest
				.getDataHubTransferConfigurations("BaseStore");
		assertEquals(2, dataHubTransferConfigurations.size());
		assertEquals("RawSAPBaseStoreConfigurationMapping", dataHubTransferConfigurations.get(0).getRawType());
		assertEquals("RawSAPBaseStoreInformation", dataHubTransferConfigurations.get(1).getRawType());
	}

}
