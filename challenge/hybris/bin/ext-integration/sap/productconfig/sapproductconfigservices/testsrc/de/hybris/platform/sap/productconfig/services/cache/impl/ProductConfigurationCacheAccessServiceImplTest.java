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
package de.hybris.platform.sap.productconfig.services.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.cache.CacheKeyGenerator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationCacheAccessServiceImplTest
{
	private static final String CONFIG_ID = "config id";
	protected static final String TENANT_ID = "tenantId";
	private static final String ROOT_PRODUCT = "rootProduct";
	private static final String PRODUCT_CODE = "productCode";

	private ProductConfigurationCacheAccessServiceImpl classUnderTest;
	@Mock
	private CacheKeyGenerator keyGeneratorMock;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, AnalyticsDocument> analyticsDataCacheMock;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, PriceSummaryModel> priceSummaryCacheMock;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, ConfigModel> configCacheMock;
	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<String, ClassificationSystemCPQAttributesContainer>> classificationSystemCPQAttributesCacheMock;
	@Mock
	private ProductConfigurationCacheKey analyticsDataCacheKeyMock;
	@Mock
	private ProductConfigurationCacheKey priceSummaryCacheKeyMock;
	@Mock
	private ProductConfigurationCacheKey configCacheKeyMock;
	@Mock
	private ProductConfigurationCacheKey classificationSystemCPQAttributesCacheKeyMock;

	private AnalyticsDocument analyticsDocument;
	private PriceSummaryModel priceSummaryModel;
	private ConfigModel configModel;
	Map<String, ClassificationSystemCPQAttributesContainer> container;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationCacheAccessServiceImpl();
		classUnderTest.setKeyGenerator(keyGeneratorMock);
		classUnderTest.setAnalyticsCache(analyticsDataCacheMock);
		classUnderTest.setPriceSummaryCache(priceSummaryCacheMock);
		classUnderTest.setConfigCache(configCacheMock);
		classUnderTest.setClassificationSystemCPQAttributesCache(classificationSystemCPQAttributesCacheMock);

		when(keyGeneratorMock.createAnalyticsDataCacheKey(CONFIG_ID)).thenReturn(analyticsDataCacheKeyMock);
		analyticsDocument = new AnalyticsDocument();
		analyticsDocument.setRootProduct(ROOT_PRODUCT);
		when(analyticsDataCacheMock.get(analyticsDataCacheKeyMock)).thenReturn(analyticsDocument);

		when(keyGeneratorMock.createPriceSummaryCacheKey(CONFIG_ID)).thenReturn(priceSummaryCacheKeyMock);
		priceSummaryModel = new PriceSummaryModel();
		when(priceSummaryCacheMock.get(priceSummaryCacheKeyMock)).thenReturn(priceSummaryModel);

		when(keyGeneratorMock.createConfigCacheKey(CONFIG_ID)).thenReturn(configCacheKeyMock);
		configModel = new ConfigModelImpl();
		when(configCacheMock.get(configCacheKeyMock)).thenReturn(configModel);

		when(keyGeneratorMock.createClassificationSystemCPQAttributesCacheKey(PRODUCT_CODE))
				.thenReturn(classificationSystemCPQAttributesCacheKeyMock);
		container = new HashMap<>();
		when(classificationSystemCPQAttributesCacheMock.get(classificationSystemCPQAttributesCacheKeyMock)).thenReturn(container);
	}

	@Test
	public void testGetAnalyticsData()
	{
		final AnalyticsDocument result = classUnderTest.getAnalyticData(CONFIG_ID);
		assertSame(analyticsDocument, result);
		assertEquals(ROOT_PRODUCT, result.getRootProduct());
	}

	@Test
	public void testSetAnalyticsDocument()
	{
		classUnderTest.setAnalyticData(CONFIG_ID, analyticsDocument);
		verify(analyticsDataCacheMock).put(analyticsDataCacheKeyMock, analyticsDocument);
	}

	@Test
	public void testGetPriceSummaryData()
	{
		final PriceSummaryModel result = classUnderTest.getPriceSummaryState(CONFIG_ID);
		assertSame(priceSummaryModel, result);
	}

	@Test
	public void testSetPriceSummaryData()
	{
		classUnderTest.setPriceSummaryState(CONFIG_ID, priceSummaryModel);
		verify(priceSummaryCacheMock).put(priceSummaryCacheKeyMock, priceSummaryModel);
	}

	@Test
	public void testGetConfigurationModelEngineState()
	{
		final ConfigModel result = classUnderTest.getConfigurationModelEngineState(CONFIG_ID);
		assertSame(configModel, result);
	}

	@Test
	public void testSetConfigurationModelEngineState()
	{
		classUnderTest.setConfigurationModelEngineState(CONFIG_ID, configModel);
		verify(configCacheMock).put(configCacheKeyMock, configModel);
	}

	@Test
	public void testRemoveConfigModelStates()
	{
		classUnderTest.removeConfigAttributeState(CONFIG_ID);
		verify(priceSummaryCacheMock).remove(priceSummaryCacheKeyMock);
		verify(analyticsDataCacheMock).remove(analyticsDataCacheKeyMock);
		verify(configCacheMock).remove(configCacheKeyMock);
	}

	@Test
	public void testGetCachedNameMap()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> result = classUnderTest.getCachedNameMap(PRODUCT_CODE);
		assertSame(container, result);
	}

	@Test
	public void testGetCachedNameMapNull()
	{
		when(classificationSystemCPQAttributesCacheMock.get(classificationSystemCPQAttributesCacheKeyMock)).thenReturn(null,
				container);
		final Map<String, ClassificationSystemCPQAttributesContainer> result = classUnderTest.getCachedNameMap(PRODUCT_CODE);
		assertNotNull(result);
		verify(classificationSystemCPQAttributesCacheMock).putIfAbsent(same(classificationSystemCPQAttributesCacheKeyMock),
				anyMap());
	}
}
