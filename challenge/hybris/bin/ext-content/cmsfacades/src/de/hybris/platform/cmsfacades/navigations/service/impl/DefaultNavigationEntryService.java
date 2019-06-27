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
package de.hybris.platform.cmsfacades.navigations.service.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryItemModelConverter;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@code NavigationEntryService}.
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade} instead.
 */
@Deprecated
public class DefaultNavigationEntryService implements NavigationEntryService
{
	private CMSNavigationService navigationService;
	private NavigationEntryConverterRegistry navigationEntryConverterRegistry;

	@Override
	@Deprecated
	public CMSNavigationEntryModel createNavigationEntry(final NavigationEntryData navigationEntryData,
			final CatalogVersionModel catalogVersion)
	{
		final NavigationEntryItemModelConverter navigationEntryItemModelConverter = getNavigationEntryConverterRegistry()
				.getNavigationEntryItemModelConverter(navigationEntryData.getItemSuperType()).orElseThrow(
						() -> new ConversionException("Converter not found for Item Type " + navigationEntryData.getItemSuperType()));

		final ItemModel itemModel = navigationEntryItemModelConverter.getConverter().apply(navigationEntryData);
		final CMSNavigationEntryModel navigationEntryModel = getNavigationService().createCmsNavigationEntry(catalogVersion,
				itemModel);
		return navigationEntryModel;
	}

	@Override
	@Deprecated
	public void deleteNavigationEntries(final String navigationNodeUid) throws CMSItemNotFoundException
	{
		final CMSNavigationNodeModel navigationNodeModel = getNavigationService().getNavigationNodeForId(navigationNodeUid);
		if (!CollectionUtils.isEmpty(navigationNodeModel.getEntries()))
		{
			final boolean removeFlag = navigationNodeModel.getEntries().stream()
					.map(entryModel -> getNavigationService().removeNavigationEntryByUid(navigationNodeModel, entryModel.getUid()))
					.reduce(true, (removedA, removedB) -> removedA && removedB);
			if (!removeFlag)
			{
				throw new CMSItemNotFoundException(
						"Could not remove all navigation node entries for node [" + navigationNodeUid + "]");
			}
		}

	}

	protected CMSNavigationService getNavigationService()
	{
		return navigationService;
	}

	@Required
	public void setNavigationService(final CMSNavigationService navigationService)
	{
		this.navigationService = navigationService;
	}

	protected NavigationEntryConverterRegistry getNavigationEntryConverterRegistry()
	{
		return navigationEntryConverterRegistry;
	}

	@Required
	public void setNavigationEntryConverterRegistry(final NavigationEntryConverterRegistry navigationEntryConverterRegistry)
	{
		this.navigationEntryConverterRegistry = navigationEntryConverterRegistry;
	}

}
