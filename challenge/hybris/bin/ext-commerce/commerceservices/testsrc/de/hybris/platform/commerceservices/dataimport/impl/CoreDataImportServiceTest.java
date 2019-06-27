/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.dataimport.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.config.impl.HybrisConfiguration;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.validation.services.ValidationService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@UnitTest
public class CoreDataImportServiceTest
{
	private static final String TEST_EXTENSION_NAME = "testExtension";
	private static final String TEST_PRODUCT_CATALOG_NAME = "test";
	private static final String TEST_CONTENT_CATALOG_NAME = "test";
	private static final String TEST_STORE_NAME = "test";
	private static final String RESPONSIVE_CONTENT_FILE = String.format(
			"/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-responsive-content.impex", TEST_EXTENSION_NAME,
			TEST_CONTENT_CATALOG_NAME);

	private static final String RESPONSIVE_STORE_FILE = String.format("/%s/import/coredata/stores/%s/store-responsive.impex",
			TEST_EXTENSION_NAME, TEST_STORE_NAME);
	private static final String RESPONSIVE_SITE_FILE = String.format("/%s/import/coredata/stores/%s/site-responsive.impex",
			TEST_EXTENSION_NAME, TEST_STORE_NAME);


	private CoreDataImportService coreDataImportService;

	@Mock
	private AbstractSystemSetup setup;

	@Mock
	private SystemSetupContext ctx;

	@Mock
	private ValidationService validation;

	@Mock
	private SetupImpexService setupImpexService;

	@Mock
	private SetupSyncJobService setupSyncJobService;

	@Mock
	private SetupSolrIndexerService setupSolrIndexerService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private EventService eventService;

	@Mock
	private ConfigurationService configurationService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		coreDataImportService = Mockito.spy(new CoreDataImportService());

		Mockito.doReturn(setupImpexService).when(coreDataImportService).getSetupImpexService();
		Mockito.doReturn(setupSyncJobService).when(coreDataImportService).getSetupSyncJobService();
		Mockito.doReturn(setupSolrIndexerService).when(coreDataImportService).getSetupSolrIndexerService();
		Mockito.doReturn(catalogVersionService).when(coreDataImportService).getCatalogVersionService();
		Mockito.doReturn(eventService).when(coreDataImportService).getEventService();
		Mockito.doReturn(configurationService).when(coreDataImportService).getConfigurationService();

		Mockito.doReturn(TEST_EXTENSION_NAME).when(ctx).getExtensionName();
		Mockito.doReturn(validation).when(coreDataImportService).getBeanForName("validationService");

