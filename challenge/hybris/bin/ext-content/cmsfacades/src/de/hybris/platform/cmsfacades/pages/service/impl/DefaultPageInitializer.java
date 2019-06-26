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
package de.hybris.platform.cmsfacades.pages.service.impl;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cmsfacades.pages.service.PageInitializer;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@code PageInitializer}
 */
public class DefaultPageInitializer implements PageInitializer
{
	private CMSAdminContentSlotService adminContentSlotService;

	@Override
	public AbstractPageModel initialize(final AbstractPageModel page)
	{
		//only shared slots are already present on this newly created page
		final Set<String> slotPositions = getAdminContentSlotService().getContentSlotsForPage(page).stream()
				.map(ContentSlotData::getPosition).collect(Collectors.toSet());

		page.getMasterTemplate().getAvailableContentSlots().stream()
				.filter(contentSlotName -> !slotPositions.contains(contentSlotName.getName())).forEach(contentSlotName ->
		//only create and associate a slot if the position is not already used by a shared slot
		getAdminContentSlotService().createContentSlot(page, null, contentSlotName.getName(), contentSlotName.getName()));
		return page;
	}

	public CMSAdminContentSlotService getAdminContentSlotService()
	{
		return adminContentSlotService;
	}

	@Required
	public void setAdminContentSlotService(final CMSAdminContentSlotService adminContentSlotService)
	{
		this.adminContentSlotService = adminContentSlotService;
	}
}
