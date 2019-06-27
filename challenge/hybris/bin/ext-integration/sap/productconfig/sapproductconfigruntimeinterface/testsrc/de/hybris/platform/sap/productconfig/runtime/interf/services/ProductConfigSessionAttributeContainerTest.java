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
package de.hybris.platform.sap.productconfig.runtime.interf.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyAnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyPricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


@UnitTest
public class ProductConfigSessionAttributeContainerTest
{

	ProductConfigSessionAttributeContainer classUnderTest = new ProductConfigSessionAttributeContainer();

	@Test
	public void testGetClassificationSystemCPQAttributes()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> result = classUnderTest
				.getClassificationSystemCPQAttributes();
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetConfigurationModelEngineStates()
	{
		final Map<String, ConfigModel> result = classUnderTest.getConfigurationModelEngineStates();
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testSetterGetterCartEntryConfigurations()
	{
		Map<String, String> result = classUnderTest.getCartEntryConfigurations();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Map<String, String> cartEntryConfigurations = new HashMap<>();
		cartEntryConfigurations.put("key_1", "value_1");
		cartEntryConfigurations.put("key_2", "value_2");
		cartEntryConfigurations.put("key_3", "value_3");

		classUnderTest.setCartEntryConfigurations(cartEntryConfigurations);
		result = classUnderTest.getCartEntryConfigurations();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(cartEntryConfigurations, result);
	}

	@Test
	public void testSetterGetterCartEntryDraftConfigurations()
	{
		Map<String, String> result = classUnderTest.getCartEntryDraftConfigurations();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Map<String, String> cartEntryDraftConfigurations = new HashMap<>();
		cartEntryDraftConfigurations.put("key_1", "value_1");
		cartEntryDraftConfigurations.put("key_2", "value_2");
		cartEntryDraftConfigurations.put("key_3", "value_3");

		classUnderTest.setCartEntryDraftConfigurations(cartEntryDraftConfigurations);
		result = classUnderTest.getCartEntryDraftConfigurations();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(cartEntryDraftConfigurations, result);
	}

	@Test
	public void testSetterGetterCartEntryUiStatuses()
	{
		Map<String, Object> result = classUnderTest.getCartEntryUiStatuses();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Map<String, Object> cartEntryUiStatuses = new HashMap<>();
		cartEntryUiStatuses.put("key_1", new Object());
		cartEntryUiStatuses.put("key_2", new Object());
		cartEntryUiStatuses.put("key_3", new Object());

		classUnderTest.setCartEntryUiStatuses(cartEntryUiStatuses);
		result = classUnderTest.getCartEntryUiStatuses();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(cartEntryUiStatuses, result);
	}

	@Test
	public void testSetterGetterProductUiStatuses()
	{
		Map<String, Object> result = classUnderTest.getProductUiStatuses();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Map<String, Object> productUiStatuses = new HashMap<>();
		productUiStatuses.put("key_1", new Object());
		productUiStatuses.put("key_2", new Object());
		productUiStatuses.put("key_3", new Object());

		classUnderTest.setProductUiStatuses(productUiStatuses);
		result = classUnderTest.getProductUiStatuses();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(productUiStatuses, result);
	}

	@Test
	public void testSetterGetterIndexedProperties()
	{
		Set<String> result = classUnderTest.getIndexedProperties();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Set<String> indexedProperties = new HashSet<>();
		indexedProperties.add("index_1");
		indexedProperties.add("index_2");
		indexedProperties.add("index_3");

		classUnderTest.setIndexedProperties(indexedProperties);
		result = classUnderTest.getIndexedProperties();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(indexedProperties, result);
	}

	@Test
	public void testSetterGetterProductConfigurations()
	{
		Map<String, String> result = classUnderTest.getProductConfigurations();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Map<String, String> productConfigurations = new HashMap<>();
		productConfigurations.put("product_1", "config_1");
		productConfigurations.put("product_2", "config_2");
		productConfigurations.put("product_3", "config_3");

		classUnderTest.setProductConfigurations(productConfigurations);
		result = classUnderTest.getProductConfigurations();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(productConfigurations, result);
	}

	@Test
	public void testSetterGetterPriceSummaryStates()
	{
		Map<String, PriceSummaryModel> result = classUnderTest.getPriceSummaryStates();
		assertNotNull(result);
		assertTrue(result.isEmpty());

		final Map<String, PriceSummaryModel> priceSummaryStates = new HashMap<>();
		priceSummaryStates.put("key_1", new PriceSummaryModel());
		priceSummaryStates.put("key_2", new PriceSummaryModel());
		priceSummaryStates.put("key_3", new PriceSummaryModel());

		classUnderTest.setPriceSummaryStates(priceSummaryStates);
		result = classUnderTest.getPriceSummaryStates();
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(priceSummaryStates, result);
	}

	@Test
	public void testGetAnalyticDataStates()
	{
		final Map<String, AnalyticsDocument> result = classUnderTest.getAnalyticDataStates();
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testSetterGetterAnalyticData()
	{
		AnalyticsDocument result = classUnderTest.getAnalyticData("configId");
		assertNull(result);

		classUnderTest.setAnalyticData("configId", new AnalyticsDocument());
		result = classUnderTest.getAnalyticData("configId");
		assertNotNull(result);

		final Map<String, AnalyticsDocument> analyticsDataStates = classUnderTest.getAnalyticDataStates();
		assertNotNull(analyticsDataStates);
		assertFalse(analyticsDataStates.isEmpty());
		assertEquals(1, analyticsDataStates.size());
	}

	@Test
	public void testSetterGetterConfigurationProvider()
	{
		final ConfigurationProvider provider = new DummyConfigurationProvider();
		classUnderTest.setConfigurationProvider(provider);
		assertEquals(provider, classUnderTest.getConfigurationProvider());
	}

	@Test
	public void testSetterGetterPricingProvider()
	{
		final PricingProvider provider = new DummyPricingProvider();
		classUnderTest.setPricingProvider(provider);
		assertEquals(provider, classUnderTest.getPricingProvider());
	}

	@Test
	public void testSetterGetterAnalyticsProvider()
	{
		final AnalyticsProvider provider = new DummyAnalyticsProvider();
		classUnderTest.setAnalyticsProvider(provider);
		assertEquals(provider, classUnderTest.getAnalyticsProvider());
	}
}
