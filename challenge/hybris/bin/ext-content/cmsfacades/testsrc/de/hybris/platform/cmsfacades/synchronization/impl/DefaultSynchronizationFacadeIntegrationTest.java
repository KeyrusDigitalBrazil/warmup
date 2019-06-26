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
package de.hybris.platform.cmsfacades.synchronization.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.ACTIVE_SYNC_JOB_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED1;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_PHONES;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionSyncJobModelMother;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Test;


public class DefaultSynchronizationFacadeIntegrationTest extends BaseIntegrationTest
{


	@Resource
	private DefaultSynchronizationFacade defaultSynchronizationFacade;

	@Resource
	private CatalogVersionSyncJobModelMother catalogVersionSyncJobModelMother;
	@Resource
	private UserService userService;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Test
	public void shouldNotReturnAnyErrorsIfSyncPrincipalsOnlyFalseAndPrincipalHasWritePermissionsToTargetCatalogVersion()
	{
		// GIVEN
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setTargetVersionId(ONLINE.getVersion());
		syncRequestData.setSourceVersionId(STAGED1.getVersion());
		syncRequestData.setCatalogId(ID_PHONES.name());
		catalogVersionSyncJobModelMother.createPhoneSyncJobFromOnlineToStaged();
		userService.getCurrentUser()
				.setWritableCatalogVersions(Arrays.asList(catalogVersionModelMother.createPhoneOnlineCatalogVersionModel()));

		String errorCode = null;

		// WHEN
		try
		{
			defaultSynchronizationFacade.createCatalogSynchronization(syncRequestData);
		}
		catch (final ValidationException e)
		{
			errorCode = e.getValidationObject().getFieldErrors().get(0).getCode();
		}

		// THEN
		assertNull("Should not return any errors", errorCode);
	}

	@Test
	public void shouldReturnActiveSyncJobRequiredErrorIfNoActiveJobs()
	{
		// GIVEN
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setTargetVersionId(ONLINE.getVersion());
		syncRequestData.setSourceVersionId(STAGED1.getVersion());
		syncRequestData.setCatalogId(ID_PHONES.name());
		final CatalogVersionSyncJobModel syncJob = catalogVersionSyncJobModelMother.createPhoneSyncJobFromOnlineToStaged();
		syncJob.setActive(false);

		String errorCode = null;

		// WHEN
		try
		{
			defaultSynchronizationFacade.createCatalogSynchronization(syncRequestData);
		}
		catch (final ValidationException e)
		{
			errorCode = e.getValidationObject().getFieldErrors().get(0).getCode();
		}

		// THEN
		assertThat("Should return active sync job required error", errorCode, is(ACTIVE_SYNC_JOB_REQUIRED));
	}

	@Test
	public void shouldReturnUnauthorizedUnsufficientSynchronizationAccessErrorIfSyncPrincipalsOnlyIsTrueAndPrincipalNotInSyncPrincipalsList()
	{
		// GIVEN
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setTargetVersionId(ONLINE.getVersion());
		syncRequestData.setSourceVersionId(STAGED1.getVersion());
		syncRequestData.setCatalogId(ID_PHONES.name());
		final CatalogVersionSyncJobModel syncJob = catalogVersionSyncJobModelMother.createPhoneSyncJobFromOnlineToStaged();
		syncJob.setSyncPrincipalsOnly(true);
		String errorCode = null;

		// WHEN
		try
		{
			defaultSynchronizationFacade.createCatalogSynchronization(syncRequestData);
		}
		catch (final ValidationException e)
		{
			errorCode = e.getValidationObject().getFieldErrors().get(0).getCode();
		}

		// THEN
		assertThat("Should return unauthorized synchronization insufficient access error", errorCode,
				is(UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS));
	}
}
