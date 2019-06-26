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

import java.util.List;


/**
 * simplification of site related interactions.
 */
public interface SiteFacade
{
	/**
	 * Lists all sites for which user has at-least read access to one of the non-active catalog versions.
	 *
	 * @return All sites that are configured; never <tt>null</tt>
	 */
	List<SiteData> getAllSiteData();

	/**
	 * Lists all sites that are configured for the given list of catalogIds where the catalog id represents the lowest
	 * level catalog in the hierarchy for a site.
	 *
	 * @param catalogIds
	 *           - the catalog identifiers
	 * @return All sites where the catalog ids are the lowest catalog in the catalog hierarchy; never <tt>null</tt>
	 */
	List<SiteData> getSitesForCatalogs(final List<String> catalogIds);

}
