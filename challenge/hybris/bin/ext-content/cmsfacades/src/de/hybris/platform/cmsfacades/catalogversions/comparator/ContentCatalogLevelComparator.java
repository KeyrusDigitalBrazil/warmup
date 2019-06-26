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
package de.hybris.platform.cmsfacades.catalogversions.comparator;

import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Required;


/**
 * Compare {@code CatalognModel} by catalog level where the catalog was defined. This is used in the
 * context of multi-country.
 */
public class ContentCatalogLevelComparator implements Comparator<ContentCatalogModel>
{
	private CatalogLevelService cmsCatalogLevelService;

	@Override
	public int compare(final ContentCatalogModel catalog1, final ContentCatalogModel catalog2)
	{
		if (catalog1 == null)
		{
			return 1;
		}
		else if (catalog2 == null)
		{
			return -1;
		}
		else
		{
			final int entry1CatalogLevel = getCmsCatalogLevelService().getCatalogLevel(catalog1);
			final int entry2CatalogLevel = getCmsCatalogLevelService().getCatalogLevel(catalog2);
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
