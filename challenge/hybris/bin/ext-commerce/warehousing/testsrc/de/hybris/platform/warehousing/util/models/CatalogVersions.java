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

import de.hybris.platform.catalog.daos.CatalogVersionDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.warehousing.util.builder.CatalogVersionModelBuilder;

import org.springframework.beans.factory.annotation.Required;


public class CatalogVersions extends AbstractItems<CatalogVersionModel>
{
	public static final String VERSION_STAGING = "staging";
	public static final String VERSION_ONLINE = "Online";

	private CatalogVersionDao catalogVersionDao;
	private Catalogs catalogs;
	private Currencies currencies;
	private ContentCatalogs contentCatalogs;

	public CatalogVersionModel Staging()
	{
		return Staging(getCatalogs().Primary());
	}

	public CatalogVersionModel Staging(final CatalogModel catalogModel)
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getCatalogVersionDao().findCatalogVersions(Catalogs.ID_PRIMARY, VERSION_STAGING),
				() -> CatalogVersionModelBuilder.aModel().withCatalog(catalogModel)
						.withDefaultCurrency(getCurrencies().AmericanDollar()).withActive(Boolean.TRUE).withVersion(VERSION_STAGING)
						.build());
	}

	public CatalogVersionModel Online()
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getCatalogVersionDao().findCatalogVersions(ContentCatalogs.CONTENTCATALOG_ID, VERSION_ONLINE),
				() -> CatalogVersionModelBuilder.aModel().withDefaultCurrency(getCurrencies().AmericanDollar())
						.withActive(Boolean.TRUE).withVersion(VERSION_ONLINE).withCatalog(getContentCatalogs().contentCatalog_online())
						.build());
	}

	public CatalogVersionDao getCatalogVersionDao()
	{
		return catalogVersionDao;
	}

	@Required
	public void setCatalogVersionDao(final CatalogVersionDao catalogVersionDao)
	{
		this.catalogVersionDao = catalogVersionDao;
	}

	public Catalogs getCatalogs()
	{
		return catalogs;
	}

	@Required
	public void setCatalogs(final Catalogs catalogs)
	{
		this.catalogs = catalogs;
	}

	public ContentCatalogs getContentCatalogs()
	{
		return contentCatalogs;
	}

	@Required
	public void setContentCatalogs(final ContentCatalogs contentCatalogs)
	{
		this.contentCatalogs = contentCatalogs;
	}

	public Currencies getCurrencies()
	{
		return currencies;
	}

	@Required
	public void setCurrencies(final Currencies currencies)
	{
		this.currencies = currencies;
	}
}
