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
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.enums.ProductConfigurationPersistenceCleanUpMode;
import de.hybris.platform.sap.productconfig.services.job.PersistenceCleanUpJob;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationPersistenceCleanUpCronJobModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class PersistenceCleanUpJobIntegrationTest extends CPQServiceLayerTest
{
	private static final Logger LOG = Logger.getLogger(PersistenceCleanUpJobIntegrationTest.class);

	private static final int NUMBER_UNBOUND_CONFIGS = 5;
	private static final int NUMBER_PRODUCT_BOUND_CONFIGS = 2;
	private static final int NUMBER_CART_BOUND_CONFIGS = 1;

	@Resource(name = "cronJobService")
	protected CronJobService cronJobService;

	@Resource(name = "sapProductConfigDefaultPersistenceCleanUpJob")
	protected PersistenceCleanUpJob persistenceCleanUpJob;

	@Resource(name = "sapProductConfigProductLinkStrategy")
	protected ConfigurationProductLinkStrategy productEntryLinkStrategy;

	@Resource(name = "sapProductConfigPersistentLifecycleTestChecker")
	protected LifecycleStrategiesTestChecker persistenceTestChecker;


	@Before
	public void setUp() throws Exception
	{
		assumeTrue("This test can only be executed with persitent Lifecycle", isPersistentLifecycle());
		prepareCPQData();
		importCronJobTestData();

		//create configurations that are not product bound
		IntStream.rangeClosed(1, NUMBER_UNBOUND_CONFIGS)
				.forEach(a -> configurationLifecycleStrategy.createDefaultConfiguration(KB_CPQ_LAPTOP));

		//create product bound product configs
		final ConfigModel configurationLaptop = configurationLifecycleStrategy.createDefaultConfiguration(KB_CPQ_LAPTOP);
		productEntryLinkStrategy.setConfigIdForProduct(PRODUCT_CODE_CPQ_LAPTOP, configurationLaptop.getId());

		final ConfigModel configurationSimple = configurationLifecycleStrategy.createDefaultConfiguration(KB_Y_SAP_SIMPLE_POC);
		productEntryLinkStrategy.setConfigIdForProduct(PRODUCT_CODE_YSAP_SIMPLE_POC, configurationSimple.getId());

		//create cart bound configuration
		final ProductModel product = productService.getProductForCode(PRODUCT_CODE_CPQ_HOME_THEATER);
		final CartEntryModel newEntry = cartService.addNewEntry(cartService.getSessionCart(), product, 1, product.getUnit());
		modelService.save(newEntry);
		final ConfigModel configurationTheater = configurationLifecycleStrategy.createDefaultConfiguration(KB_CPQ_HOME_THEATER);
		cpqAbstractOrderEntryLinkStrategy.setConfigIdForCartEntry(newEntry.getPk().toString(), configurationTheater.getId());
	}

	@Test
	public void testPerformCleanUpProductBound()
	{
		final ProductConfigurationPersistenceCleanUpCronJobModel cronJobModel = (ProductConfigurationPersistenceCleanUpCronJobModel) cronJobService
				.getCronJob("sapProductConfigPersistenceCleanUpCronJob");
		assertNotNull(cronJobModel);
		cronJobModel.setCleanUpMode(ProductConfigurationPersistenceCleanUpMode.ONLYPRODUCTRELATED);
		executeCronJobAndCheck(cronJobModel, NUMBER_UNBOUND_CONFIGS + NUMBER_CART_BOUND_CONFIGS);
	}

	@Test
	public void testPerformCleanUpProductBoundModifiedInPast()
	{
		final ProductConfigurationPersistenceCleanUpCronJobModel cronJobModel = (ProductConfigurationPersistenceCleanUpCronJobModel) cronJobService
				.getCronJob("sapProductConfigPersistenceCleanUpCronJob");
		cronJobModel.setThresholdDays(1);
		cronJobModel.setCleanUpMode(ProductConfigurationPersistenceCleanUpMode.ONLYPRODUCTRELATED);
		executeCronJobAndCheck(cronJobModel, NUMBER_UNBOUND_CONFIGS + NUMBER_PRODUCT_BOUND_CONFIGS + NUMBER_CART_BOUND_CONFIGS);
	}

	@Test
	public void testPerformCleanUpOrphaned()
	{
		final ProductConfigurationPersistenceCleanUpCronJobModel cronJobModel = (ProductConfigurationPersistenceCleanUpCronJobModel) cronJobService
				.getCronJob("sapProductConfigPersistenceCleanUpCronJob");
		assertNotNull(cronJobModel);
		cronJobModel.setCleanUpMode(ProductConfigurationPersistenceCleanUpMode.ONLYORPHANED);
		executeCronJobAndCheck(cronJobModel, NUMBER_PRODUCT_BOUND_CONFIGS + NUMBER_CART_BOUND_CONFIGS);
	}

	@Test
	public void testPerformCleanUpOrphanedThresholdMustNotMatter()
	{
		final ProductConfigurationPersistenceCleanUpCronJobModel cronJobModel = (ProductConfigurationPersistenceCleanUpCronJobModel) cronJobService
				.getCronJob("sapProductConfigPersistenceCleanUpCronJob");
		assertNotNull(cronJobModel);
		cronJobModel.setThresholdDays(1);
		cronJobModel.setCleanUpMode(ProductConfigurationPersistenceCleanUpMode.ONLYORPHANED);
		executeCronJobAndCheck(cronJobModel, NUMBER_PRODUCT_BOUND_CONFIGS + NUMBER_CART_BOUND_CONFIGS);
	}

	@Test
	public void testPerformCleanUpAll()
	{
		final ProductConfigurationPersistenceCleanUpCronJobModel cronJobModel = (ProductConfigurationPersistenceCleanUpCronJobModel) cronJobService
				.getCronJob("sapProductConfigPersistenceCleanUpCronJob");
		assertNotNull(cronJobModel);
		cronJobModel.setCleanUpMode(ProductConfigurationPersistenceCleanUpMode.ALL);
		executeCronJobAndCheck(cronJobModel, NUMBER_CART_BOUND_CONFIGS);
	}


	protected void executeCronJobAndCheck(final ProductConfigurationPersistenceCleanUpCronJobModel cronJobModel,
			final int expectedNumberOfPersistedConfigs)
	{
		persistenceTestChecker
				.checkNumberOfConfigsPersisted(NUMBER_UNBOUND_CONFIGS + NUMBER_PRODUCT_BOUND_CONFIGS + NUMBER_CART_BOUND_CONFIGS);
		final PerformResult result = persistenceCleanUpJob.perform(cronJobModel);
		checkResult(result);
		persistenceTestChecker.checkNumberOfConfigsPersisted(expectedNumberOfPersistedConfigs);
	}

	protected void checkResult(final PerformResult result)
	{
		assertNotNull(result);
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	protected void importCronJobTestData() throws ImpExException, Exception
	{
		LOG.info("CREATING CRONJOB DATA FOR CPQ-TEST....");
		importCsv("/sapproductconfigservices/test/sapProductConfig_cronjob.impex", "utf-8");
	}


}
