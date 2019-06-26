/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.catalog.daos.CatalogDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.warehousing.util.builder.CatalogModelBuilder;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class Catalogs extends AbstractItems<CatalogModel>
{
	public static final String ID_PRIMARY = "primary";

	private CatalogDao catalogDao;
	private CatalogVersions catalogVersions;

	public CatalogModel Primary()
	{
		final CatalogModel catalogModel = getOrSaveAndReturn(() -> getCatalogDao().findCatalogById(ID_PRIMARY),
				() -> CatalogModelBuilder.aModel().withDefaultCatalog(Boolean.TRUE).withId(ID_PRIMARY)
						.withName(ID_PRIMARY, Locale.ENGLISH).build());

		if (catalogModel.getActiveCatalogVersion() == null)
		{
			catalogModel.setActiveCatalogVersion(getCatalogVersions().Staging(catalogModel));
			getModelService().save(catalogModel);
		}
		return catalogModel;
	}

	public CatalogDao getCatalogDao()
	{
		return catalogDao;
	}

	@Required
	public void setCatalogDao(final CatalogDao catalogDao)
	{
		this.catalogDao = catalogDao;
	}

	protected CatalogVersions getCatalogVersions()
	{
		return catalogVersions;
	}

	@Required
	public void setCatalogVersions(final CatalogVersions catalogVersions)
	{
		this.catalogVersions = catalogVersions;
	}
}
