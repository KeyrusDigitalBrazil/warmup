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
package de.hybris.platform.cmsfacades.resolvers.sites.impl;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;

import com.google.common.collect.Lists;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Resolver that uses a {@link CMSSiteModel} to resolve a homepage thumbnail URL
 */
public class DefaultSiteThumbnailResolver implements SiteThumbnailResolver
{

	private CMSAdminPageService cmsAdminPageService;
	private CMSAdminSiteService cmsAdminSiteService;
	private CMSCatalogVersionService cmsCatalogVersionService;

	@Override
	public Optional<String> resolveHomepageThumbnailUrl(final CMSSiteModel cmsSiteModel)
	{
		final ContentPageModel homepage = getCmsAdminPageService().getHomepage(cmsSiteModel);
		return resolveHomepageThumbnailUrl(homepage);
	}

	@Override
	public Optional<String> resolveHomepageThumbnailUrl(final CatalogVersionModel catalogVersion)
	{
		ContentCatalogModel contentCatalogModel = (ContentCatalogModel)catalogVersion.getCatalog();
		CMSSiteModel siteModel = getCmsAdminSiteService().getActiveSite();

		List<CatalogVersionModel> catalogVersionModelList = getCmsCatalogVersionService().getSuperCatalogsCatalogVersions(contentCatalogModel, siteModel);
		catalogVersionModelList.add(catalogVersion);

		Optional<ContentPageModel> optionalContentPageModel = Lists.reverse(catalogVersionModelList).stream()
				.filter(catalogVersionModel -> catalogVersionModel.getActive().equals(catalogVersion.getActive()))
				.map(catalogVersionModel -> getCmsAdminPageService().getHomepage(catalogVersionModel))
				.filter(Objects::nonNull)
				.findFirst();

		return optionalContentPageModel.flatMap(this::resolveHomepageThumbnailUrl);
	}

	/**
	 * Can be called to resolve the homepage thumbnail url. This method will also replace any leading '~' with a '/'
	 * using the {@link #replacePrefixTildeWithSlash(String)} method.
	 *
	 * @param homepage
	 *           the ContentPageModel representing the homepage
	 * @return Optional thumbnail url; {@code Optional.empty()} when no url is found
	 */
	protected Optional<String> resolveHomepageThumbnailUrl(final ContentPageModel homepage)
	{
		if (Objects.nonNull(homepage) && Objects.nonNull(homepage.getPreviewImage()))
		{
			final String imgURL = homepage.getPreviewImage().getDownloadURL();
			return ofNullable(replacePrefixTildeWithSlash(imgURL));
		}
		return empty();
	}

	/**
	 * Used to replace a leading '~' with a '/'
	 *
	 * <pre>
	 * replacePrefixTildeWithSlash("~/someUri")    = "/someUri"
	 * replacePrefixTildeWithSlash("~someUri")     = "/someUri"
	 * replacePrefixTildeWithSlash("~//someUri")   = "//someUri"
	 * </pre>
	 *
	 * @param thumbnailUri
	 *           a uri to a thumbnail
	 * @return A uri with the '~' removed
	 */
	protected String replacePrefixTildeWithSlash(final String thumbnailUri)
	{
		String url = thumbnailUri;
		if (StringUtils.isNotEmpty(url))
		{
			url = url.trim();
		}
		return (startsWith(url, "~")) ? "/" + removeStart(removeStart(url, "~"), "/") : url;
	}

	@Required
	public void setCmsAdminPageService(final CMSAdminPageService cmsAdminPageService)
	{
		this.cmsAdminPageService = cmsAdminPageService;
	}

	protected CMSAdminPageService getCmsAdminPageService()
	{
		return cmsAdminPageService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected CMSCatalogVersionService getCmsCatalogVersionService()
	{
		return cmsCatalogVersionService;
	}

	@Required
	public void setCmsCatalogVersionService(CMSCatalogVersionService cmsCatalogVersionService)
	{
		this.cmsCatalogVersionService = cmsCatalogVersionService;
	}
}


