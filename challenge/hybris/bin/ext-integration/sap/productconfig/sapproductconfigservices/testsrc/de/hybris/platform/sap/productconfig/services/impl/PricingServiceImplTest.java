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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PricingServiceImplTest
{
	@InjectMocks
	private PricingServiceImpl classUnderTest;

	@Mock
	private PricingProvider mockedPricingProvider;
	@Mock
	private ProviderFactory mockedProviderFactory;
	@Mock
	private ProductConfigurationCacheAccessService mockedProductConfigurationCacheAccessService;
	@Mock
	private PricingConfigurationParameter mockedPricingConfigurationParameter;
	@Mock
	private ConfigurationModelCacheStrategy mockedConfigurationModelCacheStrategy;

	@Mock
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;

	@Mock
	private ProductConfigurationService productConfigurationService;

	private static final String CONFIG_ID = "1";
	private static final String PRODUCT_CODE = "PRODUCT";
	private PriceSummaryModel priceSummaryModel;

	private final ConfigModel configModel = new ConfigModelImpl();


	@Before
	public void setUp()
	{
		priceSummaryModel = new PriceSummaryModel();
		given(mockedProviderFactory.getPricingProvider()).willReturn(mockedPricingProvider);
		when(mockedPricingConfigurationParameter.isPricingSupported()).thenReturn(true);
		when(mockedProductConfigurationCacheAccessService.getPriceSummaryState(CONFIG_ID)).thenReturn(priceSummaryModel);
		configModel.setId(CONFIG_ID);
	}

	@Test
	public void testProductConfigurationService()
	{
		assertEquals(productConfigurationService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testPricingNotFoundInCache() throws PricingEngineException, ConfigurationEngineException
	{
		when(mockedProductConfigurationCacheAccessService.getPriceSummaryState(CONFIG_ID)).thenReturn(null);
		when(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(configModel);
		when(mockedPricingProvider.getPriceSummary(Mockito.anyString(), Mockito.any())).thenReturn(priceSummaryModel);
		final PriceSummaryModel priceSummary = classUnderTest.getPriceSummary(CONFIG_ID);
		assertNotNull(priceSummary);
	}

	@Test
	public void testConfigNotFoundInCache() throws PricingEngineException, ConfigurationEngineException
	{
		when(mockedProductConfigurationCacheAccessService.getPriceSummaryState(CONFIG_ID)).thenReturn(null);
		when(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(null);
		when(productConfigurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(configModel);
		when(mockedPricingProvider.getPriceSummary(Mockito.anyString(), Mockito.any())).thenReturn(priceSummaryModel);
		final PriceSummaryModel priceSummary = classUnderTest.getPriceSummary(CONFIG_ID);
		assertNotNull(priceSummary);
	}

	@Test(expected = IllegalStateException.class)
	public void testConfigNotFoundInCacheAndNotFromService() throws PricingEngineException, ConfigurationEngineException
	{
		when(mockedProductConfigurationCacheAccessService.getPriceSummaryState(CONFIG_ID)).thenReturn(null);
		when(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(null);
		when(productConfigurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(null);
		classUnderTest.getPriceSummary(CONFIG_ID);
	}

	@Test
	public void testGetPriceSummaryFromCache() throws PricingEngineException
	{
		final PriceSummaryModel result = classUnderTest.getPriceSummary(CONFIG_ID);
		assertNotNull(result);
		assertEquals(priceSummaryModel, result);
	}

	@Test
	public void testGetPriceSummaryShouldBeCached() throws PricingEngineException, ConfigurationEngineException
	{
		final String configId = "123";
		final ConfigModel configModel = new ConfigModelImpl();
		final PriceSummaryModel newPriceSummaryModel = new PriceSummaryModel();

		when(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(configId)).thenReturn(configModel);
		when(mockedPricingProvider.getPriceSummary(Mockito.anyString(), Mockito.any())).thenReturn(newPriceSummaryModel);

		final PriceSummaryModel result = classUnderTest.getPriceSummary(configId);
		assertNotNull(result);
		assertEquals(newPriceSummaryModel, result);
		verify(mockedProductConfigurationCacheAccessService, times(1)).setPriceSummaryState(configId, newPriceSummaryModel);
	}


	public void testGetPriceSummaryWithException() throws PricingEngineException, ConfigurationEngineException
	{
		// first service call fails
		when(mockedPricingProvider.getPriceSummary(Mockito.anyString(), Mockito.any())).thenThrow(new PricingEngineException());
		assertNull(classUnderTest.getPriceSummary(CONFIG_ID));
		assertNull(mockedProductConfigurationCacheAccessService.getPriceSummaryState(CONFIG_ID));
		assertTrue(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID).hasPricingError());

		//second service call successful
		final PriceSummaryModel result = new PriceSummaryModel();
		doReturn(result).when(mockedPricingProvider).getPriceSummary(Mockito.anyString(), Mockito.any());
		assertEquals(result, classUnderTest.getPriceSummary(CONFIG_ID));
		assertEquals(result, mockedProductConfigurationCacheAccessService.getPriceSummaryState(CONFIG_ID));
		assertFalse(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(CONFIG_ID).hasPricingError());
	}

	@Test
	public void testFillValuePricesOldApi() throws PricingEngineException
	{
		final String kbId = "111";
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		classUnderTest.fillValuePrices(updateModels, kbId);
		verify(mockedPricingProvider, Mockito.times(1)).fillValuePrices(updateModels, kbId);
	}

	@Test
	public void testFillValuePricesExceptionOldApi() throws PricingEngineException
	{
		doThrow(new PricingEngineException()).when(mockedPricingProvider).fillValuePrices(Mockito.anyList(), Mockito.anyString());
		final String kbId = "111";
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		classUnderTest.fillValuePrices(updateModels, kbId);
	}

	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbId("111");
		configModel.setId(CONFIG_ID);
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		classUnderTest.fillValuePrices(updateModels, configModel);
		verify(mockedPricingProvider, Mockito.times(1)).fillValuePrices(updateModels, configModel.getKbId());
	}

	@Test
	public void testFillValuePricesException() throws PricingEngineException
	{
		doThrow(new PricingEngineException()).when(mockedPricingProvider).fillValuePrices(Mockito.anyList(), Mockito.anyString());
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbId("111");
		configModel.setId(CONFIG_ID);
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		classUnderTest.fillValuePrices(updateModels, configModel);
	}

	@Test
	public void testIsActive()
	{
		given(Boolean.valueOf(mockedPricingProvider.isActive())).willReturn(Boolean.TRUE);
		assertTrue(classUnderTest.isActive());
	}

	@Test
	public void testIsActiveNoPricingCustomized()
	{
		given(Boolean.valueOf(mockedPricingProvider.isActive())).willReturn(Boolean.TRUE);
		when(mockedPricingConfigurationParameter.isPricingSupported()).thenReturn(false);
		assertFalse(classUnderTest.isActive());
	}

	@Test
	public void testIsNotActive()
	{
		given(Boolean.valueOf(mockedPricingProvider.isActive())).willReturn(Boolean.FALSE);
		assertFalse(classUnderTest.isActive());
	}

	@Test
	public void testFillConfigPrices()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final PriceSummaryModel priceSummary = new PriceSummaryModel();
		priceSummary.setBasePrice(new PriceModelImpl());
		priceSummary.setCurrentTotalPrice(new PriceModelImpl());
		priceSummary.setSelectedOptionsPrice(new PriceModelImpl());
		classUnderTest.fillConfigPrices(priceSummary, configModel);
		assertEquals(priceSummary.getBasePrice(), configModel.getBasePrice());
		assertEquals(priceSummary.getCurrentTotalPrice(), configModel.getCurrentTotalPrice());
		assertEquals(priceSummary.getSelectedOptionsPrice(), configModel.getSelectedOptionsPrice());
	}

	@Test
	public void testFillOverviewPrices() throws PricingEngineException, ConfigurationEngineException
	{
		final PriceSummaryModel priceSummaryModel = new PriceSummaryModel();
		when(mockedPricingProvider.getPriceSummary(Mockito.anyString(), Mockito.any())).thenReturn(priceSummaryModel);
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		classUnderTest.fillOverviewPrices(configModel);
		Mockito.verify(mockedPricingProvider).fillValuePrices(configModel);
	}

	@Test
	public void testFillOverviewPricesException() throws PricingEngineException, ConfigurationEngineException
	{
		final String configId = "123";
		when(mockedPricingProvider.getPriceSummary(Mockito.anyString(), Mockito.any())).thenThrow(new PricingEngineException());
		doThrow(new PricingEngineException()).when(mockedPricingProvider).fillValuePrices(Mockito.any());
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(configId);
		when(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(configId)).thenReturn(configModel);
		classUnderTest.fillOverviewPrices(configModel);
		verify(mockedPricingProvider).fillValuePrices(configModel);
		assertTrue(configModel.hasPricingError());
	}

	@Test
	public void testPricingConfigurationParameter()
	{
		assertEquals(mockedPricingConfigurationParameter, classUnderTest.getPricingConfigurationParameter());
	}

	@Test
	public void testPrepareRetrievalOptionsWithDate()
	{
		final Date date = new Date();
		when(assignmentResolverStrategy.retrieveCreationDateForRelatedEntry(CONFIG_ID)).thenReturn(date);
		when(assignmentResolverStrategy.retrieveRelatedProductCode(CONFIG_ID)).thenReturn(PRODUCT_CODE);
		final ConfigurationRetrievalOptions options = classUnderTest.prepareRetrievalOptionsWithDate(configModel);
		assertNotNull(options);
		assertEquals(date, options.getPricingDate());
		assertNull(options.getPricingProduct());
	}

	@Test
	public void testPrepareRetrievalOptionsForProduct()
	{
		configModel.setSingleLevel(true);
		when(assignmentResolverStrategy.retrieveRelatedProductCode(CONFIG_ID)).thenReturn(PRODUCT_CODE);
		final ConfigurationRetrievalOptions options = classUnderTest.prepareRetrievalOptions(configModel);
		assertNotNull(options);
		assertEquals(PRODUCT_CODE, options.getPricingProduct());
	}

	@Test
	public void testPrepareRetrievalOptionsForProductMultilevel()
	{
		configModel.setSingleLevel(false);
		final ConfigurationRetrievalOptions options = classUnderTest.prepareRetrievalOptions(configModel);
		assertNotNull(options);
		assertNull(options.getPricingProduct());
	}
}
