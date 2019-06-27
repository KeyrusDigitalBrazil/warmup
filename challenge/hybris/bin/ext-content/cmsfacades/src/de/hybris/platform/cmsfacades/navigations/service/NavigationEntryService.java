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
package de.hybris.platform.cmsfacades.navigations.service;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;


/**
 * Navigation Entry service interface which deals with methods related to navigation node entries operations.
 */
public interface NavigationEntryService
{

	/**
	 * Creates a navigation node entry
	 *
	 * @deprecated since 1811.
	 * @param navigationEntryData
	 *           the navigation node entry data with the Item ID and Item Type to be assigned to the navigation node.
	 * @param catalogVersion
	 *           the catalog version model in which the node entry will be created.
	 * @return the new Navigation Node Entry assignment.
	 */
	@Deprecated
	CMSNavigationEntryModel createNavigationEntry(final NavigationEntryData navigationEntryData,
			final CatalogVersionModel catalogVersion);

	/**
	 * Deletes all navigation entries associated with the given navigation node uid.
	 *
	 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade#deleteCMSItemByUuid(String)} instead.
	 * @param navigationNodeUid
	 *           the node where the entries will be removed from.
	 * @throws CMSItemNotFoundException
	 *            when the navigation node does not exist.
	 */
	@Deprecated
	void deleteNavigationEntries(final String navigationNodeUid) throws CMSItemNotFoundException;
}

