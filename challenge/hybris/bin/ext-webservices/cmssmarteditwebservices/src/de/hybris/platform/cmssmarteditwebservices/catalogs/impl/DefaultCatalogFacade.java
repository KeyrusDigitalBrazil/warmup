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
package de.hybris.platform.cmssmarteditwebservices.catalogs.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.CatalogData;
import de.hybris.platform.cmssmarteditwebservices.catalogs.CatalogFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CatalogFacade} to retrieve information about catalogs which the
 * current user has access (read and/or write) to.
 */
public class DefaultCatalogFacade implements CatalogFacade
{
	private BaseSiteService baseSiteService;
	private CMSAdminSiteService cmsAdminSiteService;
	private CMSCatalogVersionService cmsCatalogVersionService;
	private Converter<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData> entryToCatalogDataConverter;
	private Converter<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData> entryToContentCatalogDataConverter;
	private ContentCatalogLevelComparator contentCatalogLevelComparator;

	@Override
	public List<CatalogData> getContentCatalogs(final String siteId)
	{
		final CMSSiteModel cmsSiteModel = getCmsAdminSiteService().getSiteForId(siteId);
		getCmsAdminSiteService().setActiveSite(cmsSiteModel);
		return getCmsCatalogVersionService().getContentCatalogsAndVersions(true, true, cmsSiteModel).entrySet().stream()
				.sorted(getContentCatalogLevelComparator())
				.map(entry -> getEntryToContentCatalogDataConverter().convert(entry)).collect(Collectors.toList());
	}

	@Override
	public List<CatalogData> getProductCatalogs(final String siteId)
	{
		final BaseSiteModel baseSiteModel = getBaseSiteService().getBaseSiteForUID(siteId);
		return getCmsCatalogVersionService().getProductCatalogsAndVersions(true, true, baseSiteModel).entrySet().stream()
				.map(entry -> getEntryToCatalogDataConverter().convert(entry)).collect(Collectors.toList());
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected CMSCatalogVersionService getCmsCatalogVersionService()
	{
		return cmsCatalogVersionService;
	}

	@Required
	public void setCmsCatalogVersionService(final CMSCatalogVersionService cmsCatalogVersionService)
	{
		this.cmsCatalogVersionService = cmsCatalogVersionService;
	}

	protected Converter<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData> getEntryToCatalogDataConverter()
	{
		return entryToCatalogDataConverter;
	}

	@Required
	public void setEntryToCatalogDataConverter(
			final Converter<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData> entryToCatalogDataConverter)
	{
		this.entryToCatalogDataConverter = entryToCatalogDataConverter;
	}

	protected Converter<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData> getEntryToContentCatalogDataConverter()
	{
		return entryToContentCatalogDataConverter;
	}

	@Required
	public void setEntryToContentCatalogDataConverter(
			final Converter<Entry<CatalogModel, Set<CatalogVersionModel>>, CatalogData> entryToContentCatalogDataConverter)
	{
		this.entryToContentCatalogDataConverter = entryToContentCatalogDataConverter;
	}

	protected ContentCatalogLevelComparator getContentCatalogLevelComparator()
	{
		return contentCatalogLevelComparator;
	}

	@Required
	public void setContentCatalogLevelComparator(final ContentCatalogLevelComparator contentCatalogLevelComparator)
	{
		this.contentCatalogLevelComparator = contentCatalogLevelComparator;
	}

}
