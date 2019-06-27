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

package de.hybris.platform.odata2services.odata.persistence.lookup;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import org.apache.olingo.odata2.api.edm.EdmException;

public interface ItemLookupStrategy
{
	/**
	 * Looks for an {@code Item} on the commerce suite based on the given information.
	 *
	 * @param lookupRequest request information
	 * @return an item model matching the request condition, i.e. the integration key value.
	 * @throws EdmException when request is invalid
	 */
	ItemModel lookup(final ItemLookupRequest lookupRequest) throws EdmException;

	/**
	 * Looks for {@code Item}s in the commerce suite based on the given request parameters.
	 *
	 * @param lookupRequest request information conditions, i.e. type of items to find, page conditions
	 * @return result of the lookup based on the given criteria
	 * @throws EdmException when request is invalid
	 */
	ItemLookupResult<ItemModel> lookupItems(final ItemLookupRequest lookupRequest) throws EdmException;

	/**
	 * Counts how many items in the platform match the provided request conditions.
	 * @param lookupRequest a request specifying an item type, at a minimum, and possibly other conditions. For example, the
	 * request may point to the objects nested in the request's base item type and referred by the navigation segments.
	 * @return number of items in the platform matching the request conditions.
	 * @throws EdmException if the request is invalid
	 */
	int count(ItemLookupRequest lookupRequest) throws EdmException;
}
