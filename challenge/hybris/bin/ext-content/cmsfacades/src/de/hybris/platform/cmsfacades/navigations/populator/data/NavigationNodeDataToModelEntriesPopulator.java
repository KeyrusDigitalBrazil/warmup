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
package de.hybris.platform.cmsfacades.navigations.populator.data;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link CMSNavigationNodeModel} entries, assuming that it will always replace the
 * existing ones.
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade} instead.
 */
@Deprecated
public class NavigationNodeDataToModelEntriesPopulator implements Populator<NavigationNodeData, CMSNavigationNodeModel>
{
	private NavigationEntryService navigationEntryService;

	@Override
	public void populate(final NavigationNodeData source, final CMSNavigationNodeModel target) throws ConversionException
	{
		if (CollectionUtils.isEmpty(source.getEntries()))
		{
			return;
		}
		target.setEntries(source.getEntries().stream()
				.map(entryData -> getNavigationEntryService().createNavigationEntry(entryData, target.getCatalogVersion()))
				.collect(Collectors.toList()));
	}

	protected NavigationEntryService getNavigationEntryService()
	{
		return navigationEntryService;
	}

	@Required
	public void setNavigationEntryService(final NavigationEntryService navigationEntryService)
	{
		this.navigationEntryService = navigationEntryService;
	}
}
