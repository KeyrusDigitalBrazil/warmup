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
package de.hybris.platform.cmsfacades.synchronization;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.exception.ValidationException;


/**
 * Facade for managing Synchronization.
 */
public interface SynchronizationFacade
{
	/**
	 * Get a synchronization status by {@link SyncRequestData}.
	 *
	 * @param syncJobRequest
	 *           the synchronization request data
	 * @return the {@link SyncJobData}
	 * @throws ValidationException
	 *            if there any validation errors.
	 */
	SyncJobData getSynchronizationByCatalogSourceTarget(SyncRequestData syncJobRequest) throws ValidationException;


	/**
	 * Get the status of the last synchronization job by {@link SyncRequestData}. Information is retrieved based on the catalog version target.
	 *
	 * @param syncJobRequest
	 * 				the synchronization request data
	 * @return the {@link SyncJobData}
	 * @throws ValidationException
	 * 				if there are any validation errors
	 */
	SyncJobData getLastSynchronizationByCatalogTarget(SyncRequestData syncJobRequest) throws ValidationException;

	/**
	 * Attempts to create a synchronization job. It may not succeed due to internal constraints, and in this case it will
	 * throw an exception.
	 *
	 * @param syncJobRequest
	 *           the synchronization request data
	 * @return the synchronization job status
	 * @throws SynchronizationInProgressException
	 *            when the there is already a synchronization in progress
	 * @throws CMSItemNotFoundException
	 *            when it cannot find one of the catalog versions defined in the request.
	 */
	SyncJobData createCatalogSynchronization(SyncRequestData syncJobRequest)
			throws SynchronizationInProgressException, CMSItemNotFoundException;
}
