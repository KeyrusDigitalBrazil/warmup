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
package de.hybris.platform.multicountrysampledataaddon.setup.impl;

import de.hybris.platform.addonsupport.setup.impl.DefaultAddonSampleDataImportService;
import de.hybris.platform.core.initialization.SystemSetupContext;

/**
 * This class extends {@link DefaultAddonSampleDataImportService} and specifies how to import sample data for multicountry
 * set-ups.
 */
public class MultiCountryAddOnSampleDataImportService extends DefaultAddonSampleDataImportService
{
	private static final String CONTENT_CATALOG_PATH_PREFIX = "/contentCatalogs/";

	@Override
	protected void importContentCatalog(final SystemSetupContext context, final String importRoot, final String catalogName)
	{
		// 1- Create catalog
		importImpexFile(context, importRoot + CONTENT_CATALOG_PATH_PREFIX + catalogName + "ContentCatalog/catalog.impex", false);

		// 2- Create sync jobs for the catalog
		createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

		// 3- Import content catalog from impex
		super.importContentCatalog(context, importRoot, catalogName);

		// 4- Add synchronization permissions controlling access to synchronize catalog in cmscockpit
		importImpexFile(context, importRoot + CONTENT_CATALOG_PATH_PREFIX + catalogName + "ContentCatalog/local-sync.impex", false);

		// 5- Synchronize the catalog content using the sync job defined in step 2
		synchronizeContentCatalog(context, catalogName, true);
	}
}
