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

import de.hybris.platform.cmsfacades.data.CatalogVersionData;

import java.util.Comparator;


/**
 * Implementation of a {@link Comparator} which orders {@link CatalogVersionData} dto by putting the active version
 * first and the non-active versions are ordered by {@code version} value.
 */
public class CatalogVersionDataComparator implements Comparator<CatalogVersionData>
{

	@Override
	public int compare(final CatalogVersionData source, final CatalogVersionData target)
	{
		if (source.getActive())
		{
			return -1;
		}
		return source.getVersion().compareTo(target.getVersion());
	}

}
