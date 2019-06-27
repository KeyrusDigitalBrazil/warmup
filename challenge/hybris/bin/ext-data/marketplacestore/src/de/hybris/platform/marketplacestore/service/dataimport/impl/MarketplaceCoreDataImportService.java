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

import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;

/**
 * Marketplace core data import service
 *
 */
public class MarketplaceCoreDataImportService extends CoreDataImportService
{
	@Override
	protected void importContentCatalog(final String extensionName, final String contentCatalogName)
	{
		super.importContentCatalog(extensionName, contentCatalogName);

		getSetupImpexService().importImpexFile(String.format(
				"/%s/import/coredata/contentCatalogs/marketplaceContentCatalog/marketplace-cms-responsive-content.impex",
				extensionName), false);
	}
}
