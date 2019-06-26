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
package de.hybris.platform.cmsfacades.sites.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmsfacades.sites.SiteFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Iterables;


/**
 * Default implementation of {@link SiteFacade}.
 */
public class DefaultSiteFacade implements SiteFacade
{
	private boolean writePermissionRequired;
	private boolean readPermissionRequired;
	private CMSCatalogVersionService cmsCatalogVersionService;
	private CMSAdminSiteService cmsAdminSiteService;
	private Converter<CMSSiteModel, SiteData> cmsSiteModelConverter;
	private Comparator<SiteData> siteDataComparator;
	private CatalogVersionService catalogVersionService;
	private UserService userService;
	private Comparator<ContentCatalogModel> cmsContentCatalogLevelComparator;

	@Override
	public List<SiteData> getAllSiteData()
	{
		final UserModel currentUser = getUserService().getCurrentUser();

		final Collection<CatalogVersionModel> allowedCatalogVersions = new ArrayList<>();

		allowedCatalogVersions.addAll(getCatalogVersionService().getAllReadableCatalogVersions(currentUser).stream()
				.filter(catalogVersion -> !catalogVersion.getActive()).collect(Collectors.toList()));

		final Set<CMSSiteModel> allowedSites = new HashSet<>();
		getCmsAdminSiteService().getSites().stream().filter(cmsSiteModel -> !cmsSiteModel.getContentCatalogs().isEmpty()).forEach(cmsSiteModel -> {
			// setting active site to be consumed by the CatalogLevelService
			getCmsAdminSiteService().setActiveSite(cmsSiteModel);

			// order the content catalogs in a site and fiuind the leaf catalog
			final List<ContentCatalogModel> orderedContentCatalogs = cmsSiteModel.getContentCatalogs().stream()
					.sorted(getCmsContentCatalogLevelComparator()).collect(Collectors.toList());
			final ContentCatalogModel contentCatalogForSite = Iterables.getLast(orderedContentCatalogs);

			// add the site to the result list if user has write permission to the leaf catalog
			allowedCatalogVersions.stream()
					.filter(catalogVersion -> catalogVersion.getCatalog().getPk().equals(contentCatalogForSite.getPk())).findFirst()
					.ifPresent(match -> allowedSites.add(cmsSiteModel));
		});

		return allowedSites.stream().map(site -> getCmsSiteModelConverter().convert(site)) //
				.sorted(getSiteDataComparator()).collect(Collectors.toList());
	}

	@Override
	public List<SiteData> getSitesForCatalogs(final List<String> catalogIds)
	{
		final Set<CMSSiteModel> allowedSites = new HashSet<>();
		getCmsAdminSiteService().getSites().forEach(cmsSiteModel -> {
			// setting active site to be consumed by the CatalogLevelService
			getCmsAdminSiteService().setActiveSite(cmsSiteModel);

			// order the content catalogs in a site and find the leaf catalog
			final List<ContentCatalogModel> orderedContentCatalogs = cmsSiteModel.getContentCatalogs().stream()
					.sorted(getCmsContentCatalogLevelComparator()).collect(Collectors.toList());
			final ContentCatalogModel contentCatalogForSite = Iterables.getLast(orderedContentCatalogs);

			if (catalogIds.contains(contentCatalogForSite.getId()))
			{
				allowedSites.add(cmsSiteModel);
			}
		});

		return allowedSites.stream().map(site -> getCmsSiteModelConverter().convert(site)) //
				.collect(Collectors.toList());
	}

	@Required
	public void setWritePermissionRequired(final boolean writePermissionRequired)
	{
		this.writePermissionRequired = writePermissionRequired;
	}

	/**
	 * This method is used to identify whether users need write permissions to be retrieved by this facade.
	 *
	 * @return if permission is needed or not
	 */
	protected boolean isWritePermissionRequired()
	{
		return writePermissionRequired;
	}

	@Required
	public void setReadPermissionRequired(final boolean readPermissionRequired)
	{
		this.readPermissionRequired = readPermissionRequired;
	}

	/**
	 * This method is used to identify whether users need read permissions to be retrieved by this facade.
	 *
	 * @return if permission is needed or not
	 */
	protected boolean isReadPermissionRequired()
	{
		return readPermissionRequired;
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

	@Required
	public void setSiteDataComparator(final Comparator<SiteData> siteDataComparator)
	{
		this.siteDataComparator = siteDataComparator;
	}

	protected Comparator<SiteData> getSiteDataComparator()
	{
		return siteDataComparator;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsSiteModelConverter(final Converter<CMSSiteModel, SiteData> cmsSiteModelConverter)
	{
		this.cmsSiteModelConverter = cmsSiteModelConverter;
	}

	protected Converter<CMSSiteModel, SiteData> getCmsSiteModelConverter()
	{
		return cmsSiteModelConverter;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Comparator<ContentCatalogModel> getCmsContentCatalogLevelComparator()
	{
		return cmsContentCatalogLevelComparator;
	}

	@Required
	public void setCmsContentCatalogLevelComparator(final Comparator<ContentCatalogModel> cmsContentCatalogLevelComparator)
	{
		this.cmsContentCatalogLevelComparator = cmsContentCatalogLevelComparator;
	}

}
