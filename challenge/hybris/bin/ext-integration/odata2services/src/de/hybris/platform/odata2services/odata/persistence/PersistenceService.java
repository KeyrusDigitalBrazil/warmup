/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Service to create or update platform items.
 */
public interface PersistenceService
{
	/**
	 * Create or update a commerce suite item and return it as an ODataEntry
	 *
	 * @param storageRequest Parameter object that holds values for creating or updating an item.
	 * @throws EdmException when not being able to persist the given info
	 * @return the ODataEntry that was created or updated
	 */
	ODataEntry createEntityData(StorageRequest storageRequest) throws EdmException;

	/**
	 * Get a commerce suite item as an ODataEntry
	 *
	 * @param lookupRequest Parameter object that holds values for getting an item.
	 * @param options Parameter that indicates options to be used during item conversion.
	 * @throws EdmException when not being able to persist the given info
	 * @return the ODataEntry that was required
	 */
	ODataEntry getEntityData(ItemLookupRequest lookupRequest, ConversionOptions options) throws EdmException;

	/**
	 * Searches for items matching the conditions of the specifed request
	 *
	 * @param lookupRequest specifies what items need to be found and conditions for matching the items existing in the platform.
	 * @param options Parameter that indicates options to be used during item conversion
	 * @throws EdmException when not being able to retrieve the entities
	 * @return result of searching for ODataEntries matching the specified request conditions in the platform
	 */
	ItemLookupResult<ODataEntry> getEntities(final ItemLookupRequest lookupRequest, ConversionOptions options) throws EdmException;
}
