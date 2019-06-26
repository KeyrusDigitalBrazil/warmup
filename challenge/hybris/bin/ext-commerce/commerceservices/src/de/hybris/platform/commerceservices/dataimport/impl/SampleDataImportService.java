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

import de.hybris.platform.commerceservices.dataimport.AbstractDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.io.InputStream;
import java.util.List;


/**
 * Extension of {@link AbstractDataImportService} that defines services related to sample data.
 */
public class SampleDataImportService extends AbstractDataImportService
{
	public static final String IMPORT_SAMPLE_DATA = "importSampleData";
	public static final String CUSTOMER_SUPPORT_BACKOFFICE_EXTENSION_NAME = "customersupportbackoffice";
	public static final String ORDER_MANAGEMENT_BACKOFFICE_EXTENSION_NAME = "ordermanagementbackoffice";
	public static final String ASSISTED_SERVICE_EXTENSION_NAME = "assistedservicestorefront";

	@Override
	public void execute(final AbstractSystemSetup systemSetup, final SystemSetupContext context, final List<ImportData> importData)
	{
		final boolean importSampleData = systemSetup.getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA);

		if (importSampleData)
		{
			for (final ImportData data : importData)
			{
				importAllData(systemSetup, context, data, true);
			}
		}
	}

	@Override
	protected void importCommonData(final String extensionName)
	{

		if (isExtensionLoaded(CUSTOMER_SUPPORT_BACKOFFICE_EXTENSION_NAME)
				|| isExtensionLoaded(ORDER_MANAGEMENT_BACKOFFICE_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/backoffice/customersupport/customersupport-groups.impex", extensionName),
					false);

			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/backoffice/customersupport/customersupport-users.impex", extensionName),
					false);
			getSetupImpexService().importImpexFile(String.format(
					"/%s/import/sampledata/backoffice/customersupport/customersupport-savedqueries.impex", extensionName), false);
			getSetupImpexService().importImpexFile(String.format(
					"/%s/import/sampledata/backoffice/customersupport/customersupport-accessrights.impex", extensionName), false);
			getSetupImpexService().importImpexFile(String.format(
					"/%s/import/sampledata/backoffice/customersupport/customersupport-restrictions.impex", extensionName), false);
		}

		if (isExtensionLoaded(CUSTOMER_SUPPORT_BACKOFFICE_EXTENSION_NAME) && isExtensionLoaded(ASSISTED_SERVICE_EXTENSION_NAME))
		{
			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/backoffice/customersupport/customersupport-assistedservice-groups.impex",
							extensionName),
					false);
		}
	}

	@Override
	protected void importProductCatalog(final String extensionName, final String productCatalogName)
	{
		// Load Units
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/classifications-units.impex",
						extensionName, productCatalogName), false);

		// Load Categories
		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/productCatalogs/%sProductCatalog/categories.impex", extensionName, productCatalogName), false);

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories-classifications.impex",
						extensionName, productCatalogName),
				false);

		// Load Suppliers
		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/productCatalogs/%sProductCatalog/suppliers.impex", extensionName, productCatalogName), false);
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/suppliers-media.impex",
						extensionName, productCatalogName), false);

		// Load medias for Categories as Suppliers loads some new Categories
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories-media.impex",
						extensionName, productCatalogName), false);

		// Load Products
		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/productCatalogs/%sProductCatalog/products.impex", extensionName, productCatalogName), false);
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-media.impex",
						extensionName, productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-classifications.impex", extensionName,
						productCatalogName),
				false);

		// Load Products Relations
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-relations.impex",
						extensionName, productCatalogName), false);

		// Load Products Fixes
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-fixup.impex",
						extensionName, productCatalogName), false);

		// Load Prices
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-prices.impex",
						extensionName, productCatalogName), false);

		// Load Stock Levels
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-stocklevels.impex",
						extensionName, productCatalogName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-pos-stocklevels.impex", extensionName,
						productCatalogName),
				false);

		// Load Taxes
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-tax.impex",
						extensionName, productCatalogName), false);

		// Load Multi-Dimensial Products
		importMultiDProductCatalog(extensionName, productCatalogName);

	}

	protected void importMultiDProductCatalog(final String extensionName, final String productCatalogName)
	{
		// Load Multi-Dimension Categories
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-categories.impex",
						extensionName, productCatalogName),
				false);
		// Load Multi-Dimension Products
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products.impex",
						extensionName, productCatalogName),
				false);
		// Load Multi-Dimension Products-Media
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products-media.impex",
						extensionName, productCatalogName),
				false);
		// Load Multi-Dimension Products-Prices
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products-prices.impex",
						extensionName, productCatalogName),
				false);
		// Load Multi-Dimension Products-Stocklevels
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products-stock-levels.impex",
						extensionName, productCatalogName),
				false);
		// Load Multi-Dimension Products-Stocklevels
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products-tax.impex",
						extensionName, productCatalogName),
				false);
		// Load Multi-Dimension Products-pos-stocklevels
		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products-pos-stocklevels.impex",
				extensionName, productCatalogName), false);
		// Load Multi-Dimension Products-classifications
		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/productCatalogs/%sProductCatalog/multi-d/dimension-products-classifications.impex",
				extensionName, productCatalogName), false);
		// Load future stock for multi -D products
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-futurestock.impex",
						extensionName, productCatalogName), false);
	}

	@Override
	protected void importContentCatalog(final String extensionName, final String contentCatalogName)
	{
		if (isResponsive())
		{
			final String responsiveContentFile = String.format(
					"/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-responsive-content.impex", extensionName,
					contentCatalogName);
			if (getInputStream(responsiveContentFile) != null)
			{
				getSetupImpexService().importImpexFile(responsiveContentFile, false);
			}
			else
			{
				getSetupImpexService()
						.importImpexFile(String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-content.impex",
								extensionName, contentCatalogName), false);
			}
		}
		else
		{
			getSetupImpexService()
					.importImpexFile(String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-content.impex",
							extensionName, contentCatalogName), false);

			if (getConfigurationService().getConfiguration().getBoolean(IMPORT_MOBILE_DATA, false))
			{
				getSetupImpexService().importImpexFile(
						String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-mobile-content.impex", extensionName,
								contentCatalogName),
						false);
			}
		}

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/email-content.impex",
						extensionName, contentCatalogName), false);

	}

	@Override
	protected void importStore(final String extensionName, final String storeName, final String productCatalogName)
	{
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/points-of-service-media.impex", extensionName, storeName), false);
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/points-of-service.impex", extensionName, storeName), false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/stores/%s/warehouses.impex", extensionName, storeName), false);

		getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/reviews.impex",
				extensionName, productCatalogName), false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/stores/%s/promotions.impex", extensionName, storeName), false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/stores/%s/consents.impex", extensionName, storeName), false);
	}

	@Override
	protected void importJobs(final String extensionName, final String storeName)
	{
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/stores/%s/jobs.impex", extensionName, storeName), false);
	}

	@Override
	protected void importSolrIndex(final String extensionName, final String storeName)
	{
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/stores/%s/solr.impex", extensionName, storeName), false);

		getSetupSolrIndexerService().createSolrIndexerCronJobs(String.format("%sIndex", storeName));
	}

	protected InputStream getInputStream(final String fileName)
	{
		return getClass().getResourceAsStream(fileName);
	}

	protected boolean isResponsive()
	{
		return ResponsiveUtils.isResponsive();
	}
}
