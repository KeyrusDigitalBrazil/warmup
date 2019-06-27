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
package de.hybris.platform.addonsupport.setup;

import de.hybris.platform.addonsupport.setup.events.AddonSampleDataImportedEvent;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.List;


/**
 * Defines services for importing addon sample data
 */
public interface AddOnSampleDataImportService
{
	/**
	 * Imports sample data
	 *
	 * @param extensionName
	 *           the extension name
	 * @param context
	 *           the system setup context
	 * @param importData
	 *           the {@link List} of {@link ImportData}
	 * @param solrReindex
	 *           whether to run the solr indexer cronjob
	 */
	void importSampleData(final String extensionName, final SystemSetupContext context, final List<ImportData> importData,
			boolean solrReindex);

	/**
	 * Imports sample data without publishing a new {@link AddonSampleDataImportedEvent}
	 *
	 * @param extensionName
	 *           the extension name
	 * @param context
	 *           the system setup context
	 * @param importData
	 *           the {@link List} of {@link ImportData}
	 * @param solrReindex
	 *           whether to run the solr indexer cronjob
	 */
	void importSampleDataTriggeredByAddon(final String extensionName, final SystemSetupContext context,
			final List<ImportData> importData, boolean solrReindex);
}
