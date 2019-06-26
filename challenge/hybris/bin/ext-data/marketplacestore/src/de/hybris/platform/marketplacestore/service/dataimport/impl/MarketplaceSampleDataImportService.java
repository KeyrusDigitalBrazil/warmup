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
package de.hybris.platform.marketplacestore.service.dataimport.impl;

import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.marketplacestore.constants.MarketplacestoreConstants;


/**
 * Marketplace sample data import
 */
public class MarketplaceSampleDataImportService extends SampleDataImportService
{
	private static final String MARKETPLACE = "marketplace";

	@Override
	protected void importContentCatalog(final String extensionName, final String contentCatalogName)
	{
		super.importContentCatalog(extensionName, contentCatalogName);

		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/contentCatalogs/marketplaceContentCatalog/marketplace-cms-responsive-content.impex",
				extensionName), false);
	}

	@Override
	protected void importStore(final String extensionName, final String storeName, final String productCatalogName)
	{
		super.importStore(extensionName, storeName, productCatalogName);

		if (MARKETPLACE.equals(storeName))
		{

			getSetupImpexService().importImpexFile(
					String.format("/%s/import/sampledata/productCatalogs/globalMarketplaceProductCatalog/products-vendors.impex",
							extensionName),
					false);
		}
	}

	@Override
	protected void importCommonData(final String extensionName)
	{
		// We need to import vendor catalog version first as they need to be
		// assigned to many user groups

		getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/marketplace/vendors.impex", extensionName),
				false);

		super.importCommonData(extensionName);
		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/marketplace/vendors-catalogs.impex", extensionName), false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/marketplace/vendors-media.impex", extensionName), false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/marketplace/vendors-usergroups.impex", extensionName), false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/marketplace/marketplace-users.impex", extensionName), false);

		getSetupImpexService().importImpexFile(String.format("/%s/import/sampledata/marketplace/vendorusers.impex", extensionName),
				false);

		getSetupImpexService()
				.importImpexFile(String.format("/%s/import/sampledata/marketplace/vendor-warehouses.impex", extensionName), false);

	}

	@Override
	protected void importAllData(final AbstractSystemSetup systemSetup, final SystemSetupContext context,
			final ImportData importData, final boolean syncCatalogs)
	{
		super.importAllData(systemSetup, context, importData, syncCatalogs);

		// We need to import below three items after sync as they all need
		// product categories in online version.
		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/marketplace/vendors-category.impex", MarketplacestoreConstants.EXTENSIONNAME),
				false);

		getSetupImpexService().importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/globalMarketplaceProductCatalog/products-category.impex",
						MarketplacestoreConstants.EXTENSIONNAME),
				false);

		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/sampledata/productCatalogs/globalMarketplaceProductCatalog/vendor-products-classifications.impex",
				MarketplacestoreConstants.EXTENSIONNAME), false);
	}
}
