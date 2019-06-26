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
package de.hybris.platform.sap.productconfig.services.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPagingUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class ProductConfigAfterInitializationStartEventListenerTest
{
	private static final String CONFIG_ID_1 = "cID_1";
	private static final String VERSION_1 = "version_1";
	private static final String CONFIG_ID_2 = "cID_2";
	private static final String VERSION_2 = "version_2";

	private ProductConfigAfterInitializationStartEventListenerForTest classUnderTest;
	private ProductConfigAfterInitializationStartEventListener realClassUnderTest;
	private ProductConfigurationPagingUtil productConfigurationPagingUtil;

	@Mock
	private ProductConfigurationPersistenceService productConfigurationPersistenceService;
	@Mock
	private ConfigurationProvider configurationProvider;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ProductConfigEventListenerUtil eventUtil;


	private AfterInitializationStartEvent evt;
	private ProductConfigurationModel model;
	private BaseSiteModel testBaseSite;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigAfterInitializationStartEventListenerForTest();
		realClassUnderTest = new ProductConfigAfterInitializationStartEventListener();
		productConfigurationPagingUtil = new ProductConfigurationPagingUtil();

		evt = new AfterInitializationStartEvent();
		evt.setParams(Collections.singletonMap(ProductConfigAfterInitializationStartEventListener.INIT_METHOD_PARAM_NAME,
				ProductConfigAfterInitializationStartEventListener.INIT_MODE));

		// mock config search result
		final SearchPageData searchPageData = new SearchPageData<>();
		searchPageData.setResults(new ArrayList());
		model = new ProductConfigurationModel();
		model.setConfigurationId(CONFIG_ID_1);
		model.setVersion(VERSION_1);
		searchPageData.getResults().add(model);
		final ProductConfigurationModel model2 = new ProductConfigurationModel();
		model2.setConfigurationId(CONFIG_ID_2);
		model2.setVersion(VERSION_2);
		searchPageData.getResults().add(model2);

		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(2);
		pagination.setNumberOfPages(2);
		searchPageData.setPagination(pagination);
		given(productConfigurationPersistenceService.getAll(ProductConfigurationPagingUtil.PAGE_SIZE, 0))
				.willReturn(searchPageData);
		testBaseSite = new BaseSiteModel();
		given(eventUtil.getBaseSiteFromCronJob()).willReturn(testBaseSite);


	}

	@Test
	public void testCreateEmptyResult()
	{
		final SearchPageData<ProductConfigurationModel> result = classUnderTest.createEmptyResult();
		assertNotNull(result);
		assertNotNull(result.getResults());
		assertEquals(0, result.getResults().size());
		assertNotNull(result.getPagination());
		assertEquals(0, result.getPagination().getCurrentPage());
		assertEquals(0, result.getPagination().getNumberOfPages());
		assertEquals(0, result.getPagination().getTotalNumberOfResults());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetConfigurationProviderCPSNotImplemented()
	{
		realClassUnderTest.getConfigurationProviderCPS();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProductConfigurationPagingUtilNotImplemented()
	{
		realClassUnderTest.getProductConfigurationPagingUtil();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProductConfigurationPersistenceServiceNotImplemented()
	{
		realClassUnderTest.getProductConfigurationPersistenceService();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProductConfigEventListenerUtilNotImplemented()
	{
		realClassUnderTest.getProductConfigEventListenerUtil();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetBaseSiteServiceNotImplemented()
	{
		realClassUnderTest.getBaseSiteService();
	}

	@Test
	public void testOnEventNotInitialiazed()
	{
		// test only works if unit tests are executed without starting server
		assumeFalse(ServicelayerUtils.isSystemInitialized());
		classUnderTest.onEvent(evt);
		verifyZeroInteractions(configurationProvider, eventUtil, baseSiteService, productConfigurationPersistenceService);
	}

	@Test
	public void testOnEventInternalUpdate()
	{
		evt.setParams(
				Collections.singletonMap(ProductConfigAfterInitializationStartEventListener.INIT_METHOD_PARAM_NAME, "update"));
		classUnderTest.onEventInternal(evt);
		verifyZeroInteractions(configurationProvider, eventUtil, baseSiteService, productConfigurationPersistenceService);
	}

	@Test
	public void testOnEventInternalInit()
	{
		classUnderTest.onEventInternal(evt);
		verify(configurationProvider).releaseSession(CONFIG_ID_1, VERSION_1);
		verify(configurationProvider).releaseSession(CONFIG_ID_2, VERSION_2);
		verify(baseSiteService).setCurrentBaseSite(testBaseSite, false);
	}

	@Test
	public void testOnEventPrepareFails()
	{
		given(eventUtil.getBaseSiteFromCronJob()).willReturn(null);
		classUnderTest.onEvent(evt);
		verifyZeroInteractions(baseSiteService, configurationProvider);
	}

	@Test
	public void testOnEventNothingFound()
	{
		given(productConfigurationPersistenceService.getAll(ProductConfigurationPagingUtil.PAGE_SIZE, 0))
				.willReturn(classUnderTest.createEmptyResult());
		classUnderTest.onEvent(evt);
		verifyZeroInteractions(baseSiteService, configurationProvider);
	}

	@Test
	public void testprepeareCleanUpeWrongProvider()
	{
		classUnderTest.cpsProvider = configurationProvider;
		assertFalse(classUnderTest.prepeareCleanUp());
		verifyZeroInteractions(baseSiteService);
	}


	@Test
	public void testPrepeareCleanUpNoBaseSite()
	{
		given(eventUtil.getBaseSiteFromCronJob()).willReturn(null);
		assertFalse(classUnderTest.prepeareCleanUp());
		verifyZeroInteractions(baseSiteService);
	}

	@Test
	public void testOnEventInternalHandlesException()
	{
		willThrow(RuntimeException.class).given(configurationProvider).releaseSession(anyString(), anyString());
		classUnderTest.onEventInternal(evt);
		verify(configurationProvider, times(2)).releaseSession(anyString(), anyString());
	}

	@Test
	public void testReleaseConfigException()
	{
		willThrow(RuntimeException.class).given(configurationProvider).releaseSession(anyString(), anyString());
		final StringBuilder builder = new StringBuilder();
		classUnderTest.releaseConfig(configurationProvider, model, builder);
		assertEquals(CONFIG_ID_1 + ProductConfigAfterInitializationStartEventListener.ID_SEPERATOR, builder.toString());
	}

	@Test
	public void testReleaseConfigIgnoreCreatedByMock()
	{
		model.setKbLogsys("MOCK");
		classUnderTest.releaseConfig(configurationProvider, model, null);
		verifyZeroInteractions(configurationProvider);
	}

	@Test
	public void testGetConfigsNotFirstPageDoesNotExecutesPrepare()
	{
		classUnderTest.getConfigs(1);
		verifyZeroInteractions(eventUtil, baseSiteService);
	}

	@Test
	public void testLogConfigIds()
	{
		final String allIds = classUnderTest.collectAllConfigIds();
		assertTrue(allIds.contains(CONFIG_ID_1));
		assertTrue(allIds.contains(CONFIG_ID_2));
	}


	private class ProductConfigAfterInitializationStartEventListenerForTest
			extends ProductConfigAfterInitializationStartEventListener
	{

		public ConfigurationProvider cpsProvider = new CPSConfiguratioProviderForTest();


		@Override
		protected ProductConfigurationPersistenceService getProductConfigurationPersistenceService()
		{
			return productConfigurationPersistenceService;
		}

		@Override
		protected ConfigurationProvider getConfigurationProviderCPS()
		{
			return cpsProvider;
		}

		@Override
		protected ProductConfigurationPagingUtil getProductConfigurationPagingUtil()
		{
			return productConfigurationPagingUtil;
		}

		@Override
		protected ProductConfigEventListenerUtil getProductConfigEventListenerUtil()
		{
			return eventUtil;
		}

		@Override
		protected BaseSiteService getBaseSiteService()
		{
			return baseSiteService;
		}
	}

	/**
	 * productive code checks whether the provider name contains 'CPS', jence we can not mock it diretly, but need a
	 * class with containing 'CPS' in it's name that delegates to the mock
	 */
	private class CPSConfiguratioProviderForTest implements ConfigurationProvider
	{

		@Override
		public ConfigModel createDefaultConfiguration(final KBKey kbKey)
		{
			return null;
		}

		@Override
		public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
		{
			return false;
		}

		@Override
		public String changeConfiguration(final ConfigModel model) throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
		{
			return null;
		}

		@Override
		public void releaseSession(final String configId)
		{
			configurationProvider.releaseSession(configId);
		}

		@Override
		public void releaseSession(final String configId, final String version)
		{
			configurationProvider.releaseSession(configId, version);
		}

		@Override
		public boolean isKbForDateExists(final String productCode, final Date kbDate)
		{
			return false;
		}
	}
}
