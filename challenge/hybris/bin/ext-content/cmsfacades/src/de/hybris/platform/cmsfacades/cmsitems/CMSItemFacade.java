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
package de.hybris.platform.cmsfacades.cmsitems;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Map;


/**
 * Content facade interface which deals with CRUD and search operations related to any CMSItem by passing on valued
 * Maps.
 */
public interface CMSItemFacade
{
	/**
	 * A Search for CMSItems by their universally unique identifiers
	 *
	 * @param uuids
	 *           The list of uuids of the CMSItems to look for
	 * @return A list of the corresponding CMSItems
	 * @throws CMSItemNotFoundException
	 *            if the item is not found
	 */
	List<Map<String, Object>> findCMSItems(List<String> uuids) throws CMSItemNotFoundException;

	/**
	 * A paged Search for CMSItems in a given catalogversion. Optionally filter by name/uid or typeCode
	 *
	 * @param cmsItemSearchData
	 *           The catalog and filter information
	 * @param pageableData
	 *           The paging information
	 * @return A SearchResult containing the paging information and the results
	 * @throws de.hybris.platform.cmsfacades.exception.ValidationException
	 *            When catalog or paging information is missing
	 */
	SearchResult<Map<String, Object>> findCMSItems(final CMSItemSearchData cmsItemSearchData, final PageableData pageableData);

	/**
	 * Get one single CMSItem by its uuid (Universal Unique Identifier) For more information about Unique Identifiers,
	 * see {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}
	 *
	 * @param uuid
	 *           - the universal unique identifier
	 * @return the {@code Map<String, Object>} representation of the CMS Item
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given {@code uuid}.
	 */
	Map<String, Object> getCMSItemByUuid(final String uuid) throws CMSItemNotFoundException;

	/**
	 * Create CMS Items given the attribute value map.
	 *
	 * @param itemMap
	 *           - the {@code Map<String, Object>} that contains the attributes values for the new CMSItem.
	 * @return the Map tha represents the newly created CMS Item.
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given {@code uuid}.
	 */
	Map<String, Object> createItem(final Map<String, Object> itemMap) throws CMSItemNotFoundException;

	/**
	 * Updates a CMS Item given the attribute value map and its uuid. For more information about Unique Identifiers, see
	 * {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}
	 *
	 * @param uuid
	 *           - the universal unique identifier
	 * @param itemMap
	 *           - the {@code Map<String, Object>} that contains the attributes values for the new CMSItem.
	 * @return the Map tha represents the newly created CMS Item.
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given {@code uuid}.
	 */
	Map<String, Object> updateItem(final String uuid, final Map<String, Object> itemMap) throws CMSItemNotFoundException;

	/**
	 * Deletes one single CMSItem by its uuid (Universal Unique Identifier)
	 *
	 * @param uuid
	 *           - the universal unique identifier
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given {@code uuid}.
	 */
	void deleteCMSItemByUuid(final String uuid) throws CMSItemNotFoundException;

	/**
	 * Validates a CMS Item given the attribute value map and its uuid. For more information about Unique Identifiers,
	 * see {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}
	 *
	 * @param uuid
	 *           - the universal unique identifier
	 * @param itemMap
	 *           - the {@code Map<String, Object>} that contains the attributes values for the new CMSItem.
	 * @return the Map that represents the CMS Item.
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given {@code uuid}.
	 */
	Map<String, Object> validateItemForUpdate(final String uuid, final Map<String, Object> itemMap)
			throws CMSItemNotFoundException;

	/**
	 * Validates CMS Items given the attribute value map.
	 *
	 * @param itemMap
	 *           - the {@code Map<String, Object>} that contains the attributes values for the new CMSItem.
	 * @return the Map that represents the newly created CMS Item.
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given {@code uuid}.
	 */
	Map<String, Object> validateItemForCreate(final Map<String, Object> itemMap) throws CMSItemNotFoundException;
}
