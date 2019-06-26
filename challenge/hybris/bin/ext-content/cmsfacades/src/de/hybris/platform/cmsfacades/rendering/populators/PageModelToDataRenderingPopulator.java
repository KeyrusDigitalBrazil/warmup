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
package de.hybris.platform.cmsfacades.rendering.populators;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator used to add all the information required to render a CMS Page.
 */
public class PageModelToDataRenderingPopulator implements Populator<AbstractPageModel, AbstractPageData>
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private CMSPageService cmsPageService;
	private Converter<ContentSlotData, PageContentSlotData> contentSlotModelToDataRenderingConverter;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public void populate(final AbstractPageModel sourceModel, final AbstractPageData targetData)
	{
		targetData.setUid(sourceModel.getUid());
		targetData.setName(sourceModel.getName());
		targetData.setTypeCode(sourceModel.getItemtype());
		targetData.setLocalizedTitle(sourceModel.getTitle());
		targetData.setDefaultPage(sourceModel.getDefaultPage());
		targetData.setTemplate(sourceModel.getMasterTemplate().getUid());

		// Slots
		targetData.setContentSlots( //
				getCmsPageService().getContentSlotsForPage(sourceModel).stream() //
						.filter(this::isSlotActive) //
						.map(getContentSlotModelToDataRenderingConverter()::convert) //
						.collect(Collectors.toList()) //
		);
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------

	/**
	 * This method is used to determine whether a content slot is active or not.
	 * 
	 * @param contentSlot
	 *           the content slot whose status to check
	 * @return true if the slot is active, false otherwise.
	 */
	protected boolean isSlotActive(final ContentSlotData contentSlot)
	{
		final Date activeFrom = contentSlot.getContentSlot().getActiveFrom();
		final Date activeUntil = contentSlot.getContentSlot().getActiveUntil();
		if (activeFrom != null && activeUntil != null)
		{
			final Date date = new Date();
			if (activeFrom.after(date) || activeUntil.before(date))
			{
				return false;
			}
		}
		return contentSlot.getContentSlot().getActive();
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	@Required
	public void setCmsPageService(final CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}

	protected Converter<ContentSlotData, PageContentSlotData> getContentSlotModelToDataRenderingConverter()
	{
		return contentSlotModelToDataRenderingConverter;
	}

	@Required
	public void setContentSlotModelToDataRenderingConverter(
			final Converter<ContentSlotData, PageContentSlotData> contentSlotModelToDataRenderingConverter)
	{
		this.contentSlotModelToDataRenderingConverter = contentSlotModelToDataRenderingConverter;
	}
}
