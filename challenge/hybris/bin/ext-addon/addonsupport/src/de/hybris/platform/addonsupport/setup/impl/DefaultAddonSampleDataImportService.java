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
package de.hybris.platform.addonsupport.setup.impl;

import de.hybris.platform.addonsupport.setup.AddOnSampleDataImportService;
import de.hybris.platform.addonsupport.setup.events.AddonSampleDataImportedEvent;
import de.hybris.platform.commerceservices.dataimport.AbstractDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AddOnSampleDataImportService}
 */
public class DefaultAddonSampleDataImportService extends AbstractSystemSetup implements AddOnSampleDataImportService
{
	private static final String BEGIN_IMPORTING_STORE_MSG = "Begin importing store";
	private static final String INITIALIZING_JOB_MSG = "initializing job";
	private static final String SYNCHRONIZING_MSG = "synchronizing";
	private static final String STORES_URL = "/stores/";
	private static final String CONTENT_CATALOGS_URL = "/contentCatalogs/";
	private static final String PRODUCT_CATALOGS_URL = "/productCatalogs/";
	private static final String IMPORT_URL = "/import";
	private static final String IMPORT_SAMPLE_DATA = "importSampleData";
	private static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

	private ConfigurationService configurationService;

	@Override
	public void importSampleData(final String extensionName, final SystemSetupContext context, final List<ImportData> importData,
			final boolean solrReindex)
	{
		doImportSampleData(extensionName, context, importData, solrReindex, false);

		final SystemSetupContext eventContext = new SystemSetupContext(context.getParameterMap(), context.getType(),
				context.getProcess(), extensionName);
		getEventService().publishEvent(new AddonSampleDataImportedEvent(eventContext, importData));
	}

	@Override
	public void importSampleDataTriggeredByAddon(final String extensionName, final SystemSetupContext context,
			final List<ImportData> importData, final boolean solrReindex)
	{
		doImportSampleData(extensionName, context, importData, solrReindex, true);
	}