		Mockito.doReturn(Boolean.TRUE).when(setup).getBooleanSystemSetupParameter(ctx, CoreDataImportService.IMPORT_CORE_DATA);
		Mockito.doReturn(Boolean.TRUE).when(setup)
				.getBooleanSystemSetupParameter(ctx, CoreDataImportService.ACTIVATE_SOLR_CRON_JOBS);
	}

	@Test
	public void testImportExecution()
	{
		final InOrder order = Mockito.inOrder(setupImpexService, setupSyncJobService, setupSolrIndexerService, validation);
		final Configuration configuration = Mockito.mock(HybrisConfiguration.class);

		final ImportData sampleImportData = new ImportData();
		sampleImportData.setProductCatalogName(TEST_PRODUCT_CATALOG_NAME);
		sampleImportData.setContentCatalogNames(Arrays.asList(TEST_CONTENT_CATALOG_NAME));
		sampleImportData.setStoreNames(Arrays.asList(TEST_STORE_NAME));

		// Set the responsive Flag to FALSE
		given(Boolean.valueOf(coreDataImportService.isResponsive())).willReturn(Boolean.FALSE);

		// Not import mobile data
		given(coreDataImportService.getConfigurationService().getConfiguration()).willReturn(configuration);
		given(Boolean.valueOf(configuration.getBoolean(CoreDataImportService.IMPORT_MOBILE_DATA, false))).willReturn(Boolean.FALSE);
		coreDataImportService.execute(setup, ctx, Arrays.asList(sampleImportData));

		verifyImportCommonData(order);
		verifyImportProductCatalog(order);
		verifyImportContentCatalog(order, 0, 1, 0);
		verifyImportStore(order, 0, 1);
		verifyImportSolrIndex(order);

		// Import mobile data
		given(Boolean.valueOf(configuration.getBoolean(CoreDataImportService.IMPORT_MOBILE_DATA, false))).willReturn(Boolean.TRUE);
		coreDataImportService.execute(setup, ctx, Arrays.asList(sampleImportData));

		verifyImportContentCatalog(order, 0, 1, 1);
	}

	@Test
	public void testImportExecutionWithNoResponsiveImpexFiles()
	{
		final InOrder order = Mockito.inOrder(setupImpexService, setupSyncJobService, setupSolrIndexerService, validation);

		final ImportData sampleImportData = new ImportData();
		sampleImportData.setProductCatalogName(TEST_PRODUCT_CATALOG_NAME);
		sampleImportData.setContentCatalogNames(Arrays.asList(TEST_CONTENT_CATALOG_NAME));
		sampleImportData.setStoreNames(Arrays.asList(TEST_STORE_NAME));

		// Set the responsive Flag to TURE, and mock as if no responsive impex files are present.
		given(Boolean.valueOf(coreDataImportService.isResponsive())).willReturn(Boolean.TRUE);
		Mockito.doReturn(null).when(coreDataImportService).getInputStream(RESPONSIVE_CONTENT_FILE);
		Mockito.doReturn(null).when(coreDataImportService).getInputStream(RESPONSIVE_SITE_FILE);
		Mockito.doReturn(null).when(coreDataImportService).getInputStream(RESPONSIVE_STORE_FILE);

		coreDataImportService.execute(setup, ctx, Arrays.asList(sampleImportData));

		verifyImportCommonData(order);
		verifyImportProductCatalog(order);
		verifyImportContentCatalog(order, 0, 1, 0);
		verifyImportStore(order, 0, 1);
		verifyImportSolrIndex(order);
	}

	@Test
	public void testImportExecutionWithResponsiveImpexFiles() throws FileNotFoundException
	{
		final InOrder order = Mockito.inOrder(setupImpexService, setupSyncJobService, setupSolrIndexerService, validation);

		final ImportData sampleImportData = new ImportData();
		sampleImportData.setProductCatalogName(TEST_PRODUCT_CATALOG_NAME);
		sampleImportData.setContentCatalogNames(Arrays.asList(TEST_CONTENT_CATALOG_NAME));
		sampleImportData.setStoreNames(Arrays.asList(TEST_STORE_NAME));

		// Set the responsive Flag to TURE, and mock as if responsive impex files are present.
		given(Boolean.valueOf(coreDataImportService.isResponsive())).willReturn(Boolean.TRUE);
		Mockito.doReturn(Mockito.mock(FileInputStream.class)).when(coreDataImportService).getInputStream(RESPONSIVE_CONTENT_FILE);
		Mockito.doReturn(Mockito.mock(FileInputStream.class)).when(coreDataImportService).getInputStream(RESPONSIVE_SITE_FILE);
		Mockito.doReturn(Mockito.mock(FileInputStream.class)).when(coreDataImportService).getInputStream(RESPONSIVE_STORE_FILE);

		coreDataImportService.execute(setup, ctx, Arrays.asList(sampleImportData));

		verifyImportCommonData(order);
		verifyImportProductCatalog(order);
		verifyImportContentCatalog(order, 1, 0, 0);
		verifyImportStore(order, 1, 0);
		verifyImportSolrIndex(order);
	}

	private void verifyImportCommonData(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/coredata/common/essential-data.impex", TEST_EXTENSION_NAME), true);
	}

	private void verifyImportProductCatalog(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/coredata/productCatalogs/%sProductCatalog/catalog.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);
	}

	private void verifyImportContentCatalog(final InOrder order, final int numberOfTimesResponsiveFilesImportInvoked,
			final int numberOfTimesDesktopFilesImportInvoked, final int numberOfTimesMobileFilesImportInvoked)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/catalog.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);

		order.verify(setupImpexService, Mockito.times(numberOfTimesResponsiveFilesImportInvoked)).importImpexFile(
				RESPONSIVE_CONTENT_FILE, false);

		order.verify(setupImpexService, Mockito.times(numberOfTimesDesktopFilesImportInvoked)).importImpexFile(
				String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-content.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);
		order.verify(setupImpexService, Mockito.times(numberOfTimesMobileFilesImportInvoked)).importImpexFile(
				String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/cms-mobile-content.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);

		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/coredata/contentCatalogs/%sContentCatalog/email-content.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);
	}

	private void verifyImportStore(final InOrder order, final int numberOfTimesResponsiveFilesImportInvoked,
			final int numberOfTimesNonResponsiveFilesImportInvoked)
	{
		order.verify(setupImpexService, Mockito.times(numberOfTimesNonResponsiveFilesImportInvoked)).importImpexFile(
				String.format("/%s/import/coredata/stores/%s/store.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);
		order.verify(setupImpexService, Mockito.times(numberOfTimesResponsiveFilesImportInvoked)).importImpexFile(
				RESPONSIVE_STORE_FILE, false);

		order.verify(setupImpexService, Mockito.times(numberOfTimesNonResponsiveFilesImportInvoked)).importImpexFile(
				String.format("/%s/import/coredata/stores/%s/site.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);
		order.verify(setupImpexService, Mockito.times(numberOfTimesResponsiveFilesImportInvoked)).importImpexFile(
				RESPONSIVE_SITE_FILE, false);
	}

	private void verifyImportSolrIndex(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/coredata/stores/%s/solr.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);

		order.verify(setupSolrIndexerService).createSolrIndexerCronJobs(String.format("%sIndex", TEST_STORE_NAME));

		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/coredata/stores/%s/solrtrigger.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);
	}
}
