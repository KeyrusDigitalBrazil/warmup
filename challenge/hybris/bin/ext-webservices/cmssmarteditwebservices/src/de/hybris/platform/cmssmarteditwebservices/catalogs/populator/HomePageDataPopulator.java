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
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.HomePageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@link HomePageData} dto from a {@link CatalogVersionModel}
 */
public class HomePageDataPopulator implements Populator<CatalogVersionModel, HomePageData>
{
	private CMSAdminPageService cmsAdminPageService;
	private CMSAdminSiteService cmsAdminSiteService;
	private CMSCatalogVersionService cmsCatalogVersionService;
	private Converter<AbstractPageModel, AbstractPageData> abstractPageDataConverter;

	@Override
	public void populate(final CatalogVersionModel source, final HomePageData target) throws ConversionException
	{
		// in a multi-country setup the homepage could be coming from a parent catalog
		final CMSSiteModel site = getCmsAdminSiteService().getActiveSite();
		final ContentPageModel onlineHomePage = getCmsAdminPageService().getHomepage(site);

		final List<CatalogVersionModel> catalogVersionsHierarchy = getCmsCatalogVersionService()
				.getFullHierarchyForCatalogVersion(source, site);
		final ContentPageModel currentHomePage = getCmsAdminPageService().getHomepage(catalogVersionsHierarchy);

		if (currentHomePage != null)
		{
			target.setCurrent(getAbstractPageDataConverter().convert(currentHomePage));
		}
		if (onlineHomePage != null)
		{
			target.setOld(getAbstractPageDataConverter().convert(onlineHomePage));
		}

		final List<CatalogVersionModel> parentActiveCatalogVersions = getCmsCatalogVersionService()
				.getSuperCatalogsActiveCatalogVersions((ContentCatalogModel) source.getCatalog(), site);
		final ContentPageModel fallbackHomePage = getCmsAdminPageService().getHomepage(parentActiveCatalogVersions);
		if (fallbackHomePage != null)
		{
			target.setFallback(getAbstractPageDataConverter().convert(fallbackHomePage));
		}
	}

	protected CMSAdminPageService getCmsAdminPageService()
	{
		return cmsAdminPageService;
	}

	@Required
	public void setCmsAdminPageService(final CMSAdminPageService cmsAdminPageService)
	{
		this.cmsAdminPageService = cmsAdminPageService;
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

	protected Converter<AbstractPageModel, AbstractPageData> getAbstractPageDataConverter()
	{
		return abstractPageDataConverter;
	}

	@Required
	public void setAbstractPageDataConverter(final Converter<AbstractPageModel, AbstractPageData> abstractPageDataConverter)
	{
		this.abstractPageDataConverter = abstractPageDataConverter;
	}
}