	protected void doImportSampleData(final String extensionName, final SystemSetupContext context,
			final List<ImportData> importData, final boolean solrReindex, final boolean triggeredByAddon)
	{
		if (getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA))
		{
			String importRoot = "/" + extensionName + IMPORT_URL;

			if (triggeredByAddon)
			{
				importRoot = importRoot + "/addons/" + context.getExtensionName();
			}

			importCommonData(context, importRoot);

			for (final ImportData importd : importData)
			{
				importProductCatalog(context, importRoot, importd.getProductCatalogName());
			}

			for (final ImportData importd : importData)
			{
				for (final String contentCatalogName : importd.getContentCatalogNames())
				{
					importContentCatalog(context, importRoot, contentCatalogName);
					importStore(context, importRoot, contentCatalogName);
				}
			}

			for (final ImportData importd : importData)
			{
				for (final String storeName : importd.getStoreNames())
				{
					importStoreLocations(context, importRoot, storeName);

				}

				importStoreInitialData(context, importRoot, importd.getStoreNames(), importd.getProductCatalogName(),
						importd.getContentCatalogNames(), solrReindex);

			}
		}
	}

	/**
	 * Imports Common Data
	 */
	protected void importCommonData(final SystemSetupContext context, final String importRoot)
	{
		logInfo(context, "Importing Common Data...");

		importImpexFile(context, importRoot + "/common/user-groups.impex", false);

		// support a general script for extra uncategorized stuff
		importImpexFile(context, importRoot + "/common/common-addon-extra.impex", false);

	}

	protected boolean isExtensionLoaded(final List<String> loadedExtensionNames, final String extensionNameToCheck)
	{
		return loadedExtensionNames.contains(extensionNameToCheck);
	}

	protected void importProductCatalog(final SystemSetupContext context, final String importRoot, final String catalogName)
	{
		logInfo(context, "Begin importing Product Catalog [" + catalogName + "]");

		// Load Units
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/classifications-units.impex",
				false);

		// Load Categories
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/categories.impex", false);

		importImpexFile(context,
				importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/categories-classifications.impex", false);

		// Load Suppliers
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/suppliers.impex", false);
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/suppliers-media.impex", false);

		// Load medias for Categories as Suppliers loads some new Categories
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/categories-media.impex", false);

		// Load Products
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products.impex", false);

		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-media.impex", false);
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-classifications.impex",
				false);

		// Load Products Relations
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-relations.impex",
				false);

		// Load Products Fixes
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-fixup.impex", false);

		// Load Prices
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-prices.impex", false);

		// Load Stock Levels
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-stocklevels.impex",
				false);

		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-pos-stocklevels.impex",
				false);

		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-tax.impex", false);


		// Load Users Access Rights for specific catalog
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/users.impex", false);

		// support a general script for extra uncategorized script
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + catalogName + "ProductCatalog/products-addon-extra.impex",
				false);

	}

	protected void importContentCatalog(final SystemSetupContext context, final String importRoot, final String catalogName)
	{
		logInfo(context, "Begin importing Content Catalog [" + catalogName + "]");

		if (ResponsiveUtils.isResponsive())
		{
			final String responsiveContentFile = importRoot + CONTENT_CATALOGS_URL + catalogName
					+ "ContentCatalog/cms-responsive-content.impex";
			if (getClass().getResourceAsStream(responsiveContentFile) != null)
			{
				importImpexFile(context, responsiveContentFile, false);
			}
			else
			{
				importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-content.impex", false);
			}
		}
		else
		{
			importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-content.impex", false);

			if (getConfigurationService().getConfiguration().getBoolean(AbstractDataImportService.IMPORT_MOBILE_DATA, false))
			{
				importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-mobile-content.impex",
						false);
			}
		}

		importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/email-content.impex", false);

		// support a general script for extra uncategorized script
		importImpexFile(context, importRoot + CONTENT_CATALOGS_URL + catalogName + "ContentCatalog/cms-addon-extra.impex", false);

		logInfo(context, "Done importing Content Catalog [" + catalogName + "]");
	}

	protected void importStoreLocations(final SystemSetupContext context, final String importRoot, final String storeName)
	{
		logInfo(context, BEGIN_IMPORTING_STORE_MSG + " [" + storeName + "]");

		importImpexFile(context, importRoot + STORES_URL + storeName + "/user-groups.impex", false);

		importImpexFile(context, importRoot + STORES_URL + storeName + "/points-of-service-media.impex", false);
		importImpexFile(context, importRoot + STORES_URL + storeName + "/points-of-service.impex", false);

		// support a general script for extra uncategorized script
		importImpexFile(context, importRoot + STORES_URL + storeName + "/points-of-service-addon-extra.impex", false);

		logInfo(context, "Done importing store [" + storeName + "]");
	}



	protected void importStore(final SystemSetupContext context, final String importRoot, final String storeName)
	{
		logInfo(context, BEGIN_IMPORTING_STORE_MSG + " [" + storeName + "]");

		importImpexFile(context, importRoot + STORES_URL + storeName + "/store.impex", false);
		importImpexFile(context, importRoot + STORES_URL + storeName + "/site.impex", false);

		logInfo(context, "Done importing store [" + storeName + "]");
	}

	protected void importStoreInitialData(final SystemSetupContext context, final String importRoot, final List<String> storeNames,
			final String productCatalog, final List<String> contentCatalogs, final boolean solrReindex)
	{
		for (final String storeName : storeNames)
		{

			logInfo(context, BEGIN_IMPORTING_STORE_MSG + " [" + storeName + "]");

			logInfo(context, "Begin importing warehouses for [" + storeName + "]");

			importImpexFile(context, importRoot + STORES_URL + storeName + "/warehouses.impex", false);
		}

		// perform product sync job
		final boolean productSyncSuccess = synchronizeProductCatalog(context, productCatalog, true);
		if (!productSyncSuccess)
		{
			logInfo(context, "Product catalog synchronization for [" + productCatalog
					+ "] did not complete successfully, that's ok, we will rerun it after the content catalog sync.");
		}

		for (final String storeName : storeNames)
		{
			importImpexFile(context, importRoot + STORES_URL + storeName + "/solr.impex", false);
		}

		synchronizeContent(context, productCatalog, contentCatalogs, productSyncSuccess);

		// Load reviews after synchronization is done
		importImpexFile(context, importRoot + PRODUCT_CATALOGS_URL + productCatalog + "ProductCatalog/reviews.impex", false);

		processStoreNames(context, importRoot, storeNames, productCatalog, solrReindex);
	}

	protected void synchronizeContent(final SystemSetupContext context, final String productCatalog,
			final List<String> contentCatalogs, final boolean productSyncSuccess)
	{
		// perform content sync jobs
		for (final String contentCatalog : contentCatalogs)
		{
			synchronizeContentCatalog(context, contentCatalog, true);
		}

		if (!productSyncSuccess)
		{
			// Rerun the product sync if required
			logInfo(context, "Rerunning product catalog synchronization for [" + productCatalog + "]");
			if (!synchronizeProductCatalog(context, productCatalog, true))
			{
				logError(context, "Rerunning product catalog synchronization for [" + productCatalog
						+ "], failed please consult logs for more details.", null);
			}
		}
	}

	protected void processStoreNames(final SystemSetupContext context, final String importRoot, final List<String> storeNames,
			final String productCatalog, final boolean solrReindex)
	{
		for (final String storeName : storeNames)
		{
			// Load promotions after synchronization is done
			importImpexFile(context, importRoot + STORES_URL + storeName + "/promotions.impex", false);

			// Load consents
			importImpexFile(context, importRoot + STORES_URL + storeName + "/consents.impex", false);

			if (solrReindex)
			{
				// Index product data
				logInfo(context, "Begin SOLR re-index [" + storeName + "]");
				executeSolrIndexerCronJob(storeName + "Index", true);
				logInfo(context, "Done SOLR re-index [" + storeName + "]");
			}

			if (getBooleanSystemSetupParameter(context, ACTIVATE_SOLR_CRON_JOBS))
			{
				logInfo(context, "Activating SOLR index job for [" + productCatalog + "]");
				activateSolrIndexerCronJobs(productCatalog + "Index");
			}
		}
	}


	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA, "Import Sample Data", true));
		params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", true));

		return params;
	}

	protected boolean synchronizeProductCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
	{
		logInfo(context,
				"Begin synchronizing Product Catalog [" + catalogName + "] - " + (sync ? SYNCHRONIZING_MSG : INITIALIZING_JOB_MSG));

		createProductCatalogSyncJob(context, catalogName + "ProductCatalog");

		boolean result = true;

		if (sync)
		{
			final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName + "ProductCatalog");
			if (isSyncRerunNeeded(syncCronJobResult))
			{
				logInfo(context, "Product catalog [" + catalogName + "] sync has issues.");
				result = false;
			}
		}

		logInfo(context, "Done " + (sync ? SYNCHRONIZING_MSG : INITIALIZING_JOB_MSG) + " Product Catalog [" + catalogName + "]");
		return result;
	}

	protected boolean synchronizeContentCatalog(final SystemSetupContext context, final String catalogName, final boolean sync)
	{
		logInfo(context,
				"Begin synchronizing Content Catalog [" + catalogName + "] - " + (sync ? SYNCHRONIZING_MSG : INITIALIZING_JOB_MSG));

		createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

		boolean result = true;

		if (sync)
		{
			final PerformResult syncCronJobResult = executeCatalogSyncJob(context, catalogName + "ContentCatalog");
			if (isSyncRerunNeeded(syncCronJobResult))
			{
				logInfo(context, "Catalog catalog [" + catalogName + "] sync has issues.");
				result = false;
			}
		}

		logInfo(context, "Done " + (sync ? SYNCHRONIZING_MSG : INITIALIZING_JOB_MSG) + " Content Catalog [" + catalogName + "]");
		return result;
	}


	protected boolean isExtensionLoaded(final String extensionNameToCheck)
	{
		final List<String> loadedExtensionNames = getLoadedExtensionNames();
		return loadedExtensionNames.contains(extensionNameToCheck);
	}

	protected List<String> getLoadedExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
