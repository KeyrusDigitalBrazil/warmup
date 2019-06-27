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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.List;


public class CatalogVersionSyncJobModelBuilder
{
	private final CatalogVersionSyncJobModel model;

	private CatalogVersionSyncJobModelBuilder()
	{
		model = new CatalogVersionSyncJobModel();
	}

	private CatalogVersionSyncJobModelBuilder(final CatalogVersionSyncJobModel model)
	{
		this.model = model;
	}

	protected CatalogVersionSyncJobModel getModel()
	{
		return this.model;
	}

	public static CatalogVersionSyncJobModelBuilder aModel()
	{
		return new CatalogVersionSyncJobModelBuilder();
	}

	public static CatalogVersionSyncJobModelBuilder fromModel(final CatalogVersionSyncJobModel model)
	{
		return new CatalogVersionSyncJobModelBuilder(model);
	}

	public CatalogVersionSyncJobModelBuilder withTargetCatalogVersion(final CatalogVersionModel targetCatalogVersion)
	{
		getModel().setTargetVersion(targetCatalogVersion);
		return this;
	}

	public CatalogVersionSyncJobModelBuilder withSourceCatalogVersion(final CatalogVersionModel sourceCatalogVersion)
	{
		getModel().setSourceVersion(sourceCatalogVersion);
		return this;
	}

	public CatalogVersionSyncJobModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public CatalogVersionSyncJobModelBuilder withActive(final boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public CatalogVersionSyncJobModelBuilder withSyncPrincipalsOnly(final boolean syncPrincipalsOnly)
	{
		getModel().setSyncPrincipalsOnly(syncPrincipalsOnly);
		return this;
	}

	public CatalogVersionSyncJobModelBuilder withSyncPrincipals(final List<PrincipalModel> syncPrincipals)
	{
		getModel().setSyncPrincipals(syncPrincipals);
		return this;
	}

	public CatalogVersionSyncJobModel build()
	{
		return this.getModel();
	}
}
