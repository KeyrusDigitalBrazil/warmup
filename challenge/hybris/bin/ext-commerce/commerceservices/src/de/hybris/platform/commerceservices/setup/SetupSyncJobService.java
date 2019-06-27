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
package de.hybris.platform.commerceservices.setup;

import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Set;


/**
 * Service that handles creating synchronization jobs.
 */
public interface SetupSyncJobService
{
	/**
	 * Ensure that a product catalog sync job exists for the specified catalog id. The sync job is created between the
	 * Staged and Online catalog versions only if there is no existing sync job.
	 * 
	 * @param catalogId
	 *           the catalog id to search sync job for.
	 */
	void createProductCatalogSyncJob(String catalogId);

	/**
	 * Ensure that a cms content catalog sync job exists for the specified catalog id. The sync job is created between
	 * the Staged and Online catalog versions only if there is no existing sync job.
	 * 
	 * @param catalogId
	 *           the catalog id
	 */
	void createContentCatalogSyncJob(String catalogId);

	/**
	 * Sets up a dependency relationship between the CatalogVersionSyncJob for a catalog and the CatalogVersionSyncJobs for a set of dependant catalogs.
	 *
	 * @param catalogId
	 *           the catalog id
	 * @param dependentCatalogIds
	 *           the dependant catalog ids
	 */
	void assignDependentSyncJobs(String catalogId, Set<String> dependentCatalogIds);

	/**
	 * Run the catalog sync for the specified catalog.
	 * 
	 * @param catalogId
	 *           the catalog id
	 * @return an instance of {@link PerformResult} containing the sync job result and status
	 */
	PerformResult executeCatalogSyncJob(String catalogId);
}
