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
package de.hybris.platform.cmsfacades.sites;


import de.hybris.platform.cmsfacades.data.SiteData;

import java.util.Comparator;


/**
 * Implementation of a {@link Comparator} which uses the natural ordering of uid in a {@link SiteData} dto.
 */
public class SiteDataUidComparator implements Comparator<SiteData>
{

	@Override
	public int compare(final SiteData that, final SiteData other)
	{
		return that.getUid().compareTo(other.getUid());
	}
}
