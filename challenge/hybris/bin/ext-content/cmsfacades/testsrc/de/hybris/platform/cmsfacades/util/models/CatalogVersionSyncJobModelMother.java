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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.cmsfacades.util.builder.CatalogVersionSyncJobModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.SyncJobDao;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class CatalogVersionSyncJobModelMother extends AbstractModelMother<CatalogVersionSyncJobModel>
{
	public static final String PHONE_SYNC_JOB = "phoneSyncJob";

	private SyncJobDao syncJobDao;
	private CatalogVersionModelMother catalogVersionModelMother;

	public CatalogVersionSyncJobModel createSyncJob(
			final String code,
			final CatalogVersionModel target,
			final CatalogVersionModel source,
			final boolean active,
			final boolean syncPrincipalsOnly,
			final List<PrincipalModel> syncPrincipals)
	{
		return getFromCollectionOrSaveAndReturn(() -> {
			return getSyncJobDao().getSyncJobsByCode(code);
		}, () -> {
			return CatalogVersionSyncJobModelBuilder.aModel() //
					.withActive(active) //
					.withSourceCatalogVersion(source) //
					.withTargetCatalogVersion(target) //
					.withSyncPrincipals(syncPrincipals) //
					.withSyncPrincipalsOnly(syncPrincipalsOnly) //
					.build();
		});
	}

	public CatalogVersionSyncJobModel createPhoneSyncJobFromOnlineToStaged()
	{
		return createSyncJob(PHONE_SYNC_JOB, getOnlinePhoneCatalog(), getStagedPhoneCatalog(), true, false,
				Collections.emptyList());
	}

	protected CatalogVersionModel getOnlinePhoneCatalog()
	{
		return catalogVersionModelMother.createPhoneOnlineCatalogVersionModel();
	}

	protected CatalogVersionModel getStagedPhoneCatalog()
	{
		return catalogVersionModelMother.createPhoneStaged1CatalogVersionModel();
	}

	protected SyncJobDao getSyncJobDao()
	{
		return syncJobDao;
	}

	@Required
	public void setSyncJobDao(final SyncJobDao syncJobDao)
	{
		this.syncJobDao = syncJobDao;
	}

	protected CatalogVersionModelMother getCatalogVersionModelMother()
	{
		return catalogVersionModelMother;
	}

	@Required
	public void setCatalogVersionModelMother(CatalogVersionModelMother catalogVersionModelMother)
	{
		this.catalogVersionModelMother = catalogVersionModelMother;
	}
}
