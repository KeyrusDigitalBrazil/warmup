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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOverviewPopulator;
import de.hybris.platform.sap.productconfig.facades.populator.VariantOverviewPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationOverviewFacadeImplTest
{
	public ConfigurationOverviewFacadeImpl classUnderTest;

	private static final String CONFIG_ID = "config_id";
	private static final String PRODUCT_CODE = "4711";

	@Mock
	private ConfigurationOverviewPopulator configurationOverviewPopulator;

	@Mock
	private VariantOverviewPopulator variantOverviewPopulator;

	@Mock
	private ProductConfigurationService productConfigurationService;

	@Mock
	private ProductService productService;

	@Mock
	private PricingService pricingService;

	private final ConfigModel configModel = ConfigurationTestData.createConfigModel();;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationOverviewFacadeImpl();
		classUnderTest.setConfigurationOverviewPopulator(configurationOverviewPopulator);
		classUnderTest.setVariantOverviewPopulator(variantOverviewPopulator);
		classUnderTest.setConfigurationService(productConfigurationService);
		classUnderTest.setProductService(productService);
		classUnderTest.setPricingService(pricingService);
		Mockito.when(productConfigurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(configModel);
	}


	@Test
	public void testGetOverviewForConfigurationNull()
	{
		ConfigurationOverviewData configOverviewData = null;
		configOverviewData = classUnderTest.getOverviewForConfiguration(CONFIG_ID, configOverviewData);
		assertNotNull(configOverviewData);
	}

	@Test
	public void testGetVaraintForProductVariantNull()
	{
		ConfigurationOverviewData configOverviewData = null;
		configOverviewData = classUnderTest.getOverviewForProductVariant(PRODUCT_CODE, configOverviewData);
		assertNotNull(configOverviewData);
	}

	@Test
	public void testGetOverviewForConfiguration_pricingServiceActive()
	{
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.TRUE);
		classUnderTest.getOverviewForConfiguration(CONFIG_ID, null);
		Mockito.verify(pricingService).fillOverviewPrices(configModel);
	}

	@Test
	public void testGetOverviewForConfiguration_pricingServiceInactive()
	{
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.FALSE);
		classUnderTest.getOverviewForConfiguration(CONFIG_ID, null);
		Mockito.verify(pricingService, Mockito.times(0)).fillOverviewPrices(configModel);
	}
}
