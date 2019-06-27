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
package de.hybris.platform.powertoolsstore.services.dataimport.impl;

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;


/**
 * Implementation to handle specific Core Data Import services to Powertools.
 */
public class PowertoolsCoreDataImportService extends CoreDataImportService
{
	/**
	 * Imports store data related to Powertools. Imports a site-override impex if available.
	 *
	 * @param extensionName
	 *           the extension name used.
	 * @param storeName
	 *           the store to import for.
	 * @param productCatalogName
	 *           the product catalog used.
	 */
	@Override
	protected void importStore(final String extensionName, final String storeName, final String productCatalogName)
	{
		super.importStore(extensionName, storeName, productCatalogName);

		if (getConfigurationService().getConfiguration().getBoolean("setup.siteoverride", false))
		{
			getSetupImpexService()
					.importImpexFile(String.format("/%s/import/coredata/stores/%s/site-override.impex", extensionName, storeName), false);
		}
	}
}
