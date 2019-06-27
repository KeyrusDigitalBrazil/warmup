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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationDeepCopyHandlerImplTest
{
	private static final String DUMMY_EXT_CONFIG = "dummyExtConfig";
	private static final String PRODUCT_CODE = "pCode";
	private static final String CONFIG_ID = "config123";
	private static final String NEW_CONFIG_ID = "config456";

	private ConfigurationDeepCopyHandlerImpl classUnderTest;

	@Mock
	private ProductConfigurationService configurationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationDeepCopyHandlerImpl();
		classUnderTest.setConfigurationService(configurationService);

		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		configModel.setRootInstance(new InstanceModelImpl());
		configModel.getRootInstance().setName(PRODUCT_CODE);

		final ConfigModel newConfigModel = new ConfigModelImpl();
		newConfigModel.setId(NEW_CONFIG_ID);

		given(configurationService.retrieveConfigurationModel(CONFIG_ID)).willReturn(configModel);
		given(configurationService.retrieveExternalConfiguration(CONFIG_ID)).willReturn(DUMMY_EXT_CONFIG);
		final KBKey kbKey;
		given(configurationService.createConfigurationFromExternal(any(KBKey.class), eq(DUMMY_EXT_CONFIG)))
				.willReturn(newConfigModel);
		given(configurationService.createConfigurationFromExternal(any(KBKey.class), eq(DUMMY_EXT_CONFIG), anyString(), any()))
				.willReturn(newConfigModel);
	}

	@Test
	public void testDeepCopyConfigurationNoForce()
	{
		// implementation ignores force flag
		final String newConfigId = classUnderTest.deepCopyConfiguration(CONFIG_ID, PRODUCT_CODE, null, false,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		assertEquals(NEW_CONFIG_ID, newConfigId);
	}


	@Test
	public void testDeepCopyConfiguration()
	{
		final String newConfigId = classUnderTest.deepCopyConfiguration(CONFIG_ID, PRODUCT_CODE, null, true,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		verify(configurationService).createConfigurationFromExternal(any(KBKey.class), eq(DUMMY_EXT_CONFIG), (String) isNull(),
				any());

		assertNotEquals(CONFIG_ID, newConfigId);
	}

	@Test
	public void testDeepCopyConfigurationExtConfig()
	{
		final String newConfigId = classUnderTest.deepCopyConfiguration(CONFIG_ID, PRODUCT_CODE, DUMMY_EXT_CONFIG, true,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		assertNotEquals(CONFIG_ID, newConfigId);
		verify(configurationService, times(0)).retrieveExternalConfiguration(CONFIG_ID);
	}

	@Test
	public void testDeepCopyConfigurationNoProductCode()
	{
		final String newConfigId = classUnderTest.deepCopyConfiguration(CONFIG_ID, null, null, true,
				ProductConfigurationRelatedObjectType.UNKNOWN);
		assertNotEquals(CONFIG_ID, newConfigId);
	}

	@Test
	public void testPrepareRetrievalOptions()
	{
		final ConfigurationRetrievalOptions result = classUnderTest
				.prepareRetrievalOptions(ProductConfigurationRelatedObjectType.UNKNOWN);
		assertNotNull(result);
		assertEquals(ProductConfigurationRelatedObjectType.UNKNOWN, result.getRelatedObjectType());
	}


}
