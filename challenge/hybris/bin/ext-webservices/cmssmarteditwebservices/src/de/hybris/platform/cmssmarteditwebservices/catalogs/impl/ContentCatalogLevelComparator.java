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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Compare {@code Entry<CatalogModel, Set<CatalogVersionModel>>} by catalog level where the catalog slot was defined.
 * This is used in the context of multi-country.
 */
public class ContentCatalogLevelComparator implements Comparator<Entry<CatalogModel, Set<CatalogVersionModel>>>
{
	private CatalogLevelService cmsCatalogLevelService;

	@Override
	public int compare(final Entry<CatalogModel, Set<CatalogVersionModel>> entry1,
			final Entry<CatalogModel, Set<CatalogVersionModel>> entry2)
	{
		if (entry1 == null)
		{
			return 1;
		}
		else if (entry2 == null)
		{
			return -1;
		}
		else
		{
			final int entry1CatalogLevel = getCmsCatalogLevelService().getCatalogLevel((ContentCatalogModel) entry1.getKey());
			final int entry2CatalogLevel = getCmsCatalogLevelService().getCatalogLevel((ContentCatalogModel) entry2.getKey());
			return Integer.compare(entry1CatalogLevel, entry2CatalogLevel);
		}
	}

	protected CatalogLevelService getCmsCatalogLevelService()
	{
		return cmsCatalogLevelService;
	}

	@Required
	public void setCmsCatalogLevelService(final CatalogLevelService cmsCatalogLevelService)
	{
		this.cmsCatalogLevelService = cmsCatalogLevelService;
	}
}
