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
package de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminTypeRestrictionsService;
import de.hybris.platform.cmsfacades.data.ContentSlotTypeRestrictionsData;
import de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions.PageContentSlotTypeRestrictionsFacade;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PageContentSlotTypeRestrictionsFacade}.
 */
public class DefaultPageContentSlotTypeRestrictionsFacade implements PageContentSlotTypeRestrictionsFacade
{
	private CMSAdminTypeRestrictionsService cmsAdminTypeRestrictionsService;
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	private CMSAdminPageService cmsAdminPageService;
	private CMSAdminSiteService cmsAdminSiteService;
	private CatalogLevelService catalogLevelService;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
	private CMSCatalogVersionService cmsCatalogVersionService;
	private CatalogVersionService catalogVersionService;

	@Override
	public ContentSlotTypeRestrictionsData getTypeRestrictionsForContentSlotUID(final String pageUid, final String contentSlotUid)
			throws CMSItemNotFoundException
	{
		final Set<CMSComponentTypeModel> typeRestrictionsForContentSlot = getSessionSearchRestrictionsDisabler().execute(() ->
		{
			try
			{
				final AbstractPageModel page = getCmsAdminPageService().getPageForIdFromActiveCatalogVersion(pageUid);
				final ContentSlotModel contentSlot = getCmsAdminContentSlotService()
						.getContentSlotForIdAndCatalogVersions(contentSlotUid, getCatalogVersionService().getSessionCatalogVersions());
				return getCmsAdminTypeRestrictionsService().getTypeRestrictionsForContentSlot(page, contentSlot);
			}
			catch (final CMSItemNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		});

		final ContentSlotTypeRestrictionsData contentSlotTypeRestrictions = new ContentSlotTypeRestrictionsData();
		contentSlotTypeRestrictions.setContentSlotUid(contentSlotUid);

		final List<String> collect = typeRestrictionsForContentSlot.stream().map(TypeModel::getCode).collect(Collectors.toList());
		contentSlotTypeRestrictions.setValidComponentTypes(collect);

		return contentSlotTypeRestrictions;

	}

	protected CMSAdminTypeRestrictionsService getCmsAdminTypeRestrictionsService()
	{
		return cmsAdminTypeRestrictionsService;
	}

	@Required
	public void setCmsAdminTypeRestrictionsService(final CMSAdminTypeRestrictionsService cmsAdminTypeRestrictionsService)
	{
		this.cmsAdminTypeRestrictionsService = cmsAdminTypeRestrictionsService;
	}

	protected CMSAdminContentSlotService getCmsAdminContentSlotService()
	{
		return cmsAdminContentSlotService;
	}

	@Required
	public void setCmsAdminContentSlotService(final CMSAdminContentSlotService cmsAdminContentSlotService)
	{
		this.cmsAdminContentSlotService = cmsAdminContentSlotService;
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

	protected CatalogLevelService getCatalogLevelService()
	{
		return catalogLevelService;
	}

	@Required
	public void setCatalogLevelService(final CatalogLevelService catalogLevelService)
	{
		this.catalogLevelService = catalogLevelService;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
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

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
