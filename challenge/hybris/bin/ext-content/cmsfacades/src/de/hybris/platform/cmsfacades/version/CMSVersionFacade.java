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
package de.hybris.platform.cmsfacades.version;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.CMSVersionNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Map;


/**
 * Content facade interface which deals with methods related to cms version operations.
 */
public interface CMSVersionFacade
{
	/**
	 * Retrieves a paginated result of CMSVersionData matching the search criteria
	 *
	 * @param itemUUID
	 *           the universal unique identifier of the item being searched on
	 * @param mask
	 *           the mask applied when searching
	 * @param pageableData
	 *           the pagination object
	 * @return the search result object
	 * @throws CMSItemNotFoundException
	 *            when no item found for the given itemUUID
	 */
	SearchResult<CMSVersionData> findVersionsForItem(String itemUUID, String mask, PageableData pageableData)
			throws CMSItemNotFoundException;

	/**
	 * Retrieves a CMSVersionData for the version identified by its uid
	 *
	 * @param uid
	 *           the uid of the cms version
	 * @return the {@code CMSVersionData}
	 * @throws CMSVersionNotFoundException
	 *            when no version found for the given version id
	 */
	CMSVersionData getVersion(String uid) throws CMSVersionNotFoundException;

	/**
	 * Creates a CMSVersionModel for an item
	 *
	 * @param cmsVersionData
	 *           the version data object
	 * @return the {@code CMSVersionData}
	 */
	CMSVersionData createVersion(CMSVersionData cmsVersionData);

	/**
	 * Updates a CMSVersionModel for an item
	 *
	 * @param cmsVersionData
	 *           the version data object
	 * @return the {@code CMSVersionData}
	 */
	CMSVersionData updateVersion(CMSVersionData cmsVersionData);

	/**
	 * Rolls back to a CMSVersionModel for an item
	 *
	 * @param cmsVersionData
	 *           the version data object
	 */
	void rollbackVersion(CMSVersionData cmsVersionData);

	/**
	 * Deletes a CMSVersionModel for an item
	 *
	 * @param cmsVersionData
	 *           the version data object
	 */
	void deleteVersion(CMSVersionData cmsVersionData);

	/**
	 * Returns a CMSItem by its versionUid and item uuid. For more information about Unique Identifiers, see
	 * {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}.
	 *
	 * @param cmsVersionData
	 *           the version data object
	 * @return a CMS item as a map
	 */
	Map<String, Object> getItemByVersion(CMSVersionData cmsVersionData);
}
