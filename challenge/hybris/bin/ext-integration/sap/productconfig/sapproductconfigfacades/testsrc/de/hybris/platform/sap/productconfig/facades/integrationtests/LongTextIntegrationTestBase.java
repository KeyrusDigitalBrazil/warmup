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
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.cache.CacheKeyGenerator;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@ManualTest
public class LongTextIntegrationTestBase extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(LongTextIntegrationTestBase.class);

	@Resource(name = "sapProductConfigClassificationSystemCPQAttributesCacheAccess")
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<String, ClassificationSystemCPQAttributesContainer>> classificationSystemCPQAttributesCache;

	@Resource(name = "sapProductConfigCacheKeyGenerator")
	private CacheKeyGenerator keyGenerator;

	private static final String LONG_TEXT_FOR_CSTIC = "Customized long text description for the ";
	private static final String LONG_TEXT_FOR_CSTIC_VALUE = "Customer defined new text for ";

	@Override
	public void initProviders()
	{
		ensureMockProvider();
	}

	@Before
	public void setUp() throws Exception
	{
		clearCache();
		prepareCPQData();
	}

	@After
	public void clearCache()
	{
		classificationSystemCPQAttributesCache
				.remove(keyGenerator.createClassificationSystemCPQAttributesCacheKey(PRODUCT_CODE_CPQ_LAPTOP));
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCsv("/sapproductconfigfacades/test/sapProductConfig_longText_testData.impex", "utf-8");
	}

	@Test
	public void testGetHybrisLongTextForCsticAndValue()
	{
		final ConfigurationData defaultConfiguration = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		assertNotNull(defaultConfiguration);
		final List<UiGroupData> csticGroupsFlat = defaultConfiguration.getCsticGroupsFlat();
		assertEquals(3, csticGroupsFlat.size());
		final UiGroupData uiGroupData = csticGroupsFlat.get(0);
		final List<CsticData> cstics = uiGroupData.getCstics();
		assertNotNull(cstics);
		final CsticData display = cstics.get(0);

		assertEquals(LONG_TEXT_FOR_CSTIC + "display", display.getLongText());
		List<CsticValueData> values = display.getDomainvalues();
		assertNotNull(values);
		assertEquals(LONG_TEXT_FOR_CSTIC_VALUE + "display 15", values.get(1).getLongText());

		final CsticData memory = cstics.get(2);
		assertEquals(LONG_TEXT_FOR_CSTIC + "memory", memory.getLongText());
		values = memory.getDomainvalues();
		assertNotNull(values);
		assertEquals(LONG_TEXT_FOR_CSTIC_VALUE + "memory 32GB", values.get(0).getLongText());
	}

	@Test
	public void testGetLongTextForCstic()
	{
		final ConfigurationData defaultConfiguration = cpqFacade.getConfiguration(KB_KEY_CONF_PIPE);
		assertNotNull(defaultConfiguration);
		final List<UiGroupData> csticGroupsFlat = defaultConfiguration.getCsticGroupsFlat();
		assertEquals(1, csticGroupsFlat.size());
		final UiGroupData uiGroupData = csticGroupsFlat.get(0);
		final List<CsticData> cstics = uiGroupData.getCstics();
		assertNotNull(cstics);
		final CsticData type = cstics.get(0);
		final String actualText = type.getLongText();
		assertNotNull(actualText);
		assertEquals("Pipe Type Long\n\nText Line 2", actualText.toString().trim());
	}

}
