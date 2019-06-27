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
package de.hybris.platform.cmsfacades.pagescontentslots.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.pagescontentslots.PageContentSlotFacade;
import de.hybris.platform.cmsfacades.pagescontentslots.converter.ContentSlotDataConverter;
import de.hybris.platform.cmsfacades.pagescontentslots.service.PageContentSlotConverterRegistry;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PageContentSlotFacade}.
 */
public class DefaultPageContentSlotFacade implements PageContentSlotFacade
{
	private CMSAdminContentSlotService adminContentSlotService;
	private CMSAdminPageService adminPageService;
	private PageContentSlotConverterRegistry pageContentSlotConverterRegistry;
	private ContentSlotDataConverter contentSlotDataConverter;

	@Override
	public List<PageContentSlotData> getContentSlotsByPage(final String pageId)
			throws CMSItemNotFoundException, ConversionException
	{
		AbstractPageModel page = null;
		try
		{
			page = getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			throw new CMSItemNotFoundException("Can not find page with uid \"" + pageId + "\".", e);
		}

		return getAdminContentSlotService().getContentSlotsForPage(page).stream()
				.map(contentSlot -> getContentSlotDataConverter().convert(contentSlot)).collect(Collectors.toList());
	}

	protected CMSAdminContentSlotService getAdminContentSlotService()
	{
		return adminContentSlotService;
	}

	@Required
	public void setAdminContentSlotService(final CMSAdminContentSlotService adminContentSlotService)
	{
		this.adminContentSlotService = adminContentSlotService;
	}

	protected PageContentSlotConverterRegistry getPageContentSlotConverterRegistry()
	{
		return pageContentSlotConverterRegistry;
	}

	@Required
	public void setPageContentSlotConverterRegistry(final PageContentSlotConverterRegistry pageContentSlotConverterRegistry)
	{
		this.pageContentSlotConverterRegistry = pageContentSlotConverterRegistry;
	}

	protected CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

	protected ContentSlotDataConverter getContentSlotDataConverter()
	{
		return contentSlotDataConverter;
	}

	@Required
	public void setContentSlotDataConverter(final ContentSlotDataConverter contentSlotDataConverter)
	{
		this.contentSlotDataConverter = contentSlotDataConverter;
	}

}
