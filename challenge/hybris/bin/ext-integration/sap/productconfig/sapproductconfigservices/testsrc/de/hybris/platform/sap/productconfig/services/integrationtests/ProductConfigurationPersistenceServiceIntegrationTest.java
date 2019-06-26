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
package de.hybris.platform.sap.productconfig.services.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;

import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class ProductConfigurationPersistenceServiceIntegrationTest extends CPQServiceLayerTest
{
	private static final int SIZE_UNBOUND_CONFIGS = 10;
	private static final int SIZE_PRODUCT_BOUND_CONFIGS = 3;

	private static final int PAGE_SIZE = 2;
	private static final int LAST_PAGE = 1;

	@Resource(name = "sapProductConfigProductConfigurationPersistenceService")
	private ProductConfigurationPersistenceService productConfigurationPersistenceService;

	@Resource(name = "sapProductConfigPersistenceConfigurationProductLinkStrategy")
	protected ConfigurationProductLinkStrategy productEntryLinkStrategy;

	@Resource(name = "sapProductConfigPersistenceConfigurationLifecycleStrategy")
	protected ConfigurationLifecycleStrategy configurationLifecycleStrategy;



	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();


		//create configurations that are not product bound
		IntStream.rangeClosed(1, SIZE_UNBOUND_CONFIGS)
				.forEach(a -> configurationLifecycleStrategy.createDefaultConfiguration(KB_CPQ_LAPTOP));

		//create product bound product configs
		final ConfigModel configurationLaptop = configurationLifecycleStrategy.createDefaultConfiguration(KB_CPQ_LAPTOP);
		productEntryLinkStrategy.setConfigIdForProduct(PRODUCT_CODE_CPQ_LAPTOP, configurationLaptop.getId());

		final ConfigModel configurationSimple = configurationLifecycleStrategy.createDefaultConfiguration(KB_Y_SAP_SIMPLE_POC);
		productEntryLinkStrategy.setConfigIdForProduct(PRODUCT_CODE_YSAP_SIMPLE_POC, configurationSimple.getId());

		final ConfigModel configurationHomeTheater = configurationLifecycleStrategy.createDefaultConfiguration(KB_CPQ_HOME_THEATER);
		productEntryLinkStrategy.setConfigIdForProduct(PRODUCT_CODE_CPQ_HOME_THEATER, configurationHomeTheater.getId());

	}

	@Test
	public void testPaginatedQueryFirstPage()
	{
		final SearchPageData<ProductConfigurationModel> productRelatedByThreshold = productConfigurationPersistenceService
				.getProductRelatedByThreshold(0, PAGE_SIZE, 0);
		assertNotNull(productRelatedByThreshold);

		//check paged results
		final List<ProductConfigurationModel> results = productRelatedByThreshold.getResults();
		assertNotNull(results);
		assertEquals(PAGE_SIZE, results.size());
		final ProductConfigurationModel productConfigurationModel = results.get(0);
		assertNotNull(productConfigurationModel.getConfigurationId());

		//check total result number
		assertEquals(SIZE_PRODUCT_BOUND_CONFIGS, productRelatedByThreshold.getPagination().getTotalNumberOfResults());


	}

	@Test
	public void testPaginatedQueryMultiplePages()
	{
		//check results for first page
		checkPage(0);

		//last page
		final SearchPageData<ProductConfigurationModel> productRelatedByThreshold = productConfigurationPersistenceService
				.getProductRelatedByThreshold(0, PAGE_SIZE, LAST_PAGE);

		assertEquals(SIZE_PRODUCT_BOUND_CONFIGS % PAGE_SIZE, productRelatedByThreshold.getResults().size());
	}

	@Test
	public void testPaginatedQuerySelectPastEntries()
	{
		final SearchPageData<ProductConfigurationModel> productRelatedByThreshold = productConfigurationPersistenceService
				.getProductRelatedByThreshold(1, PAGE_SIZE, 0);

		//check paged results: Nothing expected
		assertEquals(0, productRelatedByThreshold.getResults().size());

		//check total result number: Nothing expected
		assertEquals(0, productRelatedByThreshold.getPagination().getTotalNumberOfResults());
	}

	protected void checkPage(final int currentPage)
	{
		final SearchPageData<ProductConfigurationModel> productRelatedByThreshold = productConfigurationPersistenceService
				.getProductRelatedByThreshold(0, PAGE_SIZE, currentPage);

		//check paged results
		assertEquals(PAGE_SIZE, productRelatedByThreshold.getResults().size());
	}

}
